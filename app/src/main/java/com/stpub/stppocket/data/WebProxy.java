package com.stpub.stppocket.data;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.stpub.stppocket.ParagraphActivity;
import com.stpub.stppocket.PublicationActivity;
import com.stpub.stppocket.PublicationTableDataAdapter;
import com.stpub.stppocket.R;
import com.stpub.stppocket.RulebookActivity;
import com.stpub.stppocket.SectionActivity;
import com.stpub.stppocket.StpTableView;
import com.stpub.stppocket.TopicActivity;
import com.stpub.stppocket.helper.Helper;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import de.codecrafters.tableview.listeners.TableDataClickListener;

/**
 * Created by i-worx on 2017-07-13.
 */

public class WebProxy extends AsyncTask<String, String, String> {
    public Context context;

    public String urlType;
    public String tableHeaderText;
    private  Exception exception;

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TABLE_HEADER = "TABLE_HEADER";
    public static final String TAG = "WebProxy";

    public WebProxy(Context context, String urlType){
        this.context = context;
        this.urlType = urlType;
    }

    protected void onPreExecute() {
        //progressBar.setVisibility(View.VISIBLE);
    }

    protected String doInBackground(String... args){
        try {
            String URL_END_POINT = context.getString(R.string.URL_END_POINT);
            int count = args.length;
            String busType = args[0];
            String para1 = args[1];

            URL url;
            if (count == 2){
                url = new URL(URL_END_POINT + buildURL(busType, para1, null));
            }else {
                url = new URL(URL_END_POINT + buildURL(busType, para1, args[2]));
            }

            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally {
                httpURLConnection.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }


    private String buildURL(String urlType, String value1, String value2){
        String result = null;
        tableHeaderText = value2;
        switch (urlType){
            case "publication":
                result = "Publications?userId=" + value1;
                break;
            case "topic":
                result = "Topics?acronym=" + value1 + "&userid=" + value2;
                break;
            case "rulebook":
                result = "Rulebook?topicKey=" + value1;
                break;
            case "section":
                result = "Section?rbKey=" + value1;
                break;
            case "paragraph":
                result = "Para?SectionKey=" + value1;
                break;
            default:
                break;
        }
        return result;
    }


    private class MyTableClickListener implements TableDataClickListener<TableData> {

        @Override
        public void onDataClicked(final int rowIndex, final TableData clickedData){
            Activity activity = (Activity) context;
            String activityName = activity.getClass().getName();

            // Paragraph row was clicked, just refresh the table view;
            // Otherwise arouse a new activity to display.
            if(activityName.contains("ParagraphActivity")){
                if (rowIndex == 0){
                    // Ignore it if the first row is clicked.
                } else {
                    // Needs to refresh the table.
                    final StpTableView myTableView = (StpTableView) activity.findViewById(R.id.tableView);
                    ParaTableDataAdapter tableDataAdapter = (ParaTableDataAdapter)myTableView.getDataAdapter();

                    // Replace the first row with the click row.
                    tableDataAdapter.remove(tableDataAdapter.getItem(0));
                    tableDataAdapter.insert(clickedData, 0);
                    tableDataAdapter.notifyDataSetChanged();
                }
            } else {
                Intent intent = getLinkedIntent(activityName);

                intent.putExtra(EXTRA_MESSAGE, "" + clickedData.getKey());
                intent.putExtra(TABLE_HEADER, clickedData.getTitle());
                context.startActivity(intent);
            }
        }


        // To identify which activity is next to this activity.
        private Intent getLinkedIntent(String activityName){
            LinkedList<Intent> linkedList = new LinkedList<Intent>();

            linkedList.add(new Intent(context, PublicationActivity.class));
            linkedList.add(new Intent(context, TopicActivity.class));
            linkedList.add(new Intent(context, RulebookActivity.class));
            linkedList.add(new Intent(context, SectionActivity.class));
            linkedList.add(new Intent(context, ParagraphActivity.class));

            Log.i(TAG, "get next activity for: " + activityName);
            for (int i = 0; i < linkedList.size() - 1; i++) {
                Log.i(TAG, "loop:" + linkedList.get(i).getComponent().getClassName());
                if (activityName.contains(linkedList.get(i).getComponent().getClassName())){
                    Log.i(TAG, "getLinkedIntent return: " + linkedList.get(i+1).getComponent().getClassName());
                    return linkedList.get(i + 1);
                }
            }

            return  linkedList.getLast();
        }
    }


    private void buildTable(String busType, String jsonData){
        Activity activity = (Activity) context;
        final StpTableView myTableView = (StpTableView) activity.findViewById(R.id.tableView);

        if(myTableView != null){
            try {

                DataFactory dataFactory = new DataFactory(busType);
                MyTableDataAdapter tableDataAdapter;
                if (busType.equals("paragraph")){
                    tableDataAdapter = new ParaTableDataAdapter(context, dataFactory.createParaList(jsonData), myTableView);
                } else {
                    tableDataAdapter = new MyTableDataAdapter(context, dataFactory.createTableList(jsonData), myTableView);
                }
                myTableView.addDataClickListener(new MyTableClickListener());
                myTableView.setDataAdapter(tableDataAdapter);
            } catch (JSONException e){
                Log.e("buildTable", e.getMessage());
            }
        }
    }


    protected void onPostExecute(String response){
        //progressBar.setVisibility(View.GONE);
        Log.i("IN FO", response);
        buildTable(urlType, response);
        return;
    }
}

