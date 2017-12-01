package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.JFileChooser;

/**
 * 検索対象の画像についてエッジのある範囲を保管(探索、走査)
 */
public class searchPre {
    public static final int interval = 40; //探索点を置く間隔
    public static final int w = 300; //めんどいので画像の横幅を定義しておく
    public static final int ra = 16; //角度の分母。分子はπ
    public static final double up = 100; //上限値
    public static final double down = 20; //下限値
    ImageUtility iu = new ImageUtility();

    public static void main(String[] args) throws IOException {

        File file1 = new File("./seaTest.jpg");
        File file2 = new File("./seaTest2.jpg");
        double[][][] point1 = extractFeature(file1);

        for (int i = 0; i <= 156 * 5; i++) {
            System.out.println("\nnum: " + i);
            File file = new File("C:\\detectEdge\\edgetest2\\img (" + String.valueOf(i + 1) + ").jpg");
            JFileChooser jfilechooser = new JFileChooser();
            String filename = jfilechooser.getName(file1);
            String filename2 = jfilechooser.getName(file);

            System.out.println("filename is " + filename);
            point1 = extractFeature(file1);
            System.out.println("filename is " + filename2);
            double[][][] point2 = extractFeature(file);

            File out1 = new File("C:\\detectEdge\\ruiji", "img" + String.valueOf(i + 1 + "__.jpg"));
            BufferedImage image = drawStr(ImageIO.read(file), calcSimilarity(point1, point2));
            ImageIO.write(image, "jpg", out1);

        }
        /*
        double[][][] point2 = extractFeature(file2);
        File file3 = new File("./tet.jpg");
        double[][][] point3 = extractFeature(file3);
        File file4 = new File("./testst.jpg");
        double[][][] point4 = extractFeature(file4);
        File file5 = new File("./testtest.jpg");
        //double[][][] point5 = extractFeature(file5);
        
        //calcSimilarity(point1, point1);
        drawStr(ImageIO.read(file2), calcSimilarity(point1, point2));
        File out1 = new File("im1.jpg");
        ImageIO.write(drawStr(ImageIO.read(file2), calcSimilarity(point1, point2)), "jpg", out1);
        
        //calcSimilarity(point1, point3);
        drawStr(ImageIO.read(file3), calcSimilarity(point1, point3));
        drawStr(ImageIO.read(file4), calcSimilarity(point1, point4));
        //calcSimilarity(point1, point5);
        */

    }

