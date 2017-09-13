package com.stpub.stppocket.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stpub.stppocket.R;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

/**
 * Created by i-worx on 2017-07-17.
 */

public class MyTableDataAdapter extends LongPressAwareTableDataAdapter<TableData> {
    private static final int TEXT_SIZE = 15;
    public int rowClicked = 0;


    public MyTableDataAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView){
        super(context, data, tableView);
    }


    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final  TableData tableData = getRowData(rowIndex);
        View renderedView = null;

        renderedView = renderString(tableData.getTitle(), false);
        rowClicked = getRowClicked();

//        Log.i("Adapter", "row clicked = " + rowClicked);
        if(rowIndex != 0 && rowIndex == rowClicked){
            renderedView.setBackgroundColor(Color.YELLOW);
        }

        return renderedView;
    }


    private boolean isPublicationInLocal(String acronym){
        DBHandler db = DBHandler.getInstance(getContext());
        SQLiteDatabase stpDb = db.getReadableDatabase();
        if(db.getPublication(stpDb, acronym) != null){
            return true;
        } else {
            return false;
        }
    }



    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final TableData tableData = getRowData(rowIndex);
        View renderedView = null;

        Log.i("Adapter", "long press.");

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
        return renderString(tableData.getTitle(), false);
    }

    // if it is state difference, display it it in different color.
    public View renderString(final String value, boolean isStateDiff){
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 25, 20, 25);
        textView.setTextSize(TEXT_SIZE);

        if(isStateDiff){
            textView.setTextColor(Color.BLACK);
        }

        return textView;
    }




    public void setRowClicked(final int rowIndex){
        this.rowClicked = rowIndex;
        Log.i("Adapter", "set row clicked: " + rowIndex);
    }


    public int getRowClicked(){
        return rowClicked;
    }
}
