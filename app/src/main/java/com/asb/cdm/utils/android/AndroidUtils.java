package com.asb.cdm.utils.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class AndroidUtils {

	/**
	 * Easy AlertDialog creation method. Wraps the whole
	 * {@link AlertDialog.Builder} process into one massive method call.
	 * <p>
	 * Providing <b>null</b> for any of the ButtonListeners will result in that
	 * button not appearing on the AlertDialog. This makes it so that you don't
	 * have to use every button.
	 * 
	 * @param title
	 *            Title of the AlertDialog
	 * @param message
	 *            Message in the body of the AlertDialog
	 * @param cancelable
	 *            If the AlertDialog is cancelable
	 * @param posistiveButtonText
	 *            Text on the Positive button on the AlertDialog
	 * @param positiveButtonListener
	 *            OnClick Listener for Positive Button on the AlertDialog
	 * @param negativeButtonText
	 *            Text on the Negative button on the AlertDialog
	 * @param negativeButtonListener
	 *            OnClick Listener for Negative Button on the AlertDialog
	 * @param neutralButtonText
	 *            Text on the Negative button on the AlertDialog
	 * @param neutralButtonListener
	 *            OnClick Listener for Neutral Button on the AlertDialog
	 * @param onCancelListener
	 *            Listener for if the AlertDialog is canceled (either by hitting
	 *            the back button or clicking somewhere else on the screen). Has
	 *            no function if cancelable is false.
	 * @param context
	 *            An application context to build the AlertDialog off of.
	 * @return a brand-spanken new AlertDialog to be displayed
	 */
	public static final AlertDialog getAlertDialog(String title, String message, boolean cancelable, String posistiveButtonText,
			DialogInterface.OnClickListener positiveButtonListener, String negativeButtonText, DialogInterface.OnClickListener negativeButtonListener,
			String neutralButtonText, DialogInterface.OnClickListener neutralButtonListener, DialogInterface.OnCancelListener onCancelListener, Context context) {
		AlertDialog.Builder adBuilder = new AlertDialog.Builder(context);
		if (title != null)
			adBuilder.setTitle(title);
		if (message != null)
			adBuilder.setMessage(message);
		if (positiveButtonListener != null)
			adBuilder.setPositiveButton(posistiveButtonText, positiveButtonListener);
		if (negativeButtonListener != null)
			adBuilder.setNegativeButton(negativeButtonText, negativeButtonListener);
		if (neutralButtonListener != null)
			adBuilder.setNeutralButton(neutralButtonText, neutralButtonListener);
		if (onCancelListener != null)
			adBuilder.setOnCancelListener(onCancelListener);
		adBuilder.setCancelable(cancelable);
		return adBuilder.create();
	}

	/**
	 * Set all of the supplied views to the supplied Visbility State.
	 * 
	 * @param visibilityState
	 *            e.g. View.VISIBLE, View.GONE, View.INVISIBLE
	 * @param views
	 *            an Array of Views to apply the visibility to
	 */
	public static final void setVisibility(int visibilityState, View... views) {
		for (View view : views) {
			if (view != null)
				view.setVisibility(visibilityState);
		}
	}

	/**
	 * Returns the devices current API Level
	 * 
	 * @return Android Version Number of the current device
	 */
	public static final int API_LEVEL() {
		return android.os.Build.VERSION.SDK_INT;
	}

}
