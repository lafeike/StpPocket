package com.stpub.stppocket.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i-worx on 2017-07-11.
 */

public final class DataFactory {

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
}
