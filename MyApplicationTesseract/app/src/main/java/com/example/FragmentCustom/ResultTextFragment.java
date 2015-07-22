package com.example.FragmentCustom;

import info.androidhive.slidingmenu.adapter.CopyOfCustomListAdapterKanji;
import info.androidhive.slidingmenu.adapter.CustomListAdapter;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.database.DbHelper;
import com.example.database.Kanji;
import com.example.database.Word;
import com.example.lkhuyen.myapplicationtesseract.R;

public class ResultTextFragment extends Fragment {

	/**
	 * @param args
	 */

	View view;

	ImageView image;
	String key;

	DbHelper db;
	ArrayList<Word> arrWord;
	ArrayList<Kanji> arrKanji;
	ListView lvSearchResult;
	CustomListAdapter customW;
	CopyOfCustomListAdapterKanji customK;

	LayoutInflater layoutInflater;
	
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		key = bundle.getString("key");
		arrWord = new ArrayList<Word>();
		arrKanji = new ArrayList<Kanji>();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		layoutInflater = inflater;
			view = inflater.inflate(R.layout.result_text_layout, container,
					false);

			image = (ImageView) view.findViewById(R.id.imResultText);

		return view;
	}

//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//	    super.setUserVisibleHint(isVisibleToUser);
//
//	    // Make sure that we are currently visible
//	    if (this.isVisible()) {
//	        // If we are becoming invisible, then...
//	        if (!isVisibleToUser) {
//	            Log.d("MyFragment", key);
//	            // TODO stop audio playback
//	        }
//	    }
//	}

}
