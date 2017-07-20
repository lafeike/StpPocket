package com.stpub.stppocket.data;

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
        final List<TableData> tableDatas = new ArrayList<>();

        String columnShow = getColumnName().get("showColumn");
        String columnHide = getColumnName().get("hideColumn");

        for (int i=0; i<n; i++){
            final JSONObject tableData = jsonArray.getJSONObject(i);
            TableData t = new TableData(tableData.getString(columnShow), tableData.getInt(columnHide));
            tableDatas.add(t);
        }

        return tableDatas;
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
                map.put("", "");
                break;
            default:
                break;
        }

        return map;
    }
}
