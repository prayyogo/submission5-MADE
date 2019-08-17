package com.dicoding.prayogo.movieapp.widget

import android.content.Intent
import android.widget.RemoteViewsService

class StackWidgetService:RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent?): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext)
    }
}