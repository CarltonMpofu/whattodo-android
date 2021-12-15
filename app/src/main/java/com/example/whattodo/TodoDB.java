package com.example.whattodo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.PublicKey;
import java.util.ArrayList;

public class TodoDB {

    private final String KEY_ID = "_id";
    private final String KEY_TODO = "_todo";

    private final String DATABASE_NAME = "TodoDB";
    private final String DATABASE_TABLE = "TodoTable";
    private final Integer DATABASE_VERSION = 1;

    private final Context ourContext;
    private DBHelper ourHelper;
    private SQLiteDatabase ourDatabase;

    public  TodoDB(Context context){
        ourContext = context;
    }

    class DBHelper extends SQLiteOpenHelper
    {
        public DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Is executed when database file does not exist
        @Override
        public void onCreate(SQLiteDatabase db) {
            /*
                CREATE TABLE TodoTable (_id INTEGER PRIMARY KEY AUTOINCREMENT,
                   todo TEXT NOT NULL);
             */
            String sqlcode = "CREATE TABLE " + DATABASE_TABLE +
                    " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TODO + " TEXT NOT NULL);";

            db.execSQL(sqlcode);
        }

        // Is executed when database file exists but stored version is lower than requested version
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Save data if you want to use it later on
            String sqlCode = "DROP TABLE IF EXISTS " + DATABASE_TABLE;
            db.execSQL(sqlCode);
        }
    }

    public TodoDB open() throws SQLException
    {
        ourHelper = new DBHelper(ourContext);
        ourDatabase = ourHelper.getWritableDatabase();
        return  this;
    }

    public  void close()
    {
        ourHelper.close();
    }

    public long createEntry(String todo)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TODO, todo);

        return ourDatabase.insert(DATABASE_TABLE, null, cv);
    }
    
    public long deleteEntry(String todoId){
        return ourDatabase.delete(DATABASE_TABLE, KEY_ID + "=?", new String[] {todoId });
    }

    public long updateEntry(String todoId, String myTodo)
    {
        ContentValues cv = new ContentValues();
        cv.put(KEY_TODO, myTodo);

        return ourDatabase.update(DATABASE_TABLE, cv, KEY_ID + "=?", new String[]{todoId});
    }

    public ArrayList<Todo> getAllEntries(){

        String[] columns = new String[]{KEY_ID, KEY_TODO};

        Cursor cursor = ourDatabase.query(DATABASE_TABLE, columns, null, null,
                null, null,null);

        int idxRowId = cursor.getColumnIndex(KEY_ID);
        int idxTodo = cursor.getColumnIndex(KEY_TODO);

        ArrayList<Todo> results = new ArrayList<Todo>();

        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            String myId = cursor.getString(idxRowId);
            String myTodo = cursor.getString(idxTodo);

            Todo todo = new Todo(myTodo);
            todo.setID(myId);

            results.add(todo);
        }

        cursor.close();

        return  results;
    }

}
