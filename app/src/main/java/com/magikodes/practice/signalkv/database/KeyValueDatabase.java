package com.magikodes.practice.signalkv.database;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.magikodes.practice.signalkv.crypto.DatabaseSecret;
import com.magikodes.practice.signalkv.crypto.DatabaseSecretProvider;
import com.magikodes.practice.signalkv.keyvalue.KeyValueDataSet;
import com.magikodes.practice.signalkv.keyvalue.KeyValuePersistentStorage;
import com.magikodes.practice.signalkv.util.concurrent.AppExecutors;

import net.zetetic.database.sqlcipher.SQLiteDatabase;
import net.zetetic.database.sqlcipher.SQLiteOpenHelper;

import java.security.Key;
import java.util.Collection;
import java.util.Map;

public class KeyValueDatabase extends SQLiteOpenHelper implements SignalDatabaseOpenHelper, KeyValuePersistentStorage {

    private static final String TAG = "KeyValueDatabase";

    public static final int     DATABASE_VERSION    = 1;
    public static final String  DATABASE_NAME       = "signal-key-value.db";

    private static final String TABLE_NAME      = "key_value";
    private static final String ID              = "_id";
    private static final String KEY             = "key";
    private static final String VALUE           = "value";
    private static final String TYPE            = "type";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + ID      + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                                                                    KEY     + " TEXT UNIQUE, " +
                                                                                    VALUE   + " TEXT, " +
                                                                                    TYPE    + " INTEGER)";

    private static volatile KeyValueDatabase instance;

    private final Application application;

    public static @NonNull KeyValueDatabase getInstance(@NonNull Application context) {
        if (instance == null) {
            synchronized (KeyValueDatabase.class) {
                if (instance == null) {
                    SqlCipherLibraryLoader.load();
                    instance = new KeyValueDatabase(context, DatabaseSecretProvider.getOrCreateDatabaseSecret(context));
                }
            }
        }
        return instance;
    }

    public static boolean exists(Context context) { return context.getDatabasePath(DATABASE_NAME).exists(); }

    private KeyValueDatabase(@NonNull Application application, @NonNull DatabaseSecret databaseSecret) {
        super(application, DATABASE_NAME, databaseSecret.asString(), null, DATABASE_VERSION, 0, null, new SqlCipherDatabaseHook(), false);

        this.application = application;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "onCreate()");

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "onUpgrade(" + oldVersion + ", " + newVersion + ")");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.i(TAG, "onOpen()");

        db.enableWriteAheadLogging();
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public SQLiteDatabase getSqlCipherDatabase() { return getWritableDatabase(); }

    @Override
    public String getDatabaseName() { return DATABASE_NAME; }

    @Override
    public void writeDataSet(@NonNull KeyValueDataSet dataSet, @NonNull Collection<String> removes) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            for (Map.Entry<String, Object> entry : dataSet.getValues().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                Class type = dataSet.getType(key);

                ContentValues contentValues = new ContentValues(3);
                contentValues.put(KEY, key);

                if (type == byte[].class) {
                    contentValues.put(VALUE, (byte[]) value);
                    contentValues.put(TYPE, Type.BLOB.getId());
                } else if (type == Boolean.class) {
                    contentValues.put(VALUE, (boolean) value);
                    contentValues.put(TYPE, Type.BOOLEAN.getId());
                } else if (type == Float.class) {
                    contentValues.put(VALUE, (float) value);
                    contentValues.put(TYPE, Type.FLOAT.getId());
                } else if (type == Integer.class) {
                    contentValues.put(VALUE, (int) value);
                    contentValues.put(TYPE, Type.INTEGER.getId());
                } else if (type == Long.class) {
                    contentValues.put(VALUE, (long) value);
                    contentValues.put(TYPE, Type.LONG.getId());
                } else if (type == String.class) {
                    contentValues.put(VALUE, (String) value);
                    contentValues.put(TYPE, Type.STRING.getId());
                } else {
                    throw new AssertionError("Unknown type: " + type);
                }

                db.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
            }

            String deleteQuery = KEY + " = ?";
            for (String remove : removes) {
                db.delete(TABLE_NAME, deleteQuery, new String[]{remove});
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @NonNull
    @Override
    public KeyValueDataSet getDataSet() {
        KeyValueDataSet dataSet = new KeyValueDataSet();

        try (Cursor cursor = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, null)) {
            while (cursor != null && cursor.moveToNext()) {
                Type    type    = Type.fromId(cursor.getInt(cursor.getColumnIndexOrThrow(TYPE)));
                String  key     = cursor.getString(cursor.getColumnIndexOrThrow(KEY));

                switch (type) {
                    case BLOB: dataSet.putBlob(key, cursor.getBlob(cursor.getColumnIndexOrThrow(VALUE)));
                        break;
                    case BOOLEAN: dataSet.putBoolean(key, cursor.getInt(cursor.getColumnIndexOrThrow(VALUE)) == 1);
                        break;
                    case FLOAT:
                        dataSet.putFloat(key, cursor.getFloat(cursor.getColumnIndexOrThrow(VALUE)));
                        break;
                    case INTEGER:
                        dataSet.putInteger(key, cursor.getInt(cursor.getColumnIndexOrThrow(VALUE)));
                        break;
                    case LONG:
                        dataSet.putLong(key, cursor.getLong(cursor.getColumnIndexOrThrow(VALUE)));
                        break;
                    case STRING:
                        dataSet.putString(key, cursor.getString(cursor.getColumnIndexOrThrow(VALUE)));
                        break;
                }
            }
        }
        return dataSet;
    }

    private enum Type {
        BLOB(0), BOOLEAN(1), FLOAT(2), INTEGER(3), LONG(4), STRING(5);

        final int id;

        Type(int id) { this.id = id; }

        public int getId() { return id; }

        public static Type fromId(int id) { return values()[id]; }
    }
}