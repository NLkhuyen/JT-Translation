package com.example.FragmentCustom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lkhuyen.myapplicationtesseract.R;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	// Declare the number of ViewPager pages
	private String titles[];
	Drawable myDrawable;
	Context mContext;
	private int enabled;

	public ViewPagerAdapter(FragmentManager fm, Context mContext, String[] title, int enable) {
		super(fm);
		this.titles = title;
		myDrawable = mContext.getResources()
				.getDrawable(R.drawable.ic_launcher);
		this.enabled = enable;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragmenttab = new Fragment();
		if (enabled == 1) {
			fragmenttab = new FragmentTab1();
			Bundle bundle = new Bundle();
			bundle.putString("key", titles[position]);
			fragmenttab.setArguments(bundle);
			return fragmenttab;
		} else {
			if(titles[position].equalsIgnoreCase("Scan Result")){
				fragmenttab = new ResultTextFragment();
				Bundle bundle = new Bundle();
				bundle.putString("key", titles[position]);
				fragmenttab.setArguments(bundle);
				return fragmenttab;
			}
			else if (titles[position].equalsIgnoreCase("JV Dictionary")){
				fragmenttab = new JVDictionaryFragment();
				Bundle bundle = new Bundle();
				bundle.putString("key", titles[position]);
				fragmenttab.setArguments(bundle);
				return fragmenttab;
			}
			else if (titles[position].equalsIgnoreCase("Kanji Dictionary")){
				fragmenttab = new KanjiFragment();
				Bundle bundle = new Bundle();
				bundle.putString("key", titles[position]);
				fragmenttab.setArguments(bundle);
				return fragmenttab;
			}
			return fragmenttab;
		}
	}
	
	public CharSequence getPageTitle(int position) {
		return titles[position];
		//
		// SpannableStringBuilder sb = new SpannableStringBuilder(" " + "Page #"
		// + position); // space added before text for convenience
		//
		// myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(),
		// myDrawable.getIntrinsicHeight());
		// ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE);
		// sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		//
		// return sb;
	}

	@Override
	public int getCount() {
		return titles.length;
	}

}