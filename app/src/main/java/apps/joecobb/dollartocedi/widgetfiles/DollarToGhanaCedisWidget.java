package apps.joecobb.dollartocedi.widgetfiles;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import apps.joecobb.dollartocedi.R;
import hivatec.ir.easywebservice.Callback;
import hivatec.ir.easywebservice.EasyWebservice;
import hivatec.ir.easywebservice.Method;

/**
 * Implementation of App Widget functionality.
 */
public class DollarToGhanaCedisWidget extends AppWidgetProvider {
    private static final String MyOnClick = "myOnClickTag";

    protected static PendingIntent getPendingSelfIntent(Context context) {
        Intent intent = new Intent(context, DollarToGhanaCedisWidget.class);
        intent.setAction(DollarToGhanaCedisWidget.MyOnClick);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    static void updateAppWidget(Context context, final AppWidgetManager appWidgetManager,
                                final int appWidgetId) {


        // Construct the RemoteViews object
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.usd_to_cedis_widget);


        views.setOnClickPendingIntent(R.id.refresh,
                getPendingSelfIntent(context));

        new EasyWebservice("https://free.currencyconverterapi.com/api/v6/convert?q=USD_GHS&compact=ultra&apiKey=bd3a5493764339e37f01")
                .method(Method.GET) //default
                .call(new Callback.AB<Boolean, String>("res", "USD_GHS") { //should map response params
                    @Override
                    public void onSuccess(Boolean res, String USD_GHS) {
                        Log.d("TheResponse:", String.valueOf(USD_GHS));
                        views.setTextViewText(R.id.appwidget_text, "USD 1 = GHS " + String.valueOf(Math.round(Double.parseDouble(USD_GHS) * 100.0) / 100.0));
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                        //you can work with res and msg which are in json response
                    }

                    @Override
                    public void onError(String error) {
                        Log.d("TheResponseErr:", error);
                        //if any error encountered
                    }
                });
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
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

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context,intent);

        if (MyOnClick.equals(intent.getAction())) {
            final RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.usd_to_cedis_widget);
            views.setViewVisibility(R.id.progressBar, View.VISIBLE);
            views.setViewVisibility(R.id.refresh, View.GONE);

            AppWidgetManager.getInstance(context).updateAppWidget(
                    new ComponentName(context, DollarToGhanaCedisWidget.class),views);
            new EasyWebservice("https://free.currencyconverterapi.com/api/v6/convert?q=USD_GHS&compact=ultra&apiKey=bd3a5493764339e37f01")
                    .method(Method.GET) //default
                    .call(new Callback.AB<Boolean, String>("res", "USD_GHS") { //should map response params
                        @Override
                        public void onSuccess(Boolean res, String USD_GHS) {
                            Log.d("TheResponse:", String.valueOf(USD_GHS));
                            views.setViewVisibility(R.id.progressBar, View.GONE);
                            views.setViewVisibility(R.id.refresh, View.VISIBLE);
                            views.setTextViewText(R.id.appwidget_text, "USD 1 = GHS " + String.valueOf(Math.round(Double.parseDouble(USD_GHS) * 100.0) / 100.0));
                            AppWidgetManager.getInstance(context).updateAppWidget(
                                    new ComponentName(context, DollarToGhanaCedisWidget.class),views);
                            Toast.makeText(context, "updated",Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(String error) {
                            views.setViewVisibility(R.id.progressBar, View.GONE);
                            views.setViewVisibility(R.id.refresh, View.VISIBLE);
                            AppWidgetManager.getInstance(context).updateAppWidget(
                                    new ComponentName(context, DollarToGhanaCedisWidget.class),views);
                            Log.d("TheResponseErr:", error);
                            //if any error encountered
                        }
                    });
        }
    }
}

