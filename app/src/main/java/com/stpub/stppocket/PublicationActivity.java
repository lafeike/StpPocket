package com.stpub.stppocket;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.stpub.stppocket.data.DBHandler;
import com.stpub.stppocket.data.DataFactory;
import com.stpub.stppocket.data.Publication;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.datatype.DatatypeFactory;

import de.codecrafters.tableview.listeners.SwipeToRefreshListener;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;


import static com.stpub.stppocket.R.id.progressBar;
import static com.stpub.stppocket.R.id.tableView;

import com.stpub.stppocket.helper.Helper;

public class PublicationActivity extends AppCompatActivity {
    ProgressBar progressBar;
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TABLE_HEADER = "TABLE_HEADER"; // Publication title selected will show on the table header of Topic.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        if(message.equals("offline")){ // User clicked 'Browse Offline' button, display the downloaded publications.
            buildTable();
        } else {
            ((Helper) this.getApplication()).setUserId(String.valueOf(message));

            if (message.length() != 0){
                CallWebApi myTask = new CallWebApi();
                myTask.execute(message);
            }
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    private class PublicationClickListener implements TableDataClickListener<Publication>{

        @Override
        public void onDataClicked(final int rowIndex, final Publication clickedData){
            Log.i("clickListener", "acronym =" + clickedData.getAcronym());
            showTopic(clickedData.getAcronym(), clickedData.getTitle());
        }
    }


    private class PublicationLongClickListener implements TableDataLongClickListener<Publication>{

        @Override
        public boolean onDataLongClicked(int rowIndex, final Publication clickedData){
            final String acronym = "Download " + clickedData.getAcronym() + "?";
            //Toast.makeText(PublicationActivity.this, acronym, Toast.LENGTH_SHORT).show();

            AlertDialog.Builder builder = new AlertDialog.Builder(PublicationActivity.this);
            builder.setTitle("Download Publication")
                .setMessage("Begin to download " + clickedData.getAcronym() + "?")
                .setPositiveButton( "Yes",
                    new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which){
                            Log.i("PublicationActivity", "will downloading.");
                            progressBar.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                            downloadPublication(clickedData.getAcronym());
                        }
                    })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which){
                                Log.i("PublicationActivity", "cancel downloading.");
                                dialog.dismiss();
                }

            }).show();

            return true;
        }


        private void downloadPublication(String acronym){
            WebProxy myTask = new WebProxy(PublicationActivity.this, "download");
            String userId = ((Helper)PublicationActivity.this.getApplication()).getUserId();
            myTask.execute("download", acronym, userId);
        }
    }


    public void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }


    public void hideProgressBar(){
        progressBar.setVisibility(View.INVISIBLE);
    }


    public void showMessage(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT );
        toast.show();
    }


    private void showTopic(String acronym, String title){
        Intent intent = new Intent(this, TopicActivity.class);
        intent.putExtra(EXTRA_MESSAGE, acronym);
        intent.putExtra("TABLE_HEADER", title);
        startActivity(intent);
    }


    class CallWebApi extends AsyncTask<String, String, String> {

        private  Exception exception;

        protected void onPreExecute() {
            //progressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... args){
            try {
                String URL_END_POINT = getString(R.string.URL_END_POINT);
                String userId = args[0];
                URL url = new URL(URL_END_POINT + "Publications?userId=" + userId);
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

        protected void onPostExecute(String response){
            if(response == null) {
                response = "There was an error.";
            }
            buildTable(response);
        }
    }


    // build table view with SQLite data
    private void buildTable(){
        final StpTableView stpTableView = (StpTableView) findViewById(R.id.tableView);
        stpTableView.addDataClickListener(new PublicationClickListener());

        if(stpTableView != null){
            try {
                DBHandler db = new DBHandler(this);
                SQLiteDatabase stpDb = db.getReadableDatabase();
                final PublicationTableDataAdapter publicationTableDataAdapter = new PublicationTableDataAdapter(this, db.getAllPublications(stpDb), stpTableView);
                stpTableView.setDataAdapter(publicationTableDataAdapter);
            } catch (Exception e){
                Log.e("PublicationActivity", e.getMessage());
            }
        }
    }


    // Build table view with json string.
    private void buildTable(String jsonData){
        final StpTableView publicationTableView = (StpTableView) findViewById(R.id.tableView);
        publicationTableView.addDataClickListener(new PublicationClickListener());
        publicationTableView.addDataLongClickListener(new PublicationLongClickListener());

        if(publicationTableView != null){
            try {
                final PublicationTableDataAdapter publicationTableDataAdapter = new PublicationTableDataAdapter(this, DataFactory.createPublicationList(jsonData), publicationTableView);
                publicationTableView.setDataAdapter(publicationTableDataAdapter);
            } catch (JSONException e){
                Log.e("PublicationActivity", e.getMessage());
            }
        }
    }
}
