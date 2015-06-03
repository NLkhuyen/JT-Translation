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

public class JT_ImageProccessor {
	
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

	public static List<Rect> extractTextLine(Mat image) {
		
		List<Rect> textLines = new ArrayList<Rect>();
		
	    Mat imgGray = new Mat();
	    Mat imgSobel = new Mat();
	    Mat imgThreshold = new Mat();
	    Mat element = new Mat();
	    
	    Imgproc.cvtColor(image, imgGray, Imgproc.COLOR_RGB2GRAY); // chuyen sang anh gray
	    Imgproc.Sobel(imgGray, imgSobel, CvType.CV_8U, 1, 0, 3, 1, 0, Imgproc.BORDER_DEFAULT); // nhan cac canh, cac le
	    Imgproc.threshold(imgSobel, imgThreshold, 0, 255, Imgproc.THRESH_OTSU + Imgproc.THRESH_BINARY); // chuyen sang anh den trang (binary image)
	    element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(17, 3) ); // chi so nay la tam thoi 
	    Imgproc.morphologyEx(imgThreshold, imgThreshold, Imgproc.MORPH_CLOSE, element); 
	    List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
	    Imgproc.findContours(imgThreshold, contours, new Mat(), 0, 1); 

	    List<MatOfPoint2f> contoursPoly = new ArrayList<MatOfPoint2f>();
	    for(int i = 0; i < contours.size(); ++i) {
	    	contoursPoly.add(new MatOfPoint2f());
	    }
	    for(int i = 0; i < contours.size(); ++i) {
	    	if(contours.get(i).size().area() > 50) {
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
	    	}
	    }
	    
	    return textLines;
	} 
}
