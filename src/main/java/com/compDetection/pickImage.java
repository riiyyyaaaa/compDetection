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
    public static final int number = 156;
    public static final String origURI = "C:\\detectEdge\\resizeImage\\"; //元画像（リサイズ済）
    public static final String edgeURI = "C:\\detectEdge\\queryImage\\"; //エッジ画像

    public static void main(String[] args) throws IOException {
        //String queryImage = "C://photo//";
        //File file1 = new File(queryImage);

        for (int i = 0; i < number; i++) {
            System.out.printf("Image %3d: ", i + 1);
            Scanner scan = new Scanner(System.in);
            ImageUtility iu = new ImageUtility();

            String[] val = new String[10];

            int num = 0;
            while (num < 5) {
                if (num == 0) {
                    //0の時、番号の画像を左にもってくる
                    //int val0 = scan.nextInt();
                    File file = new File(origURI + "img (" + String.valueOf(i + 1) + ").jpg");
                    File fileEdge = new File(edgeURI + "img (" + String.valueOf(i + 1) + ").jpg");
                    BufferedImage image = ImageIO.read(file);
                    BufferedImage imageEdge = ImageIO.read(fileEdge);
                    File idealimage = new File("C:\\detectEdge\\Ideal\\ideal\\" + String.valueOf(i + 1) + ".jpg");
                    File idealEdge = new File("C:\\detectEdge\\Ideal\\ideal\\" + String.valueOf(i + 1) + "edge.jpg");
                    ImageIO.write(image, "jpg", idealimage);
                    ImageIO.write(imageEdge, "jpg", idealEdge);
                    //System.out.println("start");
                    num++;

                } else {
                  //i=0の時の画像m右側に連結していく
                  //String name = "./ideal" + String.valueOf(i + 1);
                  //File file = new File(name);

                  File Ideal = new File("C:\\detectEdge\\Ideal\\ideal\\" + String.valueOf(i + 1) + ".jpg");
                  File idealEdge = new File("C:\\detectEdge\\Ideal\\ideal\\" + String.valueOf(i + 1) + "edge.jpg");

                  val[num] = scan.next();

                  String Imagename = origURI + "img (" + val[num] + ").jpg";
                  String edgename = edgeURI + "img (" + val[num] + ").jpg";
                  //System.out.println("name: " + Imagename + "   num:" + val[num]);

                  File file2 = new File(Imagename);
                  File edgefile = new File(edgename);

                  BufferedImage image = ImageIO.read(Ideal);
                  BufferedImage image2 = ImageIO.read(file2);
                  BufferedImage ideal = iu.outputResult(image, image2);

                  BufferedImage edge = ImageIO.read(edgefile);
                  BufferedImage edge2 = ImageIO.read(idealEdge);
                  BufferedImage idealedgecom = iu.outputResult(edge2, edge);


                  ImageIO.write(ideal, "jpg", Ideal);
                  ImageIO.write(idealedgecom, "jpg", idealEdge);
                  num++;
                }
            }

                  File Ideal = new File("C:\\detectEdge\\Ideal\\ideal\\" + String.valueOf(i + 1) + ".jpg");
                  File idealEdge = new File("C:\\detectEdge\\Ideal\\ideal\\" + String.valueOf(i + 1) + "edge.jpg");

                  BufferedImage ideal = ImageIO.read(Ideal);
                  BufferedImage idealedgecom = ImageIO.read(idealEdge);

                  BufferedImage resultImg = iu.outputResultLongi(ideal, idealedgecom);
                  ImageIO.write(resultImg, "jpg", Ideal);

            //BufferedImage image = new BufferedImage
            //iu.outputResult()
        }
        System.out.println("finish");
    }
}
