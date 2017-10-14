package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class calculate {
    public static void main() {

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
     * ガウシアンフィルタの生成
     */
    public static double[][] Gfilter(int ro) {
        int n = ro * 3; //フィルタのサイズ
        double[][] filter = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                filter[i][j] = Math.exp(-(j * j + i * i) / 2 * ro * ro) / (2 * Math.PI * ro * ro);
                System.out.print(" " + filter[i][j]);
            }
        }

        return filter;
    }

    /**
     * フィルタとの畳み込み計算
     */
    public static void convo(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int cal = 0;
        int ro = 1; //σの値、自分で変える。
        int w = read.getWidth(), h = read.getHeight();
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();

        System.out.println("+++++++++++FILTER++++++++++");
        double filter[][] = Gfilter(ro);

        System.out.println("\n+++++++++++CONVO+++++++++++");

        for (int i = 1; i < w - 1; i++) {
            for (int j = 1; j < h - 1; j++) {
                cal = 0;
                for (int k = 0; k < filter.length; k++) {
                    for (int l = 0; l < filter.length; l++) {
                        int c = read.getRGB(i - 1 + l, j - 1 + k);
                        int r = iu.r(c);
                        cal += r * filter[l][k];
                        System.out.print(" " + cal);
                    }
                }
                if (cal > 225) {
                    cal = 225;
                } else if (cal < 0) {
                    cal = 0;
                }
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                write.setRGB(i, j, (int) rgb);
            }
        }
        File f2 = new File("convo.jpg");
        ImageIO.write(write, "jpg", f2);
    }

    public static void canny1(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int cal = 0;
        int ro = 1; //σの値、自分で変える。
        int w = read.getWidth(), h = read.getHeight();
        ArrayList rgbList = new ArrayList<Integer>();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();

        System.out.println("+++++++++++FILTER++++++++++");
        double filter[][] = Gfilter(ro);
        double filterX[][] = new double[filter.length - 1][filter.length - 1]; //x方向に一次微分したガウシアンフィルタ
        double filterY[][] = new double[filter.length - 1][filter.length - 1]; //y方向に一次微分したガウシアンフィルタ

        //フィルターをｘ方向に一次微分
        for (int y = 0; y < filter.length; y++) {

            for (int x = 0; x < filter.length; x++) {

                filterX[y][x] = filter[y][x + 1] - filter[y][x];
                System.out.print(" " + x + "," + y + filterX[x][y]);
            }
        }

        //フィルターをy方向に一次微分
        for (int y = 0; y < filter.length; y++) {

            for (int x = 0; x < filter.length; x++) {

                filterY[y][x] = filter[y + 1][x] - filter[y][x];
                System.out.print(" " + x + "," + y + filterY[x][y]);
            }
        }

        //一次微分したガウシアンフィルタと元画像の畳み込み
        for (int i = 1; i < w - 1; i++) {
            for (int j = 1; j < h - 1; j++) {
                cal = 0;
                for (int k = 0; k < filter.length; k++) {
                    for (int l = 0; l < filter.length; l++) {
                        //TODO
                        int c = read.getRGB(i - 1 + l, j - 1 + k);
                        int r = iu.r(c);
                        cal += r * filter[l][k];
                        System.out.print(" " + cal);
                        //TODO
                    }
                }
                if (cal > 225) {
                    cal = 225;
                } else if (cal < 0) {
                    cal = 0;
                }
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                write.setRGB(i, j, (int) rgb);
            }
        }

        System.out.println("\n+++++++++++CONVO+++++++++++");
        //ガウシアンと元画像の畳み込み
        for (int i = 1; i < w - 1; i++) {
            for (int j = 1; j < h - 1; j++) {
                cal = 0;
                for (int k = 0; k < filter.length; k++) {
                    for (int l = 0; l < filter.length; l++) {
                        int c = read.getRGB(i - 1 + l, j - 1 + k);
                        int r = iu.r(c);
                        cal += r * filter[l][k];
                        System.out.print(" " + cal);
                    }
                }
                if (cal > 225) {
                    cal = 225;
                } else if (cal < 0) {
                    cal = 0;
                }
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                write.setRGB(i, j, (int) rgb);
            }
        }

        System.out.println("+++++++++++bibun++++++++++");
        //出力画像をx方向に一次微分

        //出力画像をy方向に一次微分

        File f2 = new File("convo.jpg");
        ImageIO.write(write, "jpg", f2);
    }

    public static ArrayList Gaussian() throws IOException {

        ImageUtility iu = new ImageUtility();

        File f = new File("C:\\Users\\riya\\Documents\\compDetection\\src\\main\\java\\com\\compDetection\\mono.png");
        BufferedImage read = ImageIO.read(f);
        int w = read.getWidth(), h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);

        ArrayList rgbList = new ArrayList<>();
        int row = 1;
        int n = 3 * row;
        int c;
        int rgb;
        double bubun;
        double all;

        for (int y = 3; y < h - 2; y++) {

            for (int x = 3; x < w - 2; x++) {
                all = 0;

                for (int v = -1; v < n - 1; v++) {
                    bubun = 0;

                    for (int u = -1; u < n - 1; u++) {
                        c = read.getRGB(x - u, y - v);
                        //System.out.print(" " + c);

                        int r = iu.r(c);
                        int g = iu.g(c);
                        int b = iu.b(c);
                        rgb = iu.rgb(r, g, b);
                        bubun += Math.exp(-(u * u + v * v) / (2 * row * row)) / (2 * Math.PI * row * row) * rgb;
                        System.out.print(" " + bubun);
                    }
                    all += bubun;
                    //  System.out.print(" " + all);
                }
                //rgb = iu.gray((int) all);
                write.setRGB(x, y, (int) all);
                //rgbList.add(x, cal);
            }
        }
        File f2 = new File("imgTestqwqw.jpg");
        ImageIO.write(write, "jpg", f2);

        return rgbList;
    }

    //グレースケール画像にガウシアンフィルタ
    public static void monoGaussianFilter(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int w = read.getWidth();
        int h = read.getHeight();
        BufferedImage write = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        ImageUtility iu = new ImageUtility();
        //filterを指定。後で動的に変えられるようにメソッド作る
        double[][] filter = { { 0.11f, 0.11f, 0.11f }, { 0.11f, 0.12f, 0.11f }, { 0.11f, 0.11f, 0.11f } };
        double[][] filter2 = { { -1, -1, -1 }, { -1, 9, -1 }, { -1, -1, -1 } };

        double cal;

        for (int j = 1; j < h - 1; j++) {
            for (int i = 1; i < w - 1; i++) {
                cal = 0;
                for (int l = 0; l < 3; l++) {
                    for (int m = 0; m < 3; m++) {
                        int c = read.getRGB(i - 1 + m, j - 1 + l);
                        double r = iu.r(c);
                        System.out.print(" color:" + c + " , r:" + r);
                        //int rgb = iu.rgb(r, g, b);
                        r *= filter[l][m];
                        cal += r;
                        // System.out.print("cal:" + calr + " " + r + " ");
                    }
                    System.out.println();
                }
                System.out.println();
                int rgb = iu.rgb((int) cal, (int) cal, (int) cal);
                //System.out.print(" " + (int) rgb);
                write.setRGB(i, j, (int) rgb);
            }
        }
        File f2 = new File("tetemono.jpg");
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
        ImageIO.write(writeImage, "png", new File("monoShiro.png"));
    }

    static BufferedImage toMono(BufferedImage src) {
        BufferedImage dist = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        dist.getGraphics().drawImage(src, 0, 0, null);
        return dist;
    }

}