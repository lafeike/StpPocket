package com.stpub.stppocket;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import com.stpub.stppocket.data.DataFactory;
import com.stpub.stppocket.data.Publication;

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

public class PublicationActivity extends AppCompatActivity {
    TableLayout tableLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        Log.i("INFO", "UserId: " + message);

        if (message.length() != 0){
            myToolbar.setTitle(message);
            setSupportActionBar(myToolbar);
            CallWebApi myTask = new CallWebApi();
            myTask.execute(message);
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
            final String acronym = "click:" + clickedData.getAcronym();
            Toast.makeText(PublicationActivity.this, acronym, Toast.LENGTH_SHORT).show();
        }
    }


    private class PublicationLongclickListener implements TableDataLongClickListener<Publication>{

        @Override
        public boolean onDataLongClicked(int rowIndex, final Publication clickedData){
            final String acronym = "Download " + clickedData.getAcronym() + "?";
            Toast.makeText(PublicationActivity.this, acronym, Toast.LENGTH_SHORT).show();
            return true;
        }
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
            //progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            buildTable(response);
        }
    }

    private void buildTable(String jsonData){
        final SortablePublicationTableView publicationTableView = (SortablePublicationTableView) findViewById(R.id.tableView);
        publicationTableView.addDataClickListener(new PublicationClickListener());
        publicationTableView.addDataLongClickListener(new PublicationLongclickListener());

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
