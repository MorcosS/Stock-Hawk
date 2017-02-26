package com.udacity.stockhawk.Widget;

import android.app.LauncherActivity;
import android.app.LauncherActivity.ListItem;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.DbHelper;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.ui.Detail_Activity;

import java.util.ArrayList;

import static android.R.style.Widget;

/**
 * Created by MorcosS on 2/14/17.
 */

public class WidgetItemFactory implements RemoteViewsService.RemoteViewsFactory {
    private ArrayList listItemList = new ArrayList();
    private Context context = null;
    private int appWidgetId;
    Cursor cursor;


    public WidgetItemFactory(Context context, Intent intent) {
        this.context = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        populateListItem();
    }

    private void populateListItem() {
        for (int i = 0; i < 10; i++) {

        }

    }

    @Override
    public void onCreate() {
        initCursor();
        if (cursor != null) {
            cursor.moveToFirst();
        }
    }

    @Override
    public void onDataSetChanged() {
        initCursor();
    }


    private void initCursor(){
        if (cursor != null) {
            cursor.close();
        }
        final long identityToken = Binder.clearCallingIdentity();
        /**This is done because the widget runs as a separate thread
         when compared to the current app and hence the app's data won't be accessible to it
         because I'm using a content provided **/
        cursor = context.getContentResolver().query(
                Contract.Quote.URI,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        cursor.close();
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */
    @Override
    public RemoteViews getViewAt(int i) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_item_quote);
        cursor.moveToPosition(i);
        remoteViews.setTextViewText(R.id.symbol,cursor.getString(Contract.Quote.POSITION_SYMBOL));
        remoteViews.setTextViewText(R.id.price,cursor.getFloat(Contract.Quote.POSITION_PRICE)+"");
        float rawAbsoluteChange = cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);
        remoteViews.setTextViewText(R.id.change,cursor.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE)+"");
        Bundle infos = new Bundle();
        infos.putInt("Symbol", i);
        final Intent activityIntent = new Intent();
        activityIntent.putExtras(infos);
        remoteViews.setOnClickFillInIntent(R.id.list_item, activityIntent);
        if (rawAbsoluteChange > 0) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
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
}