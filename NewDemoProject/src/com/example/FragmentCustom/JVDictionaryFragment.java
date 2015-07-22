package com.example.FragmentCustom;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.data.MySingleton;
import com.example.database.DbHelper;
import com.example.database.Word;
import com.example.util.ExpandAnimation;
import com.javacodegeeks.androidcameraexample2.R;

public class JVDictionaryFragment extends Fragment {
	ArrayList<Word> arrWord;
	String key;

	DbHelper db;

	ListView lvSearchResult;
	CustomListAdapter customW;

	LayoutInflater layoutInflater;

	View view;

	@Override
	public void onCreate( Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		key = bundle.getString("key");
		arrWord = new ArrayList<Word>();
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

		arrWord = db.getWordMatched("先生");

		if (arrWord != null) {

			customW = new CustomListAdapter(getActivity(), inflater, arrWord);

			lvSearchResult.setAdapter(customW);
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
						arrWord.clear();
						arrWord = db.getWordMatched(s);
						if (arrWord != null) {
							customW.updateAdapter(arrWord);
							lvSearchResult.invalidateViews();
							lvSearchResult.setAdapter(customW);
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
