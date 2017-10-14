package com.compDetection;
/*
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class detectionEdge {

    /** 
     * 輪郭を抽出して、輪郭を囲む四角形を描画します。 
     
    public static void main(String[] args) {
        System.loadLibrary("opencv");
        Mat src = Highgui.imread("img.png", 0);
        Mat hierarchy = Mat.zeros(new Size(5, 5), CvType.CV_8UC1);
        Mat invsrc = src.clone();
        Core.bitwise_not(src, invsrc);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        //一番外側のみでOK  
        Imgproc.findContours(invsrc, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_TC89_L1);
        Mat dst = Mat.zeros(new Size(src.width(), src.height()), CvType.CV_8UC3);
        Scalar color = new Scalar(255, 255, 255);

        Imgproc.drawContours(dst, contours, -1, color, 1);

        int i = 0;
        for (i = 0; i < contours.size(); i++) {
            MatOfPoint ptmat = contours.get(i);

            //頂点描画  
            /* 
             int k=0; 
             for(k=0;k<ptmat.height();k++) 
            { 
                double[] m=ptmat.get(k, 0); 
                vertex.x=m[0]; 
                vertex.y=m[1]; 
                Core.circle(dst, vertex, 2, color,-1); 
            }

            color = new Scalar(255, 0, 0);
            MatOfPoint2f ptmat2 = new MatOfPoint2f(ptmat.toArray());
            RotatedRect bbox = Imgproc.minAreaRect(ptmat2);
            Rect box = bbox.boundingRect();
            Core.circle(dst, bbox.center, 5, color, -1);
            color = new Scalar(0, 255, 0);
            Core.rectangle(dst, box.tl(), box.br(), color, 2);

        }
        Highgui.imwrite("test.png", dst);
    }
}
*/