    /**
     * 任意の座標から探索点を隣接８方向に走査。
     * 255以外のピクセルにぶつかるかx、y軸に接したら走査終了
     * （！要改変！）RGB値が100以上をエッジとしているけどヒストグラムによる適切判定すればいらないかも（！要改変！）
     */
    public static double[][][] extractFeature(File queryfile) throws IOException {
        ImageUtility iu = new ImageUtility();
        BufferedImage readQ = ImageIO.read(queryfile); //入力画像

        int h = readQ.getHeight(), w = readQ.getWidth();
        boolean notfind = true;
        int num = 0; //探索点のナンバリング
        //探索点（20*20）の用意. RGB値、距離、元の位置(ナンバリングしておく？)
        double[][][] point = new double[((h / interval) - 1) * ((h / interval) - 1)][ra][3];

        System.out.println("h, w:" + h + "," + w);

        //探索点の放出(2値化、値の近似、省略は行わないとする,とりあえず)
        for (int y = interval; y < h - 1; y += interval) {
            for (int x = interval; x < w - 1; x += interval) {
                for (int i = 0; i < ra; i++) {
                    //1つの探索点から32方向に走査
                    int siX = (int) (Math.sqrt(2) * Math.cos(i * Math.PI / ra));
                    int siY = (int) (Math.sqrt(2) * Math.sin(i * Math.PI / ra));
                    int count = 0;
                    double x1 = x;
                    double y1 = y;
                    //System.out.print("\n" + num + ": ");
                    //System.out.printf("%5d", num);
                    notfind = true;
                    while (notfind) {
                        x1 += (Math.cos(i * Math.PI / ra));
                        y1 += (Math.sin(i * Math.PI / ra));
                        int c = iu.r(readQ.getRGB((int) x1, (int) y1));

                        //System.out.print("(x,y) = ");
                        //System.out.printf("(%3d, %3d), ", (int) x1, (int) y1);

                        //RGB値255かx, y軸に接したら走査終了
                        //KOKOKOKOKO=================================================
                        if (c >= 100) {
                            point[num][i][0] = 1; //探索成功
                            point[num][i][1] = c; //RGB値の保存
                            point[num][i][2] = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)); //ユークリッド距離
                            //System.out.println(num + ": long = " + point[num][i][2]);
                            //System.out.println(num + ": color = " + point[num][i][1]);
                            notfind = false;
                        } else if (x1 >= w - 1 || x1 <= 1 || y1 >= h - 1 || y1 <= 1) {
                            point[num][i][0] = 1; //探索成功
                            point[num][i][1] = 0; //x,yに接したため
                            point[num][i][2] = Math.sqrt((x1 - x) * (x1 - x) + (y1 - y) * (y1 - y)); //x, yに接したため
                            //System.out.println(num + "!:" + point[num][i][2]);
                            notfind = false;
                        } else if (count >= 500) {
                            point[num][i][0] = 0; //探索失敗
                            //point[num][i][1] = 0; //RGB値の保存
                            //point[num][i][2] = 800; //失敗したため

                            System.out.println("!!!!!!FAIL!!!!!");
                            break;
                        }
                        count++;
                    }
                }
                //System.out.println("");
                num++;
            }
        }
        System.out.println("");

        //特徴量の表示
        System.out.println("特徴量");
        for (int i = 0; i < num; i++) {
            //System.out.printf("%5d: ", i);
            for (int j = 0; j < ra; j++) {
                if (point[i][j][0] == 0) {
                    System.out.printf("  F  ");
                } else {
                    System.out.printf("%5d", (int) point[i][j][2]);
                }
            }
            System.out.println("");

        }

        return point;
    }

    /**
     * 点の探索範囲を計算
     */
    public static double calcRange(double[][][] point) {
        double sum = 0;
        return sum;
    }

    /**
     * 論文を参考にした類似度計算
     */
    public static double calcSimilarity2(double[][][] point1, double[][][] point2) {
        double similarity = 0; //類似度
        double[] range1 = new double[((w / interval) - 1) * ((w / interval) - 1)]; //point1の各点の探索範囲
        double[] range2 = new double[((w / interval) - 1) * ((w / interval) - 1)]; //point2の各点の探索範囲

        //探索範囲の計算
        for (int i = 0; i < ((w / interval) - 1) * ((w / interval) - 1); i++) {
            range1[i] = 0;
            range2[i] = 0;
            for (int j = 0; j < ra; j++) {
                if ((point1[i][j][0] != 0) && (point2[i][j][0] != 0)) {
                    //共に探索成功している場合
                    range1[i] += point1[i][j][2];
                    range2[i] += point2[i][j][2];
                    System.out.printf(" %4.1f , %4.1f ", range1[i], range2[i]);
                } else {
                    //探索失敗している場合
                    System.out.printf("  F  ");
                }
            }
        }

        return similarity;
    }

    /**
     * extractFeatureをもとに類似度計算を行う
     */
    public static double calcSimilarity(double[][][] point1, double[][][] point2) {
        double similarity = 0;
        double sum = 0;
        double val = 0;
        double x = 0;

        System.out.println("\n類似度計算");
        for (int i = 0; i < ((w / interval) - 1) * ((w / interval) - 1); i++) {
            for (int j = 0; j < ra; j++) {
                /*
                if (((point1[i][j][1] != 0) && (point2[i][j][1] == 0))
                        || ((point2[i][j][1] != 0) && (point1[i][j][1] == 0))) {
                    sum += (w - 1 - interval) * Math.sqrt(2);
                
                }
                */
                if ((point1[i][j][0] != 0) && (point2[i][j][0] != 0)) {
                    val = calcAbsol(point1[i][j][2], point2[i][j][2]);

                    //距離の差に上限下限を決める。上限(up)以上は100,下限(down)以下ならば0にした
                    if (down < val && val < up) {
                        sum += val;
                        System.out.printf("%5.0f", val);
                    } else if (val < down) {
                        sum += 0;
                        System.out.printf("%5.0f", down);

                    } else {
                        sum += up;
                        System.out.printf("%5.0f", up);
                    }
                    //System.out.printf("%4.1f - %4.1f = %5.1f ", point1[i][j][2], point2[i][j][2],calcAbsol(point1[i][j][2], point2[i][j][2]));
                } else if (point1[i][j][1] == 0 || point2[i][j][1] == 0) {
                    sum += 100;
                    System.out.printf(" 100 ");
                } else {
                    System.out.printf("  F  ");
                }
            }
            System.out.println("sum up: " + sum);
        }

        similarity = 100 / (1 + sum / 100);

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

    /**
     * 文字列を画像に書き込む
     */
    public static BufferedImage drawStr(BufferedImage read, double value) {
        Graphics graphics = read.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.drawString(String.valueOf(value), w - 50, w - 20);

        return read;
    }
}