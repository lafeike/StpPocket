package com.stpub.stppocket.data;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stpub.stppocket.ParagraphActivity;
import com.stpub.stppocket.PublicationActivity;
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
import de.codecrafters.tableview.listeners.TableDataLongClickListener;

/**
 * Created by Rafy on 2017-07-13.
 * Call the web api and refresh the UI with data from the server.
 */

public class WebProxy extends AsyncTask<String, String, WebProxy.AsyncTaskResult<String>> {
    public Context context;

    private String urlType;
    private String tableHeaderText;

    ProgressBar progressBar;

    private static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private static final String TABLE_HEADER = "TABLE_HEADER";
    private static final String TAG = "WebProxy";

    public WebProxy(Context context, String urlType){
        this.context = context;
        this.urlType = urlType;
    }

    protected void onPreExecute() {// needs to change here.

    }

    protected AsyncTaskResult<String> doInBackground(String... args){
        try {
            String URL_END_POINT = context.getString(R.string.URL_END_POINT);
            int count = args.length;
            String busType = args[0];
            String para1 = args[1];

            if(urlType.equals("offline")){
                buildTableFromDb(busType, para1);
                return new AsyncTaskResult<String>("");
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
                    return new AsyncTaskResult<String>(stringBuilder.toString());
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage(), e);
                    return new AsyncTaskResult<String>(e);
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
            return new AsyncTaskResult<String>(e);
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
                int state = ((Helper) context.getApplicationContext()).getStateSelected();
                if( state == 0){
                    result = "Para?SectionKey=" + value1;
                } else {
                    String sb = ((Helper) context.getApplicationContext()).getStates().get(state).replace(" ", "%20");
                    result = "ParaController/" + value1 + "/" + sb;
                }

                break;
            case "download":
                result = "PublicationsController/" + value1 + "/" + value2;
                break;
            default:
                break;
        }
        return result;
    }


    protected class AsyncTaskResult<String> {
        private String result;
        private Exception error;

        public String getResult() {
            return result;
        }

        public Exception getError() {
            return error;
        }

        public AsyncTaskResult(String result) {
            super();
            this.result = result;
        }

        public AsyncTaskResult(Exception error) {
            super();
            this.error = error;
        }
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
                    tableDataAdapter.setRowClicked(rowIndex);

                    tableDataAdapter.notifyDataSetChanged();
                }
            } else {
                Intent intent = getLinkedIntent(activityName);
                intent.putExtra(EXTRA_MESSAGE, clickedData.getKey());
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
        Activity activity = (Activity) context;
        final StpTableView stpTableView = (StpTableView) activity.findViewById(R.id.tableView);

        if(stpTableView != null){
            DBHandler db = DBHandler.getInstance(activity);
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

    public class PublicationLongClickListener implements TableDataLongClickListener<TableData> {

        @Override
        public boolean onDataLongClicked(int rowIndex, final TableData clickedData){

            Activity activity = (Activity) context;
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            progressBar = activity.findViewById(R.id.progressBar);
            builder.setTitle("Download Publication")
                    .setMessage("Begin to download " + clickedData.getKey() + "?")
                    .setPositiveButton( "Yes",
                            new DialogInterface.OnClickListener(){
                                public void onClick(DialogInterface dialog, int which){
                                    progressBar.setVisibility(View.VISIBLE);
                                    dialog.dismiss();
                                    downloadPublication(clickedData.getKey());
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which){
                                    dialog.dismiss();
                                }

                            }).show();

            return true;
        }


        private void downloadPublication(String acronym){
            WebProxy myTask = new WebProxy(context, "download");
            String userId = ((Helper)context.getApplicationContext()).getUserId();
            myTask.execute("download", acronym, userId);
        }
    }


    private void buildTable(String busType, String jsonData){
        Activity activity = (Activity) context;
        final StpTableView myTableView = activity.findViewById(R.id.tableView);

        if(myTableView != null){
            try {
                DataFactory dataFactory = new DataFactory(busType);
                MyTableDataAdapter tableDataAdapter;

                switch (busType){
                    case "paragraph":
                        tableDataAdapter = new ParaTableDataAdapter(context, dataFactory.createParaList(jsonData), myTableView);
                        myTableView.setDataAdapter(tableDataAdapter);
                        break;
                    case "topic":
                        tableDataAdapter = new MyTableDataAdapter(context, dataFactory.extractTopic(jsonData), myTableView);
                        ((Helper)activity.getApplication()).setStates(dataFactory.extractStates(jsonData));
                        myTableView.setDataAdapter(tableDataAdapter);
                        break;
                    case "publication":
                        myTableView.addDataLongClickListener(new PublicationLongClickListener());
                        tableDataAdapter = new PublicationTableDataAdapter(context, dataFactory.createTableList(jsonData), myTableView);
                        myTableView.setDataAdapter(tableDataAdapter);
                        break;
                    default:
                        tableDataAdapter = new MyTableDataAdapter(context, dataFactory.createTableList(jsonData), myTableView);
                        myTableView.setDataAdapter(tableDataAdapter);
                }
                myTableView.addDataClickListener(new MyTableClickListener());

            } catch (JSONException e){
                Log.e("buildTable", e.getMessage());
                e.printStackTrace();
            }
        }
    }


    protected void onPostExecute(AsyncTaskResult<String> response){
        //progressBar.setVisibility(View.GONE);
        //Log.i("INFO", response);
        if(response.getError() != null) {
            Toast.makeText(context, response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }
        if(urlType.equals("download")){
            // Save data into SQLite.
            DBHandler db = DBHandler.getInstance(context);
            SQLiteDatabase stpDB = db.getWritableDatabase();
            DataFactory dataFactory = new DataFactory(urlType);
            try {
                db.addPublication(stpDB, dataFactory.extractPublication(response.getResult()));
                db.addTable(stpDB, dataFactory.extractTable(response.getResult(), "topic"), "topic");
                db.addTable(stpDB, dataFactory.extractTable(response.getResult(), "rulebook"), "rulebook");
                db.addTable(stpDB, dataFactory.extractTable(response.getResult(), "section"), "section");
                db.addParagraph(stpDB, dataFactory.extractParagraph(response.getResult()));
                PublicationActivity activity = (PublicationActivity) context;
                activity.hideProgressBar();
                activity.showMessage("Downloaded successfully.");
            }
            catch (JSONException e){
                Log.e("ERROR", e.getMessage());
            }
            finally {
                db.close();
            }
        }else if(urlType.equals("offline")) {
            // do nothing.
            Log.d("WebProxy", "offline.");
        }
        else{
            // Refresh table view.
            buildTable(urlType, response.getResult());
        }

        return;
    }
}

