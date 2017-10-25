package com.zmx.opencvtest;


import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;


/**
 * Created by zhangwenchao on 2017/9/29.
 *
 * 执行两个图片的相减操作，用于抓取用户所画的部分，去除本来就有的部分
 */
public class ImageSub {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);   //载入opencv all库
    }

    public static void sub(String two_path, String one_path, String des_path) {


        // 读取图像，不改变图像的原始信息
        Mat image1 = Imgcodecs.imread(two_path, Imgcodecs.CV_LOAD_IMAGE_COLOR);
        Mat image2 = Imgcodecs.imread(one_path, Imgcodecs.CV_LOAD_IMAGE_COLOR);

        Mat image3 = new Mat(image1.size(), CvType.CV_64F);
        Core.subtract(image2, image1, image3);

        Mat image5 = new Mat(image1.size(), CvType.CV_64F);
        Core.bitwise_not(image3, image5);

        Imgcodecs.imwrite(des_path, image5);

        System.out.println("图片相减成功");
    }

    public static void main(String[] args) {

        ImageSub.sub("E:/merge.png",
                     "E:/test.jpg",
                     "E:/res.jpg");


        System.out.println("图片相减成功");


    }

}

