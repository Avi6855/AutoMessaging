package com.avinashpatil.app.automessage.data.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.avinashpatil.app.automessage.data.dao.AutoReplyLogDao;
import com.avinashpatil.app.automessage.data.dao.AutoReplyLogDao_Impl;
import com.avinashpatil.app.automessage.data.dao.BlacklistDao;
import com.avinashpatil.app.automessage.data.dao.BlacklistDao_Impl;
import com.avinashpatil.app.automessage.data.dao.ContactDao;
import com.avinashpatil.app.automessage.data.dao.ContactDao_Impl;
import com.avinashpatil.app.automessage.data.dao.CustomMessageDao;
import com.avinashpatil.app.automessage.data.dao.CustomMessageDao_Impl;
import com.avinashpatil.app.automessage.data.dao.DiscrepancyLogDao;
import com.avinashpatil.app.automessage.data.dao.DiscrepancyLogDao_Impl;
import com.avinashpatil.app.automessage.data.dao.GroupDao;
import com.avinashpatil.app.automessage.data.dao.GroupDao_Impl;
import com.avinashpatil.app.automessage.data.dao.LastSeenCallDao;
import com.avinashpatil.app.automessage.data.dao.LastSeenCallDao_Impl;
import com.avinashpatil.app.automessage.data.dao.PriorityDao;
import com.avinashpatil.app.automessage.data.dao.PriorityDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AutoMessageDatabase_Impl extends AutoMessageDatabase {
  private volatile ContactDao _contactDao;

  private volatile GroupDao _groupDao;

  private volatile CustomMessageDao _customMessageDao;

  private volatile AutoReplyLogDao _autoReplyLogDao;

  private volatile PriorityDao _priorityDao;

  private volatile BlacklistDao _blacklistDao;

  private volatile LastSeenCallDao _lastSeenCallDao;

  private volatile DiscrepancyLogDao _discrepancyLogDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `contacts` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `photoUri` TEXT, `groupId` INTEGER, `isPriority` INTEGER NOT NULL, `isBlacklisted` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `groups` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `description` TEXT, `defaultMessageId` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `custom_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `body` TEXT NOT NULL, `groupType` TEXT NOT NULL, `isDefault` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `auto_reply_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `contactId` TEXT NOT NULL, `contactName` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `messageText` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `dayKey` TEXT NOT NULL, `callType` TEXT NOT NULL, `isAutoReply` INTEGER NOT NULL, `status` TEXT NOT NULL, `attempts` INTEGER NOT NULL, `error` TEXT, `sentTimestamp` INTEGER, `deliveredTimestamp` INTEGER)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_auto_reply_logs_phoneNumber_dayKey` ON `auto_reply_logs` (`phoneNumber`, `dayKey`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `priority_contacts` (`contactId` TEXT NOT NULL, `contactName` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`contactId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `blacklist` (`contactId` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `reason` TEXT, `addedAt` INTEGER NOT NULL, PRIMARY KEY(`contactId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `last_seen_calls` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `callId` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, `contactId` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `discrepancy_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `callId` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `contactId` TEXT, `contactName` TEXT, `callType` TEXT NOT NULL, `callTimestamp` INTEGER NOT NULL, `durationSec` INTEGER NOT NULL, `status` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `resolvedAt` INTEGER, `notes` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5dba876a4af9424cfacbefa27e0e8066')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `contacts`");
        db.execSQL("DROP TABLE IF EXISTS `groups`");
        db.execSQL("DROP TABLE IF EXISTS `custom_messages`");
        db.execSQL("DROP TABLE IF EXISTS `auto_reply_logs`");
        db.execSQL("DROP TABLE IF EXISTS `priority_contacts`");
        db.execSQL("DROP TABLE IF EXISTS `blacklist`");
        db.execSQL("DROP TABLE IF EXISTS `last_seen_calls`");
        db.execSQL("DROP TABLE IF EXISTS `discrepancy_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsContacts = new HashMap<String, TableInfo.Column>(9);
        _columnsContacts.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("photoUri", new TableInfo.Column("photoUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("groupId", new TableInfo.Column("groupId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("isPriority", new TableInfo.Column("isPriority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("isBlacklisted", new TableInfo.Column("isBlacklisted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsContacts.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoContacts = new TableInfo("contacts", _columnsContacts, _foreignKeysContacts, _indicesContacts);
        final TableInfo _existingContacts = TableInfo.read(db, "contacts");
        if (!_infoContacts.equals(_existingContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "contacts(com.avinashpatil.app.automessage.data.entity.ContactEntity).\n"
                  + " Expected:\n" + _infoContacts + "\n"
                  + " Found:\n" + _existingContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsGroups = new HashMap<String, TableInfo.Column>(6);
        _columnsGroups.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroups.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroups.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroups.put("defaultMessageId", new TableInfo.Column("defaultMessageId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroups.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsGroups.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGroups = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGroups = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGroups = new TableInfo("groups", _columnsGroups, _foreignKeysGroups, _indicesGroups);
        final TableInfo _existingGroups = TableInfo.read(db, "groups");
        if (!_infoGroups.equals(_existingGroups)) {
          return new RoomOpenHelper.ValidationResult(false, "groups(com.avinashpatil.app.automessage.data.entity.GroupEntity).\n"
                  + " Expected:\n" + _infoGroups + "\n"
                  + " Found:\n" + _existingGroups);
        }
        final HashMap<String, TableInfo.Column> _columnsCustomMessages = new HashMap<String, TableInfo.Column>(7);
        _columnsCustomMessages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomMessages.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomMessages.put("body", new TableInfo.Column("body", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomMessages.put("groupType", new TableInfo.Column("groupType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomMessages.put("isDefault", new TableInfo.Column("isDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomMessages.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCustomMessages.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCustomMessages = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCustomMessages = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCustomMessages = new TableInfo("custom_messages", _columnsCustomMessages, _foreignKeysCustomMessages, _indicesCustomMessages);
        final TableInfo _existingCustomMessages = TableInfo.read(db, "custom_messages");
        if (!_infoCustomMessages.equals(_existingCustomMessages)) {
          return new RoomOpenHelper.ValidationResult(false, "custom_messages(com.avinashpatil.app.automessage.data.entity.CustomMessageEntity).\n"
                  + " Expected:\n" + _infoCustomMessages + "\n"
                  + " Found:\n" + _existingCustomMessages);
        }
        final HashMap<String, TableInfo.Column> _columnsAutoReplyLogs = new HashMap<String, TableInfo.Column>(14);
        _columnsAutoReplyLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("contactId", new TableInfo.Column("contactId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("contactName", new TableInfo.Column("contactName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("messageText", new TableInfo.Column("messageText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("dayKey", new TableInfo.Column("dayKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("callType", new TableInfo.Column("callType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("isAutoReply", new TableInfo.Column("isAutoReply", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("attempts", new TableInfo.Column("attempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("error", new TableInfo.Column("error", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("sentTimestamp", new TableInfo.Column("sentTimestamp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAutoReplyLogs.put("deliveredTimestamp", new TableInfo.Column("deliveredTimestamp", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAutoReplyLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAutoReplyLogs = new HashSet<TableInfo.Index>(1);
        _indicesAutoReplyLogs.add(new TableInfo.Index("index_auto_reply_logs_phoneNumber_dayKey", true, Arrays.asList("phoneNumber", "dayKey"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoAutoReplyLogs = new TableInfo("auto_reply_logs", _columnsAutoReplyLogs, _foreignKeysAutoReplyLogs, _indicesAutoReplyLogs);
        final TableInfo _existingAutoReplyLogs = TableInfo.read(db, "auto_reply_logs");
        if (!_infoAutoReplyLogs.equals(_existingAutoReplyLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "auto_reply_logs(com.avinashpatil.app.automessage.data.entity.AutoReplyLogEntity).\n"
                  + " Expected:\n" + _infoAutoReplyLogs + "\n"
                  + " Found:\n" + _existingAutoReplyLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsPriorityContacts = new HashMap<String, TableInfo.Column>(3);
        _columnsPriorityContacts.put("contactId", new TableInfo.Column("contactId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPriorityContacts.put("contactName", new TableInfo.Column("contactName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPriorityContacts.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPriorityContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPriorityContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPriorityContacts = new TableInfo("priority_contacts", _columnsPriorityContacts, _foreignKeysPriorityContacts, _indicesPriorityContacts);
        final TableInfo _existingPriorityContacts = TableInfo.read(db, "priority_contacts");
        if (!_infoPriorityContacts.equals(_existingPriorityContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "priority_contacts(com.avinashpatil.app.automessage.data.entity.PriorityEntity).\n"
                  + " Expected:\n" + _infoPriorityContacts + "\n"
                  + " Found:\n" + _existingPriorityContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsBlacklist = new HashMap<String, TableInfo.Column>(4);
        _columnsBlacklist.put("contactId", new TableInfo.Column("contactId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlacklist.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlacklist.put("reason", new TableInfo.Column("reason", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBlacklist.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBlacklist = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBlacklist = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBlacklist = new TableInfo("blacklist", _columnsBlacklist, _foreignKeysBlacklist, _indicesBlacklist);
        final TableInfo _existingBlacklist = TableInfo.read(db, "blacklist");
        if (!_infoBlacklist.equals(_existingBlacklist)) {
          return new RoomOpenHelper.ValidationResult(false, "blacklist(com.avinashpatil.app.automessage.data.entity.BlacklistEntity).\n"
                  + " Expected:\n" + _infoBlacklist + "\n"
                  + " Found:\n" + _existingBlacklist);
        }
        final HashMap<String, TableInfo.Column> _columnsLastSeenCalls = new HashMap<String, TableInfo.Column>(4);
        _columnsLastSeenCalls.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLastSeenCalls.put("callId", new TableInfo.Column("callId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLastSeenCalls.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLastSeenCalls.put("contactId", new TableInfo.Column("contactId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLastSeenCalls = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLastSeenCalls = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLastSeenCalls = new TableInfo("last_seen_calls", _columnsLastSeenCalls, _foreignKeysLastSeenCalls, _indicesLastSeenCalls);
        final TableInfo _existingLastSeenCalls = TableInfo.read(db, "last_seen_calls");
        if (!_infoLastSeenCalls.equals(_existingLastSeenCalls)) {
          return new RoomOpenHelper.ValidationResult(false, "last_seen_calls(com.avinashpatil.app.automessage.data.entity.LastSeenCallEntity).\n"
                  + " Expected:\n" + _infoLastSeenCalls + "\n"
                  + " Found:\n" + _existingLastSeenCalls);
        }
        final HashMap<String, TableInfo.Column> _columnsDiscrepancyLogs = new HashMap<String, TableInfo.Column>(12);
        _columnsDiscrepancyLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("callId", new TableInfo.Column("callId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("contactId", new TableInfo.Column("contactId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("contactName", new TableInfo.Column("contactName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("callType", new TableInfo.Column("callType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("callTimestamp", new TableInfo.Column("callTimestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("durationSec", new TableInfo.Column("durationSec", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("resolvedAt", new TableInfo.Column("resolvedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiscrepancyLogs.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDiscrepancyLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDiscrepancyLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDiscrepancyLogs = new TableInfo("discrepancy_logs", _columnsDiscrepancyLogs, _foreignKeysDiscrepancyLogs, _indicesDiscrepancyLogs);
        final TableInfo _existingDiscrepancyLogs = TableInfo.read(db, "discrepancy_logs");
        if (!_infoDiscrepancyLogs.equals(_existingDiscrepancyLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "discrepancy_logs(com.avinashpatil.app.automessage.data.entity.DiscrepancyLogEntity).\n"
                  + " Expected:\n" + _infoDiscrepancyLogs + "\n"
                  + " Found:\n" + _existingDiscrepancyLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "5dba876a4af9424cfacbefa27e0e8066", "f1972063dfdc77d6c2be5210ae921c65");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "contacts","groups","custom_messages","auto_reply_logs","priority_contacts","blacklist","last_seen_calls","discrepancy_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `contacts`");
      _db.execSQL("DELETE FROM `groups`");
      _db.execSQL("DELETE FROM `custom_messages`");
      _db.execSQL("DELETE FROM `auto_reply_logs`");
      _db.execSQL("DELETE FROM `priority_contacts`");
      _db.execSQL("DELETE FROM `blacklist`");
      _db.execSQL("DELETE FROM `last_seen_calls`");
      _db.execSQL("DELETE FROM `discrepancy_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(ContactDao.class, ContactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(GroupDao.class, GroupDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(CustomMessageDao.class, CustomMessageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AutoReplyLogDao.class, AutoReplyLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PriorityDao.class, PriorityDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BlacklistDao.class, BlacklistDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LastSeenCallDao.class, LastSeenCallDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DiscrepancyLogDao.class, DiscrepancyLogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public ContactDao contactDao() {
    if (_contactDao != null) {
      return _contactDao;
    } else {
      synchronized(this) {
        if(_contactDao == null) {
          _contactDao = new ContactDao_Impl(this);
        }
        return _contactDao;
      }
    }
  }

  @Override
  public GroupDao groupDao() {
    if (_groupDao != null) {
      return _groupDao;
    } else {
      synchronized(this) {
        if(_groupDao == null) {
          _groupDao = new GroupDao_Impl(this);
        }
        return _groupDao;
      }
    }
  }

  @Override
  public CustomMessageDao customMessageDao() {
    if (_customMessageDao != null) {
      return _customMessageDao;
    } else {
      synchronized(this) {
        if(_customMessageDao == null) {
          _customMessageDao = new CustomMessageDao_Impl(this);
        }
        return _customMessageDao;
      }
    }
  }

  @Override
  public AutoReplyLogDao autoReplyLogDao() {
    if (_autoReplyLogDao != null) {
      return _autoReplyLogDao;
    } else {
      synchronized(this) {
        if(_autoReplyLogDao == null) {
          _autoReplyLogDao = new AutoReplyLogDao_Impl(this);
        }
        return _autoReplyLogDao;
      }
    }
  }

  @Override
  public PriorityDao priorityDao() {
    if (_priorityDao != null) {
      return _priorityDao;
    } else {
      synchronized(this) {
        if(_priorityDao == null) {
          _priorityDao = new PriorityDao_Impl(this);
        }
        return _priorityDao;
      }
    }
  }

  @Override
  public BlacklistDao blacklistDao() {
    if (_blacklistDao != null) {
      return _blacklistDao;
    } else {
      synchronized(this) {
        if(_blacklistDao == null) {
          _blacklistDao = new BlacklistDao_Impl(this);
        }
        return _blacklistDao;
      }
    }
  }

  @Override
  public LastSeenCallDao lastSeenCallDao() {
    if (_lastSeenCallDao != null) {
      return _lastSeenCallDao;
    } else {
      synchronized(this) {
        if(_lastSeenCallDao == null) {
          _lastSeenCallDao = new LastSeenCallDao_Impl(this);
        }
        return _lastSeenCallDao;
      }
    }
  }

  @Override
  public DiscrepancyLogDao discrepancyLogDao() {
    if (_discrepancyLogDao != null) {
      return _discrepancyLogDao;
    } else {
      synchronized(this) {
        if(_discrepancyLogDao == null) {
          _discrepancyLogDao = new DiscrepancyLogDao_Impl(this);
        }
        return _discrepancyLogDao;
      }
    }
  }
}
