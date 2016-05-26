package data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "dbCustomCollection";
    private static final int dbVersion = 1;
    private Context context = null;

    public DatabaseHelper(Context context) {
        super(context, NAME, null, dbVersion);
        this.context = context;
    }

    //create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        createCollectionTable(db);
        createCollectionItemTable(db);
    }

    //drop table if exists (tablename)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CollectionItemTable.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + CollectionTable.TABLENAME);
        onCreate(db);
    }

    public void createCollectionTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + CollectionTable.TABLENAME + " (" + CollectionTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CollectionTable.NAME + " TEXT NOT NULL, " + CollectionTable.DESCRIPTION + " TEXT, " +
                CollectionTable.BASEPHOTO + " TEXT);";
        db.execSQL(sql);
    }

    public void createCollectionItemTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + CollectionItemTable.TABLENAME + " (" + CollectionItemTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CollectionItemTable.NAME + " TEXT NOT NULL, " + CollectionItemTable.DESCRIPTION + " TEXT, " +
                CollectionItemTable.CUSTOMINDEX + " TEXT, " + CollectionItemTable.BASEPHOTO + " TEXT, " + CollectionItemTable.VALUE + " REAL, " + CollectionItemTable.FKCOLLECTIONID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + CollectionItemTable.FKCOLLECTIONID + ") REFERENCES " + CollectionTable.TABLENAME + " (" + CollectionTable.ID + "));";
    }

    public ArrayList<Collection> getCollections() {
        return new CollectionTable().getCollections();
    }

    private class CollectionTable {
        public static final String TABLENAME = "tblCollectionTable";
        public static final String ID = "Id";
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String BASEPHOTO = "Base64Photo";

        public long insertCollection(Collection collection) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            if (collection.getId() != -1) {
                cv.put(ID, collection.getId());
            }
            cv.put(NAME, collection.getTitle());
            cv.put(DESCRIPTION, collection.getDescription());
            cv.put(BASEPHOTO, collection.getBase64Feature());
            return db.insertWithOnConflict(TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }

        public Collection processSingle(Cursor cursor) {
            Collection collection = new Collection();
            if (cursor != null) {
                collection.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                collection.setTitle(cursor.getString(cursor.getColumnIndex(NAME)));
                collection.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                collection.setBase64Feature(cursor.getString(cursor.getColumnIndex(BASEPHOTO)));
                collection.setItems(new CollectionItemTable().getItemsByCollectionId(collection.getId()));
            }
            return collection;
        }

        public ArrayList<Collection> processMultiple(Cursor cursor) {
            ArrayList<Collection> collections = new ArrayList<>();
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    collections.add(processSingle(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return collections;
        }

        public ArrayList<Collection> getCollections() {
            SQLiteDatabase db = getReadableDatabase();
            return processMultiple(db.query(TABLENAME, null, null, null, null, null, null));
        }

        public void deleteCollectionByCollectionId(int collectionId) {
            new CollectionItemTable().deleteItemsByCollectionId(collectionId);
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, ID + " = ?", new String[]{Integer.toString(collectionId)});
        }

    }

    private class CollectionItemTable {
        public static final String TABLENAME = "tblCollectionItem";
        public static final String ID = "Id";
        public static final String NAME = "Name";
        public static final String DESCRIPTION = "Description";
        public static final String VALUE = "Value";
        public static final String BASEPHOTO = "Base64Photo";
        public static final String CUSTOMINDEX = "CustomIndexReminder";
        public static final String FKCOLLECTIONID = "Fk_CollectionId";

        public long insertCollectionItem(CollectionItem item) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            if (item.getId() != -1) {
                cv.put(ID, item.getId());
            }
            cv.put(NAME, item.getName());
            cv.put(DESCRIPTION, item.getDescription());
            cv.put(VALUE, item.getValue());
            cv.put(BASEPHOTO, item.getBase64Photo());
            cv.put(CUSTOMINDEX, item.getCustomIndexReminder());
            cv.put(FKCOLLECTIONID, item.getFkCollectionId());
            return db.insertWithOnConflict(TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }

        public CollectionItem processSingleItem(Cursor cursor) {
            CollectionItem item = new CollectionItem();
            if (cursor != null) {
                item.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                item.setFkCollectionId(cursor.getInt(cursor.getColumnIndex(FKCOLLECTIONID)));
                item.setBase64Photo(cursor.getString(cursor.getColumnIndex(BASEPHOTO)));
                item.setCustomIndexReminder(cursor.getString(cursor.getColumnIndex(CUSTOMINDEX)));
                item.setValue(cursor.getDouble(cursor.getColumnIndex(VALUE)));
            }
            return item;
        }

        public ArrayList<CollectionItem> processMultiple(Cursor cursor) {
            ArrayList<CollectionItem> items = new ArrayList<>();
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    items.add(processSingleItem(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return items;
        }

        public ArrayList<CollectionItem> getItemsByCollectionId(int collectionId) {
            SQLiteDatabase db = getReadableDatabase();
            return processMultiple(db.query(TABLENAME, null, FKCOLLECTIONID + " = ?", new String[]{Integer.toString(collectionId)}, null, null, null));
        }

        public void deleteItemsByCollectionId(int collectionId) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, FKCOLLECTIONID + " = ?", new String[]{Integer.toString(collectionId)});
        }

        public void deleteItemByCollectionItemId(int collectionItemId) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, ID + " = ?", new String[]{Integer.toString(collectionItemId)});
        }
    }
}