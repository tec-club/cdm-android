package com.asb.cdm.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRR extends TextView {
	
	private String mFontPath = "fonts/Roboto-Regular.ttf";

    public TextViewRR(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewRR(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewRR(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), mFontPath);
            setTypeface(tf);
        }
    }

}
