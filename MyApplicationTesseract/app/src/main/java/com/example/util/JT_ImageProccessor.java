package com.example.util;

import java.lang.reflect.Array;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;

//import javafx.scene.shape.DrawMode;

import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.*;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import java.util.Random;

public class JT_ImageProccessor {
	
	public static enum TextDirection {
		HORIZONTAL, 
		VERTICAL
	}
	
	// extract text area
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

	public static List<Rect> extractTextLine(Mat image, TextDirection textDirection) {
		
		List<Rect> textLines = new ArrayList<Rect>();
		
	    Mat imgGray = new Mat();
	    Mat imgSobel = new Mat();
	    Mat imgThreshold = new Mat();
	    Mat element = new Mat();
	    
	    //Mat dst = new Mat();
	    //Imgproc.resize(image, image, new Size(image.width()*2, image.height()*2), 0, 0, Imgproc.INTER_LINEAR);
	    
	    Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_RGB2GRAY); // chuyen sang anh gray
	    Imgproc.Sobel(imgGray, imgSobel, CvType.CV_8U, 1, 0, 3, 1, 0, Imgproc.BORDER_DEFAULT); // nhan cac canh, cac le
	    //Highgui.imwrite("picture/img_sobel_2.jpg", imgSobel);
	    Imgproc.threshold(imgSobel, imgThreshold, 0, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY); // chuyen sang anh den trang (binary image)
	    //Highgui.imwrite("picture/img_threshold_2.jpg", imgThreshold);
	    element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(10, 10) ); // chi so nay la tam thoi 
	    Imgproc.morphologyEx(imgThreshold, imgThreshold, Imgproc.MORPH_CLOSE, element); 
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(imgThreshold, contours, new Mat(), 0, 1); 
	    
	    //Mat contourImage = new Mat(image.size(), CvType.CV_8UC3);
	    //Random ran = new Random();
	    //for(int i = 0; i < contours.size(); ++i) {
	    //	Imgproc.drawContours(contourImage, contours, i, new Scalar(ran.nextInt(255), ran.nextInt(255), ran.nextInt(255)));
	    //}
	    //Highgui.imwrite("picture/contourImage9.jpg", contourImage);

	    //List<MatOfPoint2f> contoursPoly = new ArrayList<MatOfPoint2f>();
	    //for(int i = 0; i < contours.size(); ++i) {
	    //	contoursPoly.add(new MatOfPoint2f());
	    //}
	    
	    for(int i = 0; i < contours.size(); ++i) {
	    	/*if(contours.get(i).size().area() > 50) {
	    		Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), contoursPoly.get(i), 3, true);
	    		Mat newMat = new Mat();
	    		contoursPoly.get(i).convertTo(newMat, -1);
	    		Rect appRect = Imgproc.boundingRect(contours.get(i));
	    		
	    		if((appRect.x - 3) > 0) {
	    			appRect.x = appRect.x - 3;
	    			appRect.width = appRect.width + 3;
	    		}
	    		if((appRect.y - 3) > 0) {
	    			appRect.y = appRect.y - 3;
	    			appRect.height = appRect.height + 3;
	    		}
	    		
	    		//if(appRect.width > appRect.height) {
	    			textLines.add(appRect);
	    		//}
	    	}*/
	    	MatOfPoint2f approxCurve = new MatOfPoint2f();
	    	if(contours.get(i).size().area() > 50) {
	    		//Convert contours(i) from MatOfPoint to MatOfPoint2f
	            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
	            //Processing on mMOP2f1 which is in type MatOfPoint2f
	            double approxDistance = Imgproc.arcLength(contour2f, true)*0.1;
	            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

	            //Convert back to MatOfPoint
	            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

	            // Get bounding rect of contour
	            Rect rect = Imgproc.boundingRect(points);
	            textLines.add(rect);
	    	}
	    }
	    
	    if(textDirection == TextDirection.HORIZONTAL)
	    	getHorizontalLine(textLines);
	    else
	    	getVerticalLine(textLines);
	    
	    System.out.println(textLines.size());
	    return textLines;
	} 
	
	private static void getHorizontalLine(List<Rect> rectList) {
		//Boolean wentOn = true;
		//while(wentOn == true) {
			//wentOn = false;
			int counter = 0;
			Rect rect1, rect2, newRect;
			while(rectList.size() > 1 && (counter < rectList.size() - 1)) {
				rect1 = rectList.get(counter);
				rect2 = rectList.get(counter + 1);
				newRect = checkHorizontalRect(rect1, rect2);
				if(newRect != null) {
					rectList.remove(counter);
					rectList.set(counter, newRect);
				} else {
					counter++;
				}
			}
			
			counter = 0;
			Rect rectCounter;
			while(counter < rectList.size() - 1) {
				rectCounter = rectList.get(counter);
				
				int counterRun = counter + 1;
				while(counterRun < rectList.size()) {
					newRect = checkHorizontalRect(rectCounter, rectList.get(counterRun));
					if(newRect != null) {
						//rectCounter = newRect;
						rectList.set(counter, newRect);
						rectList.remove(counterRun);
					}else {
						counterRun++;
					}
				}
				
				counter++;
			}
		//}
	}
	
	private static void getVerticalLine(List<Rect> rectList) {
		
		
		//Boolean wentOn = true;
		//while(wentOn == true) {
			//wentOn = false;
			/*int counter = 0;
			while(rectList.size() > 1 && (counter < rectList.size() - 1)) {
				Rect rect1 = rectList.get(counter);
				Rect rect2 = rectList.get(counter + 1);
				Rect newRect = checkVerticalRect(rect1, rect2);
				if(newRect != null) {
					rectList.remove(counter);
					rectList.set(counter, newRect);
				} else {
					counter++;
				}
			}*/
			
			int counter = 0;
			while(counter < rectList.size() - 1) {
				Rect rectCounter = rectList.get(counter);
				
				int counterRun = counter + 1;
				while(counterRun < rectList.size()) {
					Rect newRect = checkVerticalRect(rectCounter, rectList.get(counterRun));
					if(newRect != null) {
						//rectCounter = newRect;
						rectList.set(counter, newRect);
						rectList.remove(counterRun);
					}else {
						counterRun++;
					}
				}
				
				counter++;
			} 
		//}
	}
	
	private static Rect checkHorizontalRect(Rect rect1, Rect rect2) {
		int rect1Bottom = rect1.y + rect1.height;
		int rect2Bottom = rect2.y + rect2.height; 
		int rect1Right = rect1.x + rect1.width;
		int rect2Right = rect2.x + rect2.width;
		
		if((rect1.y <= rect2.y && rect2.y <= rect1Bottom)
				|| (rect2.y <= rect1.y && rect1.y <= rect2Bottom)) {
			int x = rect1.x < rect2.x ? rect1.x : rect2.x;
			int y = rect1.y < rect2.y ? rect1.y : rect2.y;
			int width = (rect1Right > rect2Right ? rect1Right : rect2Right) - x;
			int height = (rect1Bottom > rect2Bottom ? rect1Bottom : rect2Bottom) - y;
			
			return new Rect(x, y, width, height);
		}
		
		return null;
	}
	
	private static Rect checkVerticalRect(Rect rect1, Rect rect2) {
		int rect1Bottom = rect1.y + rect1.height;
		int rect2Bottom = rect2.y + rect2.height; 
		int rect1Right = rect1.x + rect1.width;
		int rect2Right = rect2.x + rect2.width;
		
		if((rect1.x <= rect2.x && rect2.x <= rect1Right)
				|| (rect2.x <= rect1.x && rect1.x <= rect2Right)) {
			int x = rect1.x < rect2.x ? rect1.x : rect2.x;
			int y = rect1.y < rect2.y ? rect1.y : rect2.y;
			int width = (rect1Right > rect2Right ? rect1Right : rect2Right) - x;
			int height = (rect1Bottom > rect2Bottom ? rect1Bottom : rect2Bottom) - y;
			
			return new Rect(x, y, width, height);
		}
		
		return null;
	}
}
