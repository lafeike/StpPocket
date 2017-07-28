package com.stpub.stppocket.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;
import java.util.List;

import de.codecrafters.tableview.TableView;

/**
 * Created by i-worx on 2017-07-25.
 */

public class ParaTableDataAdapter extends MyTableDataAdapter {

    public ParaTableDataAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView){
        super(context, data, tableView);
    }


    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        String[] sdType = {"Audit", "Applicability", "External", "Info"};

        View renderView = null;
        Paragraph tableData;
        if(rowIndex == 0){
            tableData = (Paragraph) getRowData(0);
            if (tableData.getTitle().equals("Mock")){
                tableData = (Paragraph) getRowData(1); // skip the first row, if it just contains fake data.
            }
            renderView = renderString(tableData.getParaNum() + " " + tableData.getQuestion() + "\n\n" + tableData.getGuideNote());
        } else {
            tableData = (Paragraph) getRowData(rowIndex );
            renderView = renderString(tableData.getParaNum() + ": " + tableData.getTitle());
        }

        return renderView;
    }


    public void refreshEvents(){
        notifyDataSetChanged();
    }
}
