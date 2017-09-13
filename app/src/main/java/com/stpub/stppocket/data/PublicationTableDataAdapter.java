package com.stpub.stppocket.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stpub.stppocket.data.DBHandler;


import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

/**
 * Created by i-worx on 2017-07-11.
 */

public class PublicationTableDataAdapter extends MyTableDataAdapter {

    private static final int TEXT_SIZE = 15;


    public PublicationTableDataAdapter(final Context context, final List<TableData> data, final TableView<TableData> tableView){
        super(context, data, tableView);
    }


    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final  TableData publication = getRowData(rowIndex);
        View renderedView = null;
        if (isPublicationInLocal(publication.getKey())){ // if the publication has been downloaded in local DB, show it in BLACK font.
            renderedView = renderString(publication.getKey() + ": " + publication.getTitle(), true);
        } else {
            renderedView = renderString(publication.getKey() + ": " + publication.getTitle(), false);
        }


        return renderedView;
    }


    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final TableData publication = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex){
            case 0:
                renderedView = renderString(publication.getKey() + ": " + publication.getTitle(), false);
                break;
            default:
                renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);
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


    public View renderString(final String value, boolean inLocalDb){
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 25, 20, 25);
        textView.setTextSize(TEXT_SIZE);

        if (inLocalDb) {
            textView.setTextColor(Color.BLACK);
        }

        return textView;
    }



}
