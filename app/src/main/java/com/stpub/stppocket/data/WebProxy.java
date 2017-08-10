package com.stpub.stppocket.data;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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

import static com.stpub.stppocket.R.id.progressBar;

/**
 * Created by i-worx on 2017-07-13.
 */

public class WebProxy extends AsyncTask<String, String, String> {
    public Context context;

    public String urlType;
    public String tableHeaderText;
    private  Exception exception;

    ProgressBar progressBar;

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TABLE_HEADER = "TABLE_HEADER";
    public static final String TAG = "WebProxy";

    public WebProxy(Context context, String urlType){
        this.context = context;
        this.urlType = urlType;

    }

    protected void onPreExecute() {// needs to change here.

    }

    protected String doInBackground(String... args){
        try {
            String URL_END_POINT = context.getString(R.string.URL_END_POINT);
            int count = args.length;
            String busType = args[0];
            String para1 = args[1];

            Log.i("WebProxy", "doInBackground: busType =" + busType + ", para1 = " + para1);

            if(urlType.equals("offline")){
                buildTableFromDb(busType, para1);
                return null;
            } else {
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
            case "download":
                result = "PublicationsController/" + value1 + "/" + value2;
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
            Log.d("onDataClick", "rowIndex = " + rowIndex );
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
                    Log.d("onDataClick", "count = " + tableDataAdapter.getCount() );
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

            for (int i = 0; i < linkedList.size() - 1; i++) {
                if (activityName.contains(linkedList.get(i).getComponent().getClassName())){
                    return linkedList.get(i + 1);
                }
            }

            return  linkedList.getLast();
        }
    }


    private void buildTableFromDb(String busType, String value){
        Log.i("FromDB", "busType=" + busType + " value =" + value);
        Activity activity = (Activity) context;
        final StpTableView stpTableView = (StpTableView) activity.findViewById(R.id.tableView);
        Log.d("WebProxy", "activity =" + activity.getLocalClassName());
        if(stpTableView != null){
            DBHandler db = new DBHandler(activity);
            try {

                SQLiteDatabase stpDb = db.getReadableDatabase();
                MyTableDataAdapter tableDataAdapter;
                if(busType.equals("paragraph")){
                    tableDataAdapter = new ParaTableDataAdapter(context, db.getParagraph(stpDb, value), stpTableView);
                } else {
                    tableDataAdapter = new MyTableDataAdapter(context, db.getTableData(stpDb, busType, value), stpTableView);
                }

                stpTableView.setDataAdapter(tableDataAdapter);
                stpTableView.addDataClickListener(new MyTableClickListener());
            } catch (Exception e){
                Log.e("WebProxy", e.getMessage());
                e.printStackTrace();
            } finally {
                db.close();
            }
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
                e.printStackTrace();
            }
        }
    }


    protected void onPostExecute(String response){
        //progressBar.setVisibility(View.GONE);
        //Log.i("INFO", response);
        if(urlType.equals("download")){
            // Save data into SQLite.
            Log.d("DEBUG", "Now start to save publication into SQLite.");
            DBHandler db = new DBHandler(context);
            SQLiteDatabase stpDB = db.getWritableDatabase();
            DataFactory dataFactory = new DataFactory(urlType);
            try {
                db.addPublication(stpDB, dataFactory.extractPublication(response));
                db.addTable(stpDB, dataFactory.extractTable(response, "topic"), "topic");
                db.addTable(stpDB, dataFactory.extractTable(response, "rulebook"), "rulebook");
                db.addTable(stpDB, dataFactory.extractTable(response, "section"), "section");
                db.addParagraph(stpDB, dataFactory.extractParagraph(response));
                PublicationActivity activity = (PublicationActivity) context;
                activity.hideProgressBar();
                activity.showMessage("Downloaded successfully.");
            }
            catch (JSONException e){
                Log.e("ERROR", e.getMessage());
            }
            finally {
                Log.i("WebProxy", "downloaded.");
                db.close();
            }
        }else if(urlType.equals("offline")) {
            // do nothing.
            Log.d("WebProxy", "offline.");
        }
        else{
            // Refresh table view.
            Log.d("WebProxy", "build table.");
            buildTable(urlType, response);
        }

        return;
    }
}

