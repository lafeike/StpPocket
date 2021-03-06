package com.stpub.stppocket.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.stpub.stppocket.R;
import com.stpub.stppocket.helper.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.codecrafters.tableview.TableView;

/**
 * Created by i-worx on 2017-07-25.
 */

public class ParaTableDataAdapter extends MyTableDataAdapter {

    public Context context;

    public ParaTableDataAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView){

        super(context, data, tableView);
        this.context = context;
    }


    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        String[] sdType = {"Audit", "Applicability", "External", "Info"};
        String state;

        View renderView = null;
        Paragraph tableData;

        final int selectedState = ((Helper) context.getApplicationContext()).getStateSelected();
        ArrayList<String> stateList = ((Helper) context.getApplicationContext()).getStates();
        if(stateList.size() != 0){
            state = ((Helper) context.getApplicationContext()).getStates().get(selectedState);
        } else {
            state = "";
        }

        if(rowIndex == 0){
            tableData = (Paragraph) getRowData(0);
            if (tableData.getTitle().equals("Mock")){
                tableData = (Paragraph) getRowData(1); // skip the first row, if it just contains fake data.
            }

            if (Arrays.asList(sdType).contains(tableData.getQuestion())){
                renderView = renderHtml("<b>" + state + "</b><br>" + tableData.getGuideNote());
                //renderView.setAlpha(0.6f);
            } else {
                renderView = renderHtml(tableData.getParaNum() + " " + tableData.getQuestion() + "<br><br>" + tableData.getGuideNote());
            }
        } else {
            tableData = (Paragraph) getRowData(rowIndex );

            if (Arrays.asList(sdType).contains(tableData.getQuestion())){
                renderView = renderString(state + "-" + tableData.getParaNum() + ": " + tableData.getTitle()
                    + "(" + tableData.getQuestion() + ")", true);
            } else {
                renderView = renderString(tableData.getParaNum() + ": " + tableData.getTitle(), false);
            }
            if(rowIndex == rowClicked){
                renderView.setBackgroundColor(getResources().getColor(R.color.color_orange));
            }
        }

        return renderView;
    }

    // if it is state difference, display it it in different color.
    public View renderHtml(final String value) {
        final WebView textView = new WebView(getContext());
        textView.getSettings().setJavaScriptEnabled(true);
        textView.loadData(value, "text/html", null);

        return textView;
    }


}
