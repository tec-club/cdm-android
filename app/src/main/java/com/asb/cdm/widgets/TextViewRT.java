package com.asb.cdm.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRT extends TextView {

    public TextViewRT(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewRT(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewRT(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
            setTypeface(tf);
        }
    }

}
