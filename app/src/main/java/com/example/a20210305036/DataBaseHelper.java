package com.example.a20210305036;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "ShoppingList.db";

    private static final int DATABASE_VERSION = 3;


    private static final String TABLE_LISTS = "shopping_lists";
    private static final String COL_LIST_ID = "id";
    private static final String COL_LIST_NAME = "list_name";
    private static final String COL_LIST_DATE = "list_date";
    private static final String COL_LIST_CATEGORY = "category";


    private static final String TABLE_ITEMS = "list_items";
    private static final String COL_ITEM_ID = "id";
    private static final String COL_FK_LIST_ID = "list_id";
    private static final String COL_ITEM_NAME = "item_name";
    private static final String COL_IS_COMPLETED = "is_completed";

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createListsTable = "CREATE TABLE " + TABLE_LISTS + " (" +
                COL_LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LIST_NAME + " TEXT, " +
                COL_LIST_DATE + " TEXT, " +
                COL_LIST_CATEGORY + " TEXT)";

        db.execSQL(createListsTable);


        String createItemsTable = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COL_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FK_LIST_ID + " INTEGER, " +
                COL_ITEM_NAME + " TEXT, " +
                COL_IS_COMPLETED + " INTEGER DEFAULT 0)";
        db.execSQL(createItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }


    public long addList(String listName, String date, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_LIST_NAME, listName);
        contentValues.put(COL_LIST_DATE, date);
        contentValues.put(COL_LIST_CATEGORY, category);


        return db.insert(TABLE_LISTS, null, contentValues);
    }


    public void addItem(long listId, String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_FK_LIST_ID, listId);
        values.put(COL_ITEM_NAME, itemName);
        values.put(COL_IS_COMPLETED, 0);

        db.insert(TABLE_ITEMS, null, values);
    }

    public Cursor getAllLists() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_LISTS, null);
    }

    public Cursor getListItems(long listId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("SELECT * FROM " + TABLE_ITEMS + " WHERE " + COL_FK_LIST_ID + " = " + listId, null);
    }


    public void updateItemStatus(long itemId, boolean isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COL_IS_COMPLETED, isDone ? 1 : 0);


        db.update(TABLE_ITEMS, values, COL_ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
    }


    public void deleteList(long listId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_LISTS, COL_LIST_ID + "=?", new String[]{String.valueOf(listId)});


        db.delete(TABLE_ITEMS, COL_FK_LIST_ID + "=?", new String[]{String.valueOf(listId)});
    }


    public String getCompletedItems() {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor c1 = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_LISTS, null);
        c1.moveToFirst();
        int totalLists = c1.getInt(0);
        c1.close();


        Cursor c2 = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS, null);
        c2.moveToFirst();
        int totalItems = c2.getInt(0);
        c2.close();


        Cursor cursor = db.rawQuery("SELECT item_name FROM list_items WHERE is_completed = 1", null);

        StringBuilder builder = new StringBuilder();


        builder.append("Report State\n");
        builder.append("--------------------------------\n");
        builder.append("Sum Of Created List: ").append(totalLists).append("\n");
        builder.append("Sum Of Goods: ").append(totalItems).append("\n");
        builder.append("Done: ").append(cursor.getCount()).append("\n\n");


        builder.append("Shopping Lists That Bought\n");
        builder.append("-------------------------------\n");

        if (cursor.getCount() == 0) {
            builder.append("There is not any good bought.");
        } else {
            int sayac = 0;
            while (cursor.moveToNext()) {
                sayac++;
                String urunAdi = cursor.getString(0);
                builder.append(sayac).append(". ").append(urunAdi).append("\n");
            }
        }

        cursor.close();
        return builder.toString();
    }
}