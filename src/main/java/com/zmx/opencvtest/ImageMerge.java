package com.zmx.opencvtest;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


/**
 * Created by zhangwenchao on 2017/9/29.
 * 图片拼接
 * 把多张宽度一样的图片拼接成一张大图片
 */
public class ImageMerge {



    public static void main(String[] args) {

        File file1 = new File("E:/test.jpg");
        File file2 = new File("E:/ling.jpg");
        File file3 = new File("E:/merge.png");
        BufferedImage image1 = null,image2 = null;
        try {
            image1 = ImageIO.read(file1);
            image2 = ImageIO.read(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        simpleMerge(image1,image2,100,300,file3);

        System.out.println("图像合并完成！");
    }



    public static boolean simpleMerge(BufferedImage image1, BufferedImage image2, int posw, int posh, File fileOutput) {

        //合并两个图像
        int w1 = image1.getWidth();
        int h1 = image1.getHeight();
        int w2 = image2.getWidth();
        int h2 = image2.getHeight();

        BufferedImage imageSaved = new BufferedImage(w1, h1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imageSaved.createGraphics();
        g2d.drawImage(image1, null, 0, 0);

        for (int i = 0; i < w2; i++) {
            for (int j = 0; j < h2; j++) {
                int rgb1 = image1.getRGB(i + posw, j + posh);
                int rgb2 = image2.getRGB(i, j);

                if (rgb1 != rgb2) {
                    rgb2 = rgb1 & rgb2;
                }
                imageSaved.setRGB(i + posw, j + posh, rgb2);
            }
        }

        boolean b = false;
        try {
            b = ImageIO.write(imageSaved, "png", fileOutput);
        } catch (IOException ie) {
            ie.printStackTrace();
        }
        return b;
    }
}