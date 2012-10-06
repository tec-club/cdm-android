package com.ampelement.cdm.fragments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ampelement.cdm.R;
import com.ampelement.cdm.coverflow.CoverFlow;
import com.ampelement.cdm.coverflow.FileImageAdapter;
import com.ampelement.cdm.coverflow.ReflectingImageAdapter;
import com.ampelement.cdm.objects.Media;
import com.ampelement.cdm.utils.DatabaseHandler;

public class MediaFragment extends Fragment {

	private CoverFlow coverFlow;
	private MediaInterface mediaInterface;

	final private List<Media> mediaObjectsInCoverFlow = new ArrayList<Media>();

	public static final String TAG = "MediaFragment";

	public interface MediaInterface {
		public void setIndicator(int indicatorID);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mediaInterface = (MediaInterface) activity;
		} catch (ClassCastException e) {
			Log.e(TAG, activity.toString() + " must implement OnUpdateListener");
			// getActivity().finish();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View mediaScreen = inflater.inflate(R.layout.media_screen, container, false);
		coverFlow = (CoverFlow) mediaScreen.findViewById(R.id.media_screen_coverflowReflect);

		final TextView mediaTitleTV = (TextView) mediaScreen.findViewById(R.id.media_screen_title);
		final TextView mediaDescriptionTV = (TextView) mediaScreen.findViewById(R.id.media_screen_description);

		coverFlow.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {

			}

		});
		coverFlow.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
				try {

					/*final Animation fadeInAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
					final Animation fadeOutAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
					AnimationSet as = new AnimationSet(true);
					as.addAnimation(fadeInAnimation);
					fadeInAnimation.setStartOffset(300);
					as.addAnimation(fadeOutAnimation);
					
					final LinearLayout textLL = (LinearLayout)mediaScreen.findViewById(R.id.media_screen_text_ll);
					textLL.startAnimation(as);*/

					Media media = mediaObjectsInCoverFlow.get(position);
					mediaTitleTV.setText(media.name);
					mediaDescriptionTV.setText(media.description);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			@Override
			public void onNothingSelected(final AdapterView<?> parent) {
				// textView.setText("Nothing clicked!");
			}
		});
		
		new SetUpImageAdapter().execute();
		mediaInterface.setIndicator(R.id.main_media_indicator);
		return mediaScreen;
	}

	private class SetUpImageAdapter extends AsyncTask<Void, Void, ReflectingImageAdapter> {

		@Override
		protected ReflectingImageAdapter doInBackground(Void... params) {

			final DatabaseHandler db = new DatabaseHandler(getActivity());
			final List<Media> mediaList = db.getAllMedias();
			int numberOfMedia = 0;
			for (Media media : mediaList) {
				if (media.type.matches(Media.TYPE_IMAGE)) {
					numberOfMedia++;
				}
			}
			String[] mediaFilePaths = new String[numberOfMedia];
			int currentMediaFilePathsItem = 0;
			for (Media media : mediaList) {
				if (media.type.matches(Media.TYPE_IMAGE)) {
					mediaFilePaths[currentMediaFilePathsItem] = (media.path);
					currentMediaFilePathsItem++;
					mediaObjectsInCoverFlow.add(media);
				}
			}
			FileImageAdapter fileImageAdapter = new FileImageAdapter(getActivity(), mediaFilePaths);
			ReflectingImageAdapter coverImageAdapter = new ReflectingImageAdapter(fileImageAdapter);

			return coverImageAdapter;
		}

		@Override
		protected void onPostExecute(ReflectingImageAdapter coverImageAdapter) {
			coverFlow.setAdapter(coverImageAdapter);
		}

	}

}
