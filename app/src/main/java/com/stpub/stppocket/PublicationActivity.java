package com.stpub.stppocket;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TableLayout;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.stpub.stppocket.R.id.progressBar;

public class PublicationActivity extends AppCompatActivity {
    TableLayout tableLayout;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);



        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        if (message.length() != 0){
            myToolbar.setTitle(message);
            setSupportActionBar(myToolbar);
            CallWebApi myTask = new CallWebApi();
            myTask.execute(message);
        }
    }

    class CallWebApi extends AsyncTask<String, String, String> {

        private  Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
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
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
        }


    }

    private void buildTable(){
        try {

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
