package com.javacodegeeks.androidcameraexample2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.FragmentCustom.ResultFragmentActivity;
import com.example.FragmentCustom.ResultTextFragment;
import com.example.FragmentCustom.SettingFragment;
import com.example.data.MySingleton;
import com.example.util.AKImageProcessor;

public class DetailFragment extends Fragment {
	TextView text;
	byte[] dataBitmap;
	File pictureFile;

	String menu;
	ProgressDialog progress;
	private Camera mCamera;
	private CameraPreview mPreview;
	private PictureCallback mPicture;
	private Button capture;
	private Context myContext;
	private LinearLayout cameraPreview;
	private boolean cameraFront = false;
//	Animation zoom_outAnim;

	Bitmap mBitmapResutl;
	View view;
	private static final String TAG = "KanjiReader.java";
	String lang;

	String DATA_PATH;

	private static final int FOCUS_AREA_SIZE = 300;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle args) {

		myContext = getActivity().getBaseContext();
		menu = getArguments().getString("Menu");
		DATA_PATH = getArguments().getString("DATA_PATH");
		lang = getArguments().getString("lang");
		// text = (TextView) view.findViewById(R.id.detail);
		
		// text.setText(menu);
		if (menu.equalsIgnoreCase("Camera")) {
			view = inflater.inflate(R.layout.camera_layout, container, false);
			initialize();
			downloadDataFileRequire();
		} else {
			view = inflater.inflate(R.layout.menu_detail_fragment, container,
					false);
		}
		return view;
	}

	public void initialize() {

		cameraPreview = (LinearLayout) view.findViewById(R.id.camera_preview);
		mPreview = new CameraPreview(myContext, mCamera);

		mPreview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				if (mCamera != null) {
					Camera camera = mCamera;
					camera.cancelAutoFocus();
					android.graphics.Rect focusRect = calculateFocusArea(event.getX(),
							event.getY());

					Parameters parameters = camera.getParameters();
					if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
						parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
					}
					if (parameters.getMaxNumFocusAreas() > 0) {
						List<Area> mylist = new ArrayList<Area>();
						mylist.add(new Camera.Area(focusRect, 1000));
						parameters.setFocusAreas(mylist);
					}

					try {
						camera.cancelAutoFocus();
						camera.setParameters(parameters);
						camera.startPreview();
						camera.autoFocus(new Camera.AutoFocusCallback() {
							@Override
							public void onAutoFocus(boolean success,
									Camera camera) {
								if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
									Parameters parameters = camera
											.getParameters();
									parameters
											.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
									if (parameters.getMaxNumFocusAreas() > 0) {
										parameters.setFocusAreas(null);
									}
									camera.setParameters(parameters);
									camera.startPreview();
								}
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				return true;
			}
		});
		cameraPreview.addView(mPreview);

		capture = (Button) view.findViewById(R.id.button_capture);
		capture.setOnClickListener(captrureListener);

		// switchCamera = (Button) findViewById(R.id.button_ChangeCamera);
		// switchCamera.setOnClickListener(switchCameraListener);
	}

	public void downloadDataFileRequire() {
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

		// lang.traineddata file with the app (in assets folder)
		// You can get them at:
		// http://code.google.com/p/tesseract-ocr/downloads/list
		// This area needs work and optimization
		if (!(new File(DATA_PATH + "tessdata/" + lang + ".traineddata"))
				.exists()) {
			try {

				AssetManager assetManager = getActivity().getAssets();
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

	@SuppressWarnings("deprecation")
	private int findBackFacingCamera() {
		int cameraId = -1;
		// Search for the back facing camera
		// get the number of cameras
		int numberOfCameras = Camera.getNumberOfCameras();
		// for every camera check
		for (int i = 0; i < numberOfCameras; i++) {
			CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK) {
				cameraId = i;
				cameraFront = false;
				break;
			}
		}
		return cameraId;
	}

	@SuppressWarnings("deprecation")
	public void onResume() {
		super.onResume();
		if(menu.equalsIgnoreCase("Camera")) {
			if (!hasCamera(myContext)) {
				Toast toast = Toast.makeText(myContext,
						"Sorry, your phone does not have a camera!",
						Toast.LENGTH_LONG);
				toast.show();
			}
			if (mCamera == null) {
				// if the front facing camera does not exist
				mCamera = Camera.open(findBackFacingCamera());
				mCamera.setDisplayOrientation(90);
				mPicture = getPictureCallback();
				mPreview.refreshCamera(mCamera);
			}
		} else {
			
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		releaseCamera();
	}

	private boolean hasCamera(Context context) {
		// check if the device has camera
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	private android.graphics.Rect calculateFocusArea(float x, float y) {
		int left = clamp(
				Float.valueOf((x / cameraPreview.getWidth()) * 2000 - 1000)
						.intValue(), FOCUS_AREA_SIZE);
		int top = clamp(
				Float.valueOf((y / cameraPreview.getHeight()) * 2000 - 1000)
						.intValue(), FOCUS_AREA_SIZE);

		return new android.graphics.Rect(left, top, left + FOCUS_AREA_SIZE, top
				+ FOCUS_AREA_SIZE);
	}

	private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {
		int result;
		if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
			if (touchCoordinateInCameraReper > 0) {
				result = 1000 - focusAreaSize / 2;
			} else {
				result = -1000 + focusAreaSize / 2;
			}
		} else {
			result = touchCoordinateInCameraReper - focusAreaSize / 2;
		}
		return result;
	}


	private PictureCallback getPictureCallback() {
		PictureCallback picture = new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				// make a new picture file
				dataBitmap = data;
				pictureFile = getOutputMediaFile();

				if (pictureFile == null) {
					return;
				}

				mBitmapResutl = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				mBitmapResutl = rotationImage(data);
				mBitmapResutl = getResizedBitmap(mBitmapResutl);
				ProcessImageAsyn progress = new ProcessImageAsyn();
				progress.execute();
				
				// refresh camera to continue preview
				mPreview.refreshCamera(mCamera);
			}
		};
		return picture;
	}
	
	
	public Bitmap rotationImage(byte[] data) {
		Bitmap bmpTemp = BitmapFactory.decodeByteArray(data, 0, data.length);

		// Getting width & height of the given image.
		int w = bmpTemp.getWidth();
		int h = bmpTemp.getHeight();

		// Setting pre rotate
		Matrix mtx = new Matrix();
		mtx.preRotate(90);

		// Rotating Bitmap
		bmpTemp = Bitmap.createBitmap(bmpTemp, 0, 0, w, h, mtx, false);
		return bmpTemp;
	}
	
	public Bitmap getResizedBitmap(Bitmap bm) {
    	DisplayMetrics metrics = getActivity().getApplicationContext().getResources().getDisplayMetrics();
    	int newWidth = metrics.widthPixels;
    	int newHeight = metrics.heightPixels;
    	
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // Create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // Recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }
	
	public void executeImage() {
		try {
			// Mat image = Highgui.imread(filename);
			Mat image = new Mat();
			Utils.bitmapToMat(mBitmapResutl, image);
			Highgui.imwrite(DATA_PATH+"original.jpg", image);

			// lay vung chu
//			Rect textArea = AKImageProcessor.extractTextArea(image); // lay
																		// toa
																		// do
																		// vung
																		// chu
//			Mat croppedImage = new Mat(image, textArea); // Cat anh
			// Highgui.imwrite("picture/testImag6_textArea.jpg", croppedImage);
			// // write ra anh

			// lay cac dong
			List<Rect> textLines = new ArrayList<Rect>();
			textLines = AKImageProcessor.extractTextLine(image);
			MySingleton.getInstance().textLines = textLines;

//			TessBaseAPI baseApi = new TessBaseAPI();
//			baseApi.setDebug(true);
//			baseApi.init(DATA_PATH, lang);

			// khoanh cac dong tren anh
			Mat image2 = image.clone();
//			File f = new File(DATA_PATH+"resultText.txt");
//			FileWriter fw = new FileWriter(f);
//			String result = "crop|readText \n";
			for (int i = 0; i < textLines.size(); ++i) {
//				long time = Calendar.getInstance().getTimeInMillis();
				Rect rect = textLines.get(i);
//				Mat cropedImage = new Mat(image2, rect);
				
//				Highgui.imwrite(_resultPath + "" + i + ".jpg", cropedImage);
//				Bitmap mBitmapTemp = Bitmap.createBitmap(rect.width,
//						rect.height, Bitmap.Config.ARGB_8888);
//				Utils.matToBitmap(cropedImage, mBitmapTemp);
//				baseApi.setImage(mBitmapTemp);
//				String recognizedText = baseApi.getUTF8Text();
				
				
	            
//				resultText.append(recognizedText);
				 Point p1 = new Point(rect.x, rect.y);
				 Point p2 = new Point(rect.x + rect.width, rect.y +
				 rect.height);
				 Core.rectangle(image2, p1, p2, new Scalar(255, 0, 0));
			}
			Highgui.imwrite(DATA_PATH+"result.jpg", image2);
			Bitmap mBit = Bitmap.createBitmap(image2.width(),
					image2.height(), Bitmap.Config.ARGB_8888);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			
			Utils.matToBitmap(image2, mBit);
			mBit.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			MySingleton.getInstance().mBitmapByteArray = byteArray;
//			fw.write(result);
//			fw.close();
//			baseApi.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	OnClickListener captrureListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
//			v.startAnimation(zoom_outAnim);
			mCamera.takePicture(null, null, mPicture);

		}
	};

	// make picture and save to a folder
	private static File getOutputMediaFile() {
		// make a new file directory inside the "sdcard" folder
		File mediaStorageDir = new File("/sdcard/", "KanjiReader");

		// if this "JCGCamera folder does not exist
		if (!mediaStorageDir.exists()) {
			// if you cannot make this folder return
			if (!mediaStorageDir.mkdirs()) {
				return null;
			}
		}

		// take the current timeStamp
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		// and make a media file:
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");

		return mediaFile;
	}

	private void releaseCamera() {
		// stop and release camera
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}
	
	private class ProcessImageAsyn extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			executeImage();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				Toast.makeText(myContext,
						"Picture saved: " + pictureFile.getAbsolutePath(),
						Toast.LENGTH_LONG).show();
				Fragment fragment = new ResultFragmentActivity();
				FragmentTransaction fragmentManager = getFragmentManager()
						.beginTransaction();
				fragmentManager.replace(R.id.content_frame, fragment)
						.commit();
				progress.dismiss();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.e("Finish", "Da chay xong");
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progress = new ProgressDialog(getActivity());
			progress.setMessage("Scanning ... ");
			progress.setIndeterminate(false);
			progress.setCancelable(false);
			progress.show();
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			 progress = ProgressDialog.show(getActivity(),"Please Wait", "Loading Date", true);
			super.onProgressUpdate(values);
		}

	}

}