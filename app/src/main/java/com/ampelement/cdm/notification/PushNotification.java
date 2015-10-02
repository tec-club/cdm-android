package com.ampelement.cdm.notification;

import android.app.Notification;
import android.app.NotificationManager;

/**
 * Base class for different notification types. Clicking the notification will open up the app
 * with an alert dialog displaying the full text. On Android<4.1 just a title, icon, small content will
 * display, while on Android>4.1, notification will expand to show larger content section if needed
 * Created by ryan on 10/1/15.
 */
public abstract class PushNotification {
    String title;
    String content;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    String category;
    int icon;

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    int priority;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public abstract Notification build();


}
