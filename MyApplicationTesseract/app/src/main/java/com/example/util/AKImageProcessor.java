package com.example.util;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class AKImageProcessor {

	private final static int LOW_THRESHOLD = 70;
	private final static int HIGHT_THRESHOLD = 140;
	private final static int NOISE_CONTOUR_SIZE = 10;
	private final static int MIN_LINE_WIDTH = 10;
	private final static int MIN_LINE_HEIGHT = 10;

	// Load OpenCV library
	// Throw exception if opencv is fail to load
	// CALL THIS FUNCTION BEFORE DOING ANYTHING ELSE
	public static void InitOpenCV() {

		try {
			System.loadLibrary("opencv_java2410");
		} catch (UnsatisfiedLinkError e) {
			throw new UnsatisfiedLinkError("Failed to load Opencv library!\n" + e);
		}

	}

	// no use
	public static Mat toThreshold(Mat image) {

		Mat imgGray = new Mat();
		Mat imgSobel = new Mat();
		Mat imgThreshold = new Mat();
		Mat temp = new Mat();

		Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_RGB2GRAY); // chuyen sang
																	// anh gray
		// Imgproc.Sobel(imgGray, imgSobel, CvType.CV_8U, 1, 0, 3, 1, 0,
		// Imgproc.BORDER_DEFAULT); // nhan cac canh, cac le
		// Photo.fastNlMeansDenoising(imgGray, imgGray, 7, 7, 21);
		// Highgui.imwrite("picture/imgh33_sobel.jpg", imgSobel);
		// Imgproc.GaussianBlur(imgGray, imgGray, new Size(5,5), 0);
		// Imgproc.threshold(imgGray, imgThreshold, 0, 255, Imgproc.THRESH_OTSU
		// + Imgproc.THRESH_BINARY); // chuyen sang anh den trang (binary image)
		// Imgproc.threshold(imgThreshold, imgThreshold, 0, 255,
		// Imgproc.THRESH_BINARY_INV); // chuyen sang anh den trang (binary
		// image)
		// Photo.fastNlMeansDenoising(imgThreshold, imgThreshold, 7, 7, 21);
		// Highgui.imwrite("picture/imgh37_afterblur_thresh_Otsu.png",
		// imgThreshold);
		// Imgproc.GaussianBlur(image, temp, new Size(3,3), 0);
		// Highgui.imwrite("picture/imgh37_gausianFromGray.png", temp);

		/*
		 * Imgproc.GaussianBlur(image, temp, new Size(3, 3), 0);
		 * Imgproc.cvtColor(temp, temp, Imgproc.COLOR_RGB2GRAY);
		 * Imgproc.threshold(temp, temp, 0, 255, Imgproc.THRESH_BINARY +
		 * Imgproc.THRESH_OTSU);
		 * Highgui.imwrite("picture/img13_Blur_gray_threshold.png", temp);
		 */

		Imgproc.adaptiveThreshold(imgGray, imgThreshold, 255,
				Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 21, 20);
//		Highgui.imwrite("picture/imgh37_AdaptiveThreshold_15_40.png",
//				imgThreshold);

		// Imgproc.GaussianBlur(imgThreshold, imgThreshold, new Size(1,1), 0);
		// Imgproc.GaussianBlur(imgThreshold, imgThreshold, new Size(5,5), 0);
		// Highgui.imwrite("picture/imgh37_AdaptiveThreshold_3_2_Blur11.png",
		// imgThreshold);

		// Imgproc.threshold(imgThreshold, imgThreshold, 0, 255,
		// Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY); // chuyen sang anh den
		// trang (binary image)
		// Highgui.imwrite("picture/img13_Gausian_thresh_Otsu3.png",
		// imgThreshold);
		return imgThreshold;
	}

	// extract text lines of image 
	public static List<Rect> extractTextLine(Mat image) {

		Mat imgGray = new Mat();
		Mat canny = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		List<Rect> textLines = new ArrayList<Rect>();

		Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_RGB2GRAY);

		Imgproc.Canny(imgGray, canny, LOW_THRESHOLD, HIGHT_THRESHOLD, 3, false);
