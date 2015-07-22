package com.example.data;

import java.util.List;

import org.opencv.core.Rect;

import com.example.database.DbHelper;

import android.graphics.Bitmap;

public class MySingleton {
	private static MySingleton instance;
	
	public byte[] mBitmapByteArray;
	public byte[] mBitmapByteArray2;
	public List<Rect> textLines;
	public DbHelper db;

	public static void initInstance() {
		if (instance == null) {
			// Create the instance
			instance = new MySingleton();
		}
	}

	public static MySingleton getInstance() {
		// Return the instance
		return instance;
	}

	

	public void customSingletonMethod() {
		// Custom method
		
	}
}
