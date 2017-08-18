package com.stpub.stppocket;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.stpub.stppocket.data.DBHandler;
import com.stpub.stppocket.data.Publication;
import com.stpub.stppocket.data.States;
import com.stpub.stppocket.data.Topic;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.listeners.TableDataClickListener;

public class TopicActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static final String TABLE_HEADER = "TABLE_HEADER"; // Topic selected will show on the table header of rulebook.
    PopupWindow popupWindow;

   // private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("DEBUG", "TopicActivity started.");
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String message = intent.getStringExtra(PublicationActivity.EXTRA_MESSAGE);
        String tableHeader = intent.getStringExtra(PublicationActivity.TABLE_HEADER);
        intent.putExtra(TABLE_HEADER, tableHeader);

        setContentView(R.layout.activity_topic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        if(((Helper)this.getApplication()).getOffline()){
            WebProxy myTask = new WebProxy(this, "offline");
            myTask.execute("topic", message);
        }else{
            if (message.length() != 0){
                //myToolbar.setTitle(message);
                String userId = ((Helper) this.getApplication()).getUserId();
                WebProxy myTask = new WebProxy(this, "topic");
                myTask.execute("topic", message, userId);
            }
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if(message.equals("EAF") || message.equals("OF")){
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }

        fab.setOnClickListener(showPopupWindow());
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

        //get the pop-up window i.e.  drop-down layout
        LinearLayout layoutInfoType = (LinearLayout)inflater.inflate(R.layout.popupwindow, (ViewGroup)findViewById(R.id.popupStates));
        popupWindow = new PopupWindow(layoutInfoType, Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT, true);


       // popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), canvasBitmap));
        popupWindow.setTouchable(true);

        //let pop-up be informed about touch events outside its window. This  should be done before setting the content of pop-up
        popupWindow.setOutsideTouchable(true);
        popupWindow.setHeight(Toolbar.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);

        //dismiss the pop-up i.e. drop-down when touched anywhere outside the pop-up
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                Log.i("topic", "touched 0.");

                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                    return true;
                }
                return false;
            }
        });





        //provide the source layout for drop-down
        popupWindow.setContentView(layoutInfoType);

        //populate the drop-down list
        final ListView listInfoType = (ListView) layoutInfoType.findViewById(R.id.dropDownStatesList);
        List<String> arrayList = ((Helper) this.getApplication()).getStates();
        final Integer selectedState = getState();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, arrayList);
        List<States> rowItem = new ArrayList<States>();
        final Integer checkedState = selectedState;
        for(int i = 0; i < arrayList.size(); i++){
            States st = new States(arrayList.get(i));
            if( i == selectedState) {
                st.setSelected(true);
                Log.i("Topic", "No." + i + " is checked.");
            }
            rowItem.add(st);
        }
        CustomListActivity adapter = new CustomListActivity(this, rowItem);

        listInfoType.setAdapter(adapter);

        listInfoType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("topic", "touched 2.");
                if(checkedState != getState()) {
                    Log.i("topic", "state changed.");
                    popupWindow.dismiss();

                    return true;
                } else {
                    Log.i("topic", "state not change.");
                    return false;
                }
            }
        });

    }

    private Integer getState(){
        return ((Helper) this.getApplication()).getStateSelected();
    }




}
