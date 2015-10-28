package com.asb.cdm.schoolloop.data;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

/**
 * Created by ryan on 10/3/15.
 */
public class SchoolLoopNotification {
    final String TAG = "SchoolLoopNotificationService";
    String title, content;
    Context context;
    int icon;
    int priority;

    public SchoolLoopNotification(String title, String content, int icon, int priority, Context ctx) {
        this.title = title;
        this.content = content;
        this.context = ctx;
        this.icon = icon;
        this.priority = priority;
    }

    public boolean showNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(icon);
        builder.setPriority(priority);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(31415, builder.build()); //TODO figure out good constant id
        return true;
    }
}
