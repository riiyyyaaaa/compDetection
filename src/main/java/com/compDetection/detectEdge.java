package com.compDetection;

//import com.compDetection.ImageUtility.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;

public class detectEdge {
    public static void main(String[] args) throws IOException {

        ImageUtility iu = new ImageUtility();
        calculate cal = new calculate();
        //File f = new File("C:\\Users\\riya\\Documents\\compDetection\\src\\main\\java\\com\\compDetection\\4.jpg");
        File f = new File("./132.jpg");
        JFileChooser filechooser = new JFileChooser();
        String filename = filechooser.getName(f);
        BufferedImage read = ImageIO.read(f);
        int w = read.getWidth(), h = read.getHeight();


        if(w!=300 || h!=300){
          //System.out.println("w, h, w*h, scale:" + w + ", " + h + ", " + w*h + "," + (double)80000/(h*w));
          System.out.println("\n\n\nStart Resize\n\n\n");
          f = iu.scaleImage(f,(double)300/w, (double)300/h);
          System.out.println("\n\n\nFinished to Resize!\n\n\n");
        }


        //cal.GaussianFilter();
        // Convolution con = new Convolution();
        //cal.monoGaussianFilter(f);
        //cal.firstX(read);
        //int ro = 1;
        //double[][] filter = cal.Gfilter(ro);
        //write = cal.convo(f, filter);
        //File f2 = new File("imgTest3.png");
        //ImageIO.write(write, "png", f2);
        cal.canny(cal.Mono(f), 1, 100, 50);
        //cal.specifyPosition(1, 1);

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
