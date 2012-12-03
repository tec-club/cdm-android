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

package com.ampelement.cdm.calendar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class Cell {
	private static final String TAG = "Cell";
	protected Rect mBound = null;
	protected int mDayOfMonth = 1; // from 1 to 31
	int mMonth = 0;
	protected Paint mTextPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
	protected int borderColor = Color.BLACK;
	int dx, dy;
	boolean selected = false;

	public Cell(int dayOfMon, int month, Rect rect, float textSize, boolean bold, int textColor, int borColor) {
		mDayOfMonth = dayOfMon;
		mMonth = month;
		mBound = rect;
		mTextPaint.setTextSize(textSize/*26f*/);
		mTextPaint.setColor(textColor);
		if (bold)
			mTextPaint.setFakeBoldText(true);

		dx = (int) mTextPaint.measureText(String.valueOf(mDayOfMonth)) / 2;
		dy = (int) (-mTextPaint.ascent() + mTextPaint.descent()) / 2;
		borderColor = borColor;
	}
	
	public void setSelected() {
		selected = true;
	}

	protected void draw(Canvas canvas) {
		Paint borderPaint = new Paint();
		borderPaint.setColor(borderColor);
		borderPaint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(mBound, borderPaint);
		if (selected) {
			Paint bgPaint = new Paint();
			bgPaint.setColor(0x66FFFFFF);
			bgPaint.setStyle(Paint.Style.FILL);
			Rect fillRectangle = mBound;
			canvas.drawRect(fillRectangle, bgPaint);
		}
		canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy, mTextPaint);
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
