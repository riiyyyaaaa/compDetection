package com.compDetection;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class Convolution extends JApplet {

    static final float[][] operator = {

            { 0.11f, 0.11f, 0.11f, //operator[0] 平滑化
                    0.11f, 0.12f, 0.11f, 0.11f, 0.11f, 0.11f },

            { -0.11f, -0.11f, -0.11f, //operator[1] 鮮鋭化
                    -0.11f, 1.88f, -0.11f, -0.11f, -0.11f, -0.11f },

            { -1.0f, 0.0f, 1.0f, //operator[2] 垂直方向エッジ検出（正）
                    -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f },

            { 1.0f, 0.0f, -1.0f, //operator[3] 垂直方向エッジ検出（負）
                    2.0f, 0.0f, -2.0f, 1.0f, 0.0f, -1.0f },

            { -1.0f, -2.0f, -1.0f, //operator[4] 水平方向エッジ検出（正）
                    0.0f, 0.0f, 0.0f, 1.0f, 2.0f, 1.0f },

            { 1.0f, 2.0f, 1.0f, //operator[5] 水平方向エッジ検出（負）
                    0.0f, 0.0f, 0.0f, -1.0f, -2.0f, -1.0f }

    };

    static final int THRES = 180; //試行錯誤で決定

    Image img_src, img_edge;
    Image[] img_dest = new Image[2];

    public void init() {

        BufferedImage[] bimg_dest = new BufferedImage[6];

        //画像ファイルを読み込み、Image画像img_srcにする
        img_src = readImageFile(
                "C:\\Users\\riya\\Documents\\compDetection\\src\\main\\java\\com\\compDetection\\138.jpg");
        //Image画像img_srcをBufferedImage画像bimg_srcに変換する
        BufferedImage bimg_src = changeToBufferedImage(img_src);

        // ---------------------- 平滑化と鮮鋭化 ----------------------
        for (int i = 0; i < 2; i++) {
            //bimg_srcをoperator[i]でコンボリューションして、
            //bimg_dest[i]を得る
            //周辺は原画像のまま
            bimg_dest[i] = makeConvolution(bimg_src, operator[i], 0);
            //bimg_dest[i]をImage画像img_dest[i]にする
            img_dest[i] = changeToImage(bimg_dest[i]);
        }

        // ----------------------- エッジ検出 -------------------------
        for (int i = 2; i < 6; i++) { //縦方向ｘ２、横方向ｘ２
            //bimg_srcをoperator[i]でコンボリューションして、
            //bimg_dest[i]を得る
            //周辺はゼロ（黒）で埋める
            bimg_dest[i] = makeConvolution(bimg_src, operator[i], 1);
        }

        //bimg_dest[2]とbimg_dest[3]の白方向ORをとり
        //反転二値画像img_vertを生成する
        Image img_vert = createBrightORedBinaryImageInverted(bimg_dest[2], bimg_dest[3]);
        //bimg_dest[4]とbimg_dest[5]の白方向ORをとり
        //反転二値画像img_horiを生成する
        Image img_hori = createBrightORedBinaryImageInverted(bimg_dest[4], bimg_dest[5]);
        //垂直方向と水平方向の合成
        //img_vertとimg_horiの黒方向のORをとり
        //非反転画像img_edgeを生成する
        img_edge = createBlackORedBinaryImageNonInverted(img_vert, img_hori);

        BufferedImage bi = changeToBufferedImage(img_edge);
        //File f2 = new File("imgTest.png");
        //ImageIO.write(img_edge, "png", f2);
    }

    //画像ファイルを読み込みImageクラスの画像にするメソッド
    public Image readImageFile(String filename) {

        Image img = getImage(getDocumentBase(), filename);
        MediaTracker mtracker = new MediaTracker(this);
        mtracker.addImage(img, 0);
        try {
            mtracker.waitForAll();
        } catch (Exception e) {
        }
        return img;

    }

    //Imageクラスの画像をBufferedImageクラスの画像に変換するメソッド
    public BufferedImage changeToBufferedImage(Image img) {

        int width = img.getWidth(this);
        int height = img.getHeight(this);
        BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = bimg.createGraphics();
        g.drawImage(img, 0, 0, null);
        //追加
        //paint(g);
        return bimg;

    }

    //BufferedImageクラスの画像をコンボリューションして
    //BufferedImageクラスの画像を得るメソッド
    public BufferedImage makeConvolution(BufferedImage bimg, float[] operator, int type) {

        Kernel kernel = new Kernel(3, 3, operator);
        ConvolveOp convop;
        if (type == 1)
            convop = new ConvolveOp(kernel, ConvolveOp.EDGE_ZERO_FILL, null);
        else
            convop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage bimg_dest = convop.filter(bimg, null);
        return bimg_dest;

    }

    //二つのBufferedImage画像の白方向ORをとり
    //反転した二値Image画像を生成するメソッド
    private Image createBrightORedBinaryImageInverted(BufferedImage bimg1, BufferedImage bimg2) {

        int width = bimg1.getWidth();
        int height = bimg1.getHeight();
        int size = width * height;

        int[] rgb1 = new int[size];
        int[] rgb2 = new int[size];
        int[] rgb_dest = new int[size];

        bimg1.getRGB(0, 0, width, height, rgb1, 0, width);
        bimg2.getRGB(0, 0, width, height, rgb2, 0, width);

        Color color1, color2, color_dest;
        int r1, g1, b1;
        int r2, g2, b2;
        int d;

        for (int i = 0; i < size; i++) {
            color1 = new Color(rgb1[i]);
            r1 = color1.getRed();
            g1 = color1.getGreen();
            b1 = color1.getBlue();

            color2 = new Color(rgb2[i]);
            r2 = color2.getRed();
            g2 = color2.getGreen();
            b2 = color2.getBlue();

            //r1,g1,b1,r2,g2,g2の中の最大値でエッジの強さを判定する
            int maxd = Math.max(Math.max(Math.max(r1, g1), Math.max(b1, r2)), Math.max(g2, b2));
            if (maxd > THRES)
                d = 0; //黒にする
            else
                d = 255; //白にする
            color_dest = new Color(d, d, d);
            rgb_dest[i] = color_dest.getRGB();

        }

        return createImage(new MemoryImageSource(width, height, rgb_dest, 0, width));

    }

    //二つのImage画像の黒方向ORをとり
    //二値非反転Image画像を生成するメソッド
    private Image createBlackORedBinaryImageNonInverted(Image img1, Image img2) {

        Color color1, color2, color_dest;
        int r1, r2, d;

        int width = img1.getWidth(this);
        int height = img1.getHeight(this);
        int size = width * height;

        int[] rgb1 = new int[size];
        int[] rgb2 = new int[size];
        int[] rgb_dest = new int[size];

        //img1画像の一次元RGB配列を得る
        PixelGrabber grabber1 = new PixelGrabber(img1, 0, 0, width, height, rgb1, 0, width);
        try {
            grabber1.grabPixels();
        } catch (InterruptedException e) {
        }

        //img2画像の一次元RGB配列を得る
        PixelGrabber grabber2 = new PixelGrabber(img2, 0, 0, width, height, rgb2, 0, width);
        try {
            grabber2.grabPixels();
        } catch (InterruptedException e) {
        }

        for (int i = 0; i < size; i++) {

            color1 = new Color(rgb1[i]);
            r1 = color1.getRed(); //二値化されているのでR成分のみで良い
            color2 = new Color(rgb2[i]);
            r2 = color2.getRed();

            d = Math.min(r1, r2); //黒方向のORを採るので、値の小さい方にする
            color_dest = new Color(d, d, d);
            rgb_dest[i] = color_dest.getRGB();

        }

        return createImage(new MemoryImageSource(width, height, rgb_dest, 0, width));

    }

    //BufferedImageクラスの画像をImageクラスの画像に変換するメソッド
    public Image changeToImage(BufferedImage bimg) {

        Image img = Toolkit.getDefaultToolkit().createImage(bimg.getSource());
        return img;

    }

    public void paint(Graphics g) {
        //画像img_srcを左上に表示する
        g.drawImage(img_src, 10, 10, this);
        //画像img_dest[0]を右上に表示する
        g.drawImage(img_dest[0], 340, 10, this);
        //画像img_dest[1]を左下に表示する
        g.drawImage(img_dest[1], 10, 260, this);
        //画像img_edgeを右下に表示する
        g.drawImage(img_edge, 340, 260, this);

    }

}