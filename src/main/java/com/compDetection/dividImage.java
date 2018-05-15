package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class dividImage extends JFrame {
    public static final ImageUtility iu = new ImageUtility();
    public static final int num = 4; // 分割するブロックの数(1辺)
    // Graphics g;

    public static void main(String[] args) throws IOException {
        String dir = "C:\\detectEdge\\fl.jpg";
        // 画像をモノクロで出力
        File file = new File(dir);
        BufferedImage read = ImageIO.read(file);
        int colorF[][] = extrColorF(intoBlock(read));

        // System.out.println("average ");
        // for (int i = 0; i < colorF.length; i++) {
        // System.out.println(colorF[i][0]);
        // }
        outputBlock(colorF);
        // new dividImage(colorF);

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
                count++;
            }
        }
        return block;
    }

    /**
     * ブロックごとの色特徴ベクトルとなるRGB値を返却
     */
    public static int[][] extrColorF(BufferedImage[] read) {
        int[][] colorF = new int[num * num][3];

        int count = 0;
        while (count < num * num) {
            int valr = 0; // とりあえず今は平均値
            int valg = 0;
            int valb = 0;
            for (int i = 0; i < read[count].getHeight(); i++) {
                for (int j = 0; j < read[count].getWidth(); j++) {
                    valr += iu.r(read[count].getRGB(j, i));
                    valg += iu.g(read[count].getRGB(j, i));
                    valb += iu.b(read[count].getRGB(j, i));
                }
            }
            colorF[count][0] = valr / (read[count].getHeight() * read[count].getWidth());
            colorF[count][1] = valg / (read[count].getHeight() * read[count].getWidth());
            colorF[count][2] = valb / (read[count].getHeight() * read[count].getWidth());
            // System.out.println(colorF[count][0] + ", " + colorF[count][1] + "," +
            // colorF[count][2]);
            count++;
        }
        return colorF;
    }

    /**
     * 分割し平均にしたブロックを1つのBUufferedImageとして出力
     */
    public static BufferedImage outputBlock(int[][] block) throws IOException {
        int bsize = 20;
        int numB = 0;
        BufferedImage output = new BufferedImage(num * bsize, num * bsize, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < num * bsize; i++) {
            for (int j = 0; j < num * bsize; j++) {
                // System.out.println("(i,j) = (" + i + "," + j + ")");
                numB = (int) (i / (bsize)) * 4 + (int) (j / (bsize));
                // System.out.print((int) (i / (bsize)) * 4 + (int) (j / (bsize)) + " ");

                output.setRGB(j, i, iu.argb(0, block[numB][0], block[numB][1], block[numB][2]));

            }
            // System.out.println();
        }
        File file = new File("bockImagefl.jpg");
        ImageIO.write(output, "jpg", file);

        return output;
    }

    // /**
    // * ブロックを一つの画像として出力 画像サイズは一定
    // */
    // public dividImage(int[] block) {
    // this.setSize(300, 200);
    // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    // this.setVisible(true);
    // this.g = this.getGraphics();
    // g.setColor(Color.RED);
    // g.fillRect(50, 50, 100, 100);

    // // for (int i = 0; i < num; i++) {
    // // for (int j = 0; j < num; j++) {
    // // g.setColor(new Color(block[i * 4 + j], 0, 0));
    // // g.fillRect(j * 100, i * 100, 100, 100);

    // // }
    // // }
    // g.dispose();
    // }
}