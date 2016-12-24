package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * Created by ramkrishna on 18/12/16.
 */

public class ListProvider implements RemoteViewsService.RemoteViewsFactory {

    private Context context = null;
    private int appWidgetId;
    Cursor cursor;

    public ListProvider(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        cursor = context.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
          new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
          QuoteColumns.ISCURRENT + " = ?",
          new String[]{"1"},
          null);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        if(cursor != null && cursor.moveToPosition(position)) {
            remoteViews.setTextViewText(R.id.stock_symbol, cursor.getString(cursor.getColumnIndexOrThrow(QuoteColumns.SYMBOL)));
            remoteViews.setTextViewText(R.id.bid_price, cursor.getString(cursor.getColumnIndexOrThrow(QuoteColumns.BIDPRICE)));
            if (Utils.showPercent){
                remoteViews.setTextViewText(R.id.change, cursor.getString(cursor.getColumnIndexOrThrow(QuoteColumns.PERCENT_CHANGE)));
            } else{
                remoteViews.setTextViewText(R.id.change, cursor.getString(cursor.getColumnIndexOrThrow(QuoteColumns.CHANGE)));
            }
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        cursor.moveToPosition(position);
        return cursor.getLong(cursor.getColumnIndexOrThrow(QuoteColumns._ID));
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
