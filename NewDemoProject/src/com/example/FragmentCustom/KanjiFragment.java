package com.example.FragmentCustom;

import info.androidhive.slidingmenu.adapter.CopyOfCustomListAdapterKanji;
import info.androidhive.slidingmenu.adapter.CustomListAdapter;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.data.MySingleton;
import com.example.database.DbHelper;
import com.example.database.Kanji;
import com.example.util.ExpandAnimation;
import com.javacodegeeks.androidcameraexample2.R;

public class KanjiFragment extends Fragment {
	
	String key;

	DbHelper db;
	
	ListView lvSearchResult;
	CopyOfCustomListAdapterKanji customK;
	
	LayoutInflater layoutInflater;
	
	View view;
	
	ArrayList<Kanji> arrKanji;
	
	@Override
	public void onCreate( Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		key = bundle.getString("key");
		arrKanji = new ArrayList<Kanji>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		layoutInflater = inflater;
		view = inflater.inflate(R.layout.fragment_dictionary_layout, container,
				false);
		lvSearchResult = (ListView) view.findViewById(R.id.lv_search_result);
		setHasOptionsMenu(true);

		db = MySingleton.getInstance().db;

		arrKanji = db.getKanjiMatched("太");
		
		for (int i = 0; i < arrKanji.size(); i++) {
			Log.d("Kanji", arrKanji.get(i).getWord2());
		}

		if (arrKanji != null) {

			customK = new CopyOfCustomListAdapterKanji(getActivity(), inflater, arrKanji);

			lvSearchResult.setAdapter(customK);
		}

		lvSearchResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				View toolbar = view.findViewById(R.id.toolbar_listview);

				// Creating the expand animation for the item
				ExpandAnimation expandAni = new ExpandAnimation(toolbar, 100);

				// Start the animation on the toolbar
				toolbar.startAnimation(expandAni);
			}
		});

		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		MenuItem item = menu.add("Search");
		item.setIcon(android.R.drawable.ic_menu_search);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		SearchView mSearchView = new SearchView(getActivity());
		mSearchView
				.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
					@Override
					public boolean onQueryTextSubmit(String s) {
						Log.w("SEARCH", "Search in fragment: " + s);
						arrKanji.clear();
						arrKanji = db.getKanjiMatched(s);
						if (arrKanji != null) {
							for (int i = 0; i < arrKanji.size(); i++) {
								Log.d("JVDic", arrKanji.get(i).getWord2());
							}
							customK.updateAdapter(arrKanji);
							lvSearchResult.invalidateViews();
							lvSearchResult.setAdapter(customK);
						}
						return false;
					}

					@Override
					public boolean onQueryTextChange(String s) {
						Log.w("SEARCH", "Typing in fragment: " + s);
						return false;
					}
				});
		item.setActionView(mSearchView);
	}

}
