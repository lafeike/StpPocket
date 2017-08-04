package com.stpub.stppocket.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i-worx on 2017-07-28.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "stpdb";

    private static final String TABLE_PUBLICATION = "publication";
    private static final String KEY_PID = "pid";
    private static final String KEY_ACRONYM = "acronym";
    private static final String KEY_TITLE = "title";

    private static final String TABLE_TOPIC = "topic";
    private static final String TABLE_RULEBOOK = "rulebook";


    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PUBLICATION_TABLE = "create table if not exists " + TABLE_PUBLICATION + "("
                + KEY_PID + " integer primary key,"
                + KEY_ACRONYM + " text,"
                + KEY_TITLE + " text)";
        String CREATE_TOPIC_TABLE = "create table if not exists " + TABLE_TOPIC + "("
                + "topic_key integer primary key,"
                + "acronym text,"
                + "topic text)";
        String CREATE_RULEBOOK_TABLE = "create table if not exists " + TABLE_RULEBOOK + "("
                + "rb_key integer primary key,"
                + "rbname text,"
                + "topic_key text)";
        db.execSQL(CREATE_PUBLICATION_TABLE);
        db.execSQL(CREATE_TOPIC_TABLE);
        db.execSQL(CREATE_RULEBOOK_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_PUBLICATION);
        db.execSQL("drop table if exists " + TABLE_TOPIC);
        db.execSQL("drop table if exists " + TABLE_RULEBOOK);
        onCreate(db);
    }


    public void addPublication(SQLiteDatabase db, Publication publication){
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ACRONYM, publication.getAcronym());
        values.put(KEY_PID, publication.getPid());
        values.put(KEY_TITLE, publication.getTitle());
        deletePublication(db, publication.getAcronym());
        db.insert(TABLE_PUBLICATION, null, values);
        //db.close();
    }


    public void deletePublication(SQLiteDatabase db, String acronym){
        //SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PUBLICATION, "acronym=?", new String[]{acronym});
        // db.close();
    }


    public void addTable(SQLiteDatabase db, List<TableData> tableData, String tableName){
        //SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String[] cName = getTableColumn(tableName);
        for (int i = 0; i < tableData.size(); i++) {
            values.put(cName[1], tableData.get(i).getKey());
            values.put(cName[2], tableData.get(i).getParentKey());
            values.put(cName[0], tableData.get(i).getTitle());
            db.insert(tableName, null, values);
        }
        //db.close();
    }


    public List<TableData> getTableData(SQLiteDatabase db, String tableName, String value){
        List<TableData> tableDataList = new ArrayList<>();

        Cursor cursor = db.query(tableName, getTableColumn(tableName),
                queryKey(tableName) + "=?", new String[]{value}, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                TableData t = new TableData(cursor.getString(0), cursor.getInt(1));
                t.setParentKey(cursor.getString(2));
                Log.i("getData", "title=" + cursor.getString(0) + ", key=" + cursor.getInt(1) + ", parentKey=" + cursor.getString(2));
                tableDataList.add(t);
            } while (cursor.moveToNext());
        }

        return  tableDataList;
    }


    private String[] getTableColumn(String tableName){

        String[] topicColumn = {"topic", "topic_key", "acronym"};
        String[] rulebookColumn = {"rbname", "rb_key", "topic_key"};
        String[] sectionColumn = {"sectname", "section_key", "rb_key"};

        switch (tableName){
            case "topic":
                return topicColumn;
            case "rulebook":
                return rulebookColumn;
            case "section":
                return sectionColumn;
            default:
                return null;
        }
    }


    private String queryKey(String tableName){
        String key = null;

        if(tableName != null && !tableName.isEmpty()){
            switch (tableName){
                case "topic":
                    key = "acronym";
                    break;
                case "rulebook":
                    key = "topic_key";
                    break;
            }
        }

        return key;
    }


    public Publication getPublication(SQLiteDatabase db, String acronym){
        //SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PUBLICATION, new String[] {KEY_ACRONYM, KEY_TITLE, KEY_PID},
                KEY_ACRONYM + "=?", new String[]{acronym}, null, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        Publication pub = new Publication(cursor.getString(0), cursor.getString(1));
        pub.setPid(cursor.getInt(2));

        return pub;
    }


    public List<Publication> getAllPublications(SQLiteDatabase db){
        List<Publication> pubList = new ArrayList<Publication>();
        String selectQuery = "select acronym,title,pid from " + TABLE_PUBLICATION;
        //SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                Publication pub = new Publication(cursor.getString(0), cursor.getString(1));
                pub.setPid(cursor.getInt(2));
                pubList.add(pub);
            } while (cursor.moveToNext());
        }

        return pubList;
    }

}
