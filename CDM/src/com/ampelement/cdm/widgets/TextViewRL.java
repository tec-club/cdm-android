package com.ampelement.cdm.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRL extends TextView {

    public TextViewRL(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewRL(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewRL(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "Roboto-Light.ttf");
            setTypeface(tf);
        }
    }

}
