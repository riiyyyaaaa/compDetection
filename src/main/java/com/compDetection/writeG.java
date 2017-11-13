package com.compDetection;

import java.awt.*;
import java.awt.geom.*;
import java.awt.Event;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

public class writeG extends JFrame {
    public static void main(String[] args) {
        writeG test = new writeG();

        test.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        test.setBounds(0, 0, 300, 300);
        test.setVisible(true);
    }

    /**
     * 画像に白の割合を描画
     */
    public static File writeRatio(File file) throws IOException {
        BufferedImage readImage = ImageIO.read(file);

        Graphics2D off = readImage.createGraphics();
        off.setPaint(Color.white);
        off.draw(new Ellipse2D.Double(30, 40, 50, 50));
        off.drawImage(readImage, null, 0, 0);
        ImageIO.write(readImage, "JPEG", file);

        return file;
    }

}