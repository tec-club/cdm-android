package com.ampelement.cdm.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRBC extends TextView {

    public TextViewRBC(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewRBC(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewRBC(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "RobotoCondensed-Regular.ttf");
            setTypeface(tf);
        }
    }

}
