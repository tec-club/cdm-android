/*
 * Copyright (C) 2011 Chris Gao <chris@exina.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ampelement.cdm.calendar.library;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

public class Cell {
	private static final String TAG = "Cell";
	protected Rect mBound = null;
	protected int mDayOfMonth = 1; // from 1 to 31
	int mMonth = 0;
	protected int mBGColor = 0xDDDDDD;
	protected float mTextSize;
	protected int mTextColor;
	protected boolean mBoldText;
	
	boolean selected = false;

	public Cell(int dayOfMon, int month, Rect rect, float textSize, boolean bold, int textColor, int bgColor) {
		mDayOfMonth = dayOfMon;
		mMonth = month;
		mBound = rect;
		mTextSize = textSize;
		mTextColor = textColor;
		mBoldText = bold;
		mBGColor = bgColor;
	}

	public void setSelected() {
		selected = true;
		mBGColor = 0x000000;
	}

	protected void draw(Canvas canvas, Paint backgroundPaint, Paint borderPaint, TextPaint textPaint) {
		// Setup for drawing
		textPaint.setTextSize(mTextSize/* 26f */);
		textPaint.setColor(mTextColor);
		textPaint.setFakeBoldText(mBoldText);
		
		backgroundPaint.setColor(mBGColor);
		backgroundPaint.setStyle(Paint.Style.FILL);
		borderPaint.setColor(0xffd4d4d4);
		borderPaint.setStyle(Paint.Style.STROKE);

		int charWidth = (int) textPaint.measureText("7");
		int dayWidth = (int) textPaint.measureText(String.valueOf(mDayOfMonth));
		int dayHeight = (int) (-textPaint.ascent() + textPaint.descent());
		
		// Actual draw calls
		canvas.drawRect(mBound, backgroundPaint);
		canvas.drawRect(mBound, borderPaint);
		canvas.drawText(String.valueOf(mDayOfMonth), mBound.right - (dayWidth + charWidth), mBound.top + dayHeight, textPaint);
	}

	public int getDayOfMonth() {
		return mDayOfMonth;
	}

	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y);
	}

	public Rect getBound() {
		return mBound;
	}

	public String toString() {
		return String.valueOf(mDayOfMonth) + "(" + mBound.toString() + ")";
	}

	public int getMonth() {
		return mMonth;
	}

}

