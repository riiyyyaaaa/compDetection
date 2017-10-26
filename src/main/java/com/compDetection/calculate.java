package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class calculate {
    public static void main() {

    }

    /**
     * ガウシアンフィルタの生成
     */
    public static double[][] Gfilter(double ro) {
        int n = 5; //フィルタのサイズ
        double[][] filter = new double[n][n];

        for (double i = -n / 2; i <= n / 2; i++) {
            for (double j = -n / 2; j <= n / 2; j++) {
                filter[(int) (i + n / 2)][(int) (j + n / 2)] = 1 / (2 * Math.PI * ro * ro)
                        * Math.exp(-(j * j + i * i) / (2 * ro * ro));

                System.out.printf("%.2f,%.2f = %.3f  ", i, j, filter[(int) (i + n / 2)][(int) (j + n / 2)]);
            }
            System.out.print("+\n");
        }

        return filter;

    }

    /**
     * フィルタと画像の畳み込み計算, BufferedImageを返却(グレースケール画像)
     */
    public static BufferedImage convo(BufferedImage read, double[][] filter) throws IOException {
        //BufferedImage read = ImageIO.read(file);
        int cal = 0;
        //int ro = 1; //σの値、自分で変える。
        int w = read.getWidth(), h = read.getHeight();
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();

        //System.out.println("+++++++++++FILTER++++++++++");
        //double filter[][] = Gfilter(ro);

        System.out.println("\n+++++++++++CONVO+++++++++++");

        for (int i = 1; i < w - filter.length + 2; i++) {
            for (int j = 1; j < h - filter.length + 2; j++) {
                cal = 0;
                for (int k = 0; k < filter.length; k++) {
                    for (int l = 0; l < filter.length; l++) {
                        int c = read.getRGB(i - 1 + l, j - 1 + k);
                        int r = iu.r(c);
                        cal += r * filter[k][l];
                        System.out.print(" " + cal);
                    }
                }
                //計算した結果が0<cal<255以外の時の処理
                if (cal > 225) {
                    cal = 225;
                } else if (cal < 0) {
                    cal = 0;
                }
                //グレースケール画像を返却
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                write.setRGB(i, j, (int) rgb);
            }
        }
        //File f2 = new File("convo.jpg");
        //ImageIO.write(write, "jpg", f2);

        return write;
    }

    /**
     * x方向、y方向それぞれに一次微分した変換後画像の値が引数
     * エッジ強度を計算
     */
    public static double calEdge(int rx, int ry) {
        double edgeSize = 0;

        edgeSize = Math.sqrt(rx * rx + ry * ry);

        return edgeSize;
    }

    /**
     * 位置とエッジ方向を最近傍法によって
     * 推定された位置を返す
     */
    public static int[][] specifyPosition(int x1, int y1) {
        int[][] pos = { { 0, 0 }, { 0, 0 } };
        double rad = Math.atan(y1 / x1); //エッジ方向
        /*
        if (rad >= Math.PI) {
            rad -= Math.PI;
        }
        if (0 <= rad && rad < Math.PI / 8 || rad >= Math.PI * 7 / 8) {
            pos[0][0] = x1;
            pos[1][0] = x1;
            pos[0][1] = y1 + 1;
            pos[1][1] = y1 - 1;
        } else if (Math.PI / 8 <= rad && rad < Math.PI * 3 / 8) {
            pos[0][0] = x1 - 1;
            pos[1][0] = x1 + 1;
            pos[0][1] = y1 + 1;
            pos[1][1] = y1 - 1;
        } else if (Math.PI * 3 / 8 <= rad && rad < Math.PI * 5 / 8) {
            pos[0][0] = x1 - 1;
            pos[1][0] = x1 + 1;
            pos[0][1] = y1;
            pos[1][1] = y1;
        } else if (Math.PI * 5 / 8 <= rad && rad < Math.PI * 7 / 8) {
            pos[0][0] = x1 + 1;
            pos[1][0] = x1 - 1;
            pos[0][1] = y1 + 1;
            pos[1][1] = y1 - 1;
        }
        */

        double x2 = x1 + Math.cos(rad); //計算から得たエッジ方向が指すx座標の垂直方向
        double y2 = y1 + Math.sin(rad); //計算から得たエッジ方向が指すy座標の垂直方向

        System.out.printf("%.3f,%.3f\n", x2, y2);

        //ガウス記号的な処理
        x2 = Math.floor(x2 + 0.5);
        y2 = Math.floor(y2 + 0.5);
        System.out.printf("%.3f,%.3f\n", x2, y2);
        pos[0][0] = (int) x2;
        pos[0][1] = (int) y2;

        System.out.printf("%d,%d\n\n", pos[0][0], pos[0][1]);

        x2 = x1 + Math.cos(rad + Math.PI); //計算から得たエッジ方向(-)が指すx座標の垂直方向
        y2 = y1 + Math.sin(rad + Math.PI); //計算から得たエッジ方向(-)が指すy座標の垂直方向
        System.out.printf("%.3f,%.3f\n", x2, y2);

        //ガウス記号的な処理
        x2 = Math.floor(x2 + 0.5);
        y2 = Math.floor(y2 + 0.5);
        System.out.printf("%.3f,%.3f\n", x2, y2);
        pos[1][0] = (int) x2;
        pos[1][1] = (int) y2;

        System.out.printf("%d,%d", pos[1][0], pos[1][1]);

        return pos;
    }

    /**
    * 画像の細線化を行う
    * エッジ強度と方向を計算。場合分けによって最大エッジ強度を求める
    * BUufferedImageを返却(引数はXYそれぞれの方向に一次微分したガウシアンフィルタと画像を畳み込んだもの)
    */
    public static BufferedImage Saisen2(BufferedImage readX, BufferedImage readY) throws IOException {

        int w = readX.getWidth(), h = readX.getHeight();
        int cx = 0, cy = 0;
        int[][] pos = new int[2][2];
        int rx = 0, ry = 0;
        double[] edgeSize = { 0, 0, 0 };
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();
        int count2 = 0;
        int count[] = { 0, 0, 0 }; //細線化したピクセル数、元から0だったピクセル数、エッジになったピクセル数(全然違うけど雰囲気)

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                //現在座標のRGB値を取得
                cx = readX.getRGB(x, y);
                //System.out.println("width =" + w + "hight" + h);
                cy = readY.getRGB(x, y);
                rx = iu.r(cx);
                ry = iu.r(cy);

                //現在座標におけるエッジ強度
                edgeSize[0] = Math.sqrt(rx * rx + ry * ry);

                //最近傍法によってエッジ方向のピクセル座標を取得
                pos = specifyPosition(x, y);

                //推定座標でのRGB値
                int cx1 = iu.r(readX.getRGB(pos[0][0], pos[0][1])); //1つめの座標のx方向微分のRGB値
                int cy1 = iu.r(readY.getRGB(pos[0][0], pos[0][1])); //1つめの座標のy方向微分のRGB値
                int cx2 = iu.r(readX.getRGB(pos[1][0], pos[1][1])); //2つめの座標のx方向微分のRGB値
                int cy2 = iu.r(readY.getRGB(pos[1][0], pos[1][1])); //2つめの座標のx方向微分のRGB値

                //推定座標におけるそれぞれのエッジ強度を計算
                edgeSize[1] = Math.sqrt(cx1 * cx1 + cy1 * cy1);
                edgeSize[2] = Math.sqrt(cx2 * cx2 + cy2 * cy2);
                //System.out.println("edgeSize:" + edgeSize[0] + ", edgeSize1:" + edgeSize[1] + ", edgeSize2:" + edgeSize[2]);

                //細線化
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
                    System.out.println("\n\nOKOK: " + count[0]);
                    edgeSize[0] = 0;
                }

                int rgb = iu.rgb((int) edgeSize[0], (int) edgeSize[0], (int) edgeSize[0]);
                write.setRGB(x, y, rgb);

            }
            File f2 = new File("saisen2Lena.jpg");
            ImageIO.write(write, "jpg", f2);
            System.out.println("\n\ncount: " + count2);
            System.out.println("\nsaisenC=0 is:" + count[0] + ", oriC=0 is: " + count[1] + ", not C=0 is:" + count[2]);
        }

        return write;
    }

    /**
     * ヒステリシス閾値処理
     * ある位置でのエッジ強度が上限以上であればエッジ、
     * 下限以下であれば非エッジ、中間の場合
     * その位置がエッジとして検出された画素に4隣接(8隣接)している時だけエッジとする
     */
    public static BufferedImage hysteresis(BufferedImage read, double up, double down) throws IOException {
        //double up = 100, down = 50; //上限値、下限値
        int w = read.getWidth(), h = read.getHeight();
        ImageUtility iu = new ImageUtility();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        double[] size = { 0, 0, 0, 0, 0 }; //隣接4方向のエッジ強度

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                //ヒステリシスのためのエッジ強度計算
                size[0] = iu.r(read.getRGB(x, y));
                size[1] = iu.r(read.getRGB(x, y - 1));
                size[2] = iu.r(read.getRGB(x - 1, y));
                size[3] = iu.r(read.getRGB(x + 1, y));
                size[4] = iu.r(read.getRGB(x, y + 1));

                //上側より大きい
                if (size[0] >= up) {
                    System.out.println("up");
                    //下側より小さい
                } else if (size[0] <= down) {
                    System.out.println("down");
                    size[0] = 0;
                } else {
                    //中間
                    if (size[0] >= size[1] && size[0] >= size[2] && size[0] >= size[3] && size[0] >= size[4]) {
                        System.out.println("edge");
                    } else {
                        size[0] = 0;
                        System.out.println("not edge");
                    }
                }
                int rgb = iu.rgb((int) size[0], (int) size[0], (int) size[0]);
                write.setRGB(x, y, rgb);

            }
            File f2 = new File("hysLena.jpg");
            ImageIO.write(write, "jpg", f2);

        }
        return write;

    }

    /**
     * cannyエッジ検出
     */
    public static void canny1(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int cal = 0;
        double ro = 1; //σの値、自分で変える。
        int w = read.getWidth(), h = read.getHeight();
        double edgeSize = 0.0; //エッジの大きさ
        double angle = 0.0; //ベクトルの角度
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage writeDelFx = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        BufferedImage writeDelFy = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        BufferedImage writeF = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();

        System.out.println("+++++++++++FILTER++++++++++");
        //ガウシアンフィルタの生成
        double filter[][] = Gfilter(ro);
        //double filterX[][] = new double[filter.length - 1][filter.length - 1]; //x方向に一次微分したガウシアンフィルタ
        double filterX[][] = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        double filterY[][] = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
        //double filterY[][] = new double[filter.length - 1][filter.length - 1]; //y方向に一次微分したガウシアンフィルタ

        //ガウシアンフィルタと元画像畳み込み
        writeF = convo(read, filter);
        File f = new File("lenaGau.jpg");
        ImageIO.write(writeF, "jpg", f);

        //ガウシアンフィルタをｘ方向に一次微分: Gx(画面の明度をかなり上げないと見えない)
        /*
        for (int y = 0; y < filter.length - 1; y++) {
        
            for (int x = 0; x < filter.length - 1; x++) {
        
                filterX[y][x] = -filter[y][x + 1] + filter[y][x];
                System.out.printf("%.3f", filterX[x][y]);
            }
            System.out.print("\n");
        }
        
        //ガウシアンフィルタをy方向に一次微分: Gy
        for (int y = 0; y < filter.length - 1; y++) {
        
            for (int x = 0; x < filter.length - 1; x++) {
        
                filterY[y][x] = -filter[y + 1][x] + filter[y][x];
                System.out.printf(" %.3f", filterY[x][y]);
            }
            System.out.print("\n");
        }
        */

        //一次微分したx方向ガウシアンフィルタと元画像の畳み込み: ΔFx(x,y)
        writeDelFx = convo(writeF, filterX);
        File fDelx = new File("DelFx.jpg");
        ImageIO.write(writeDelFx, "jpg", fDelx);

        //一次微分したy方向ガウシアンフィルタと元画像の畳み込み: ΔFy(x,y)
        writeDelFy = convo(writeF, filterY);
        File fDely = new File("DelFy.jpg");
        ImageIO.write(writeDelFy, "jpg", fDely);

        System.out.println("+++++++++++finish!!!++++++++++");

        //差分
        //sabun(writeDelFx, writeDelFy);
        //エッジ細線化

        //Saisen(writeDelFx, writeDelFy, sabun(writeDelFx, writeDelFy));

        //ヒステリシス処理
        hysteresis(Saisen2(writeDelFx, writeDelFy), 150, 100);

        //File f2 = new File("convo.jpg");
        //ImageIO.write(writeDelFx, "jpg", f2);
    }

    //グレースケール画像にガウシアンフィルタ
    public static void monoGaussianFilter(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int w = read.getWidth();
        int h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();
        int ro = 1;
        //filterを指定。後で動的に変えられるようにメソッド作る
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
                        System.out.print(" color:" + c + " , r:" + r);
                        //int rgb = iu.rgb(r, g, b);
                        r *= filterG[l][m];
                        cal += r;
                        // System.out.print("cal:" + calr + " " + r + " ");
                    }
                    System.out.println();
                }
                System.out.println();
                if (cal > 255) {
                    cal = 255;
                } else if (cal < 0) {
                    cal = 0;
                }
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                //System.out.print(" " + (int) rgb);
                write.setRGB(i, j, (int) rgb);
            }
        }
        File f2 = new File("tetemonogau.jpg");
        ImageIO.write(write, "jpg", f2);
    }

    //カラー画像に平滑化フィルタ
    public static void test(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int w = read.getWidth();
        int h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();
        //filterを指定。後で動的に変えられるようにメソッド作る
        double[][] filter = { { 0.11f, 0.11f, 0.11f }, { 0.11f, 0.12f, 0.11f }, { 0.11f, 0.11f, 0.11f } };
        double[][] filter2 = { { -1, -1, -1 }, { -1, 9, -1 }, { -1, -1, -1 } };
        double[][] firstXfilter = { { 0, 0, 0 }, { 1, -2, 1 }, { 0, 0, 0 } };

        double calr, calg, calb;

        for (int j = 1; j < h - 1; j++) {
            for (int i = 1; i < w - 1; i++) {
                calr = 0;
                calg = 0;
                calb = 0;
                for (int l = 0; l < 3; l++) {
                    for (int m = 0; m < 3; m++) {
                        int c = read.getRGB(i - 1 + m, j - 1 + l);
                        double r = iu.r(c);
                        //double g = iu.g(c);
                        //double b = iu.b(c);
                        //System.out.print(" color:" + c + " , r:" + r + ", g:" + g + ", b:" + b);
                        //int rgb = iu.rgb(r, g, b);
                        r *= firstXfilter[l][m];
                        //g *= firstXfilter[l][m];
                        //b *= firstXfilter[l][m];
                        calr += r;
                        System.out.print("cal:" + calr + " " + r + " ");
                        //calg += g;
                        //calb += b;
                    }
                    System.out.println();
                }
                System.out.println();
                if (calr > 225) {
                    calr = 255;
                } else if (calr < 0) {
                    calr = 0;
                }
                int rgb = iu.rgb((int) calr, (int) calr, (int) calr);
                //System.out.print(" " + (int) rgb);
                write.setRGB(i, j, (int) rgb);
            }
        }
        File f2 = new File("firstX.jpg");
        ImageIO.write(write, "jpg", f2);
    }

    public static void Mono(File file) throws IOException {
        BufferedImage readImage = ImageIO.read(file);
        int w = readImage.getWidth(), h = readImage.getHeight();
        BufferedImage writeImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                // ピクセル値を取得
                int c = readImage.getRGB(x, y);
                // 0.299や0.587といった値はモノクロ化の定数値
                int mono = (int) (0.299 * iu.r(c) + 0.587 * iu.g(c) + 0.114 * iu.b(c));
                // モノクロ化したピクセル値をint値に変換
                int rgb = (iu.a(c) << 24) + (mono << 16) + (mono << 8) + mono;
                writeImage.setRGB(x, y, rgb);
            }
        }
        // イメージをファイルに出力する
        ImageIO.write(writeImage, "png", new File("monoNaru.png"));
    }

    static BufferedImage toMono(BufferedImage src) {
        BufferedImage dist = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        dist.getGraphics().drawImage(src, 0, 0, null);
        return dist;
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
        ImageUtility iu = new ImageUtility();

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {
                int c[] = { 0, 0 };
                int r[] = { 0, 0 };

                for (int i = 0; i < 3; i++) {
                    c[i] = read.getRGB(x - 1 + i, y);
                    r[i] = iu.r(c[i]);
                    //int g = iu.g(c[i]);
                    //int b = iu.b(c[i]);
                    //rgb[i] = iu.rgb(r, g, b);
                    //System.out.println("BBBBBBBB");
                }
                /*
                for(int i=0;i<2;i++){
                    c[i] = read.getRGB(x+i,y);
                    r[i] = iu.r(c[i]);
                }
                */
                cal = r[0] - 2 * r[1] + r[2];
                System.out.print(" " + cal);
                rgbset = iu.rgb(cal, cal, cal);
                //rgbList.add(x, rgbset);
                write.setRGB(x, y, rgbset);
                cal = 0;
            }
        }
        File file = new File("imgTest2.jpg");
        ImageIO.write(write, "jpg", file);
        //return rgbList;
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
        ImageUtility iu = new ImageUtility();

        for (int y = 1; y < h - 1; y++) {

            for (int x = 1; x < w - 1; x++) {
                int c[] = { 0, 0 };
                int r[] = { 0, 0 };

                for (int i = 0; i < 3; i++) {
                    c[i] = read.getRGB(x, y - 1 + i);
                    r[i] = iu.r(c[i]);
                    //int g = iu.g(c[i]);
                    //int b = iu.b(c[i]);
                    //rgb[i] = iu.rgb(r, g, b);
                    //System.out.println("BBBBBBBB");
                }
                /*
                for(int i=0;i<2;i++){
                    c[i] = read.getRGB(x+i,y);
                    r[i] = iu.r(c[i]);
                }
                */
                cal = r[0] - 2 * r[1] + r[2];
                System.out.print(" " + cal);
                rgbset = iu.rgb(cal, cal, cal);
                //rgbList.add(x, rgbset);
                write.setRGB(x, y, rgbset);
                cal = 0;
            }
        }
        File file = new File("imgTest2.jpg");
        ImageIO.write(write, "jpg", file);
        //return rgbList;
    }

    /**
    * ２つの画像の差分
    */
    public static BufferedImage sabun(BufferedImage readX, BufferedImage readY) throws IOException {
        int w = readX.getWidth(), h = readX.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();
        int cx = 0, cy = 0, rx = 0, ry = 0;
        double val = 0;

        for (int i = 1; i < h; i++) {
            for (int j = 1; j < w; j++) {
                cx = readX.getRGB(j, i);
                //System.out.println("width =" + w + "hight" + h);
                cy = readY.getRGB(j, i);
                rx = iu.r(cx);
                ry = iu.r(cy);

                if (rx - ry < 0) {
                    val = ry - rx;
                } else {
                    val = rx - ry;
                }

                // val = Math.sqrt(rx * rx + ry * ry);

                if (val > 255) {
                    val = 255;
                } else if (val < 0) {
                    val = 0;
                }

                int rgb = iu.rgb((int) val, (int) val, (int) val);
                write.setRGB(j, i, rgb);

            }
            File f2 = new File("sabun2Lena.jpg");
            ImageIO.write(write, "jpg", f2);

        }

        return write;
    }

}
