package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class calculate {
    public static final ImageUtility iu = new ImageUtility();
    public static final int upratio = 5000; // 初期値1200
    public static final int downratio = 500; // 初期値1000

    public static void main() {
    }

    /**
     * cannyエッジ検出
     */
    public static void canny(File file, double ro, double up, double down, String filenumber) throws IOException {

        BufferedImage read = ImageIO.read(file);
        int cal = 0;
        int w = read.getWidth(), h = read.getHeight();
        int repeatCount = 0; // 何回エッジの再計算を行ったか
        int repeat = 3; // エッジの差異計算が必要かどうか

        System.out.println("w,h:" + w + ", " + h);

        double edgeSize = 0.0; // エッジの大きさ
        double angle = 0.0; // ベクトルの角度
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage writeDelFx = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        BufferedImage writeDelFy = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        BufferedImage writeF = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        JFileChooser filechooser = new JFileChooser();
        String filename = filechooser.getName(file);
        String imagename;

        // エッジが適切な量になるまで繰り返す
        while (repeat != 0) {

            if (repeatCount >= 100) {
                // 回数が50を超えたら強制的にループを抜ける
                break;
            }

            // repeatの値によって上限値下限値を変更
            if (repeat == 1) {
                System.out.printf("too much ");
                // エッジが多かった場合
                ro += 0.1;
                up += 10;
                down += 10;

            } else if (repeat == 2) {
                System.out.printf("not enough ");
                // エッジが少なかった場合
                if (repeatCount < 10) {
                    ro -= 0.1;
                }
                up -= 10;
                down -= 10;

            }

            // System.out.println("+++++++++++FILTER++++++++++");
            // ガウシアンフィルタの生成
            double filter[][] = Gfilter(ro);
            // double filterX[][] = new double[filter.length - 1][filter.length - 1];
            // //x方向に一次微分したガウシアンフィルタ
            double filterX[][] = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
            double filterY[][] = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
            // double filterY[][] = new double[filter.length - 1][filter.length - 1];
            // //y方向に一次微分したガウシアンフィルタ
            double pFilterX[][] = { { -1, 0, 1 }, { -1, 0, 1 }, { -1, 0, 1 } };
            double pFilterY[][] = { { -1, -1, -1 }, { 0, 0, 0 }, { 1, 1, 1 } };

            // ガウシアンフィルタと元画像畳み込み
            writeF = convo(read, filter);
            File f = new File("Gau" + filename);
            // ImageIO.write(writeF, "jpg", f);

            // 一次微分したx方向ガウシアンフィルタと元画像の畳み込み: ΔFx(x,y)
            writeDelFx = convo(writeF, pFilterX);
            File fDelx = new File("DelFx.jpg");
            // ImageIO.write(writeDelFx, "jpg", fDelx);

            // 一次微分したy方向ガウシアンフィルタと元画像の畳み込み: ΔFy(x,y)
            writeDelFy = convo(writeF, pFilterY);
            File fDely = new File("DelFy.jpg");
            // ImageIO.write(writeDelFy, "jpg", fDely);

            // 差分
            sabun(writeDelFx, writeDelFy);
            // エッジ細線化

            // Saisen(writeDelFx, writeDelFy, sabun(writeDelFx, writeDelFy));

            // ヒステリシス処理
            writeF = Saisen2(writeDelFx, writeDelFy);
            // ImageIO.write(writeF, "jpg", f2);
            writeF = hysteresis(writeF, up, down);

            // x, y = 0 を黒く塗りつぶす
            writeF = fillBlack(writeF);

            // とりあえず二値化しておく、後で変更しそう
            // writeF = tranc2Value(writeF);

            // 画像に白の割合を描画
            // int[] his = histogram(writeF);
            // int value = whiteRatio(his);
            int value = countEdge(writeF);
            System.out.println("edge number:" + value);
            repeat = edgeVal(value);
            // writeF = iu.drawStr(writeF, value, read.getWidth());

            repeatCount++;
        }

        File f2 = new File("saisen" + filename);

        // ファイル出力
        int r = (int) ro;
        int u = (int) up;
        int d = (int) down;

        if (ro - r > 0) {
            imagename = filename + "_" + String.valueOf(r) + "-" + String.format("%1$.2f", ro - r) + "_"
                    + String.valueOf(u) + "_" + String.valueOf(d) + "_" + filename;
        } else {
            imagename = filename + "_" + String.valueOf(r) + "_" + String.valueOf(u) + "_" + String.valueOf(d) + "_"
                    + filename;
        }
        // 出力先(計算した値を名前にしたバージョン)フォルダを指定
        f2 = new File("C:\\detectEdge\\searchedEdge", imagename);
        // 出力先フォルダ(ナンバリング)を指定
        File fnumber = new File("C:\\detectEdge\\queryImage", "img (" + filenumber + ").jpg");

        ImageIO.write(writeF, "jpg", f2);
        ImageIO.write(writeF, "jpg", fnumber);
        // File textfile = new File("c:\\detectEdge\\edgetext3.txt");
        // FileWriter filewriter = new FileWriter(textfile, true);
        // filewriter.write(", " + imagename + ": " +
        // String.valueOf(judgeHistogram(histogram(f2))));
        // filewriter.close();
    }

    /**
     * 画像にヒストグラムによる白の割合値を記載する
     */
    public static BufferedImage writeRatio(BufferedImage read) throws IOException {
        int[] array = histogram(read);
        int value = whiteRatio(array);
        BufferedImage write = iu.drawStr(read, value, read.getWidth());

        return write;
    }

    /**
     * ガウシアンフィルタの生成
     */
    public static double[][] Gfilter(double ro) {
        int n = 5; // フィルタのサイズ
        double[][] filter = new double[n][n];

        for (double i = -n / 2; i <= n / 2; i++) {
            for (double j = -n / 2; j <= n / 2; j++) {
                filter[(int) (i + n / 2)][(int) (j + n / 2)] = 1 / (2 * Math.PI * ro * ro)
                        * Math.exp(-(j * j + i * i) / (2 * ro * ro));

                // System.out.printf("%.2f,%.2f = %.3f ", i, j, filter[(int) (i + n / 2)][(int)
                // (j + n / 2)]);
            }
            // System.out.print("+\n");
        }

        return filter;

    }

    /**
     * フィルタと画像の畳み込み計算, BufferedImageを返却(グレースケール画像)
     */
    public static BufferedImage convo(BufferedImage read, double[][] filter) throws IOException {
        // BufferedImage read = ImageIO.read(file);
        int cal = 0;
        // int ro = 1; //σの値、自分で変える。
        int w = read.getWidth(), h = read.getHeight();
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // ImageUtility iu = new ImageUtility();

        // System.out.println("+++++++++++FILTER++++++++++");
        // double filter[][] = Gfilter(ro);

        // System.out.println("\n+++++++++++CONVO+++++++++++");

        for (int i = 1; i < w - filter.length + 2; i++) {
            for (int j = 1; j < h - filter.length + 2; j++) {
                cal = 0;
                for (int k = 0; k < filter.length; k++) {
                    for (int l = 0; l < filter.length; l++) {
                        int c = read.getRGB(i - 1 + l, j - 1 + k);
                        int r = iu.r(c);
                        cal += r * filter[k][l];
                        // System.out.print(" " + cal);
                    }
                }
                // 計算した結果が0<cal<255以外の時の処理
                if (cal > 225) {
                    cal = 225;
                } else if (cal < 0) {
                    cal = 0;
                }
                // グレースケール画像を返却
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                write.setRGB(i, j, (int) rgb);
            }
        }
        // File f2 = new File("convo.jpg");
        // ImageIO.write(write, "jpg", f2);

        return write;
    }

    /**
     * x方向、y方向それぞれに一次微分した変換後画像の値が引数 エッジ強度を計算
     */
    public static double calEdge(int rx, int ry) {
        double edgeSize = 0;

        edgeSize = Math.sqrt(rx * rx + ry * ry);

        return edgeSize;
    }

    /**
     * 位置とエッジ方向を最近傍法によって 推定された位置を返す
     */
    public static int[][] specifyPosition(int x1, int y1) {
        int[][] pos = { { 0, 0 }, { 0, 0 } };
        double rad = Math.atan(y1 / x1); // エッジ方向
        /*
         * if (rad >= Math.PI) { rad -= Math.PI; } if (0 <= rad && rad < Math.PI / 8 ||
         * rad >= Math.PI * 7 / 8) { pos[0][0] = x1; pos[1][0] = x1; pos[0][1] = y1 + 1;
         * pos[1][1] = y1 - 1; } else if (Math.PI / 8 <= rad && rad < Math.PI * 3 / 8) {
         * pos[0][0] = x1 - 1; pos[1][0] = x1 + 1; pos[0][1] = y1 + 1; pos[1][1] = y1 -
         * 1; } else if (Math.PI * 3 / 8 <= rad && rad < Math.PI * 5 / 8) { pos[0][0] =
         * x1 - 1; pos[1][0] = x1 + 1; pos[0][1] = y1; pos[1][1] = y1; } else if
         * (Math.PI * 5 / 8 <= rad && rad < Math.PI * 7 / 8) { pos[0][0] = x1 + 1;
         * pos[1][0] = x1 - 1; pos[0][1] = y1 + 1; pos[1][1] = y1 - 1; }
         */

        double x2 = x1 + Math.cos(rad); // 計算から得たエッジ方向が指すx座標の垂直方向
        double y2 = y1 + Math.sin(rad); // 計算から得たエッジ方向が指すy座標の垂直方向

        // System.out.printf("%.3f,%.3f\n", x2, y2);

        // ガウス記号的な処理
        x2 = Math.floor(x2 + 0.5);
        y2 = Math.floor(y2 + 0.5);
        // System.out.printf("%.3f,%.3f\n", x2, y2);
        pos[0][0] = (int) x2;
        pos[0][1] = (int) y2;

        // System.out.printf("%d,%d\n\n", pos[0][0], pos[0][1]);

        x2 = x1 + Math.cos(rad + Math.PI); // 計算から得たエッジ方向(-)が指すx座標の垂直方向
        y2 = y1 + Math.sin(rad + Math.PI); // 計算から得たエッジ方向(-)が指すy座標の垂直方向
        // System.out.printf("%.3f,%.3f\n", x2, y2);

        // ガウス記号的な処理
        x2 = Math.floor(x2 + 0.5);
        y2 = Math.floor(y2 + 0.5);
        // System.out.printf("%.3f,%.3f\n", x2, y2);
        pos[1][0] = (int) x2;
        pos[1][1] = (int) y2;

        // System.out.printf("%d,%d", pos[1][0], pos[1][1]);

        return pos;
    }

    /**
     * 画像の細線化を行う エッジ強度と方向を計算。場合分けによって最大エッジ強度を求める
     * BUufferedImageを返却(引数はXYそれぞれの方向に一次微分したガウシアンフィルタと画像を畳み込んだもの)
     */
    public static BufferedImage Saisen2(BufferedImage readX, BufferedImage readY) throws IOException {

        int w = readX.getWidth(), h = readX.getHeight();
        int cx = 0, cy = 0;
        int[][] pos = new int[2][2];
        int rx = 0, ry = 0;
        double[] edgeSize = { 0, 0, 0 };
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // ImageUtility iu = new ImageUtility();
        int count2 = 0;
        int count[] = { 0, 0, 0 }; // 細線化したピクセル数、元から0だったピクセル数、エッジになったピクセル数(全然違うけど雰囲気)

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                // 現在座標のRGB値を取得
                cx = readX.getRGB(x, y);
                // System.out.println("width =" + w + "hight" + h);
                cy = readY.getRGB(x, y);
                rx = iu.r(cx);
                ry = iu.r(cy);

                // 現在座標におけるエッジ強度
                edgeSize[0] = Math.sqrt(rx * rx + ry * ry);

                // 最近傍法によってエッジ方向のピクセル座標を取得
                pos = specifyPosition(x, y);

                // 推定座標でのRGB値
                int cx1 = iu.r(readX.getRGB(pos[0][0], pos[0][1])); // 1つめの座標のx方向微分のRGB値
                int cy1 = iu.r(readY.getRGB(pos[0][0], pos[0][1])); // 1つめの座標のy方向微分のRGB値
                int cx2 = iu.r(readX.getRGB(pos[1][0], pos[1][1])); // 2つめの座標のx方向微分のRGB値
                int cy2 = iu.r(readY.getRGB(pos[1][0], pos[1][1])); // 2つめの座標のx方向微分のRGB値

                // 推定座標におけるそれぞれのエッジ強度を計算
                edgeSize[1] = Math.sqrt(cx1 * cx1 + cy1 * cy1);
                edgeSize[2] = Math.sqrt(cx2 * cx2 + cy2 * cy2);
                // System.out.println("edgeSize:" + edgeSize[0] + ", edgeSize1:" + edgeSize[1] +
                // ", edgeSize2:" + edgeSize[2]);

                // 細線化
                if (edgeSize[0] >= edgeSize[1] && edgeSize[0] >= edgeSize[2]) {
                    if (edgeSize[0] > 255) {
                        edgeSize[0] = 255;
                        count[2]++;
                    } else if (edgeSize[0] <= 0) {
                        edgeSize[0] = 0;
                        count[1]++;
                    } else {
                        count[2]++;
                    }
                } else {
                    count[0]++;
                    // System.out.println("\n\nOKOK: " + count[0]);
                    edgeSize[0] = 0;
                }

                int rgb = iu.rgb((int) edgeSize[0], (int) edgeSize[0], (int) edgeSize[0]);
                write.setRGB(x, y, rgb);

            }
            // File f2 = new File("saisen2Lena.jpg");
            // ImageIO.write(write, "jpg", f2);
            // System.out.println("\n\ncount: " + count2);
            // System.out.println("\nsaisenC=0 is:" + count[0] + ", oriC=0 is: " + count[1]
            // + ", not C=0 is:" + count[2]);
        }

        return write;
    }

    /**
     * ヒステリシス閾値処理 ある位置でのエッジ強度が上限以上であればエッジ、 下限以下であれば非エッジ、中間の場合
     * その位置がエッジとして検出された画素に4隣接(8隣接)している時だけエッジとする
     */
    public static BufferedImage hysteresis(BufferedImage read, double up, double down) throws IOException {
        // double up = 100, down = 50; //上限値、下限値
        int w = read.getWidth(), h = read.getHeight();
        // ImageUtility iu = new ImageUtility();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        double[] size = new double[9]; // 隣接4方向のエッジ強度
        boolean judge = true;

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                // ヒステリシスのためのエッジ強度計算
                size[0] = iu.r(read.getRGB(x, y));
                size[1] = iu.r(read.getRGB(x - 1, y - 1));
                size[2] = iu.r(read.getRGB(x, y - 1));
                size[3] = iu.r(read.getRGB(x + 1, y - 1));
                size[4] = iu.r(read.getRGB(x - 1, y));
                size[5] = iu.r(read.getRGB(x + 1, y));
                size[6] = iu.r(read.getRGB(x - 1, y + 1));
                size[7] = iu.r(read.getRGB(x, y + 1));
                size[8] = iu.r(read.getRGB(x + 1, y + 1));

                // 上側より大きい
                if (size[0] >= up) {
                    // System.out.println("up");
                    // 下側より小さい
                } else if (size[0] <= down) {
                    // System.out.println("down");
                    size[0] = 0;
                } else {
                    // 中間
                    for (int i = 1; i < size.length; i++) {
                        // そのRGB値を採用
                        if (size[i] < size[0]) {
                            judge = true;

                            // エッジではないとする
                        } else {
                            judge = false;
                            break;
                        }
                    }
                    if (judge) {
                        // System.out.println("edge");
                    } else {
                        size[0] = 0;
                        // System.out.println("not edge");
                    }
                }
                int rgb = iu.rgb((int) size[0], (int) size[0], (int) size[0]);
                write.setRGB(x, y, rgb);

            }
            // File f2 = new File("hysLena.jpg");
            // ImageIO.write(write, "jpg", f2);

        }
        return write;

    }

    /**
     * ヒストグラムを作成するための配列
     */
    public static int[] histogram(BufferedImage read) throws IOException {
        int w = read.getWidth();
        int h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // ImageUtility iu = new ImageUtility();
        int[] his = new int[255 / 15 + 5];
        // RGB値が0でないもののカウント
        // int count = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = iu.r(read.getRGB(x, y));
                his[c / 15]++;
            }
        }

        // show histogram
        // showHistogram(his);
        // return an array of histogram. the ratio fo White to Black.
        return his;
    }

    /**
     * 輪郭追跡
     */
    public static BufferedImage trackEdge(BufferedImage read) throws IOException {
        int w = read.getWidth();
        int h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        return write;
    }

    /**
     * グレースケール画像を二値化してエッジの数を数え上げる
     */
    public static int countEdge(BufferedImage read) throws IOException {
        int w = read.getWidth();
        int h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int edgenum = 0;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = iu.r(read.getRGB(x, y));
                // System.out.println(c);
                if (c != 0) {
                    edgenum++;
                }
            }
        }
        return edgenum;
    }

    /**
     * show histogram
     */
    public static void showHistogram(int[] histgram) {
        // show histogram
        for (int i = 0; i <= 255 / 15; i++) {
            System.out.printf("%3d~%3d: ", i * 15, i * 15 + 15);

            for (int j = 0; j < 30; j++) {
                if ((int) histgram[i] / 100 > 30 && j > 30) {
                    System.out.printf("~");
                    break;
                }
                if (j < (int) histgram[i] / 100) {
                    System.out.printf("*");
                } else {
                    System.out.printf(" ");
                }

            }
            System.out.printf(" %d\n", histgram[i]);
        }
    }

    /**
     * ヒストグラムからエッジのり量を返却
     */
    public static int whiteRatio(int[] his) {
        // RGB値が大きいほど白の割合が高い
        int ratio = 0;

        for (int i = 0; i <= 255 / 15; i++) {
            if (i != 255 / 15) {
                ratio += i * his[i];
            }
        }

        System.out.println("white ratio: " + ratio);
        boolean judge = true;
        return ratio;
    }

    /**
     * しきい値に従ってエッジの量が適切か判定 0->適切, 1->多い, 2->少ない
     */
    public static int edgeVal(int ratio) {
        if (ratio > upratio) {
            // エッジが多いとき
            return 1;
        } else if (ratio < downratio) {
            // エッジが少ないとき
            return 2;
        } else {
            // 適切なとき
            return 0;
        }
    }

    /**
     * 画像を二値にする
     */
    public static BufferedImage tranc2Value(BufferedImage read) {
        int w = read.getWidth();
        int h = read.getHeight();
        int rgb = 0;
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int c = iu.r(read.getRGB(x, y));
                if (c != 0) {
                    rgb = iu.rgb(255, 255, 255);
                } else {
                    rgb = iu.rgb(0, 0, 0);
                }
                write.setRGB(x, y, rgb);
            }
        }
        return write;
    }

    /**
     * x, y=0を黒くする処理
     */
    public static BufferedImage fillBlack(BufferedImage read) {
        int w = read.getWidth();
        int h = read.getHeight();

        for (int i = 0; i < w; i++) {
            int rgb = iu.rgb(0, 0, 0);
            read.setRGB(i, 0, rgb);
            read.setRGB(i, 1, rgb);
        }
        for (int i = 0; i < h; i++) {
            int rgb = iu.rgb(0, 0, 0);
            read.setRGB(0, i, rgb);
            read.setRGB(1, i, rgb);
        }

        return read;
    }

    // グレースケール画像にガウシアンフィルタ
    public static void monoGaussianFilter(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int w = read.getWidth();
        int h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // ImageUtility iu = new ImageUtility();
        int ro = 1;
        // filterを指定。後で動的に変えられるようにメソッド作る
        double[][] filter = { { 0.11f, 0.11f, 0.11f }, { 0.11f, 0.12f, 0.11f }, { 0.11f, 0.11f, 0.11f } };
        double[][] filter2 = { { -1, -1, -1 }, { -1, 9, -1 }, { -1, -1, -1 } };
        double[][] filterG = Gfilter(ro);

        double cal;

        for (int j = 1; j < h - filterG.length + 2; j++) {
            for (int i = 1; i < w - filterG.length + 2; i++) {
                cal = 0;
                for (int l = 0; l < filterG.length; l++) {
                    for (int m = 0; m < filterG.length; m++) {
                        int c = read.getRGB(i - 1 + m, j - 1 + l);
                        double r = iu.r(c);
                        // System.out.print(" color:" + c + " , r:" + r);
                        // int rgb = iu.rgb(r, g, b);
                        r *= filterG[l][m];
                        cal += r;
                        // System.out.print("cal:" + calr + " " + r + " ");
                    }
                    // System.out.println();
                }
                // System.out.println();
                if (cal > 255) {
                    cal = 255;
                } else if (cal < 0) {
                    cal = 0;
                }
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                // System.out.print(" " + (int) rgb);
                write.setRGB(i, j, (int) rgb);
            }
        }
        File f2 = new File("tetemonogau.jpg");
        ImageIO.write(write, "jpg", f2);
    }

    /**
     * グレースケール画像でx方向に一次微分
     */
    public static void firstX(BufferedImage read) throws IOException {

        int cal = 0;
        int rgbset = 0;
        int w = read.getWidth(), h = read.getHeight();
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // ImageUtility iu = new ImageUtility();

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {
                int c[] = { 0, 0 };
                int r[] = { 0, 0 };

                for (int i = 0; i < 3; i++) {
                    c[i] = read.getRGB(x - 1 + i, y);
                    r[i] = iu.r(c[i]);
                    // int g = iu.g(c[i]);
                    // int b = iu.b(c[i]);
                    // rgb[i] = iu.rgb(r, g, b);
                    // System.out.println("BBBBBBBB");
                }
                /*
                 * for(int i=0;i<2;i++){ c[i] = read.getRGB(x+i,y); r[i] = iu.r(c[i]); }
                 */
                cal = r[0] - 2 * r[1] + r[2];
                // System.out.print(" " + cal);
                rgbset = iu.rgb(cal, cal, cal);
                // rgbList.add(x, rgbset);
                write.setRGB(x, y, rgbset);
                cal = 0;
            }
        }
        File file = new File("imgTest2.jpg");
        ImageIO.write(write, "jpg", file);
        // return rgbList;
    }

    /**
     * グレースケール画像でy方向に一次微分
     */
    public static void firstY(BufferedImage read) throws IOException {

        int cal = 0;
        int rgbset = 0;
        int w = read.getWidth(), h = read.getHeight();
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // ImageUtility iu = new ImageUtility();

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {
                int c[] = { 0, 0 };
                int r[] = { 0, 0 };

                for (int i = 0; i < 3; i++) {
                    c[i] = read.getRGB(x, y - 1 + i);
                    r[i] = iu.r(c[i]);
                    // int g = iu.g(c[i]);
                    // int b = iu.b(c[i]);
                    // rgb[i] = iu.rgb(r, g, b);
                    // System.out.println("BBBBBBBB");
                }
                /*
                 * for(int i=0;i<2;i++){ c[i] = read.getRGB(x+i,y); r[i] = iu.r(c[i]); }
                 */
                cal = r[0] - 2 * r[1] + r[2];
                // System.out.print(" " + cal);
                rgbset = iu.rgb(cal, cal, cal);
                // rgbList.add(x, rgbset);
                write.setRGB(x, y, rgbset);
                cal = 0;
            }
        }
        File file = new File("imgTest2.jpg");
        ImageIO.write(write, "jpg", file);
        // return rgbList;
    }
    // ガウシアンフィルタをｘ方向に一次微分: Gx(画面の明度をかなり上げないと見えない)
    /*
     * for (int y = 0; y < filter.length - 1; y++) {
     * 
     * for (int x = 0; x < filter.length - 1; x++) {
     * 
     * filterX[y][x] = -filter[y][x + 1] + filter[y][x]; System.out.printf("%.3f",
     * filterX[x][y]); } System.out.print("\n"); }
     * 
     * //ガウシアンフィルタをy方向に一次微分: Gy for (int y = 0; y < filter.length - 1; y++) {
     * 
     * for (int x = 0; x < filter.length - 1; x++) {
     * 
     * filterY[y][x] = -filter[y + 1][x] + filter[y][x]; System.out.printf(" %.3f",
     * filterY[x][y]); } System.out.print("\n"); }
     */

    /**
     * ２つの画像の差分
     */
    public static BufferedImage sabun(BufferedImage readX, BufferedImage readY) throws IOException {
        int w = readX.getWidth(), h = readX.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        // ImageUtility iu = new ImageUtility();
        int cx = 0, cy = 0, rx = 0, ry = 0;
        double val = 0;

        for (int i = 1; i < h; i++) {
            for (int j = 1; j < w; j++) {
                cx = readX.getRGB(j, i);
                // System.out.println("width =" + w + "hight" + h);
                cy = readY.getRGB(j, i);
                rx = iu.r(cx);
                ry = iu.r(cy);

                if (rx - ry < 0) {
                    val = ry - rx;
                } else {
                    val = rx - ry;
                }

                if (val > 255) {
                    val = 255;
                } else if (val < 0) {
                    val = 0;
                }

                int rgb = iu.rgb((int) val, (int) val, (int) val);
                write.setRGB(j, i, rgb);

            }
            File f2 = new File("sabun.jpg");
            // ImageIO.write(write, "jpg", f2);

        }

        return write;
    }

}
