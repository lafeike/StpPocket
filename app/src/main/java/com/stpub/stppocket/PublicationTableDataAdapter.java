package com.stpub.stppocket;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stpub.stppocket.data.Publication;

import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.LongPressAwareTableDataAdapter;

/**
 * Created by i-worx on 2017-07-11.
 */

public class PublicationTableDataAdapter extends LongPressAwareTableDataAdapter<Publication> {

    private static final int TEXT_SIZE = 14;


    public PublicationTableDataAdapter(final Context context, final List<Publication> data, final TableView<Publication> tableView){
        super(context, data, tableView);
    }


    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final  Publication publication = getRowData(rowIndex);
        View renderedView = null;

        renderedView = renderString(publication.getAcronym() + ": " + publication.getTitle());


        return renderedView;
    }


    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView){
        final Publication pubs = getRowData(rowIndex);
        View renderedView = null;

        switch (columnIndex){
            case 0:
                renderedView = renderAcronym(pubs);
                break;
            default:
                renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);
        }

        return renderedView;
    }


    private View renderAcronym(final Publication publication){
        return renderString(publication.getAcronym());
    }


    private View renderTitle(final Publication publication){
        return renderString(publication.getTitle());
    }


    private View renderString(final String value){
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(20, 10, 20, 10);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }



}
