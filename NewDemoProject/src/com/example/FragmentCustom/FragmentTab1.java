package com.example.FragmentCustom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.javacodegeeks.androidcameraexample2.R;

public class FragmentTab1 extends Fragment {
	String key;
	
	TextView tvFragmentab;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get the view from fragmenttab1.xml
		Bundle bundle = getArguments();
		key = bundle.getString("key");
		Toast.makeText(getActivity(), key, Toast.LENGTH_SHORT).show();
		View view = inflater.inflate(R.layout.fragmenttab1, container, false);
		tvFragmentab = (TextView) view.findViewById(R.id.tvFragmentTab);
		if (key.equalsIgnoreCase("Tab1")){
			tvFragmentab.setText("Tab1");
		}else {
			tvFragmentab.setText("TabX");
		}
		
		return view;
	}
}
