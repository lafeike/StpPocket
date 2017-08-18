package com.stpub.stppocket.data;

import android.util.Log;

import com.stpub.stppocket.helper.Helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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


    public static Publication extractPublication(String jsonData) throws  JSONException {
        final JSONObject jsonObject = new JSONObject(jsonData);
        final JSONObject pub = jsonObject.getJSONObject("pb");

        //final JSONObject pub = jsonArray.getJSONObject(0);
        Publication p = new Publication(pub.getString("acronym"), pub.getString("title"));
        p.setPid(pub.getInt("publicationID"));

        return p;
    }

    // Extract topic from JSON and save the state list.
    // to parse the response from: /api/Topics?acronym=OF&userid=8191
    public  List<TableData> extractTopic(String jsonData) throws JSONException {
        final JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray = jsonObject.getJSONArray("tp");

        return parseTableData(jsonArray, "topic");
    }


    public List<String> extractStates(String jsonData) throws JSONException {
        final JSONObject jsonObject = new JSONObject(jsonData);
        List<String> state = new ArrayList<>();

        JSONObject jsonPub = jsonObject.getJSONObject("pb");
        JSONArray jsonStates = null;

        if (jsonPub != null){
            if(!jsonPub.isNull("state")) {
                jsonStates = jsonPub.getJSONArray("state");
                state.add("None");

                for (int i = 0; i < jsonStates.length(); i++) {
                    state.add(jsonStates.getString(i));
                }
            }
        }

        return state;
    }


    private static List<TableData> parseTableData(JSONArray jsonArray, String urlType) throws  JSONException{
        final List<TableData> tableData = new ArrayList<>();
        if(jsonArray == null)
            return null;
        final int n = jsonArray.length();

        String columnShow = getColumnName(urlType).get("showColumn");
        String columnHide = getColumnName(urlType).get("hideColumn");
        String parentKeyName = getColumnName(urlType).get("parentKeyName");
        Log.i("DataFactory", "columnShow: " + columnShow);

        for (int i=0; i<n; i++){
            final JSONObject j = jsonArray.getJSONObject(i);
            TableData t = new TableData(j.getString(columnShow), j.getInt(columnHide));
            t.setParentKey(j.getString(parentKeyName));
            tableData.add(t);
        }

        return tableData;

    }


    public static List<TableData> extractTable(String jsonData, String urlType) throws JSONException {
        final JSONObject jsonObject = new JSONObject(jsonData);
        JSONArray jsonArray;
        Log.i("DataFactory", "urlType = " + urlType);
        switch (urlType){
            case "topic":
                jsonArray = jsonObject.getJSONArray("tp");
                break;
            case "rulebook":
                jsonArray = jsonObject.getJSONArray("rb");
                break;
            case "section":
                jsonArray = jsonObject.getJSONArray("st");
                break;
            case "paragraph":
                jsonArray = jsonObject.getJSONArray("pg");
                break;
            default:
                jsonArray = null;
        }

        return parseTableData(jsonArray, urlType);
    }


    public static List<Paragraph> extractParagraph(String jsonData) throws JSONException {
        final JSONObject jsonObject = new JSONObject(jsonData);
        final List<Paragraph> tableData = new ArrayList<>();
        JSONObject json;

        JSONArray jsonArray = jsonObject.getJSONArray("pg");
        final int n = jsonArray.length();
        Log.i("DataFactory", "get json object pg: " + n);

        for (int i=0; i<n; i++){
            final JSONObject j = jsonArray.getJSONObject(i);
            Paragraph p = new Paragraph(j.getString("citation"), j.getInt("paraKey"));
            p.setSectionKey(j.getInt("sectionKey"));
            p.setParaNum(j.getString("paraNum"));
            p.setGuideNote(j.getString("guideNote"));
            p.setQuestion(j.getString("question"));
            tableData.add(p);
        }

        return tableData;
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
        //Log.d("DataFatcory", "create Para List: " + tableData.size());
        return tableData;
    }


    public static Map<String, String> getColumnName(String urlType){
        Map<String, String> map = new HashMap<String, String>();
        switch (urlType){
            case "publication":
                map.put("showColumn", "title");
                map.put("hideColumn", "acronym");
                map.put("parentKeyName", "");
                break;
            case "topic":
                map.put("showColumn", "topic");
                map.put("hideColumn", "topicKey");
                map.put("parentKeyName", "acronym");
                break;
            case "rulebook":
                map.put("showColumn", "rbName");
                map.put("hideColumn", "rbKey");
                map.put("parentKeyName", "topicKey");
                break;
            case "section":
                map.put("showColumn", "sectName");
                map.put("hideColumn", "sectionKey");
                map.put("parentKeyName", "rbKey");
                break;
            case "paragraph":
                map.put("showColumn", "citation");
                map.put("hideColumn", "paraKey");
                map.put("parentKeyName", "sectionKey");
                break;
            default:
                break;
        }

        return map;
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
