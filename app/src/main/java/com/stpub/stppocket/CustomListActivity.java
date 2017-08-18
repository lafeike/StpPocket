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
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.stpub.stppocket.data.States;
import com.stpub.stppocket.helper.Helper;

import java.util.Calendar;
import java.util.List;


/**
 * Created by i-worx on 2017-08-17.
 */

public class CustomListActivity extends BaseAdapter {

    Context context;
    List<States> rowItem;
    View listView;
    boolean checkState[];

    ViewHolder holder;

    public CustomListActivity(Context context, List<States> rowItem) {
        this.context = context;
        this.rowItem = rowItem;
        checkState = new boolean[rowItem.size()];

        for(int i = 0; i < rowItem.size(); i++){
            if(rowItem.get(i).getSelected())
                checkState[i] = true;
        }
    }

    @Override
    public int getCount() {
        return rowItem.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItem.indexOf(getItem(position));
    }

    public class ViewHolder {
        TextView tvItemName;
        CheckBox check;
    }

    @Override
    public View getView(final int position, final View view, ViewGroup parent) {

        holder = new ViewHolder();
        final States itm = rowItem.get(position);
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (view == null) {

            listView = new View(context);
            listView = layoutInflater.inflate(R.layout.drop_down_states_list,
                    parent, false);

            holder.tvItemName = (TextView) listView
                    .findViewById(R.id.selectStates);
            holder.check = (CheckBox) listView.findViewById(R.id.checkboxInfo);
            listView.setTag(holder);

        } else {
            listView = (View) view;
            holder = (ViewHolder) listView.getTag();
        }

        holder.tvItemName.setText(itm.getItems());
        holder.check.setChecked(checkState[position]);
        
        holder.check.setOnTouchListener(new View.OnTouchListener(){

            public boolean onTouch(View v, MotionEvent event) {
                Log.d("dropdown", "check is touched.");
                for (int i = 0; i < checkState.length; i++) {
                    if (i == position) {
                        checkState[i] = true;
                        setState(i);
                    } else {
                        checkState[i] = false;
                    }
                }
                notifyDataSetChanged();
                return false;
            }                           }
        );

        return listView;
    }

    private void setState(Integer checked){
        ((Helper) context.getApplicationContext()).setStateSelected(checked);
    }
}
