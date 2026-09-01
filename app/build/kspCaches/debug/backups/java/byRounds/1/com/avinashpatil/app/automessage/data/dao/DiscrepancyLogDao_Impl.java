package com.avinashpatil.app.automessage.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.avinashpatil.app.automessage.data.entity.DiscrepancyLogEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class DiscrepancyLogDao_Impl implements DiscrepancyLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DiscrepancyLogEntity> __insertionAdapterOfDiscrepancyLogEntity;

  private final EntityDeletionOrUpdateAdapter<DiscrepancyLogEntity> __deletionAdapterOfDiscrepancyLogEntity;

  private final EntityDeletionOrUpdateAdapter<DiscrepancyLogEntity> __updateAdapterOfDiscrepancyLogEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOld;

  private final SharedSQLiteStatement __preparedStmtOfMarkResolved;

  public DiscrepancyLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDiscrepancyLogEntity = new EntityInsertionAdapter<DiscrepancyLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `discrepancy_logs` (`id`,`callId`,`phoneNumber`,`contactId`,`contactName`,`callType`,`callTimestamp`,`durationSec`,`status`,`createdAt`,`resolvedAt`,`notes`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiscrepancyLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCallId());
        statement.bindString(3, entity.getPhoneNumber());
        if (entity.getContactId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getContactId());
        }
        if (entity.getContactName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getContactName());
        }
        statement.bindString(6, entity.getCallType());
        statement.bindLong(7, entity.getCallTimestamp());
        statement.bindLong(8, entity.getDurationSec());
        statement.bindString(9, entity.getStatus());
        statement.bindLong(10, entity.getCreatedAt());
        if (entity.getResolvedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getResolvedAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
      }
    };
    this.__deletionAdapterOfDiscrepancyLogEntity = new EntityDeletionOrUpdateAdapter<DiscrepancyLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `discrepancy_logs` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiscrepancyLogEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfDiscrepancyLogEntity = new EntityDeletionOrUpdateAdapter<DiscrepancyLogEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `discrepancy_logs` SET `id` = ?,`callId` = ?,`phoneNumber` = ?,`contactId` = ?,`contactName` = ?,`callType` = ?,`callTimestamp` = ?,`durationSec` = ?,`status` = ?,`createdAt` = ?,`resolvedAt` = ?,`notes` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DiscrepancyLogEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCallId());
        statement.bindString(3, entity.getPhoneNumber());
        if (entity.getContactId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getContactId());
        }
        if (entity.getContactName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getContactName());
        }
        statement.bindString(6, entity.getCallType());
        statement.bindLong(7, entity.getCallTimestamp());
        statement.bindLong(8, entity.getDurationSec());
        statement.bindString(9, entity.getStatus());
        statement.bindLong(10, entity.getCreatedAt());
        if (entity.getResolvedAt() == null) {
          statement.bindNull(11);
        } else {
          statement.bindLong(11, entity.getResolvedAt());
        }
        if (entity.getNotes() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getNotes());
        }
        statement.bindLong(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteOld = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM discrepancy_logs WHERE createdAt < ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkResolved = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE discrepancy_logs SET status = ?, resolvedAt = ?, notes = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DiscrepancyLogEntity log,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfDiscrepancyLogEntity.insertAndReturnId(log);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<DiscrepancyLogEntity> logs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDiscrepancyLogEntity.insert(logs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final DiscrepancyLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfDiscrepancyLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final DiscrepancyLogEntity log,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDiscrepancyLogEntity.handle(log);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOld(final long cutoff, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOld.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, cutoff);
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
          __preparedStmtOfDeleteOld.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markResolved(final long id, final String status, final long resolvedAt,
      final String notes, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkResolved.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, resolvedAt);
        _argIndex = 3;
        if (notes == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, notes);
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
          __preparedStmtOfMarkResolved.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DiscrepancyLogEntity>> getAll() {
    final String _sql = "SELECT * FROM discrepancy_logs ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"discrepancy_logs"}, new Callable<List<DiscrepancyLogEntity>>() {
      @Override
      @NonNull
      public List<DiscrepancyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCallId = CursorUtil.getColumnIndexOrThrow(_cursor, "callId");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfCallTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "callTimestamp");
          final int _cursorIndexOfDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSec");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DiscrepancyLogEntity> _result = new ArrayList<DiscrepancyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiscrepancyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCallId;
            _tmpCallId = _cursor.getString(_cursorIndexOfCallId);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpContactId;
            if (_cursor.isNull(_cursorIndexOfContactId)) {
              _tmpContactId = null;
            } else {
              _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            }
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final long _tmpCallTimestamp;
            _tmpCallTimestamp = _cursor.getLong(_cursorIndexOfCallTimestamp);
            final int _tmpDurationSec;
            _tmpDurationSec = _cursor.getInt(_cursorIndexOfDurationSec);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new DiscrepancyLogEntity(_tmpId,_tmpCallId,_tmpPhoneNumber,_tmpContactId,_tmpContactName,_tmpCallType,_tmpCallTimestamp,_tmpDurationSec,_tmpStatus,_tmpCreatedAt,_tmpResolvedAt,_tmpNotes);
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
  public Flow<List<DiscrepancyLogEntity>> getByStatus(final String status) {
    final String _sql = "SELECT * FROM discrepancy_logs WHERE status = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, status);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"discrepancy_logs"}, new Callable<List<DiscrepancyLogEntity>>() {
      @Override
      @NonNull
      public List<DiscrepancyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCallId = CursorUtil.getColumnIndexOrThrow(_cursor, "callId");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfCallTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "callTimestamp");
          final int _cursorIndexOfDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSec");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DiscrepancyLogEntity> _result = new ArrayList<DiscrepancyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiscrepancyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCallId;
            _tmpCallId = _cursor.getString(_cursorIndexOfCallId);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpContactId;
            if (_cursor.isNull(_cursorIndexOfContactId)) {
              _tmpContactId = null;
            } else {
              _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            }
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final long _tmpCallTimestamp;
            _tmpCallTimestamp = _cursor.getLong(_cursorIndexOfCallTimestamp);
            final int _tmpDurationSec;
            _tmpDurationSec = _cursor.getInt(_cursorIndexOfDurationSec);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new DiscrepancyLogEntity(_tmpId,_tmpCallId,_tmpPhoneNumber,_tmpContactId,_tmpContactName,_tmpCallType,_tmpCallTimestamp,_tmpDurationSec,_tmpStatus,_tmpCreatedAt,_tmpResolvedAt,_tmpNotes);
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
  public Flow<List<DiscrepancyLogEntity>> getByPhone(final String phone) {
    final String _sql = "SELECT * FROM discrepancy_logs WHERE phoneNumber = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phone);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"discrepancy_logs"}, new Callable<List<DiscrepancyLogEntity>>() {
      @Override
      @NonNull
      public List<DiscrepancyLogEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCallId = CursorUtil.getColumnIndexOrThrow(_cursor, "callId");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfContactId = CursorUtil.getColumnIndexOrThrow(_cursor, "contactId");
          final int _cursorIndexOfContactName = CursorUtil.getColumnIndexOrThrow(_cursor, "contactName");
          final int _cursorIndexOfCallType = CursorUtil.getColumnIndexOrThrow(_cursor, "callType");
          final int _cursorIndexOfCallTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "callTimestamp");
          final int _cursorIndexOfDurationSec = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSec");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfResolvedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "resolvedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DiscrepancyLogEntity> _result = new ArrayList<DiscrepancyLogEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DiscrepancyLogEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpCallId;
            _tmpCallId = _cursor.getString(_cursorIndexOfCallId);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpContactId;
            if (_cursor.isNull(_cursorIndexOfContactId)) {
              _tmpContactId = null;
            } else {
              _tmpContactId = _cursor.getString(_cursorIndexOfContactId);
            }
            final String _tmpContactName;
            if (_cursor.isNull(_cursorIndexOfContactName)) {
              _tmpContactName = null;
            } else {
              _tmpContactName = _cursor.getString(_cursorIndexOfContactName);
            }
            final String _tmpCallType;
            _tmpCallType = _cursor.getString(_cursorIndexOfCallType);
            final long _tmpCallTimestamp;
            _tmpCallTimestamp = _cursor.getLong(_cursorIndexOfCallTimestamp);
            final int _tmpDurationSec;
            _tmpDurationSec = _cursor.getInt(_cursorIndexOfDurationSec);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final Long _tmpResolvedAt;
            if (_cursor.isNull(_cursorIndexOfResolvedAt)) {
              _tmpResolvedAt = null;
            } else {
              _tmpResolvedAt = _cursor.getLong(_cursorIndexOfResolvedAt);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new DiscrepancyLogEntity(_tmpId,_tmpCallId,_tmpPhoneNumber,_tmpContactId,_tmpContactName,_tmpCallType,_tmpCallTimestamp,_tmpDurationSec,_tmpStatus,_tmpCreatedAt,_tmpResolvedAt,_tmpNotes);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
