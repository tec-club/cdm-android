package com.ampelement.cdm.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewBW extends TextView {

	private String mFontPath = "fonts/ballw.ttf";

	public TextViewBW(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextViewBW(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextViewBW(Context context) {
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
