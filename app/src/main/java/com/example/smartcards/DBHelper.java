package com.example.smartcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "Prod.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        String createTableQuery = "create table cards (\n" +
                "    card_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "    front_text TEXT NOT NULL,\n" +
                "    back_text TEXT NOT NULL,\n" +
                "    rating INTEGER NOT NULL,\n" +
                "    is_active BOOLEAN NOT NULL\n" +
                ");";

        DB.execSQL(createTableQuery);

        createTableQuery = "CREATE TABLE folders (folder_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, is_active BOOLEAN NOT NULL)";

        DB.execSQL(createTableQuery);

        createTableQuery = "CREATE TABLE cards_folder ("
                + "card_id INTEGER PRIMARY KEY,"
                + "folder_id INTEGER)";

        DB.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase DB, int i, int i1) {
        DB.execSQL("drop table if exists cards");
        DB.execSQL("drop table if exists folders");
        DB.execSQL("drop table if exists cards_folder");
        onCreate(DB);
    }

    public Boolean insertCards(String front, String back, int folder_id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("front_text", front);
        contentValues.put("back_text", back);
        contentValues.put("rating", 0);
        contentValues.put("is_active", 1);
        long result = DB.insert("cards", null, contentValues);

        if(result != -1){
            int card_id = (int) DatabaseUtils.longForQuery(DB, "SELECT last_insert_rowid()", null);

            ContentValues cardsFolderValues = new ContentValues();
            cardsFolderValues.put("card_id", card_id);
            cardsFolderValues.put("folder_id", folder_id);

            long cardsFolderResult = DB.insert("cards_folder", null, cardsFolderValues);

            if (cardsFolderResult != -1) {
                return true;
            }
        }
        return false;
    }

    public Boolean insertFolder(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("is_active", 1);
        long result = DB.insert("folders", null, contentValues);

        if(result != -1){
            return true;
        }
        return false;
    }

    public int insertFolderReturnID(String name) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("is_active", 0);
        long result = DB.insert("folders", null, contentValues);

        if(result != -1){
            return (int) DatabaseUtils.longForQuery(DB, "SELECT last_insert_rowid()", null);
        }
        return -1;
    }
    public Cursor getCards(int folder_id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String selectQuery = "SELECT cards.card_id, front_text, back_text, rating FROM cards " +
                "INNER JOIN cards_folder ON cards.card_id = cards_folder.card_id " +
                "WHERE cards_folder.folder_id = '" + folder_id + "'";

        Cursor cursor = DB.rawQuery(selectQuery, null);

        return cursor;
    }

    public Cursor getFolders() {
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("Select * from folders", null);

        return cursor;
    }

    public Cursor reviewCards(int folder_id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        String selectQuery = "SELECT cards.card_id, front_text, back_text, rating FROM cards " +
                "INNER JOIN cards_folder ON cards.card_id = cards_folder.card_id " +
                "WHERE cards_folder.folder_id = '" + folder_id + "' ORDER BY rating asc";

        Cursor cursor = DB.rawQuery(selectQuery, null);

        return cursor;
    }

    public boolean updateRating(int card_id, int rating) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("rating", rating);
        long result = DB.update("cards", contentValues, "card_id = ?", new String[] { String.valueOf(card_id) });

        if (result != -1) {
            return true;
        }

        return false;
    }

    public boolean updateCardsText(int card_id, String front_text, String back_text) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("front_text", front_text);
        contentValues.put("back_text", back_text);
        long result = DB.update("cards", contentValues, "card_id = ?", new String[] { String.valueOf(card_id) });

        if(result != -1) {
            return true;
        }

        return false;
    }

    public boolean deleteCards(int card_id) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("is_active", 0);
        long result = DB.update("cards", contentValues, "card_id = ?", new String[] { String.valueOf(card_id) });

        if(result != -1) {
            return true;
        }

        return false;
    }
}
