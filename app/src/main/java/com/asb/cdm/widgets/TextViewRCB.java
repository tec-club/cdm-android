package com.asb.cdm.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRCB extends TextView {

	private String mFontPath = "fonts/RobotoCondensed-Bold.ttf";

	public TextViewRCB(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextViewRCB(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextViewRCB(Context context) {
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
