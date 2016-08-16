package data;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Environment;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Created by Jeremy on 26/05/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String NAME = "dbCustomCollection";
    private static final int dbVersion = 7;
    private Context context;
    public DatabaseHelper(Context context) {
        super(context, NAME, null, dbVersion);
        this.context = context;
    }

    //create tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        createCollectionTable(db);
        createMaterialTable(db);
        createCollectionItemTable(db);
        createCollectionItemPhotoTable(db);
    }

    //drop table if exists (tablename)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PhotoTable.TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS " + MaterialTable.TABLENAME);
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
                CollectionItemTable.CUSTOMINDEX + " TEXT, " + CollectionItemTable.VALUE + " REAL, " + CollectionItemTable.FKCOLLECTIONID + " INTEGER NOT NULL, " + CollectionItemTable.FKMATERIALID + " INTEGER, " +
                "FOREIGN KEY (" + CollectionItemTable.FKCOLLECTIONID + ") REFERENCES " + CollectionTable.TABLENAME + " (" + CollectionTable.ID + ")," +
                "FOREIGN KEY (" + CollectionItemTable.FKMATERIALID + ") REFERENCES " + MaterialTable.TABLENAME + " (" + MaterialTable.ID + "));";
        db.execSQL(sql);
    }

    public void createMaterialTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + MaterialTable.TABLENAME + " (" + MaterialTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MaterialTable.NAME + " TEXT NOT NULL);";
        db.execSQL(sql);
        new MaterialTable().populateDefaultMaterial(db);
    }

    public void createCollectionItemPhotoTable(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + PhotoTable.TABLENAME + " (" + PhotoTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PhotoTable.PHOTOURI + " TEXT, " + PhotoTable.FKCOLLECTIONITEMID + " INTEGER NOT NULL, " +
                "FOREIGN KEY (" + PhotoTable.FKCOLLECTIONITEMID + ") REFERENCES " + CollectionItemTable.TABLENAME + " (" + CollectionItemTable.ID + "));";
        db.execSQL(sql);
    }

    public ArrayList<Collection> getCollections() {
        return new CollectionTable().getCollections();
    }

    public long insertCollection(Collection collection) {
        return new CollectionTable().insertCollection(collection);
    }

    public long insertCollectionItem(CollectionItem item) {
        return new CollectionItemTable().insertCollectionItem(item);
    }

    public ArrayList<CollectionItem> getCollectionItems(int collectionId) {
        return new CollectionItemTable().getItemsByCollectionId(collectionId);
    }

    public void deletePhoto(int photoId) {
        new PhotoTable().deleteCollectionItemPhotosByPhotoId(photoId);
    }

    public void deleteCollectionItem(CollectionItem item) {
        new CollectionItemTable().deleteItemByCollectionItemId(item.getId());
    }

    public void deleteCollection(Collection collection) {
        new CollectionTable().deleteCollectionByCollectionId(collection.getId());
    }

    public void deleteAllCollections() {
        new CollectionTable().deleteAll();
    }

    public ArrayList<CollectionItem> getCollectionItemsWithNoAssignedValue() {
        return new CollectionItemTable().getItemsWithMissingValues();
    }

    public void writePDF(int collectionId) throws FileNotFoundException, DocumentException {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle("Writing PDF");
        dialog.setMessage("Please Wait While I Write Your PDF...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        Document document = new Document();
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File pdf = new File(exportDir, "collections_" + sdf.format(Calendar.getInstance().getTime()) + ".pdf");
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdf));
        writer.setStrictImageSequence(true);
        document.open();
        document.setMargins(50, 45, 50, 60);
        Collection collection = new CollectionTable().getCollectionByCollectionId(collectionId);
        document.addTitle(collection.getTitle());
        document.addSubject("Listing items from " + collection.getTitle());
        document.addKeywords(collection.getTitle());

        Paragraph titlePage = new Paragraph();
        titlePage.setAlignment(Element.ALIGN_CENTER);
        Font f = new Font(Font.FontFamily.TIMES_ROMAN, 60.0f, Font.BOLD, BaseColor.BLACK);
        Font f2 = new Font(Font.FontFamily.TIMES_ROMAN, 16.0f, Font.NORMAL, BaseColor.BLACK);
        titlePage.setFont(f);
        LineSeparator sep = new LineSeparator();
        titlePage.add(collection.getTitle());
        addEmptyLine(titlePage, 1);
        titlePage.add(new Chunk(sep));
        addEmptyLine(titlePage, 2);
        document.add(titlePage);
        for (CollectionItem item : collection.getItems()) {
            item.populateScaledBitmapsFromUri(context);
        }
        for (CollectionItem item : collection.getItems()) {
            Paragraph itemParagraph = new Paragraph();
            itemParagraph.setFont(f2);
            if (!item.getPhotos().isEmpty()) {
                itemParagraph.setAlignment(Element.ALIGN_CENTER);
                itemParagraph.add("Name: " + item.getName());
                itemParagraph.setAlignment(Element.ALIGN_LEFT);
                addEmptyLine(itemParagraph, 1);
                int count = 1;
                for (CollectionItemPhoto photo: item.getPhotos()) {
                    try {
                        itemParagraph.add("Photo " + count + ": ");
                        addEmptyLine(itemParagraph, 1);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.getPhotosAsBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
                        Image image = Image.getInstance(stream.toByteArray());
                        itemParagraph.add(image);
                    } catch (IOException ex) {
                        return;
                    }
                }
            }
            addEmptyLine(itemParagraph, 2);
            itemParagraph.add("Description: " + item.getDescription());
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add("Value: $" + item.getValue() + "");
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add("Index: " + item.getCustomIndexReminder());
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add("Material: " + item.getMaterial().getName());
            addEmptyLine(itemParagraph, 1);
            itemParagraph.add(new Chunk(sep));
            addEmptyLine(itemParagraph, 1);

            document.add(itemParagraph);
        }
        document.close();
        dialog.dismiss();
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void writeCSV(int collectionId) {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        File csv = new File(exportDir, "collections_" + sdf.format(Calendar.getInstance().getTime()) + ".csv");
        try {
            csv.createNewFile();
            CSVWriter writer = new CSVWriter(new FileWriter(csv));
            new CollectionTable().writeCSV(writer, collectionId);
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public long insertMaterial(String Name) {
        Material material = new Material();
        material.setName(Name);
        return new MaterialTable().insertMaterial(material);
    }

    public ArrayList<Material> getMaterials() {
        return new MaterialTable().getMaterials();
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

        public void deleteAll() {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, null, null);
        }

        public void writeCSV(CSVWriter writer, int collectionId) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLENAME, null, ID + " = ?", new String[]{Integer.toString(collectionId)}, null, null, null);
            writer.writeNext(cursor.getColumnNames());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String[] entry = {Integer.toString(cursor.getInt(cursor.getColumnIndex(ID))), cursor.getString(cursor.getColumnIndex(NAME)), cursor.getString(cursor.getColumnIndex(DESCRIPTION)), cursor.getString(cursor.getColumnIndex(BASEPHOTO))};
                writer.writeNext(entry);
                cursor.moveToNext();
            }
            cursor.close();
            new CollectionItemTable().writeCSV(writer, collectionId);

        }

        public Collection getCollectionByCollectionId(int collectionId) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLENAME, null, ID + " = ?", new String[]{Integer.toString(collectionId)}, null, null, null);
            cursor.moveToFirst();
            Collection c = processSingle(cursor);
            cursor.close();
            return c;
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
        public static final String FKMATERIALID = "Fk_MaterialID";

        public long insertCollectionItem(CollectionItem item) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            if (item.getId() != -1) {
                cv.put(ID, item.getId());
            }
            cv.put(NAME, item.getName());
            cv.put(DESCRIPTION, item.getDescription());
            cv.put(VALUE, item.getValue());
            cv.put(CUSTOMINDEX, item.getCustomIndexReminder());
            cv.put(FKCOLLECTIONID, item.getFkCollectionId());
            cv.put(FKMATERIALID, item.getFkMaterialId());
            long id =  db.insertWithOnConflict(TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
            for (CollectionItemPhoto photo : item.getPhotos()) {
                if (id != -1) {
                    photo.setFkCollectionItemId((int) id);
                    new PhotoTable().insertPhoto(photo);
                }
            }
            return id;
        }

        public CollectionItem processSingleItem(Cursor cursor) {
            CollectionItem item = new CollectionItem();
            if (cursor != null) {
                item.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                item.setName(cursor.getString(cursor.getColumnIndex(NAME)));
                item.setDescription(cursor.getString(cursor.getColumnIndex(DESCRIPTION)));
                item.setFkCollectionId(cursor.getInt(cursor.getColumnIndex(FKCOLLECTIONID)));
                item.setCustomIndexReminder(cursor.getString(cursor.getColumnIndex(CUSTOMINDEX)));
                item.setValue(cursor.getDouble(cursor.getColumnIndex(VALUE)));
                item.setFkMaterialId(cursor.getInt(cursor.getColumnIndex(FKMATERIALID)));
                item.setPhotos(new PhotoTable().getCollectionItemPhotosByCollectionItemId(item.getId()));
                if (item.getFkMaterialId() != -1) {
                    item.setMaterial(new MaterialTable().getMaterialByMaterialId(item.getFkMaterialId()));
                }
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

        public ArrayList<CollectionItem> getItemsWithMissingValues() {
            SQLiteDatabase db = getReadableDatabase();
            DecimalFormat df = new DecimalFormat("#.00");
            return processMultiple(db.query(TABLENAME, null, VALUE + " = ? OR " + VALUE + " = ? OR " + VALUE + " = ?", new String[]{"0.00", "0.0", ".00"}, null, null, null));
        }

        public ArrayList<CollectionItem> getItemsByCollectionId(int collectionId) {
            SQLiteDatabase db = getReadableDatabase();
            return processMultiple(db.query(TABLENAME, null, FKCOLLECTIONID + " = ?", new String[]{Integer.toString(collectionId)}, null, null, null));
        }

        public void deleteItemsByCollectionId(int collectionId) {
            ArrayList<CollectionItem> items = getItemsByCollectionId(collectionId);
            for (CollectionItem i : items) {
                new PhotoTable().deleteCollectionItemPhotosByCollectionItemId(i.getId());
            }
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, FKCOLLECTIONID + " = ?", new String[]{Integer.toString(collectionId)});
        }

        public void deleteItemByCollectionItemId(int collectionItemId) {
            SQLiteDatabase db = getWritableDatabase();
            new PhotoTable().deleteCollectionItemPhotosByCollectionItemId(collectionItemId);
            db.delete(TABLENAME, ID + " = ?", new String[]{Integer.toString(collectionItemId)});
        }

        public void writeCSV(CSVWriter writer, int collectionId) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLENAME, null, FKCOLLECTIONID + " = ?", new String[]{Integer.toString(collectionId)}, null, null, null);
            writer.writeNext(cursor.getColumnNames());
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String entry[] = {Integer.toString(cursor.getInt(cursor.getColumnIndex(ID))), cursor.getString(cursor.getColumnIndex(NAME)), cursor.getString(cursor.getColumnIndex(DESCRIPTION)),
                cursor.getString(cursor.getColumnIndex(CUSTOMINDEX)), Double.toString(cursor.getDouble(cursor.getColumnIndex(VALUE))), Integer.toString(cursor.getInt(cursor.getColumnIndex(FKCOLLECTIONID)))};
                writer.writeNext(entry);
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private class PhotoTable {
        public static final String TABLENAME = "tblPhotos";
        public static final String ID = "Id";
        public static final String FKCOLLECTIONITEMID = "Fk_CollectionItemId";
        public static final String PHOTOURI = "PhotoUri";

        public long insertPhoto(CollectionItemPhoto photo) {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues cv = new ContentValues();
            if (photo.getId() != -1) {
                cv.put(ID, photo.getId());
            }
            cv.put(FKCOLLECTIONITEMID, photo.getFkCollectionItemId());
            cv.put(PHOTOURI, photo.getPhotoUri());
            return db.insertWithOnConflict(TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }

        public CollectionItemPhoto processSingle(Cursor cursor) {
            CollectionItemPhoto photo = new CollectionItemPhoto();
            if (cursor != null) {
                photo.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                photo.setFkCollectionItemId(cursor.getInt(cursor.getColumnIndex(FKCOLLECTIONITEMID)));
                photo.setPhotoUri(cursor.getString(cursor.getColumnIndex(PHOTOURI)));
            }
            return photo;
        }

        public ArrayList<CollectionItemPhoto> processMultiple(Cursor cursor) {
            ArrayList<CollectionItemPhoto> photos = new ArrayList<>();
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    photos.add(processSingle(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return photos;
        }

        public ArrayList<CollectionItemPhoto> getCollectionItemPhotosByCollectionItemId(int collectionItemId) {
            SQLiteDatabase db = getReadableDatabase();
            return processMultiple(db.query(TABLENAME, null, FKCOLLECTIONITEMID + " = ?", new String[]{Integer.toString(collectionItemId)}, null, null, null));
        }

        public void deleteCollectionItemPhotosByCollectionItemId(int collectionItemId) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, FKCOLLECTIONITEMID + " = ?", new String[]{Integer.toString(collectionItemId)});
        }

        public void deleteCollectionItemPhotosByPhotoId(int photoId) {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, ID + " = ?", new String[]{Integer.toString(photoId)});
        }

    }
    private class MaterialTable {
        public static final String TABLENAME = "tblMaterial";
        public static final String ID = "Id";
        public static final String NAME = "Name";

        public long insertMaterial(Material material) {
            ContentValues cv = new ContentValues();
            SQLiteDatabase db = getWritableDatabase();
            if (material.getId() != -1) {
                cv.put(ID, material.getId());
            }
            cv.put(NAME, material.getName());
            return db.insertWithOnConflict(TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }

        public long insertMaterial(Material material, SQLiteDatabase db) {
            ContentValues cv = new ContentValues();
            if (material.getId() != -1) {
                cv.put(ID, material.getId());
            }
            cv.put(NAME, material.getName());
            return db.insertWithOnConflict(TABLENAME, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
        }

        public Material processSingle(Cursor cursor) {
            Material material = new Material();
            if (cursor != null) {
                material.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                material.setName(cursor.getString(cursor.getColumnIndex(NAME)));
            }
            return material;
        }

        public ArrayList<Material> processMultiple(Cursor cursor) {
            ArrayList<Material> materials = new ArrayList<>();
            if (cursor != null) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    materials.add(processSingle(cursor));
                    cursor.moveToNext();
                }
                cursor.close();
            }
            return materials;
        }

        public ArrayList<Material> getMaterials() {
            SQLiteDatabase db = getReadableDatabase();
            return processMultiple(db.query(TABLENAME, null, null, null, null, null, null));
        }

        public Material getMaterialByMaterialId(int id) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLENAME, null, ID + " = ?",  new String[]{Integer.toString(id)}, null, null, null);
            cursor.moveToFirst();
            Material mat =  processSingle(cursor);
            cursor.close();
            return mat;
        }

        public void deleteMaterials() {
            SQLiteDatabase db = getWritableDatabase();
            db.delete(TABLENAME, null, null);
        }

        public void populateDefaultMaterial(SQLiteDatabase db) {
            Material mat = new Material();
            mat.setName("N/A");
            insertMaterial(mat, db);
        }
    }
}