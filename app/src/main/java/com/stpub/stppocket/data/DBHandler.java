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
    private static final String TABLE_SECTION = "section";
    private static final String TABLE_PARAGRAPH = "paragraph";


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
        String CREATE_SECTION_TABLE = "create table if not exists " + TABLE_SECTION + "("
                + "section_key integer primary key,"
                + "section_name text,"
                + "rb_key text)";
        String CREATE_PARAGRAPH_TABLE = "create table if not exists " + TABLE_PARAGRAPH + "("
                + "para_key integer primary key,"
                + "section_key integer,"
                + "para_num text,"
                + "question text,"
                + "guide_note text,"
                + "citation text)";
        db.execSQL(CREATE_PUBLICATION_TABLE);
        db.execSQL(CREATE_TOPIC_TABLE);
        db.execSQL(CREATE_RULEBOOK_TABLE);
        db.execSQL(CREATE_SECTION_TABLE);
        db.execSQL(CREATE_PARAGRAPH_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_PUBLICATION);
        db.execSQL("drop table if exists " + TABLE_TOPIC);
        db.execSQL("drop table if exists " + TABLE_RULEBOOK);
        db.execSQL("drop table if exists " + TABLE_SECTION);
        db.execSQL("drop table if exists " + TABLE_PARAGRAPH);
        onCreate(db);
    }


    public void addPublication(SQLiteDatabase db, Publication publication){
        deletePublication(db, publication.getAcronym());

        ContentValues values = new ContentValues();
        values.put(KEY_ACRONYM, publication.getAcronym());
        values.put(KEY_PID, publication.getPid());
        values.put(KEY_TITLE, publication.getTitle());

        db.insert(TABLE_PUBLICATION, null, values);
    }


    public void deletePublication(SQLiteDatabase db, String acronym){
        Cursor cTopic = db.rawQuery("select topic_key from topic where acronym='" + acronym + "'", null);
        if (cTopic.moveToFirst()){
            do {
                Integer topicKey = cTopic.getInt(0);
                Cursor cRulebook = db.rawQuery("select rb_key from rulebook where topic_key='" + topicKey + "'", null);
                if(cRulebook.moveToFirst()){
                    do{
                        Integer rbKey = cRulebook.getInt(0);
                        Cursor cSection = db.rawQuery("select section_key from section where rb_key='" + rbKey + "'", null);
                        if(cSection.moveToFirst()){
                            do{
                                Integer sectionKey = cSection.getInt(0);
                                db.delete(TABLE_PARAGRAPH, "section_key=?", new String[]{String.valueOf(sectionKey)});
                            } while (cSection.moveToNext());
                        }
                        db.delete(TABLE_SECTION, "rb_key=?", new String[]{String.valueOf(rbKey)});
                    } while (cRulebook.moveToNext());
                }
                db.delete(TABLE_RULEBOOK, "topic_key=?", new String[]{String.valueOf(topicKey)});
            }while (cTopic.moveToNext());
            db.delete(TABLE_TOPIC, "acronym=?", new String[]{acronym});
        }

        db.delete(TABLE_PUBLICATION, "acronym=?", new String[]{acronym});
    }


    public void addTable(SQLiteDatabase db, List<TableData> tableData, String tableName){
        ContentValues values = new ContentValues();
        String[] cName = getTableColumn(tableName);

        for (int i = 0; i < tableData.size(); i++) {
            values.put(cName[1], tableData.get(i).getKey());
            values.put(cName[2], tableData.get(i).getParentKey());
            values.put(cName[0], tableData.get(i).getTitle());
            db.insert(tableName, null, values);
        }
    }


    public void addParagraph(SQLiteDatabase db, List<Paragraph> paragraphs){
        ContentValues values = new ContentValues();
        for(int i = 0; i < paragraphs.size(); i++) {
            values.put("para_key", paragraphs.get(i).getKey());
            values.put("citation", paragraphs.get(i).getTitle());
            values.put("section_key", paragraphs.get(i).getSectionKey());
            values.put("para_num", paragraphs.get(i).getParaNum());
            values.put("question", paragraphs.get(i).getQuestion());
            values.put("guide_note", paragraphs.get(i).getGuideNote());
            db.insert("paragraph", null, values);
        }
    }


    public List<TableData> getTableData(SQLiteDatabase db, String tableName, String value){
        List<TableData> tableDataList = new ArrayList<>();

        Cursor cursor = db.query(tableName, getTableColumn(tableName),
                queryKey(tableName) + "=?", new String[]{value}, null, null, null, null);
        if(cursor.moveToFirst()){
            do {
                TableData t = new TableData(cursor.getString(0), cursor.getInt(1));
                t.setParentKey(cursor.getString(2));

                tableDataList.add(t);
            } while (cursor.moveToNext());
        }

        return  tableDataList;
    }


    public List<TableData> getParagraph(SQLiteDatabase db, String sectionKey){
        List<TableData> paragraphList = new ArrayList<>();

        Cursor cursor = db.query("paragraph",
                new String[]{"para_key", "citation", "section_key", "para_num", "question", "guide_note"},
                "section_key=?", new String[]{sectionKey},
                null, null, null, null);
        Paragraph p = new Paragraph("Mock", 0); // add a placeholder in the first row.
        paragraphList.add(p);

        if(cursor.moveToFirst()){
            do {
                Paragraph paragraph = new Paragraph(cursor.getString(1), cursor.getInt(0));
                paragraph.setSectionKey(cursor.getInt(2));
                paragraph.setGuideNote(cursor.getString(5));
                paragraph.setParaNum(cursor.getString(3));
                paragraph.setQuestion(cursor.getString(4));
                paragraphList.add(paragraph);
            } while (cursor.moveToNext());
        }

        return paragraphList;
    }


    private String[] getTableColumn(String tableName){

        String[] topicColumn = {"topic", "topic_key", "acronym"};
        String[] rulebookColumn = {"rbname", "rb_key", "topic_key"};
        String[] sectionColumn = {"section_name", "section_key", "rb_key"};

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
                case "section":
                    key = "rb_key";
                    break;
                case "paragraph":
                    key = "section_key";
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
