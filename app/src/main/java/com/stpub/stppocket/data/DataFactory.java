package com.stpub.stppocket.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by i-worx on 2017-07-11.
 */

public final class DataFactory {
    private static String tableType; // We will

    public DataFactory(String tableType){
        this.tableType = tableType;
    }


    public static String getTableType() {
        return tableType;
    }

    public static List<Publication> createPublicationList(String jsonData) throws JSONException{
        final JSONArray json = new JSONArray(jsonData);
        final int n = json.length();
        final List<Publication> pubs = new ArrayList<>();

        for(int i = 0; i < n; i++){
            final JSONObject pub = json.getJSONObject(i);
            Publication p = new Publication(pub.getString("acronym"), pub.getString("title"));
            pubs.add(p);
        }

        return  pubs;
    }


    public static List<TableData> createTableList(String jsonData) throws JSONException {
        final JSONArray jsonArray = new JSONArray(jsonData);
        final int n = jsonArray.length();
        final List<TableData> tableData = new ArrayList<>();

        String columnShow = getColumnName().get("showColumn");
        String columnHide = getColumnName().get("hideColumn");

        for (int i=0; i<n; i++){
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            TableData t = new TableData(jsonObject.getString(columnShow), jsonObject.getInt(columnHide));
            tableData.add(t);
        }

        return tableData;
    }


    public static List<TableData> createParaList(String jsonData) throws  JSONException {
        final JSONArray jsonArray = new JSONArray(jsonData);
        final int n = jsonArray.length();
        final List<TableData> tableData = new ArrayList<>();

        // Add a placeholder in the first row. The first row in the table view will display
        // the details of the selected row, so we put an extra row in the array
        // to render the table view correctly.
        Paragraph paragraph = new Paragraph("Mock", 0);
        tableData.add(paragraph);

        for (int i = 0; i < n; i++){
            final JSONObject jsonObject = jsonArray.getJSONObject(i);
            paragraph = new Paragraph(jsonObject.getString("citation"), jsonObject.getInt("paraKey"));
            paragraph.setSectionKey(jsonObject.getInt("sectionKey"));
            paragraph.setGuideNote(jsonObject.getString("guideNote"));
            paragraph.setParaNum(jsonObject.getString("paraNum"));
            paragraph.setQuestion(jsonObject.getString("question"));

            tableData.add(paragraph);
        }

        return tableData;
    }


    public static Map<String, String> getColumnName(){
        Map<String, String> map = new HashMap<String, String>();
        switch (getTableType()){
            case "publication":
                map.put("showColumn", "title");
                map.put("hideColumn", "acronym");
                break;
            case "topic":
                map.put("showColumn", "topic");
                map.put("hideColumn", "topicKey");
                break;
            case "rulebook":
                map.put("showColumn", "rbName");
                map.put("hideColumn", "rbKey");
                break;
            case "section":
                map.put("showColumn", "sectName");
                map.put("hideColumn", "sectionKey");
                break;
            case "paragraph":
                map.put("showColumn", "citation");
                map.put("hideColumn", "paraKey");
                break;
            default:
                break;
        }

        return map;
    }
}
