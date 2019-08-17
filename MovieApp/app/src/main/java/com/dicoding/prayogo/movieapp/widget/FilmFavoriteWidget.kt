package com.dicoding.prayogo.movieapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.dicoding.prayogo.movieapp.R

class FilmFavoriteWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(
                context,
                appWidgetManager,
                appWidgetId
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if(intent.action!=null){
            if(intent.action == TOAST_ACTION){
                val viewName=intent.getStringExtra(EXTRA_ITEM)
                Toast.makeText(context, viewName,Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    companion object {
        const val TOAST_ACTION = "com.dicoding.prayogo.TOAST_ACTION"
        const val EXTRA_ITEM = "com.dicoding.prayogo.EXTRA_ITEM"
        internal fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stack_view)
            val views = RemoteViews(context.packageName, R.layout.film_favorite_widget)
            views.setRemoteAdapter(R.id.stack_view,intent)
            views.setEmptyView(
                R.id.stack_view,
                R.id.empty_view
            )
            val toastIntent = Intent(context, FilmFavoriteWidget::class.java)
            toastIntent.action= TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val toastPendingIntent = PendingIntent.getBroadcast(context,0,toastIntent,PendingIntent.FLAG_UPDATE_CURRENT)
            views.setPendingIntentTemplate(R.id.stack_view,toastPendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

