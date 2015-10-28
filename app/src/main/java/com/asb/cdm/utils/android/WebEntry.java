package com.asb.cdm.utils.android;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

import com.asb.cdm.infoscreen.WebViewDialogFragment;

/**
 * Created by ryan on 10/2/15.
 */
public abstract class WebEntry extends NavDrawerEntry {


    @Override
    public Class<ExtendedFragment> getFragmentType() {
        return null;
    }

    @Override
    public EntryType getType() {
        return EntryType.DIALOG;
    }

    @Override
    public EntryStyle getStyle() {
        return EntryStyle.SMALL;
    }

    public abstract int getIcon();

    public abstract String getURL();

    public void runAction(Activity activity) {
        return;
    }

    public void show(AppCompatActivity activity) {
        WebViewDialogFragment.newInstance(this.getURL(), false, true).show(activity.getSupportFragmentManager(), getTitle());

    }
}
