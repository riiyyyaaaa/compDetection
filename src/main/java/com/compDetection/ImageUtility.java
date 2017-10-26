package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class ImageUtility {
    public static int a(int c) {
        return c >>> 24;
    }

    public static int r(int c) {
        return c >> 16 & 0xff;
    }

    public static int g(int c) {
        return c >> 8 & 0xff;
    }

    public static int b(int c) {
        return c & 0xff;
    }

    public static int gray(int c) {
        return c << 16 | c << 8 | c;
    }

    public static int rgb(int r, int g, int b) {
        return 0xff000000 | r << 16 | g << 8 | b;
    }

    public static int argb(int a, int r, int g, int b) {
        return a << 24 | r << 16 | g << 8 | b;
    }

    //画像を縮小
    public static void resize(File file) throws IOException {
        double scale = 0;
        BufferedImage read = ImageIO.read(file);
        //s BufferedImage destImage = resizeImage(read, scale);
    }

    /*
    public static void scaleImage(File in, File out, double scale) throws IOException {
        BufferedImage org = ImageIO.read(in);
        ImageFilter filter = new AreaAveragingScaleFilter((int) (org.getWidth() * scale),
                (int) (org.getHeight() * scale));
        ImageProducer p = new FilteredImageSource(org.getSource(), filter);
        java.awt.Image dstImage = Toolkit.getDefaultToolkit().createImage(p);
        BufferedImage dst = new BufferedImage(dstImage.getWidth(null), dstImage.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        g.drawImage(dstImage, 0, 0, null);
        g.dispose();
        ImageIO.write(dst, "jpeg", out);
    }
    */
    public static void main(String[] args) {

    }

}