//		Highgui.imwrite("picture/cannyImage.png", canny);

		Imgproc.findContours(canny, contours, new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));


		for (int i = 0; i < contours.size(); ++i) {
			MatOfPoint aContour = contours.get(i);
			if ((aContour.width() * aContour.height()) > NOISE_CONTOUR_SIZE) {
				Rect rect = Imgproc.boundingRect(contours.get(i));
				textLines.add(rect);
			}
		}

		getHorizontalLine(textLines);

		removeNoiseLine(textLines);
		
		textLines = reverseList(textLines);
		
		return textLines;
	}
	
	public static Rect extractTextArea(Mat image) {
		Mat grayImage = new Mat();
		Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_RGB2GRAY);  // convert to gray image

		MatOfKeyPoint matOfKeypoint = new MatOfKeyPoint();
		FeatureDetector fd = FeatureDetector.create(FeatureDetector.DYNAMIC_FAST); // detect keyPoint
		fd.detect(image, matOfKeypoint);
		
		return surroundKeyPoint(image, matOfKeypoint);
	}
	
	public static Rect surroundKeyPoint(Mat image, MatOfKeyPoint matOfKeyPoint) {
		List<KeyPoint> keypointList = matOfKeyPoint.toList();
		Point p0 = keypointList.get(0).pt;
		double minX = p0.x;
		double maxX = p0.x;
		double minY = p0.y;
		double maxY = p0.y;

		double x, y;
		Point p;
		for (int i = 1; i < keypointList.size(); ++i) {
			p = keypointList.get(i).pt;
			x = p.x;
			y = p.y;

			if (x < minX) {
				minX = x;
			}
			;
			if (x > maxX) {
				maxX = x;
			}
			;
			if (y < minY) {
				minY = y;
			}
			;
			if (y > maxY) {
				maxY = y;
			}
			;
		}
		
		Rect textArea = new Rect();
		if((minX - 2) > 0) minX = minX - 2; // de tranh mat net chu 
		if((minY - 2) > 0) minY = minY - 2; // de tranh mat net chu
		textArea.x = (int)Math.floor(minX);
		textArea.y = (int)Math.floor(minY);
		textArea.width = (int)Math.ceil(maxX - minX);
		textArea.height = (int)Math.ceil(maxY - minY);

		return textArea;
	}
	
	// reverse text lines order
	private static List reverseList(List list) {
		
		List newList = new ArrayList();
		
		for(int i = list.size() - 1; i >= 0; --i) {
			newList.add(list.get(i));
		}
		
		return newList;
	}

	// merge contours into lines
	private static void getHorizontalLine(List<Rect> rectList) {

		int counter = 0;
		Rect rect1, rect2, newRect;

		while (rectList.size() > 1 && (counter < rectList.size() - 1)) {
			rect1 = rectList.get(counter);
			rect2 = rectList.get(counter + 1);
			newRect = checkHorizontalRect(rect1, rect2);
			if (newRect != null) {
				rectList.remove(counter + 1);
				rectList.set(counter, newRect);

			} else {
				counter++;
			}
		}

		counter = 0;
		Rect rectCounter;
		while (counter < rectList.size() - 1) {
			rectCounter = rectList.get(counter);

			int counterRun = counter + 1;
			while (counterRun < rectList.size()) {
				newRect = checkHorizontalRect(rectCounter,
						rectList.get(counterRun));
				if (newRect != null) {
					rectList.set(counter, newRect);
					rectList.remove(counterRun);
				} else {
					counterRun++;
				}
			}

			counter++;
		}
	}

	// check if 2 rects are in the same line
	private static Rect checkHorizontalRect(Rect rect1, Rect rect2) {

		int rect1Bottom = rect1.y + rect1.height;
		int rect2Bottom = rect2.y + rect2.height;
		int rect1Right = rect1.x + rect1.width;
		int rect2Right = rect2.x + rect2.width;

		if ((rect1.y <= rect2.y && rect2.y <= rect1Bottom)
				|| (rect2.y <= rect1.y && rect1.y <= rect2Bottom)) {
			int x = rect1.x < rect2.x ? rect1.x : rect2.x;
			int y = rect1.y < rect2.y ? rect1.y : rect2.y;
			int width = (rect1Right > rect2Right ? rect1Right : rect2Right) - x;
			int height = (rect1Bottom > rect2Bottom ? rect1Bottom : rect2Bottom) - y;

			return new Rect(x, y, width, height);
		}

		return null;
	}

	// remove rect that does not satisfy min of line size.
	private static void removeNoiseLine(List<Rect> textLines) {
		int counter = 0;
		while(counter < textLines.size()){
			Rect line = textLines.get(counter);
			if((line.width < MIN_LINE_WIDTH)
					|| (line.height < MIN_LINE_HEIGHT)) {
				textLines.remove(counter);
			} else {
				counter++;
			}
		}
	}
	
	// convert a TEXT LINE to threshold image
	public static Mat toThresholdLine(Mat textLineImage) {
		
		Mat imgGray = new Mat();
		Mat imgThreshold = new Mat();

		Imgproc.cvtColor(textLineImage, imgGray, Imgproc.COLOR_RGB2GRAY);
		
		int kernel = textLineImage.height();
		if(kernel % 2 == 0) kernel -= 1; // adaptiveThreshold only accept odd kernel
		
		Imgproc.adaptiveThreshold(imgGray, imgThreshold, 255,
				Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, kernel, 20);
		
		return imgThreshold;
	}

	// convert all text lines to threshold image
	public static List<Mat> toThresholdLIne(Mat image, List<Rect> textLines) {
		
		List<Mat> thresholdLines = new ArrayList<Mat>();
		
		for(int i = 0; i < textLines.size(); ++i) {
			Mat textLineImage = new Mat(image, textLines.get(i));
			Mat imgThreshold = toThresholdLine(textLineImage);
			
			thresholdLines.add(imgThreshold);
		}
		
		return thresholdLines;
	}
}
