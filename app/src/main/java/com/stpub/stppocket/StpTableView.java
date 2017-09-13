package com.stpub.stppocket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;


/**
 * Created by i-worx on 2017-07-10.
 */

public class StpTableView extends SortableTableView {
    public static final String TAG = "StpTableView";

    public StpTableView(final Context context){
        this(context, null);
    }


    public StpTableView(final Context context, final AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }


    public StpTableView(final Context context, final AttributeSet attributes, final int styleAttributes) {
        super(context, attributes, styleAttributes);

        final String activityName = context.getClass().getSimpleName();

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, getHeader(activityName, context));

        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(context, R.color.table_header_text));
        setHeaderAdapter(simpleTableHeaderAdapter);

        final int rowColor = ContextCompat.getColor(context, R.color.table_data_row);
        setDataRowBackgroundProvider(TableDataRowBackgroundProviders.similarRowColor(rowColor));
        setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        final TableColumnWeightModel tableColumnWeightModel = new TableColumnWeightModel(1);
        tableColumnWeightModel.setColumnWeight(0, 5);
        //tableColumnWeightModel.setColumnWeight(1, 3);
        setColumnModel(tableColumnWeightModel);
    }


    private String getHeader(String activityName, final Context context){
        String headerTxt = null;

        if (activityName.equals("PublicationActivity")){
                headerTxt = "Select a publication below";
        } else {
            Activity activity = (Activity) context;
            Intent intent = activity.getIntent();
            headerTxt = intent.getStringExtra("TABLE_HEADER");
        }

        return headerTxt;
    }
}
