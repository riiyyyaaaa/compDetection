package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import java.awt.Image;
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

    public static void main(String[] args) {

    }

}