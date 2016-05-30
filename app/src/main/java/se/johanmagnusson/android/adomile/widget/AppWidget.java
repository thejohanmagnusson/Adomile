package se.johanmagnusson.android.adomile.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import se.johanmagnusson.android.adomile.MainActivity;
import se.johanmagnusson.android.adomile.R;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {

    private static final String TAG = AppWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId,
                                String mileage, String totalPrivate, String totalWork) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setTextViewText(R.id.total_mileage_private, totalPrivate);
        views.setContentDescription(R.id.total_mileage_private, totalPrivate);
        views.setTextViewText(R.id.total_mileage_work, totalWork);
        views.setContentDescription(R.id.total_mileage_private, totalWork);
        views.setTextViewText(R.id.mileage, mileage);
        views.setContentDescription(R.id.mileage, mileage);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if(intent.getAction().equals(MainActivity.ACTION_WIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));

            onUpdate(context, appWidgetManager, appWidgetIds);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.pref_summary_file_key), Context.MODE_PRIVATE);
        int inbound = prefs.getInt(context.getString(R.string.pref_inbound_key), 0);
        int outbound = prefs.getInt(context.getString(R.string.pref_outbound_key), 0);
        int privateMileage = prefs.getInt(context.getString(R.string.pref_total_private_key), 0);
        int workMileage = prefs.getInt(context.getString(R.string.pref_total_work_key), 0);

        String mileage =  String.format(context.getResources().getString(R.string.in_out_mileage), inbound, outbound);
        String totalPrivate = String.format(context.getResources().getString(R.string.trip_mileage_summary), privateMileage);
        String totalWork = String.format(context.getResources().getString(R.string.trip_mileage_summary), workMileage);

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, mileage, totalPrivate, totalWork);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

