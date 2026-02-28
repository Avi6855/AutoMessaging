package com.avinashpatil.app.automessage.utils

import android.content.Context
import com.avinashpatil.app.automessage.data.entity.ContactEntity
import com.avinashpatil.app.automessage.data.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class ContactSyncManager(
    private val context: Context,
    private val contactRepository: ContactRepository
) {
    suspend fun syncDeviceContactsToRoom() = withContext(Dispatchers.IO) {
        try {
            val deviceContacts = ContactHelper.getDeviceContacts(context)
            val roomContacts = contactRepository.getAllContacts().first()

            // Build maps for efficient lookup
            val deviceMap = deviceContacts.groupBy { normalize(it.id, it.phoneNumber) }
            val roomMap = roomContacts.associateBy { normalize(it.id, it.phoneNumber) }.toMutableMap()

            // Insert or update device contacts into Room
            for (contact in deviceContacts) {
                val key = normalize(contact.id, contact.phoneNumber)
                val existing = roomMap[key]
                if (existing == null) {
                    // New contact; insert
                    contactRepository.insertContact(contact)
                } else {
                    // Update fields that may have changed (name, photo)
                    val updated = existing.copy(
                        name = contact.name,
                        photoUri = contact.photoUri,
                        updatedAt = System.currentTimeMillis()
                    )
                    if (updated != existing) {
                        contactRepository.updateContact(updated)
                    }
                    // Remove from map to track which remain (used to detect deletions)
                    roomMap.remove(key)
                }
            }

            // Any remaining entries in roomMap are not present on device anymore
            for ((_, orphan) in roomMap) {
                // Preserve blacklist/priority flags but handle automatic removal from groups
                val removed = orphan.copy(groupId = null, updatedAt = System.currentTimeMillis())
                contactRepository.updateContact(removed)
            }
        } catch (e: Exception) {
            android.util.Log.e("ContactSyncManager", "Failed syncing contacts", e)
        }
    }

    private fun normalize(id: String, phone: String): String {
        return "$id:${normalizePhone(phone)}"
    }

    private fun normalizePhone(phone: String): String {
        return phone
            .replace(" ", "")
            .replace("-", "")
            .replace("(", "")
            .replace(")", "")
            .replace(".", "")
            .trim()
    }
}