package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.AreaAveragingScaleFilter;
import java.awt.Graphics2D;
import javax.swing.JFileChooser;

import java.io.File;
import javax.imageio.*;
import java.awt.*;

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

    //画像を縮小, scale で x, y のサイズ変更。x : y = 1 : 1
    public static File scaleImage(File in, double scaleX, double scaleY) throws IOException {
        //System.out.println("scale is " + scale);
        BufferedImage org = ImageIO.read(in);
        ImageFilter filter = new AreaAveragingScaleFilter((int) (org.getWidth() * scaleX),
                (int) (org.getHeight() * scaleY));
        JFileChooser filechooser = new JFileChooser();
        String filename = filechooser.getName(in);
        ImageProducer p = new FilteredImageSource(org.getSource(), filter);
        java.awt.Image dstImage = Toolkit.getDefaultToolkit().createImage(p);
        //BufferedImage dst = new BufferedImage(dstImage.getWidth(null), dstImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
        BufferedImage dst = new BufferedImage(dstImage.getWidth(null), dstImage.getHeight(null),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = dst.createGraphics();
        g.drawImage(dstImage, 0, 0, null);
        g.dispose();
        File out = new File("C:\\detectEdge\\edgetest", "mono" + filename);
        ImageIO.write(dst, "jpg", out);

        return out;
    }

    /**
     * 文字列を画像に書き込む
     */
    public static BufferedImage drawStr(BufferedImage read, double value, int width) {
        Graphics graphics = read.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.drawString(String.valueOf(value), width - 50, width - 20);

        return read;
    }

    /**
     * 2つ画像を横に並べた画像の作成
     */
    public static BufferedImage outputResult(BufferedImage img1, BufferedImage img2) {
        int h = img1.getHeight();
        if (img1.getHeight() < img2.getHeight()) {
            h = img2.getHeight();
        }
        BufferedImage write = new BufferedImage(img1.getWidth() + img2.getWidth(), h, BufferedImage.TYPE_INT_RGB);
        Graphics g = write.getGraphics();
        g.drawImage(img1, 0, 0, null);
        g.drawImage(img2, img1.getWidth(), 0, null);

        return write;
    }

    public static void main(String[] args) {

    }

}
