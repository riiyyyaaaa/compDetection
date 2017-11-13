package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

/**
 * 検索対象の画像についてエッジのある範囲を保管(探索、走査)
 */
public class searchPre {
    public static final int interval = 30; //探索点を置く間隔
    public static final int w = 300; //めんどいので画像の横幅を定義しておく

    public static void main(String[] args) throws IOException {
        File file = new File("./searchtest.jpg");
        extractFeature(file, file);
    }

    /**
     * 任意の座標から探索点を隣接８方向に走査。
     * 255以外のピクセルにぶつかるかx、y軸に接したら走査終了
     * （！要改変！）RGB値が100以上をエッジとしているけどヒストグラムによる適切判定すればいらないかも（！要改変！）
     */
    public static double[][][] extractFeature(File queryfile, File brousefile) throws IOException {
        ImageUtility iu = new ImageUtility();
        BufferedImage readQ = ImageIO.read(queryfile); //入力画像
        BufferedImage readB = ImageIO.read(brousefile); //被検索画像

        int h = readQ.getHeight(), w = readQ.getWidth();
        boolean notfind = true;
        int num = 0; //探索点のナンバリング
        //探索点（20*20）の用意. RGB値、距離、元の位置(ナンバリングしておく？)
        double[][][] point = new double[((h / interval) - 1) * ((h / interval) - 1)][8][3];

        System.out.println("h, w:" + h + "," + w);

        //探索点の放出(2値化、値の近似、省略は行わないとする,とりあえず)
        for (int y = interval; y < h - 1; y += interval) {
            for (int x = interval; x < w - 1; x += interval) {
                for (int i = 0; i < 8; i++) {
                    //1つの探索点から8方向に走査
                    int siX = (int) (Math.sqrt(2) * Math.cos(i * Math.PI / 4));
                    int siY = (int) (Math.sqrt(2) * Math.sin(i * Math.PI / 4));
                    int count = 0;
                    int x1 = x;
                    int y1 = y;
                    System.out.println(num);
                    //System.out.printf("%5d", num);
                    notfind = true;
                    while (notfind) {
                        x1 += siX;
                        y1 += siY;
                        int c = iu.r(readQ.getRGB(x1, y1));
                        System.out.print("(x,y) = ");
                        System.out.printf("(%3d, %3d), ", x1, y1);

                        //RGB値255かx, y軸に接したら走査終了
                        //KOKOKOKOKO=================================================
                        if (c >= 100) {
                            point[num][i][0] = 1; //探索成功
                            point[num][i][1] = c; //RGB値の保存
                            point[num][i][2] = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)); //ユークリッド距離
                            System.out.println(num + ": long = " + point[num][i][2]);
                            System.out.println(num + ": color = " + point[num][i][1]);
                            notfind = false;
                        } else if (x1 == w - 1 || x1 == 1 || y1 == h - 1 || y1 == 1) {
                            point[num][i][0] = 1; //探索成功
                            point[num][i][1] = 0; //x,yに接したため
                            //point[num][i][2] = 500; //x, yに接したため
                            System.out.println(num + "!:" + point[num][i][2]);
                            notfind = false;
                        } else if (count >= 500) {
                            point[num][i][0] = 2; //探索失敗
                            point[num][i][1] = 0; //RGB値の保存
                            //point[num][i][2] = 800; //失敗したため

                            System.out.println("!!!!!!FAIL!!!!!");
                            break;
                        }
                        count++;
                    }
                }
                num++;
            }
        }
        System.out.println("");

        System.out.println("num:" + num);

        for (int i = 0; i < num; i++) {
            System.out.printf("%5d: ", i);
            for (int j = 0; j < 8; j++) {
                if (point[i][j][0] == 1 && (int) point[i][j][2] == 500) {
                    System.out.printf("    n");

                } else if (point[i][j][0] == 2) {
                    String s = "-";
                    System.out.printf("%5s", s);
                } else {
                    System.out.printf("%5d", (int) point[i][j][2]);
                }
            }
            System.out.println("");

        }

        return point;
    }

    /**
     * extractFeatureをもとに類似度計算を行う
     */
    public static double calcSimilarity(double[][][] point1, double[][][] point2) {
        double similarity = 0;
        double sum = 0;

        for (int i = 0; i < ((w / interval) - 1) * ((w / interval) - 1); i++) {
            for (int j = 0; j < 8; j++) {
                if ((point1[i][j][1] != 0) && (point2[i][j][1] != 0)) {
                    sum += calcAbsol(point1[i][j][2], point2[i][j][2]);
                }
            }
        }

        similarity = 100 / (1 + sum);

        System.out.println("類似度:" + similarity);

        return similarity;
    }

    /**
     * 2つの値の絶対値を返す
     */
    public static double calcAbsol(double val1, double val2) {
        if (val1 >= val2) {
            return val1 - val2;
        } else {
            return val2 - val1;
        }
    }
}