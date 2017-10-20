package com.compDetection;

//import com.compDetection.ImageUtility.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class detectEdge {
    public static void main(String[] args) throws IOException {

        ImageUtility iu = new ImageUtility();
        calculate cal = new calculate();
        File f = new File(
                "C:\\Users\\riya\\Documents\\compDetection\\src\\main\\java\\com\\compDetection\\monoNaru.png");
        BufferedImage read = ImageIO.read(f);
        int w = read.getWidth(), h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        //ArrayList<Integer> rgbList = cal.firstX(read);

        //cal.Mono(f);
        //cal.GaussianFilter();
        // Convolution con = new Convolution();
        //con.init();
        //cal.test(f);
        //cal.monoGaussianFilter(f);
        //cal.firstX(read);
        //int ro = 1;
        //double[][] filter = cal.Gfilter(ro);
        //write = cal.convo(f, filter);
        //File f2 = new File("imgTest3.png");
        //ImageIO.write(write, "png", f2);
        //cal.canny1(f);
        cal.specifyPosition(1, 1);

        /*
        for (int i = 0; i < rgbList.size(); i++) {
            if (i % 4 == 0) {
                System.out.print("\n");
            }
            System.out.print(" " + i + ":" + rgbList.get(i));
        }
        */
        /*
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = read.getRGB(x, y);
                int r = 255 - iu.r(c);
                int g = 255 - iu.g(c);
                int b = 255 - iu.b(c);
                int rgb = iu.rgb(r, g, b);
                write.setRGB(x, y, rgb);
                // System.out.print("(" + x + ", " + y + ") = " + rgb + "\n");
            }
        
            File f2 = new File("imgTest.png");
            ImageIO.write(write, "png", f2);
        }
        */

    }

}