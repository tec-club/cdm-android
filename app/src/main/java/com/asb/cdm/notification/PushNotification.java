package com.asb.cdm.notification;

import android.app.Notification;

/**
 * Base class for different notification types. Clicking the notification will open up the app
 * with an alert dialog displaying the full text. On Android<4.1 just a title, icon, small content will
 * display, while on Android>4.1, notification will expand to show larger content section if needed
 * Created by ryan on 10/1/15.
 */
public abstract class PushNotification {
    String title;
    String content;

    String category;
    int icon;
    int priority;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


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
