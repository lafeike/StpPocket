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
import com.stpub.stppocket.data.TableData;
import com.stpub.stppocket.data.Topic;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import de.codecrafters.tableview.listeners.TableDataClickListener;

public class RulebookActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TABLE_HEADER = "TABLE_HEADER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rulebook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        Log.i("INFO", "TopicKey: " + message);

        if (message.length() != 0){
            //myToolbar.setTitle(message);

            WebProxy myTask = new WebProxy(this, "rulebook");
            myTask.execute("rulebook", message);
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
