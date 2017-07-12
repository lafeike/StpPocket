package com.stpub.stppocket;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.stpub.stppocket.data.Publication;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;


/**
 * Created by i-worx on 2017-07-10.
 */

public class SortablePublicationTableView extends SortableTableView<Publication> {

    public SortablePublicationTableView(final Context context){
        this(context, null);
    }


    public SortablePublicationTableView(final Context context, final AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }


    public SortablePublicationTableView(final Context context, final AttributeSet attributes, final int styleAttributes) {
        super(context, attributes, styleAttributes);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context, "acronym", "title");
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




}
