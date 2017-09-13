package com.stpub.stppocket;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.stpub.stppocket.data.DBHandler;
import com.stpub.stppocket.data.PublicationTableDataAdapter;
import com.stpub.stppocket.data.TableData;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.listeners.TableDataLongClickListener;

public class PublicationActivity extends AppCompatActivity {
    ProgressBar progressBar;
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TABLE_HEADER = "TABLE_HEADER"; // Publication title selected will show on the table header of Topic.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publication);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        if(message.equals("offline")){ // User clicked 'Browse Offline' button, display the downloaded publications.
            buildTable();
        } else {
            ((Helper) this.getApplication()).setUserId(String.valueOf(message));

            if (message.length() != 0){
                WebProxy myTask = new WebProxy(this, "publication");
                myTask.execute("publication", message);
            }
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
*/

    private class PublicationClickListener implements TableDataClickListener<TableData>{

        @Override
        public void onDataClicked(final int rowIndex, final TableData clickedData){
            showTopic(clickedData.getKey(), clickedData.getTitle());
        }
    }


    private void showTopic(String acronym, String title){
        Intent intent = new Intent(this, TopicActivity.class);
        intent.putExtra(EXTRA_MESSAGE, acronym);
        intent.putExtra("TABLE_HEADER", title);
        startActivity(intent);
    }


    public class PublicationLongClickListener implements TableDataLongClickListener<TableData>{

        @Override
        public boolean onDataLongClicked(int rowIndex, final TableData clickedData){
            //final String acronym = "Download " + clickedData.getAcronym() + "?";

            AlertDialog.Builder builder = new AlertDialog.Builder(PublicationActivity.this);
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


    // build table view with SQLite data
    void buildTable(){
        StpTableView stpTableView = findViewById(R.id.tableView);
        if(stpTableView != null){
            try {
                stpTableView.addDataClickListener(new PublicationClickListener());
                DBHandler db =  DBHandler.getInstance(this);
                SQLiteDatabase stpDb = db.getReadableDatabase();
                final PublicationTableDataAdapter publicationTableDataAdapter = new PublicationTableDataAdapter(this, db.getAllPublications(stpDb), stpTableView);
                stpTableView.setDataAdapter(publicationTableDataAdapter);
            } catch (Exception e){
                Log.e("PublicationActivity", e.getMessage());
            }
        }
    }
}