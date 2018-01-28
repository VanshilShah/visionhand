package com.vanshil.visionhand;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vanshilshah on 2017-01-14.
 */

public class VisionProcessor {

    private static final String TAG = "VisionProcessor";
    private Mat last = null;
    private boolean showOriginal = true;

    private Preferences mPrefs;

    public VisionProcessor(Preferences mPrefs) {
        this.mPrefs = mPrefs;
    }
    Scalar[][] objectsToDetect = {
            //{new Scalar(10, 50, 100), new Scalar(30, 150, 255)},
            {new Scalar(110, 150, 100), new Scalar(130, 255, 255)},RED ORANGE
            //{new Scalar(40, 100, 30), new Scalar(80, 255, 255)}, GREEN
            {new Scalar(0, 50, 50), new Scalar(40, 255, 255)},
            {new Scalar(0, 50, 50), new Scalar(40, 255, 255)}
    };

    String[] objectNames = {
            "",
            "screwdriver",
            "person",
            "bolt"
    };


    public synchronized Mat process(Mat inputPicture) {
        last = inputPicture;
        Mat result = new Mat(inputPicture.size(), inputPicture.type());

        Imgproc.cvtColor(inputPicture, result, Imgproc.COLOR_BGR2HSV);

        Imgproc.blur(result, result, new Size(10, 10));


        List<MatOfPoint> allContours = new ArrayList<>();
        for(int i = 0; i < objectsToDetect.length; i++){
            Scalar[] range = objectsToDetect[i];
            if(objectNames[i].equals(ActionStatus.getInstance().getObject())) {

                List<MatOfPoint> contours = getContours(result, range[0], range[1]);

                MatOfPoint2f approxCurve = new MatOfPoint2f();
                for (MatOfPoint contour : new ArrayList<>(contours)) {
                    double area = Imgproc.contourArea(contour);
                    allContours.add(contour);
                    if (area < 750) {
                        contours.remove(contour);
                    } else {

                        MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

                        double approxDistance = Imgproc.arcLength(contour2f, true) * 0.02;
                        Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

                        MatOfPoint points = new MatOfPoint(approxCurve.toArray());

                        Rect rect = Imgproc.boundingRect(points);
                        Imgproc.rectangle(result, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(10, 255, 255));

                        double rectCenterX = rect.x + (rect.width / 2);
                        double centerX = inputPicture.width() / 2;
                        double pixelDistance = rectCenterX - centerX;
                        double angle = pixelDistance / inputPicture.width() * 60;//60 degrees is field of view of nexus 5
                        ActionStatus.getInstance().setTheta(angle);
                    }

                }
                //}
            }
        }



        Imgproc.drawContours(inputPicture, allContours, -1, new Scalar(10, 255, 255), 1);
        if(showOriginal){
            result.release();
        }
        else{
            inputPicture.release();
        }
        return showOriginal? inputPicture : result;

    }

    public List<MatOfPoint> getContours (Mat src, Scalar min, Scalar max){
        Mat filtered = new Mat(src.size(), src.type());
        Core.inRange(src, min, max, filtered);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(filtered, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("FOUND " + contours.size() + contours);

        hierarchy.release();
        filtered.release();
        return contours;

    }


    public void deliverTouchEvent(int x, int y) {
        if(last != null){

        }
        showOriginal = !showOriginal;

    }

    private void drawGrid(int cols, int rows, Mat drawMat) {
//        for (int i = 1; i < GRID_SIZE; i++) {
//            Imgproc.line(drawMat, new Point(0, i * rows / GRID_SIZE), new Point(cols, i * rows / GRID_SIZE), new Scalar(0, 255, 0, 255), 3);
//            Imgproc.line(drawMat, new Point(i * cols / GRID_SIZE, 0), new Point(i * cols / GRID_SIZE, rows), new Scalar(0, 255, 0, 255), 3);
//        }
    }

}