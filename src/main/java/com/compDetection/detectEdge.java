package com.compDetection;

//import com.compDetection.ImageUtility.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;

public class detectEdge {
    public static double size = 100;

    public static void main(String[] args) throws IOException {
        ImageUtility iu = new ImageUtility();
        int number = 1; //ファイルの読み込み順番

        while (number <= 156) {
            calculate cal = new calculate();
            //File f = new File("C:\\Users\\riya\\Documents\\compDetection\\src\\main\\java\\com\\compDetection\\4.jpg");
            String str = "C:\\photo\\" + String.valueOf(number) + ".jpg";
            File f = new File(str);

            System.out.println(str);

            JFileChooser filechooser = new JFileChooser();
            String filename = filechooser.getName(f);
            BufferedImage read = ImageIO.read(f);
            int w = read.getWidth(), h = read.getHeight();

            //resize an image
            if (w != size || h != size) {
                //System.out.println("w, h, w*h, scale:" + w + ", " + h + ", " + w*h + "," + (double)80000/(h*w));
                System.out.println("Start Resize");
                File f2 = iu.scaleImage(f, (double) size / w, (double) size / h);
                BufferedImage read2 = ImageIO.read(f2);
                //System.out.println(read2.getWidth() + ", " + read2.getHeight());

                //detect edge
                f2 = cal.Mono(f2);
                cal.canny(f2, 0.5, 100, 50);
                cal.canny(f2, 0.5, 150, 100);
                cal.canny(f2, 1, 100, 50);
                cal.canny(f2, 1, 150, 100);
                cal.canny(f2, 1.5, 100, 50);
                //cal.canny(f2, 1.5, 150, 100);

            } else {
                File f2 = f;
                BufferedImage read2 = ImageIO.read(f2);
                System.out.println(read2.getWidth() + ", " + read2.getHeight());

                //detect edge
                File f3 = cal.Mono(f2);
                cal.canny(f3, 0.5, 100, 50);
                cal.canny(f3, 0.5, 150, 100);
                cal.canny(f3, 1, 100, 50);
                cal.canny(f3, 1, 150, 100);
                cal.canny(f3, 1.5, 100, 50);
                //cal.canny(f3, 1.5, 150, 100);

            }
            number++;

        }

        /*
        //test
        BufferedImage write = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                int rgb = iu.rgb((int) 50, (int) 50, (int) 50);
                if (j % 5 == 0) {
                    rgb = iu.rgb((int) 50, (int) 50, (int) 50);
                } else {
                    rgb = iu.rgb(0, 0, 0);
                }
                write.setRGB(j, i, rgb);
            }
        }
        File f2 = new File("test50.jpg");
        ImageIO.write(write, "jpg", f2);
        
        //test
        BufferedImage write2 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                int rgb = iu.rgb((int) 100, (int) 100, (int) 100);
                if (j % 5 == 0) {
                    rgb = iu.rgb((int) 100, (int) 100, (int) 100);
                } else {
                    rgb = iu.rgb(0, 0, 0);
                }
            }
        }
        File f3 = new File("test100.jpg");
        ImageIO.write(write, "jpg", f2);
        
        //cal.GaussianFilter();
        // Convolution con = new Convolution();
        //cal.monoGaussianFilter(f);
        //cal.firstX(read);
        //int ro = 1;
        //double[][] filter = cal.Gfilter(ro);
        //write = cal.convo(f, filter);
        //File f2 = new File("imgTest3.png");
        //ImageIO.write(write, "png", f2);
        //l.canny(cal.Mono(f), 1, 100, 50);
        //cal.specifyPosition(1, 1);
        
        */
    }

}
