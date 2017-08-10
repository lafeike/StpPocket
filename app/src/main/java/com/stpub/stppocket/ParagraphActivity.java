package com.stpub.stppocket;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.stpub.stppocket.data.DBHandler;
import com.stpub.stppocket.data.MyTableDataAdapter;
import com.stpub.stppocket.data.ParaTableDataAdapter;
import com.stpub.stppocket.data.TableData;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import de.codecrafters.tableview.listeners.TableDataClickListener;

public class ParagraphActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TABLE_HEADER = "TABLE_HEADER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paragraph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        if(((Helper) this.getApplication()).getOffline()) {
            buildTableFromDb(message);
        } else{
            if(message.length() != 0){
                WebProxy myTask = new WebProxy(this, "paragraph");
                myTask.execute("paragraph", message);
            }
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


    private void buildTableFromDb(String value){
        final StpTableView stpTableView = (StpTableView) findViewById(R.id.tableView);

        if(stpTableView != null){
            DBHandler db = new DBHandler(this);
            try {
                SQLiteDatabase stpDb = db.getReadableDatabase();
                MyTableDataAdapter tableDataAdapter = new ParaTableDataAdapter(this, db.getParagraph(stpDb, value), stpTableView);
                stpTableView.setDataAdapter(tableDataAdapter);
                stpTableView.addDataClickListener(new MyTableClickListener());
            } catch (Exception e){
                Log.e("ParagraphActivity", e.getMessage());
                e.printStackTrace();
            }finally {
                db.close();
            }
        }
    }


    private class MyTableClickListener implements TableDataClickListener<TableData> {

        @Override
        public void onDataClicked(final int rowIndex, final TableData clickedData) {
            // Paragraph row was clicked, just refresh the table view;
            // Otherwise arouse a new activity to display.
            if (rowIndex == 0) {
                // Ignore it if the first row is clicked.
            } else {
                // Needs to refresh the table.
                final StpTableView myTableView = (StpTableView) findViewById(R.id.tableView);
                ParaTableDataAdapter tableDataAdapter = (ParaTableDataAdapter) myTableView.getDataAdapter();

                // Replace the first row with the click row.
                tableDataAdapter.remove(tableDataAdapter.getItem(0));
                tableDataAdapter.insert(clickedData, 0);
                tableDataAdapter.notifyDataSetChanged();
            }
        }
    }
}
