package com.compDetection;

import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import javax.swing.JFileChooser;
import java.util.*;

/**
 * 上位五枚に当てはまる画像の連結処理
 */

public class pickImage {
    public static void main(String[] args) throws IOException {
        String queryImage = ("C://photo//");
        //File file1 = new File(queryImage);

        for (int i = 0; i < 156; i++) {
            System.out.printf("Image %3d:", i + 1);
            Scanner scan = new Scanner(System.in);
            int num = 0;
            ImageUtility iu = new ImageUtility();

            String[] val = new String[10];

            File file2 = new File(queryImage);

            if (i == 0) {
                //0の時、番号の画像を左にもってくる
                //int val0 = scan.nextInt();
                File file = new File("C:\\photo" + String.valueOf(i + 1) + ".jpg");
                BufferedImage image = ImageIO.read(file);
                File Ideal = new File("C:\\detectEdge\\Ideal\\ideal" + String.valueOf(i + 1));
                ImageIO.write(image, "jpg", Ideal);

            } else {
                //i=0の時の画像m右側に連結していく
                String name = "./ideal" + String.valueOf(i + 1);
                File file = new File(name);
                while (scan.hasNext()) {
                    val[num] = scan.next();
                    queryImage += val[num] + ".jpg";

                    BufferedImage image = ImageIO.read(file);
                    BufferedImage image2 = ImageIO.read(file2);
                    BufferedImage ideal = iu.outputResult(image, image2);

                    ImageIO.write(ideal, "jpg", file2);
                    num++;
                }
            }

            //BufferedImage image = new BufferedImage
            //iu.outputResult()
        }
        System.out.println("finish");
    }
}