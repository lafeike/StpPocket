package com.stpub.stppocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.stpub.stppocket.data.DBHandler;
import com.stpub.stppocket.data.MyTableDataAdapter;
import com.stpub.stppocket.data.ParaTableDataAdapter;
import com.stpub.stppocket.data.States;
import com.stpub.stppocket.data.TableData;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.listeners.TableDataClickListener;

public class ParagraphActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    PopupWindow popupWindow;

    private View currentSelectedView;
    private Boolean firstTimeStartup = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paragraph);
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if(((Helper) this.getApplication()).getOffline()) {
            buildTableFromDb(message);
        } else {
            if(((Helper) this.getApplication()).getStatesCount() > 1) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(showPopupWindow());
            }
            callWebApi(message);
        }
    }


    private void callWebApi(String value){
        if(value.length() != 0){
            WebProxy myTask = new WebProxy(this, "paragraph");
            myTask.execute("paragraph", value);
        }
    }


    private View.OnClickListener showPopupWindow(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiatePopUpInfoType();
                popupWindow.showAsDropDown(v, 0, 0);
            }
        };
    }


    private void initiatePopUpInfoType(){
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //get the pop-up window
        final LinearLayout layoutInfoType = (LinearLayout)inflater.inflate(R.layout.popupwindow, (ViewGroup)findViewById(R.id.popupStates));
        popupWindow = new PopupWindow(layoutInfoType, Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(Toolbar.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                } else {
                    return false;
                }
            }
        });

        //provide the source layout for drop-down
        popupWindow.setContentView(layoutInfoType);

        //populate the drop-down list
        final ListView listInfoType = (ListView) layoutInfoType.findViewById(R.id.dropDownStatesList);
        ArrayList<String> arrayList = ((Helper) this.getApplication()).getStates();

        final Integer selectedState = ((Helper) this.getApplication()).getStateSelected();
        final CustomListAdapter adapter = new CustomListAdapter(this, android.R.layout.simple_list_item_1, arrayList);

        listInfoType.setAdapter(adapter);
        listInfoType.setSelection(selectedState);

        listInfoType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                boolean flagStateChanged = isStateChanged(position);

                if(firstTimeStartup){
                    currentSelectedView = parent.getChildAt(0);
                }
                firstTimeStartup = false;
                if (currentSelectedView !=null && currentSelectedView != view) {
                    adapter.unhighlightCurrentRow(currentSelectedView);
                }

                currentSelectedView = view;
                adapter.highlightCurrentRow(currentSelectedView);

                Animation animation = new AlphaAnimation(0.3f, 1.0f);
                animation.setDuration(4000);
                view.startAnimation(animation);
                setStateSelected(position);
                adapter.setState(position);

                if(flagStateChanged){ // If the state is changed, call API to refresh paragraph.
                    callWebApi(getIntent().getStringExtra(EXTRA_MESSAGE));
                }

                popupWindow.dismiss();
            }
        });
    }


    private boolean isStateChanged(int selectedState){
        return selectedState != ((Helper) this.getApplication()).getStateSelected();
    }


    public void setStateSelected(Integer i){
        ((Helper) this.getApplication()).setStateSelected(i);
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
            } finally {
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
                tableDataAdapter.setRowClicked(rowIndex);

                tableDataAdapter.notifyDataSetChanged();
            }
        }
    }
}
