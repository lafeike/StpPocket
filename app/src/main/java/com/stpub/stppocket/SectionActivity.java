package com.stpub.stppocket;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.stpub.stppocket.data.States;
import com.stpub.stppocket.data.WebProxy;
import com.stpub.stppocket.helper.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static android.R.attr.id;
import static android.R.id.text1;
import static com.stpub.stppocket.R.color.color_orange;

public class SectionActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    PopupWindow popupWindow;

    private View currentSelectedView;
    private Boolean firstTimeStartup = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        if(((Helper) this.getApplication()).getOffline()){
            WebProxy myTask = new WebProxy(this, "offline");
            myTask.execute("section", message);
        } else {
            if (message.length() != 0){
                WebProxy myTask = new WebProxy(this, "section");
                myTask.execute("section", message);
            }

            if(((Helper) this.getApplication()).getStatesCount() > 1) {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(showPopupWindow());
            }
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

        //get the pop-up window i.e.  drop-down layout
        final LinearLayout layoutInfoType = (LinearLayout)inflater.inflate(R.layout.popupwindow, (ViewGroup)findViewById(R.id.popupStates));
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
        ArrayList<String> arrayList = ((Helper) this.getApplication()).getStates();

        final Integer selectedState = ((Helper) this.getApplication()).getStateSelected();
        final CustomListAdapter adapter = new CustomListAdapter(this, android.R.layout.simple_list_item_1, arrayList);

        listInfoType.setAdapter(adapter);
        listInfoType.setSelection(selectedState);
        //layoutInfoType.setSelected(true);
        Log.i("Section", "set selection: " + selectedState);
        adapter.notifyDataSetChanged();

        listInfoType.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Log.i("Section", "firstTime = " + firstTimeStartup);
                if(firstTimeStartup){
                    currentSelectedView = parent.getChildAt(0);
                }
                firstTimeStartup = false;
                if (currentSelectedView != null && currentSelectedView != view) {
                    adapter.unhighlightCurrentRow(currentSelectedView);
                    Log.i("section", "unhighlight current row.");
                }


                currentSelectedView = view;
                adapter.highlightCurrentRow(currentSelectedView);

                Animation animation = new AlphaAnimation(0.3f, 1.0f);
                animation.setDuration(4000);
                view.startAnimation(animation);
                setStateSelected(position);
                adapter.setState(position);

                popupWindow.dismiss();
            }
        });
    }





    public void setStateSelected(Integer i){
        ((Helper) this.getApplication()).setStateSelected(i);
    }





}