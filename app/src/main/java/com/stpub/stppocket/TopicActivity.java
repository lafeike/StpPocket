package com.stpub.stppocket;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.stpub.stppocket.data.Publication;
import com.stpub.stppocket.data.Topic;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import de.codecrafters.tableview.listeners.TableDataClickListener;

public class TopicActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = null;
    public static final String TABLE_HEADER = "TABLE_HEADER"; // Topic selected will show on the table header of rulebook.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "TopicActivity started.");
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String message = intent.getStringExtra(PublicationActivity.EXTRA_MESSAGE);
        String tableHeader = intent.getStringExtra(PublicationActivity.TABLE_HEADER);
        Log.i("TopicActivity", "tableHeader set: " + tableHeader);
        intent.putExtra(TABLE_HEADER, tableHeader);

        setContentView(R.layout.activity_topic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);



        if (message.length() != 0){
            //myToolbar.setTitle(message);
            String userId = ((Helper) this.getApplication()).getUserId();
            WebProxy myTask = new WebProxy(this, "topic");
            myTask.execute("topic", message, userId);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
