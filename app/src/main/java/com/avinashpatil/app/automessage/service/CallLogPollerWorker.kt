package com.avinashpatil.app.automessage.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.avinashpatil.app.automessage.data.database.AutoMessageDatabase
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity
import com.avinashpatil.app.automessage.data.repository.AutoReplyRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.ContactRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.DataStoreRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.GroupRepositoryImpl
import com.avinashpatil.app.automessage.data.repository.MessageRepositoryImpl
import com.avinashpatil.app.automessage.utils.AutoMessagingStateChecker
import com.avinashpatil.app.automessage.utils.ContactHelper
import com.avinashpatil.app.automessage.utils.DailyHistoryClearScheduler
import com.avinashpatil.app.automessage.utils.DailyMessageTracker
import com.avinashpatil.app.automessage.utils.DuplicatePreventer
import com.avinashpatil.app.automessage.utils.MissedCallBackfill
import com.avinashpatil.app.automessage.utils.PhoneNumberUtils
import com.avinashpatil.app.automessage.utils.SmsAntiSpamHelper
import com.avinashpatil.app.automessage.utils.dataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class CallLogPollerWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "CallLogPollerWorker"
        private const val ACTION_SMS_SENT = "com.avinashpatil.app.automessage.SMS_SENT"
        private const val ACTION_SMS_DELIVERED = "com.avinashpatil.app.automessage.SMS_DELIVERED"
    }

    override suspend fun doWork(): Result {
        return try {
            val ctx = applicationContext
            val enabled = AutoMessagingStateChecker.isAutoMessagingEnabled(ctx)
            Log.d(TAG, "AUTO_MSG: poller running, autoMessaging=$enabled")

            if (!enabled) {
                Log.d(TAG, "AUTO_MSG: poller skipped because automation disabled")
                return Result.success()
            }

            if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w(TAG, "AUTO_MSG: poller skipped, missing READ_CALL_LOG/SEND_SMS")
                return Result.success()
            }

            // Do NOT start FGS from Worker — causes ForegroundServiceDidNotStopInTimeException.
            // Instead backfill missed answered calls directly (oldest-first, capped).
            backfillMissedCalls(ctx)
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "AUTO_MSG: poller failed", e)
            Result.success()
        }
    }

    private suspend fun backfillMissedCalls(ctx: Context) {
        try {
            val db = AutoMessageDatabase.getInstance(ctx)
            val dataStoreRepo = DataStoreRepositoryImpl(ctx.dataStore)
            if (!dataStoreRepo.isAutoReplyEnabled().first()) {
                Log.d(TAG, "AUTO_MSG: backfill skipped, auto-reply disabled")
                return
            }
            val autoReplyRepo = AutoReplyRepositoryImpl(db.autoReplyLogDao(), db.lastSeenCallDao())
            val contactRepo = ContactRepositoryImpl(db.contactDao())
            val messageRepo = MessageRepositoryImpl(db.customMessageDao())
            val groupRepo = GroupRepositoryImpl(db.groupDao())

            val since = System.currentTimeMillis() - MissedCallBackfill.BACKFILL_WINDOW_MS
            val missed = MissedCallBackfill.getAnsweredCallsSince(ctx, since, 50)
                .take(MissedCallBackfill.MAX_PER_RUN)
            if (missed.isEmpty()) {
                Log.d(TAG, "AUTO_MSG: backfill found no answered calls")
                return
            }
            Log.d(TAG, "AUTO_MSG: backfilling ${missed.size} answered calls")
            var sent = 0
            for (call in missed) {
                try {
                    if (!AutoMessagingStateChecker.isAutoMessagingEnabled(ctx)) break
                    val normalized = PhoneNumberUtils.normalize(call.number)
                    if (normalized.isBlank()) continue
                    val callId = call.id.toString()
                    if (!DuplicatePreventer.shouldProcess(ctx, callId, normalized, 30_000)) continue
                    if (DailyMessageTracker.hasSentToday(ctx, normalized)) continue
                    if (!SmsAntiSpamHelper.canSendNow(ctx)) {
                        Log.w(TAG, "AUTO_MSG: backfill stopping, anti-spam throttle")
                        break
                    }
                    val contact = try {
                        ContactHelper.getContactByPhoneNumber(ctx, call.number)
                    } catch (_: Exception) { null }
                    if (contact?.id != null) {
                        try {
                            if (contactRepo.getContactById(contact.id)?.isBlacklisted == true) {
                                DuplicatePreventer.markProcessed(ctx, callId, normalized)
                                continue
                            }
                        } catch (_: Exception) {}
                    }
                    val base = messageForContact(messageRepo, groupRepo, contact)
                    val message = SmsAntiSpamHelper.prepareMessage(base, contact)
                    if (message.isBlank()) {
                        DuplicatePreventer.markProcessed(ctx, callId, normalized)
                        continue
                    }
                    // Fresh calls (<2 min) respect user delay; older backfill sends immediately to catch up.
                    val ageMs = System.currentTimeMillis() - call.date
                    if (ageMs < 2 * 60_000) {
                        try {
                            val delaySec = dataStoreRepo.getAutoReplyDelay().first()
                            if (delaySec > 0) delay(delaySec * 1000L)
                        } catch (_: Exception) {}
                    }
                    if (sendOne(ctx, autoReplyRepo, call.number, normalized, message, contact, callId, call)) {
                        sent++
                        delay(2000)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "AUTO_MSG: backfill error for ${call.number}", e)
                }
            }
            Log.d(TAG, "AUTO_MSG: backfill done, sent=$sent")
        } catch (e: Exception) {
            Log.e(TAG, "AUTO_MSG: backfill failed", e)
        }
    }

    private suspend fun messageForContact(
        messageRepo: MessageRepositoryImpl,
        groupRepo: GroupRepositoryImpl,
        contact: com.avinashpatil.app.automessage.data.entity.ContactEntity?
    ): String {
        return try {
            when {
                contact?.groupId != null -> {
                    val group = try { groupRepo.getGroupById(contact.groupId!!) } catch (_: Exception) { null }
                    messageRepo.getMessageByGroupType(group?.name ?: "DEFAULT")?.body ?: "Testing purpose."
                }
                contact?.isPriority == true -> {
                    messageRepo.getMessageByGroupType("VIP")?.body ?: "Testing purpose."
                }
                else -> {
                    messageRepo.getDefaultMessage()?.body ?: "Testing purpose."
                }
            }
        } catch (_: Exception) { "Testing purpose." }
    }

    private suspend fun sendOne(
        ctx: Context,
        autoReplyRepo: AutoReplyRepositoryImpl,
        rawNumber: String,
        normalized: String,
        message: String,
        contact: com.avinashpatil.app.automessage.data.entity.ContactEntity?,
        callId: String,
        call: MissedCallBackfill.AnsweredCall
    ): Boolean {
        return try {
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ctx.getSystemService(SmsManager::class.java) ?: SmsManager.getDefault()
            } else {
                SmsManager.getDefault()
            }
            val ts = System.currentTimeMillis()
            val dayKey = DailyHistoryClearScheduler.getTodayDayKey()
            val callTypeStr = if (call.type == android.provider.CallLog.Calls.OUTGOING_TYPE) "OUTGOING_ANSWERED" else "INCOMING_ANSWERED"
            val preLog = AutoReplyLogEntity(
                contactId = contact?.id ?: rawNumber,
                contactName = contact?.name ?: rawNumber,
                phoneNumber = rawNumber,
                messageText = message,
                timestamp = ts,
                dayKey = dayKey,
                callType = callTypeStr,
                isAutoReply = true,
                status = "PENDING",
                attempts = 0,
                error = null,
                sentTimestamp = null,
                deliveredTimestamp = null
            )
            val logId: Long = try {
                autoReplyRepo.logAutoReplyReturnId(preLog)
            } catch (e: Exception) {
                if (e is android.database.sqlite.SQLiteConstraintException) {
                    val existing = try { autoReplyRepo.getLogByPhoneAndDay(rawNumber, dayKey) } catch (_: Exception) { null }
                    // Normalized daily check already passed; raw-key conflict means same raw number already logged today
                    if (existing == null) return false
                    if (existing.status == "FAILED" && existing.attempts < 3) {
                        existing.id
                    } else {
                        DuplicatePreventer.markProcessed(ctx, callId, normalized)
                        return false
                    }
                } else throw e
            }
            val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            val requestCode = (logId % Int.MAX_VALUE).toInt()
            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                val sentIntents = ArrayList<PendingIntent>()
                val deliveredIntents = ArrayList<PendingIntent>()
                for (i in parts.indices) {
                    sentIntents.add(
                        PendingIntent.getBroadcast(
                            ctx, requestCode + i,
                            Intent(ACTION_SMS_SENT).apply {
                                putExtra("log_id", logId); putExtra("attempts", 1); putExtra("phone", rawNumber)
                            }, flags
                        )
                    )
                    deliveredIntents.add(
                        PendingIntent.getBroadcast(
                            ctx, requestCode + i + 10_000,
                            Intent(ACTION_SMS_DELIVERED).apply {
                                putExtra("log_id", logId); putExtra("phone", rawNumber)
                            }, flags
                        )
                    )
                }
                smsManager.sendMultipartTextMessage(rawNumber, null, parts, sentIntents, deliveredIntents)
            } else {
                val piSent = PendingIntent.getBroadcast(
                    ctx, requestCode,
                    Intent(ACTION_SMS_SENT).apply {
                        putExtra("log_id", logId); putExtra("attempts", 1); putExtra("phone", rawNumber)
                    }, flags
                )
                val piDelivered = PendingIntent.getBroadcast(
                    ctx, requestCode + 10_000,
                    Intent(ACTION_SMS_DELIVERED).apply {
                        putExtra("log_id", logId); putExtra("phone", rawNumber)
                    }, flags
                )
                smsManager.sendTextMessage(rawNumber, null, message, piSent, piDelivered)
            }
            try { autoReplyRepo.updateLastSeenCall(callId, contact?.id) } catch (_: Exception) {}
            DuplicatePreventer.markProcessed(ctx, callId, normalized)
            DailyMessageTracker.markSentToday(ctx, normalized)
            SmsAntiSpamHelper.recordSentIfAllowed(ctx)
            Log.d(TAG, "AUTO_MSG: backfill sent to $rawNumber (call $callId)")
            true
        } catch (e: Exception) {
            Log.e(TAG, "AUTO_MSG: backfill send failed for $rawNumber", e)
            false
        }
    }
}
