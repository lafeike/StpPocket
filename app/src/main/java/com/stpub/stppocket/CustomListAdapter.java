package com.stpub.stppocket;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.stpub.stppocket.data.States;
import com.stpub.stppocket.helper.Helper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by i-worx on 2017-08-17.
 */

public class CustomListAdapter extends ArrayAdapter<String> {
    int layoutResourceId;
    boolean firstTimeStartup;

    public CustomListAdapter(Context context, int textViewResourceId,
                       ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        layoutResourceId = textViewResourceId;
    }

    public void setFirstTimeStartup(boolean firstTimeStartup){
        this.firstTimeStartup = firstTimeStartup;
    }


    public boolean getFirstTimeStartup(){
        return firstTimeStartup;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    layoutResourceId, null);
        }

        if (firstTimeStartup && position == 0) {
            highlightCurrentRow(convertView);
        } else {
            unhighlightCurrentRow(convertView);
        }

        if(position == getState()){
            highlightCurrentRow(convertView);
        } else {
            unhighlightCurrentRow(convertView);
        }

        TextView title = (TextView) convertView
                .findViewById(android.R.id.text1);
        title.setText(getItem(position));
        return convertView;
    }


    public void highlightCurrentRow(View rowView){
        rowView.setBackgroundColor(getContext().getResources().getColor(R.color.color_orange, null));
    }


    public void unhighlightCurrentRow(View rowView){
        rowView.setBackgroundColor(Color.TRANSPARENT);
    }


    public void setState(Integer checked){
        ((Helper) getContext().getApplicationContext()).setStateSelected(checked);
    }


    public Integer getState(){
        return ((Helper) getContext().getApplicationContext()).getStateSelected();
    }

}