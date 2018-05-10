package com.compDetection;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class dividImage extends JFrame {
    public static final ImageUtility iu = new ImageUtility();
    public static final int num = 4; //分割するブロックの数(1辺)
    Graphics g;

    public static void main(String[] args) throws IOException {
        String dir = "C:\\detectEdge\\sea.jpg";
        File file = new File(dir);
        BufferedImage read = ImageIO.read(file);
        int colorF[] = extrColorF(intoBlock(read));

        System.out.println("average ");
        for (int i = 0; i < colorF.length; i++) {
            System.out.println(colorF[i]);
        }

        new dividImage(colorF);

    }

    /**
     * 引数のデータからブロックにわけたBufferedImageを配列で返却
     */
    public static BufferedImage[] intoBlock(BufferedImage origin) {
        int w = origin.getWidth();
        int h = origin.getHeight();
        int intw = w / num;
        int inth = h / num;
        BufferedImage[] block = new BufferedImage[num * num];
        int count = 0;

        System.out.println("w:" + intw + ", h:" + inth);

        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                block[count] = origin.getSubimage(j * intw, i * inth, intw, inth);
                System.out.println(block[count]);
                count++;
            }
        }
        return block;
    }

    /**
     * ブロックごとの色特徴ベクトルとなるRGB値を返却
     */
    public static int[] extrColorF(BufferedImage[] read) {
        int[] colorF = new int[num * num];

        int count = 0;
        while (count < num * num) {
            int val = 0; //とりあえず今は平均値
            for (int i = 0; i < read[count].getHeight(); i++) {
                for (int j = 0; j < read[count].getWidth(); j++) {
                    val += iu.r(read[count].getRGB(j, i));
                }
            }
            colorF[count] = val / (read[count].getHeight() * read[count].getWidth());
            count++;
        }
        return colorF;
    }

    /**
     * ブロックを一つの画像として出力
     * 画像サイズは一定
     */
    public dividImage(int[] block) {

        for (int i = 0; i < num; i++) {
            for (int j = 0; j < num; j++) {
                g.setColor(new Color(block[i * 4 + j], 0, 0));
                g.fillRect(j * 10, i * 10, 10, 10);
            }
        }
        g.dispose();
    }
}