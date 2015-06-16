package com.ampelement.cdm.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewRBI extends TextView {

	private String mFontPath = "fonts/Roboto-BoldItalic.ttf";

	public TextViewRBI(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextViewRBI(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TextViewRBI(Context context) {
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
