package com.example.util;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.data.MySingleton;
import com.example.lkhuyen.myapplicationtesseract.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

public class DrawingView extends ImageView {

	//
	private static final int RADIUS = 200;
	//
	private static final int FACTOR = 2;

	private Matrix matrix = new Matrix();

	private float mPointX = -1.0f, mPointY = -1.0f;

	private static final float TOUCH_TOLERANCE = 0;
	private Path drawPath;
	private Paint drawPaint;
	private Paint canvasPaint;
	private int paintColor = Color.BLACK;
	private Canvas drawCanvas;
	private Bitmap canvasBitmap;
	float tX;
	float tY;
	private float mX;
	private float mY;

	DisplayMetrics metrics;

	private float a, b;

	Mat originalImage;

	Handler mHandler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case -1:
				postInvalidate();
				break;
			}
		};
	};

	public void setImageBitmap(Bitmap bm) {
	};

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupDrawing();

		byte[] dataBitmap = MySingleton.getInstance().mBitmapByteArray;
		
		if(dataBitmap == null){
			canvasBitmap= ((BitmapDrawable) this.getDrawable()).getBitmap();
		}else{

		canvasBitmap = BitmapFactory.decodeByteArray(dataBitmap, 0,
				dataBitmap.length);
		}

        originalImage = new Mat();

		Utils.bitmapToMat(canvasBitmap, originalImage);
		// canvasBitmap = rotationImage(dataBitmap);
		// canvasBitmap = getResizedBitmap(canvasBitmap);

		matrix.setScale(FACTOR, FACTOR);
	}

	//
	private void drawMagnifierPart(Canvas canvas) {
		if (mPointX == -1.0f || mPointY == -1.0f)
			return;

		Path path = new Path();

		path.addCircle(RADIUS, RADIUS, RADIUS, Direction.CW);

		//
		canvas.drawBitmap(canvasBitmap, 0, 0, null);
		// ,
		canvas.translate(mPointX - RADIUS, mPointY - RADIUS * 2);
		canvas.clipPath(path);
		//
		canvas.translate(RADIUS - mPointX * FACTOR, RADIUS - mPointY * FACTOR);

		canvas.drawBitmap(canvasBitmap, matrix, null);

	}

	private void setupDrawing() {
		drawPath = new Path();
		drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		drawPaint.setColor(paintColor);
		drawPaint.setStrokeWidth(2);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeJoin(Paint.Join.MITER);
		drawPaint.setStrokeCap(Paint.Cap.BUTT);
		drawPaint.setAntiAlias(true);
		canvasPaint = new Paint(Paint.DITHER_FLAG);
		canvasPaint.setFilterBitmap(true);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		canvasBitmap = canvasBitmap.copy(Config.ARGB_8888, true);
		drawCanvas = new Canvas(canvasBitmap);
		invalidate();
	}

	@SuppressLint("DrawAllocation")
	protected void onDraw(Canvas canvas) {
		Rect dest = new Rect(0, 0, canvasBitmap.getWidth(),
				canvasBitmap.getHeight());
		Paint paint = new Paint();
		paint.setFilterBitmap(true);
		canvas.drawBitmap(canvasBitmap, null, dest, canvasPaint);
		canvas.drawPath(drawPath, drawPaint);
		drawMagnifierPart(canvas);
	}

	public Bitmap getResizedBitmap(Bitmap bm) {
		metrics = getContext().getResources().getDisplayMetrics();
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
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);

		return resizedBitmap;
	}

	private void touch_start(float x, float y) {
		drawPath.reset();
		drawPath.moveTo(x, y);
		mX = x;
		mY = y;
		a = x;
		b = y;
	}

	private void touch_move(float x) {
		float dx = x - mX;
		// float dy=Math.abs(y-mY);
		if (dx >= TOUCH_TOLERANCE) {
			mX = x;
			// mY=y;
		} else {
			drawPath.setLastPoint(x, mY);
		}
		drawPath.lineTo(mX, mY);
		// invalidate();
	}

	private void touch_up(float tX2) {
		drawPath.lineTo(mX, mY);
		// drawCanvas.drawPath(drawPath, drawPaint);
		drawCanvas.drawLine(a, b, tX2, mY, drawPaint);
		drawPath.reset();
	}

	public boolean onTouchEvent(MotionEvent event) {
		tX = event.getX();
		tY = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touch_start(tX, tY);
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(tX);
			mPointX = event.getX();
			mPointY = event.getY();
			mHandler.sendEmptyMessage(-1);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
//			touch_up(tX);
			mPointX = -1.0f;
			mPointY = -1.0f;
			mHandler.sendEmptyMessage(-1);
			drawTextRect(tX);
			break;
		}
		invalidate();
		return true;
	}

	public void drawTextRect(float tX2) {
		List<org.opencv.core.Rect> textLines = MySingleton.getInstance().textLines;
		int rectHeight = 0;
		int textBounderyY = 0;
		float min = 500;
		for (int i = 0; i < textLines.size( ); i++) {
			org.opencv.core.Rect textRect = new org.opencv.core.Rect();
			textRect = textLines.get(i);
			if(0 < (b-textRect.y) && (b-textRect.y) < min){
				min = b-textRect.y;
				rectHeight = textRect.height;
				textBounderyY = textRect.y;
			}
		}

		org.opencv.core.Rect rectRead = new org.opencv.core.Rect((int)a, textBounderyY, (int)Math.abs(tX2-a), rectHeight);
		Mat croppedImage = new Mat(originalImage, rectRead);
        croppedImage = AKImageProcessor.toThreshold(croppedImage);
        Bitmap extraTextImage = Bitmap.createBitmap(rectRead.width,
                rectRead.height, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(croppedImage, extraTextImage);

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.alert_dialog_layout);
        ImageView imgView=(ImageView)dialog.findViewById(R.id.alertImageView);
        TextView tv = (TextView) dialog.findViewById(R.id.tvTextExtraed);
        imgView.setImageBitmap(extraTextImage);
        dialog.show();
        TessBaseAPI extraText = MySingleton.getInstance().extraTextTesseract;

        extraText.setImage(extraTextImage);
        tv.setText(extraText.getUTF8Text());
        MySingleton.getInstance().textEtraded = extraText.getUTF8Text().trim();


//		Rect textRect = new Rect((int)a, textBounderyY, (int)tX2, textBounderyY+rectHeight);
//		drawCanvas.drawRect(textRect,drawPaint);
	}

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
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
}