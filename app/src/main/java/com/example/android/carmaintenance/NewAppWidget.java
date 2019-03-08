package com.example.android.carmaintenance;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private String userUid;
    private int distance;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int i=0; i<appWidgetIds.length; i++) {
            Intent intent=new Intent(context, WidgetService.class);

            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);

            intent.putExtra("distance",distance);
            intent.putExtra("username",userUid);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews widget=new RemoteViews(context.getPackageName(),
                    R.layout.new_app_widget);

            widget.setRemoteAdapter( R.id.listViewWidget,
                    intent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], widget);

        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        ComponentName componentName = new ComponentName(context.getApplicationContext(), NewAppWidget.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        super.onReceive(context, intent);
        String strAction = intent.getAction();
        distance = intent.getIntExtra("distance",1);
        userUid = intent.getStringExtra("username");

        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(strAction)) {

            onUpdate(context,appWidgetManager,appWidgetIds);
        }
    }
}

