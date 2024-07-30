package com.example.suitcase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context){
        super(context, DatabaseUtil.DATABASE_NAME, null, DatabaseUtil.DATABASE_VERSION);
    }

    //Create the users and items tables if no database exists
    @Override public void onCreate(SQLiteDatabase database){

        String CREATE_USER_TABLE = "CREATE TABLE " + DatabaseUtil.TABLE_NAME_USERS
                + "(" + DatabaseUtil.USER_KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseUtil.USER_USERNAME + " TEXT,"
                + DatabaseUtil.USER_PASSWORD + " TEXT,"
                + DatabaseUtil.USER_FULLNAME + " TEXT,"
                + DatabaseUtil.USER_EMAIL + " TEXT)";
        database.execSQL(CREATE_USER_TABLE);

        String CREATE_ITEM_TABLE = "CREATE TABLE " + DatabaseUtil.TABLE_NAME_ITEMS
                + "(" + DatabaseUtil.ITEM_KEY_ID + " INTEGER PRIMARY KEY,"
                + DatabaseUtil.USER_KEY_ID + " INTEGER,"
                + DatabaseUtil.ITEM_NAME + " TEXT,"
                + DatabaseUtil.ITEM_PRICE + " TEXT,"
                + DatabaseUtil.ITEM_DESCRIPTION + " TEXT,"
                + DatabaseUtil.ITEM_PURCHASED + " BOOLEAN,"
                + DatabaseUtil.ITEM_LAT + " DOUBLE,"
                + DatabaseUtil.ITEM_LNG + " DOUBLE,"
                + "FOREIGN KEY(" + DatabaseUtil.USER_KEY_ID + ") REFERENCES " + DatabaseUtil.TABLE_NAME_USERS + "(" + DatabaseUtil.USER_KEY_ID + ")" +" )";
        database.execSQL(CREATE_ITEM_TABLE);
    }

    @Override public void onUpgrade(SQLiteDatabase database, int oldv, int newv){

    }

    //function to add a user to the database
    public void addUser(RegisterUser user){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseUtil.USER_FULLNAME, user.getFullname());
        values.put(DatabaseUtil.USER_USERNAME, user.getUsername());
        values.put(DatabaseUtil.USER_PASSWORD, user.getPassword());
        values.put(DatabaseUtil.USER_EMAIL, user.getEmail());

        database.insert(DatabaseUtil.TABLE_NAME_USERS, null, values);
        database.close();
    }

    //function to add an item to the database linked to a provided userid
    public void addItem(Item item, int userid){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(DatabaseUtil.ITEM_NAME, item.getName());
        values.put(DatabaseUtil.ITEM_PRICE, Float.toString(item.getPrice()));
        values.put(DatabaseUtil.ITEM_DESCRIPTION, item.getDescription());
        values.put(DatabaseUtil.USER_KEY_ID, userid);
        values.put(DatabaseUtil.ITEM_LAT, item.getLat());
        values.put(DatabaseUtil.ITEM_LNG, item.getLng());

        database.insert(DatabaseUtil.TABLE_NAME_ITEMS, null, values);
        database.close();
    }

    //using a given username return the ID of that user
    public int getUserID(String username){
        String userid_query = "SELECT " + DatabaseUtil.USER_KEY_ID + " FROM "
                + DatabaseUtil.TABLE_NAME_USERS + " WHERE "
                + DatabaseUtil.USER_USERNAME + " = \"" + username + "\"";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(userid_query, null);

        int userid = 0;
        cursor.moveToFirst();
        userid = cursor.getInt(0);
        cursor.close();
        return userid;
    }

    //take a username and password and search database for a match
    public int getLogin(String username, String password){
        String loginQuery = "SELECT * FROM "
                + DatabaseUtil.TABLE_NAME_USERS + " WHERE "
                + DatabaseUtil.USER_USERNAME + " = \"" + username + "\" AND "
                + DatabaseUtil.USER_PASSWORD + " = \"" + password + "\"";
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(loginQuery, null);

        //cursor.count used to return a 1 if this user exists or a 0 if no match exists
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    //takes a userid and generates a list of every item associated with that id
    public List<Item> getItems(int userid){
        String itemQuery = "SELECT * FROM "
                + DatabaseUtil.TABLE_NAME_ITEMS + " WHERE "
                + DatabaseUtil.USER_KEY_ID + " = " + userid;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(itemQuery, null);

        List<Item> itemList = new ArrayList<Item>();

        //iterate through everything the cursor found and when finished close it
        try{
            while(cursor.moveToNext()){
                @SuppressLint("Range") Item item = new Item(cursor.getInt(cursor.getColumnIndex(DatabaseUtil.ITEM_KEY_ID)), cursor.getString(cursor.getColumnIndex(DatabaseUtil.ITEM_NAME)), cursor.getString(cursor.getColumnIndex(DatabaseUtil.ITEM_DESCRIPTION)), cursor.getFloat(cursor.getColumnIndex(DatabaseUtil.ITEM_PRICE)), (cursor.getInt(cursor.getColumnIndex(DatabaseUtil.ITEM_PURCHASED)) > 0), cursor.getDouble(cursor.getColumnIndex(DatabaseUtil.ITEM_LAT)), cursor.getDouble(cursor.getColumnIndex(DatabaseUtil.ITEM_LNG)));
                itemList.add(item);
            }
        }finally {
            cursor.close();
            database.close();
        }
        //Log.d("List: ", itemList.get(0).getName());
        return itemList;
    }

    //takes an item object and updates the corresponding item in the database using the itemid
    public void updateItem(Item item){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseUtil.ITEM_NAME, item.getName());
        values.put(DatabaseUtil.ITEM_DESCRIPTION, item.getDescription());
        values.put(DatabaseUtil.ITEM_PRICE, item.getPrice());
        values.put(DatabaseUtil.ITEM_PURCHASED, item.getPurchased());
        values.put(DatabaseUtil.ITEM_LAT, item.getLat());
        values.put(DatabaseUtil.ITEM_LNG, item.getLng());

        database.update(DatabaseUtil.TABLE_NAME_ITEMS, values, DatabaseUtil.ITEM_KEY_ID + "=?", new String[]{String.valueOf(item.getID())});
        database.close();
    }

    //takes an item object and deletes the corresponding item in the database using the itemid
    public void deleteItem(Item item){
        SQLiteDatabase database = this.getWritableDatabase();

        database.delete(DatabaseUtil.TABLE_NAME_ITEMS, DatabaseUtil.ITEM_KEY_ID + "=?", new String[]{String.valueOf(item.getID())});
        database.close();
    }

}
