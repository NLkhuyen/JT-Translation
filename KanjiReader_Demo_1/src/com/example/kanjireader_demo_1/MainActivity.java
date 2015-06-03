package com.example.kanjireader_demo_1;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.util.JT_ImageProccessor;
import com.googlecode.tesseract.android.TessBaseAPI;

public class MainActivity extends Activity {
	
	public static final String DATA_PATH = Environment
			.getExternalStorageDirectory().toString() + "/KanjiReader_demo_1/";
	private static final String TAG = "KanjiReader_demo_1";
	
	public static final String lang = "jpn";
	
	protected static final String PHOTO_TAKEN = "photo_taken";
	
	protected String _path;
	protected EditText _field;
	protected boolean _taken;
	protected Button _button;
	protected ImageView _image;
	
	static {
			if (!OpenCVLoader.initDebug()) {
				Log.e(TAG, "load library fail");
			}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_field = (EditText) findViewById(R.id.editText1);
		_button = (Button) findViewById(R.id.button1);
		_path = DATA_PATH + "ocr1.jpg";
		_image = (ImageView) findViewById(R.id.imageView1);
		
		_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startCameraActivity();
			}
		});
		
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void startCameraActivity() {
		File file = new File(_path);
		Uri outputFileUri = Uri.fromFile(file);

		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 50);

		startActivityForResult(intent, 0);
	}
	
	
	public void executeImage() {
		try{
			String filename = _path;
			Toast.makeText(getApplicationContext(), "sucess", Toast.LENGTH_SHORT).show();
			Mat image = Highgui.imread(filename);
			
			// lay vung chu
			Rect textArea = JT_ImageProccessor.extractTextArea(image); // lay toa do vung chu
			Mat croppedImage = new Mat(image, textArea); // Cat anh
//			Highgui.imwrite("picture/testImag6_textArea.jpg", croppedImage); // write ra anh
			
			// lay cac dong
			List<Rect> textLines = new ArrayList<Rect>();
			textLines = JT_ImageProccessor.extractTextLine(image);
			
			// khoanh cac dong tren anh
			Mat image2 = image.clone();
			for(int i = 0; i < textLines.size(); ++i) {
				Rect rect = textLines.get(i);
				Point p1 = new Point(rect.x, rect.y);
				Point p2 = new Point(rect.x + rect.width, rect.y + rect.height);
				Core.rectangle(image2, p1, p2, new Scalar(255, 0, 0));
			}
			// write ra anh
			Highgui.imwrite(DATA_PATH + "test1.jpg", image2);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		 OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_10, this, mLoaderCallback);
//	}
	
	public void douwnloadTesseractData() {
		String[] paths = new String[] { DATA_PATH, DATA_PATH + "tessdata/" };

		for (String path : paths) {
			File dir = new File(path);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG, "ERROR: Creation of directory " + path
							+ " on sdcard failed");
					return;
				} else {
					Log.v(TAG, "Created directory " + path + " on sdcard");
				}
			}

		}
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {

				AssetManager assetManager = getAssets();
				InputStream in = assetManager.open("tessdata/" + lang
						+ ".traineddata");
				// GZIPInputStream gin = new GZIPInputStream(in);
				OutputStream out = new FileOutputStream(DATA_PATH + "tessdata/"
						+ lang + ".traineddata");

				// Transfer bytes from in to out
				byte[] buf = new byte[1024];
				int len;
				// while ((lenf = gin.read(buff)) > 0) {
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				// gin.close();
				out.close();

				Log.v(TAG, "Copied " + lang + " traineddata");
			} catch (IOException e) {
				Log.e(TAG,
						"Was unable to copy " + lang + " traineddata "
								+ e.toString());
			}
		}
	}
	
	
	//Tesseract API
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(MainActivity.PHOTO_TAKEN, _taken);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG, "onRestoreInstanceState()");
		if (savedInstanceState.getBoolean(MainActivity.PHOTO_TAKEN)) {
			onPhotoTaken();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG, "resultCode: " + resultCode);

		if (resultCode == -1) {
//			onPhotoTaken();
			executeImage();
		} else {
			Log.v(TAG, "User cancelled");
		}
	}

	protected void onPhotoTaken() {
		_taken = true;
		
		
		//get bound over paragraph
//		TextAreaGetter.surroundProcessingImage(_path, "da_xu_ly" + _path);

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 4;

		Bitmap bitmap = BitmapFactory.decodeFile("da_xu_ly" + _path, options);

		try {
			ExifInterface exif = new ExifInterface(_path);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;

			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}

			Log.v(TAG, "Rotation: " + rotate);

			if (rotate != 0) {

				// Getting width & height of the given image.
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();

				// Setting pre rotate
				Matrix mtx = new Matrix();
				mtx.preRotate(rotate);

				// Rotating Bitmap
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			}

			// Convert to ARGB_8888, required by tess
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
			

		} catch (IOException e) {
			Log.e(TAG, "Couldn't correct orientation: " + e.toString());
		}

		// _image.setImageBitmap( bitmap );

		Log.v(TAG, "Before baseApi");

		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(DATA_PATH, lang);
		baseApi.setImage(bitmap);

		String recognizedText = baseApi.getUTF8Text();

		for (int i = 0; i < 4; i++) {
			baseApi = new TessBaseAPI();
			baseApi.init(DATA_PATH, lang);
			baseApi.setImage(bitmap);
			recognizedText += "abcd@1234";
			recognizedText += baseApi.getUTF8Text();
		}

		baseApi.end();

		// You now have the text in recognizedText var, you can do anything with
		// it.
		// We will display a stripped out trimmed alpha-numeric version of it
		// (if lang is eng)
		// so that garbage doesn't make it to the display.

		Log.v(TAG, "OCRED TEXT: " + recognizedText);

		if (lang.equalsIgnoreCase("eng")) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}

		recognizedText = recognizedText.trim();

		if (recognizedText.length() != 0) {
			_field.setText(_field.getText().toString().length() == 0 ? recognizedText
					: _field.getText() + " " + recognizedText);
			_field.setSelection(_field.getText().toString().length());
		}

		// Cycle done.
	}

}
