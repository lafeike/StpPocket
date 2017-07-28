package com.stpub.stppocket.data;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

/**
 * Created by i-worx on 2017-07-17.
 */

public class MyTableDataAdapter extends LongPressAwareTableDataAdapter<TableData> {
    private static final int TEXT_SIZE = 14;


    public MyTableDataAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView){
        super(context, data, tableView);
    }


    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final  TableData tableData = getRowData(rowIndex);
        View renderedView = null;

        renderedView = renderString(tableData.getTitle());

        return renderedView;
    }


    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final TableData tableData = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex){
            case 0:
                renderedView = renderTitle(tableData);
                break;
            default:
                renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);
        }

        return renderedView;
    }


    private View renderTitle(final TableData tableData){
        return renderString(tableData.getTitle());
    }


    public View renderString(final String value){
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }
}
