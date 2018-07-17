package com.compDetection;

//import com.compDetection.ImageUtility.*;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JFileChooser;

//import org.bytedeco.javacpp.avcodec.AVCodecContext.Get_buffer2_AVCodecContext_AVFrame_int;

import javax.imageio.ImageIO;

public class detectEdge {
    public static double size = 200; // リサイズ後の画像サイズ(px)
    public static final int imnum = 35; // 画像の数

    public static void main(String[] args) throws IOException {
        ImageUtility iu = new ImageUtility();
        int number = 1; // ファイルの読み込み順番

        while (number <= imnum) {
            calculate cal = new calculate();
            // 検索される画像のフォルダ指定
            String str = "C:\\detectEdge\\searched2\\img (" + String.valueOf(number) + ").jpg";
            File f = new File(str);

            System.out.println(str);

            JFileChooser filechooser = new JFileChooser();
            String filename = filechooser.getName(f);
            BufferedImage read = ImageIO.read(f);
            int w = read.getWidth(), h = read.getHeight();

            // リサイズ後の画像の保存先
            File resizeImage = new File("C:\\detectEdge\\resizeImage\\img (" + String.valueOf(number) + ").jpg");

            // resize an image
            if (w != size || h != size) {
                // System.out.println("w, h, w*h, scale:" + w + ", " + h + ", " + w*h + "," +
                // (double)80000/(h*w));
                System.out.println("Start Resize");
                BufferedImage read2 = iu.scaleImage(read, (double) size / w, (double) size / h);
                System.out.println("\nRGB: " + (int) read2.getRGB(0, 0) + "\n");
                // System.out.println(read2.getWidth() + ", " + read2.getHeight());

                // リサイズ後の画像の出力
                ImageIO.write(read2, "jpg", resizeImage);

                // 画像に平滑化フィルタをかける
                double ro = 1;
                double filter[][] = cal.Gfilter(ro);
                BufferedImage writeF = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

                writeF = cal.convo(read2, filter);

                File f2 = f;
                ImageIO.write(writeF, "jpg", f2);

                // detect edge
                f2 = iu.Mono(f2);
                // cal.canny(f2, 0.5, 100, 50);
                // cal.canny(f2, 0.5, 150, 100);
                cal.canny(f2, 1, 100, 50, String.valueOf(number));
                // cal.canny(f2, 1, 150, 100);
                // cal.canny(f2, 1.5, 100, 50);
                // cal.canny(f2, 1.5, 150, 100);

            } else {
                File f2 = f;
                BufferedImage read2 = ImageIO.read(f2);
                System.out.println(read2.getWidth() + ", " + read2.getHeight());
                // リサイズ後の画像の出力
                ImageIO.write(read2, "jpg", resizeImage);

                // 画像に平滑化フィルタをかける
                double ro = 1.3;
                double filter[][] = cal.Gfilter(ro);
                BufferedImage writeF = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                writeF = cal.convo(read2, filter);
                ImageIO.write(writeF, "jpg", f2);

                // detect edge
                File f3 = iu.Mono(f2);
                // cal.canny(f3, 0.5, 100, 50);
                // cal.canny(f3, 0.5, 150, 100);

                cal.canny(f3, 1, 100, 50, String.valueOf(number));
                // cal.canny(f3, 1, 150, 100);
                // cal.canny(f3, 1.5, 100, 50);
                // cal.canny(f3, 1.5, 150, 100);

            }
            number++;

        }

    }

}
