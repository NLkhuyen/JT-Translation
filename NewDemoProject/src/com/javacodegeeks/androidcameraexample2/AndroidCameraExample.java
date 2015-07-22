package com.javacodegeeks.androidcameraexample2;

import info.androidhive.slidingmenu.adapter.NavDrawerListAdapter;
import info.androidhive.slidingmenu.model.NavDrawerItem;

import java.util.ArrayList;

import org.opencv.android.OpenCVLoader;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.FragmentCustom.ResultFragmentActivity;
import com.example.FragmentCustom.SettingFragment;
import com.example.data.MySingleton;
import com.example.database.DbHelper;

public class AndroidCameraExample extends FragmentActivity {

	static {
		if (!OpenCVLoader.initDebug()) {
			Log.e("ResultText.java", "load library fail");
		}
	}
	
	ImageView image;

	public static final String lang = "jpn";

	String[] menu;
	DrawerLayout dLayout;
	ListView dList;
	NavDrawerListAdapter adapter;
	ActionBarDrawerToggle mDrawerToggle;
	DbHelper db;

	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/KanjiReader/";

//	Animation zoom_outAnim;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().setTitle("12345678");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		db = new DbHelper(getApplicationContext());
		MySingleton.getInstance().db = db;
		// image = (ImageView) findViewById(R.id.imageView1);
		slideMenu();
		onFirstRun();

	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.search:
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater searchItem = getMenuInflater();
//		searchItem.inflate(R.menu.main, menu);
//
//		// Associate searchable configuration with the SearchView
//		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//		SearchView mSearchView = (SearchView) menu.findItem(R.id.search)
//				.getActionView();
//		mSearchView.setSearchableInfo(searchManager
//				.getSearchableInfo(getComponentName()));
//
//		mSearchView
//				.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//					@Override
//					public boolean onQueryTextSubmit(String s) {
//						Log.w("SEARCH", "Search: " + s);
//						return false;
//					}
//
//					@Override
//					public boolean onQueryTextChange(String s) {
//						Log.w("SEARCH", "Typing: " + s);
//						return false;
//					}
//				});
//		return true;
//	}

	public void onFirstRun() {
		Bundle bun = new Bundle();
		bun.putString("Menu", "Camera");
		bun.putString("DATA_PATH", DATA_PATH);
		bun.putString("lang", lang);
		Fragment detail = new DetailFragment();
		detail.setArguments(bun);
		FragmentTransaction fragmentManager = getSupportFragmentManager()
				.beginTransaction();
		fragmentManager.replace(R.id.content_frame, detail).commit();
	}

	@SuppressWarnings("deprecation")
	public void slideMenu() {
		menu = new String[] { "Home", "Android", "Windows", "Linux",
				"Raspberry Pi", "WordPress", "Setting", "Dictionary Screen", "Camera" };
		dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ArrayList<NavDrawerItem> arr = new ArrayList<NavDrawerItem>();
		for (int i = 0; i < menu.length; i++) {
			arr.add(new NavDrawerItem(menu[i], R.drawable.ic_launcher));
		}
		dLayout.setScrimColor(Color.TRANSPARENT);
		dList = (ListView) findViewById(R.id.left_drawer);
		dList.setSelector(android.R.color.holo_blue_dark);
		
		
		adapter = new NavDrawerListAdapter(this, arr);

		dList.setAdapter(adapter);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, dLayout,
				R.drawable.ic_drawer, //nav menu toggle icon
				R.string.drawer_open, // nav drawer open - description for accessibility
				R.string.drawer_close // nav drawer close - description for accessibility
		);
		
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		dLayout.setDrawerListener(mDrawerToggle);

		dList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {

				dLayout.closeDrawers();
				Bundle args = new Bundle();
				if (position < menu.length) {
					args.putString("Menu", menu[position]);
				} else {
					args.putString("Menu", menu[position % menu.length]);
				}
				args.putString("DATA_PATH", DATA_PATH);
				args.putString("lang", lang);
				Fragment detail;
				if (menu[position % menu.length].equalsIgnoreCase("Setting")) {
					Fragment fragment = new SettingFragment();
					FragmentTransaction fragmentManager = getSupportFragmentManager()
							.beginTransaction();
					fragmentManager.replace(R.id.content_frame, fragment)
							.commit();
				} else if(menu[position % menu.length].equalsIgnoreCase("Dictionary Screen")){
					Fragment fragment = new ResultFragmentActivity();
					FragmentTransaction fragmentManager = getSupportFragmentManager()
							.beginTransaction();
					fragmentManager.replace(R.id.content_frame, fragment)
							.commit();
				}else{
					detail = new DetailFragment();
					detail.setArguments(args);
					FragmentTransaction fragmentManager = getSupportFragmentManager()
							.beginTransaction();
					fragmentManager.replace(R.id.content_frame, detail)
							.commit();
				}
			}

		});
	}

}