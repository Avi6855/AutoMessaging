package com.avinashpatil.app.automessage.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AutoReplyLogDao_Impl implements AutoReplyLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AutoReplyLogEntity> __insertionAdapterOfAutoReplyLogEntity;

  private final EntityDeletionOrUpdateAdapter<AutoReplyLogEntity> __deletionAdapterOfAutoReplyLogEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldLogs;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllLogs;

  private final SharedSQLiteStatement __preparedStmtOfMarkLogSent;

  private final SharedSQLiteStatement __preparedStmtOfMarkLogFailed;

  private final SharedSQLiteStatement __preparedStmtOfMarkLogDelivered;

  private final SharedSQLiteStatement __preparedStmtOfConvertAllToDelivered;

  public AutoReplyLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAutoReplyLogEntity = new EntityInsertionAdapter<AutoReplyLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `auto_reply_logs` (`id`,`contactId`,`contactName`,`phoneNumber`,`messageText`,`timestamp`,`dayKey`,`callType`,`isAutoReply`,`status`,`attempts`,`error`,`sentTimestamp`,`deliveredTimestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AutoReplyLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContactId());
        statement.bindString(3, entity.getContactName());
        statement.bindString(4, entity.getPhoneNumber());
        statement.bindString(5, entity.getMessageText());
        statement.bindLong(6, entity.getTimestamp());
        statement.bindString(7, entity.getDayKey());
        statement.bindString(8, entity.getCallType());
        final int _tmp = entity.isAutoReply() ? 1 : 0;
        statement.bindLong(9, _tmp);
        statement.bindString(10, entity.getStatus());
        statement.bindLong(11, entity.getAttempts());
        if (entity.getError() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getError());
        }
        if (entity.getSentTimestamp() == null) {
          statement.bindNull(13);
        } else {
          statement.bindLong(13, entity.getSentTimestamp());
        }
        if (entity.getDeliveredTimestamp() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getDeliveredTimestamp());
        }
      }
    };
    this.__deletionAdapterOfAutoReplyLogEntity = new EntityDeletionOrUpdateAdapter<AutoReplyLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `auto_reply_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AutoReplyLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOldLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM auto_reply_logs WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllLogs = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM auto_reply_logs";
        return _query;
      }
    };
    this.__preparedStmtOfMarkLogSent = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE auto_reply_logs SET status = ?, attempts = ?, error = ?, sentTimestamp = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkLogFailed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE auto_reply_logs SET status = ?, attempts = ?, error = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkLogDelivered = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE auto_reply_logs SET status = ?, deliveredTimestamp = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfConvertAllToDelivered = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE auto_reply_logs SET status = 'DELIVERED', deliveredTimestamp = ? WHERE status IN ('PENDING','SENT')";
        return _query;
      }
    };
  }

  @Override
  public Object insertLog(final AutoReplyLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAutoReplyLogEntity.insert(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLogReturnId(final AutoReplyLogEntity log,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAutoReplyLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertLogs(final List<AutoReplyLogEntity> logs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAutoReplyLogEntity.insert(logs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLog(final AutoReplyLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfAutoReplyLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldLogs(final long cutoffTime, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldLogs.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoffTime);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOldLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllLogs(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllLogs.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllLogs.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markLogSent(final long id, final String status, final int attempts,
      final String error, final long sentTs, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkLogSent.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, attempts);
        _argIndex = 3;
        if (error == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, error);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, sentTs);
        _argIndex = 5;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkLogSent.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markLogFailed(final long id, final String status, final int attempts,
      final String error, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkLogFailed.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, attempts);
        _argIndex = 3;
        if (error == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, error);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkLogFailed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markLogDelivered(final long id, final String status, final long deliveredTs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkLogDelivered.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, deliveredTs);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkLogDelivered.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object convertAllToDelivered(final long deliveredTs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfConvertAllToDelivered.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, deliveredTs);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfConvertAllToDelivered.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AutoReplyLogEntity>> getAllLogs() {
    final String _sql = "SELECT * FROM auto_reply_logs ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"auto_reply_logs"}, new Callable<List<AutoReplyLogEntity>>() {
      @Override
      @NonNull
      public List<AutoReplyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfIsAutoReply = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoReply");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfError = CursorUtil.getColumnIndexOrThrow(_cursor, "error");
          final int _cursorIndexOfSentTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "sentTimestamp");
          final int _cursorIndexOfDeliveredTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredTimestamp");
          final List<AutoReplyLogEntity> _result = new ArrayList<AutoReplyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AutoReplyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactId;
            _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final boolean _tmpIsAutoReply;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoReply);
            _tmpIsAutoReply = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpError;
            if (_cursor.isNull(_cursorIndexOfError)) {
              _tmpError = null;
            } else {
              _tmpError = _cursor.getString(_cursorIndexOfError);
            }
            final Long _tmpSentTimestamp;
            if (_cursor.isNull(_cursorIndexOfSentTimestamp)) {
              _tmpSentTimestamp = null;
            } else {
              _tmpSentTimestamp = _cursor.getLong(_cursorIndexOfSentTimestamp);
            }
            final Long _tmpDeliveredTimestamp;
            if (_cursor.isNull(_cursorIndexOfDeliveredTimestamp)) {
              _tmpDeliveredTimestamp = null;
            } else {
              _tmpDeliveredTimestamp = _cursor.getLong(_cursorIndexOfDeliveredTimestamp);
            }
            _item = new AutoReplyLogEntity(_tmpId,_tmpContactId,_tmpContactName,_tmpPhoneNumber,_tmpMessageText,_tmpTimestamp,_tmpDayKey,_tmpCallType,_tmpIsAutoReply,_tmpStatus,_tmpAttempts,_tmpError,_tmpSentTimestamp,_tmpDeliveredTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AutoReplyLogEntity>> getLogsByContact(final String contactId) {
    final String _sql = "SELECT * FROM auto_reply_logs WHERE contactId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, contactId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"auto_reply_logs"}, new Callable<List<AutoReplyLogEntity>>() {
      @Override
      @NonNull
      public List<AutoReplyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfIsAutoReply = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoReply");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfError = CursorUtil.getColumnIndexOrThrow(_cursor, "error");
          final int _cursorIndexOfSentTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "sentTimestamp");
          final int _cursorIndexOfDeliveredTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredTimestamp");
          final List<AutoReplyLogEntity> _result = new ArrayList<AutoReplyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AutoReplyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactId;
            _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final boolean _tmpIsAutoReply;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoReply);
            _tmpIsAutoReply = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpError;
            if (_cursor.isNull(_cursorIndexOfError)) {
              _tmpError = null;
            } else {
              _tmpError = _cursor.getString(_cursorIndexOfError);
            }
            final Long _tmpSentTimestamp;
            if (_cursor.isNull(_cursorIndexOfSentTimestamp)) {
              _tmpSentTimestamp = null;
            } else {
              _tmpSentTimestamp = _cursor.getLong(_cursorIndexOfSentTimestamp);
            }
            final Long _tmpDeliveredTimestamp;
            if (_cursor.isNull(_cursorIndexOfDeliveredTimestamp)) {
              _tmpDeliveredTimestamp = null;
            } else {
              _tmpDeliveredTimestamp = _cursor.getLong(_cursorIndexOfDeliveredTimestamp);
            }
            _item = new AutoReplyLogEntity(_tmpId,_tmpContactId,_tmpContactName,_tmpPhoneNumber,_tmpMessageText,_tmpTimestamp,_tmpDayKey,_tmpCallType,_tmpIsAutoReply,_tmpStatus,_tmpAttempts,_tmpError,_tmpSentTimestamp,_tmpDeliveredTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AutoReplyLogEntity>> getLogsByDateRange(final long startTime,
      final long endTime) {
    final String _sql = "SELECT * FROM auto_reply_logs WHERE timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endTime);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"auto_reply_logs"}, new Callable<List<AutoReplyLogEntity>>() {
      @Override
      @NonNull
      public List<AutoReplyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfIsAutoReply = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoReply");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfError = CursorUtil.getColumnIndexOrThrow(_cursor, "error");
          final int _cursorIndexOfSentTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "sentTimestamp");
          final int _cursorIndexOfDeliveredTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredTimestamp");
          final List<AutoReplyLogEntity> _result = new ArrayList<AutoReplyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AutoReplyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactId;
            _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final boolean _tmpIsAutoReply;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoReply);
            _tmpIsAutoReply = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpError;
            if (_cursor.isNull(_cursorIndexOfError)) {
              _tmpError = null;
            } else {
              _tmpError = _cursor.getString(_cursorIndexOfError);
            }
            final Long _tmpSentTimestamp;
            if (_cursor.isNull(_cursorIndexOfSentTimestamp)) {
              _tmpSentTimestamp = null;
            } else {
              _tmpSentTimestamp = _cursor.getLong(_cursorIndexOfSentTimestamp);
            }
            final Long _tmpDeliveredTimestamp;
            if (_cursor.isNull(_cursorIndexOfDeliveredTimestamp)) {
              _tmpDeliveredTimestamp = null;
            } else {
              _tmpDeliveredTimestamp = _cursor.getLong(_cursorIndexOfDeliveredTimestamp);
            }
            _item = new AutoReplyLogEntity(_tmpId,_tmpContactId,_tmpContactName,_tmpPhoneNumber,_tmpMessageText,_tmpTimestamp,_tmpDayKey,_tmpCallType,_tmpIsAutoReply,_tmpStatus,_tmpAttempts,_tmpError,_tmpSentTimestamp,_tmpDeliveredTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AutoReplyLogEntity>> getLogsByPhone(final String phone) {
    final String _sql = "SELECT * FROM auto_reply_logs WHERE phoneNumber = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phone);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"auto_reply_logs"}, new Callable<List<AutoReplyLogEntity>>() {
      @Override
      @NonNull
      public List<AutoReplyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfIsAutoReply = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoReply");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfError = CursorUtil.getColumnIndexOrThrow(_cursor, "error");
          final int _cursorIndexOfSentTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "sentTimestamp");
          final int _cursorIndexOfDeliveredTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredTimestamp");
          final List<AutoReplyLogEntity> _result = new ArrayList<AutoReplyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AutoReplyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactId;
            _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final boolean _tmpIsAutoReply;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoReply);
            _tmpIsAutoReply = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpError;
            if (_cursor.isNull(_cursorIndexOfError)) {
              _tmpError = null;
            } else {
              _tmpError = _cursor.getString(_cursorIndexOfError);
            }
            final Long _tmpSentTimestamp;
            if (_cursor.isNull(_cursorIndexOfSentTimestamp)) {
              _tmpSentTimestamp = null;
            } else {
              _tmpSentTimestamp = _cursor.getLong(_cursorIndexOfSentTimestamp);
            }
            final Long _tmpDeliveredTimestamp;
            if (_cursor.isNull(_cursorIndexOfDeliveredTimestamp)) {
              _tmpDeliveredTimestamp = null;
            } else {
              _tmpDeliveredTimestamp = _cursor.getLong(_cursorIndexOfDeliveredTimestamp);
            }
            _item = new AutoReplyLogEntity(_tmpId,_tmpContactId,_tmpContactName,_tmpPhoneNumber,_tmpMessageText,_tmpTimestamp,_tmpDayKey,_tmpCallType,_tmpIsAutoReply,_tmpStatus,_tmpAttempts,_tmpError,_tmpSentTimestamp,_tmpDeliveredTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getSuccessfulCountByPhoneInRange(final String phone, final long startTime,
      final long endTime, final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM auto_reply_logs WHERE phoneNumber = ? AND timestamp BETWEEN ? AND ? AND status IN ('SENT','DELIVERED')";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phone);
    _argIndex = 2;
    _statement.bindLong(_argIndex, startTime);
    _argIndex = 3;
    _statement.bindLong(_argIndex, endTime);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AutoReplyLogEntity>> searchLogs(final String query) {
    final String _sql = "SELECT * FROM auto_reply_logs WHERE messageText LIKE '%' || ? || '%' ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"auto_reply_logs"}, new Callable<List<AutoReplyLogEntity>>() {
      @Override
      @NonNull
      public List<AutoReplyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfIsAutoReply = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoReply");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfError = CursorUtil.getColumnIndexOrThrow(_cursor, "error");
          final int _cursorIndexOfSentTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "sentTimestamp");
          final int _cursorIndexOfDeliveredTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredTimestamp");
          final List<AutoReplyLogEntity> _result = new ArrayList<AutoReplyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AutoReplyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactId;
            _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final boolean _tmpIsAutoReply;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoReply);
            _tmpIsAutoReply = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpError;
            if (_cursor.isNull(_cursorIndexOfError)) {
              _tmpError = null;
            } else {
              _tmpError = _cursor.getString(_cursorIndexOfError);
            }
            final Long _tmpSentTimestamp;
            if (_cursor.isNull(_cursorIndexOfSentTimestamp)) {
              _tmpSentTimestamp = null;
            } else {
              _tmpSentTimestamp = _cursor.getLong(_cursorIndexOfSentTimestamp);
            }
            final Long _tmpDeliveredTimestamp;
            if (_cursor.isNull(_cursorIndexOfDeliveredTimestamp)) {
              _tmpDeliveredTimestamp = null;
            } else {
              _tmpDeliveredTimestamp = _cursor.getLong(_cursorIndexOfDeliveredTimestamp);
            }
            _item = new AutoReplyLogEntity(_tmpId,_tmpContactId,_tmpContactName,_tmpPhoneNumber,_tmpMessageText,_tmpTimestamp,_tmpDayKey,_tmpCallType,_tmpIsAutoReply,_tmpStatus,_tmpAttempts,_tmpError,_tmpSentTimestamp,_tmpDeliveredTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getLogByPhoneAndDay(final String phone, final String dayKey,
      final Continuation<? super AutoReplyLogEntity> $completion) {
    final String _sql = "SELECT * FROM auto_reply_logs WHERE phoneNumber = ? AND dayKey = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phone);
    _argIndex = 2;
    _statement.bindString(_argIndex, dayKey);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AutoReplyLogEntity>() {
      @Override
      @Nullable
      public AutoReplyLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfIsAutoReply = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoReply");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfError = CursorUtil.getColumnIndexOrThrow(_cursor, "error");
          final int _cursorIndexOfSentTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "sentTimestamp");
          final int _cursorIndexOfDeliveredTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredTimestamp");
          final AutoReplyLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactId;
            _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final boolean _tmpIsAutoReply;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoReply);
            _tmpIsAutoReply = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpError;
            if (_cursor.isNull(_cursorIndexOfError)) {
              _tmpError = null;
            } else {
              _tmpError = _cursor.getString(_cursorIndexOfError);
            }
            final Long _tmpSentTimestamp;
            if (_cursor.isNull(_cursorIndexOfSentTimestamp)) {
              _tmpSentTimestamp = null;
            } else {
              _tmpSentTimestamp = _cursor.getLong(_cursorIndexOfSentTimestamp);
            }
            final Long _tmpDeliveredTimestamp;
            if (_cursor.isNull(_cursorIndexOfDeliveredTimestamp)) {
              _tmpDeliveredTimestamp = null;
            } else {
              _tmpDeliveredTimestamp = _cursor.getLong(_cursorIndexOfDeliveredTimestamp);
            }
            _result = new AutoReplyLogEntity(_tmpId,_tmpContactId,_tmpContactName,_tmpPhoneNumber,_tmpMessageText,_tmpTimestamp,_tmpDayKey,_tmpCallType,_tmpIsAutoReply,_tmpStatus,_tmpAttempts,_tmpError,_tmpSentTimestamp,_tmpDeliveredTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLogCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM auto_reply_logs";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLogCountByContact(final String contactId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM auto_reply_logs WHERE contactId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, contactId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getLogById(final long id,
      final Continuation<? super AutoReplyLogEntity> $completion) {
    final String _sql = "SELECT * FROM auto_reply_logs WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AutoReplyLogEntity>() {
      @Override
      @Nullable
      public AutoReplyLogEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfMessageText = CursorUtil.getColumnIndexOrThrow(_cursor, "messageText");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDayKey = CursorUtil.getColumnIndexOrThrow(_cursor, "dayKey");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfIsAutoReply = CursorUtil.getColumnIndexOrThrow(_cursor, "isAutoReply");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfAttempts = CursorUtil.getColumnIndexOrThrow(_cursor, "attempts");
          final int _cursorIndexOfError = CursorUtil.getColumnIndexOrThrow(_cursor, "error");
          final int _cursorIndexOfSentTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "sentTimestamp");
          final int _cursorIndexOfDeliveredTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "deliveredTimestamp");
          final AutoReplyLogEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContactId;
            _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            final String _tmpContactName;
            _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpMessageText;
            _tmpMessageText = _cursor.getString(_cursorIndexOfMessageText);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDayKey;
            _tmpDayKey = _cursor.getString(_cursorIndexOfDayKey);
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final boolean _tmpIsAutoReply;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAutoReply);
            _tmpIsAutoReply = _tmp != 0;
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final int _tmpAttempts;
            _tmpAttempts = _cursor.getInt(_cursorIndexOfAttempts);
            final String _tmpError;
            if (_cursor.isNull(_cursorIndexOfError)) {
              _tmpError = null;
            } else {
              _tmpError = _cursor.getString(_cursorIndexOfError);
            }
            final Long _tmpSentTimestamp;
            if (_cursor.isNull(_cursorIndexOfSentTimestamp)) {
              _tmpSentTimestamp = null;
            } else {
              _tmpSentTimestamp = _cursor.getLong(_cursorIndexOfSentTimestamp);
            }
            final Long _tmpDeliveredTimestamp;
            if (_cursor.isNull(_cursorIndexOfDeliveredTimestamp)) {
              _tmpDeliveredTimestamp = null;
            } else {
              _tmpDeliveredTimestamp = _cursor.getLong(_cursorIndexOfDeliveredTimestamp);
            }
            _result = new AutoReplyLogEntity(_tmpId,_tmpContactId,_tmpContactName,_tmpPhoneNumber,_tmpMessageText,_tmpTimestamp,_tmpDayKey,_tmpCallType,_tmpIsAutoReply,_tmpStatus,_tmpAttempts,_tmpError,_tmpSentTimestamp,_tmpDeliveredTimestamp);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
