package com.example.FragmentCustom;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.util.SlidingTabLayout;
import com.javacodegeeks.androidcameraexample2.R;

public class SettingFragment extends Fragment {
	SlidingTabLayout mSlidingTabLayout;
	String[] title;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting_layout, container, false);
		
		title = new String[] { "Tab1", "Tab2" };
		// Locate the ViewPager in viewpager_main.xml
		LockableViewPager mViewPager = (LockableViewPager) view.findViewById(R.id.viewPager);
		// Set the ViewPagerAdapter into ViewPager
		mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), getActivity().getApplicationContext(),title, 1));
		
		getActivity().getActionBar().setTitle("chuyen sang roi");
		
		mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
		mSlidingTabLayout.setDistributeEvenly(true);
	    mSlidingTabLayout.setViewPager(mViewPager);
		return view;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}
}
