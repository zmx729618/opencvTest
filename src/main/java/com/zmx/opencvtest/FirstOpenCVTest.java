package com.zmx.opencvtest;


import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;

/**
 * Created by zhangwenchao on 2017/9/27.
 */
public class FirstOpenCVTest {
    static {

        //注意程序运行的时候需要在VM option添加该行 指明opencv的dll文件所在路径
        //-Djava.library.path=$PROJECT_DIR$\opencv\x64
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);   //载入opencv all库
    }

    public static void main(String[] args) throws InterruptedException {

        /*
        Mat mat = Imgcodecs.imread("E:/opencv.png");
        ImageViewer imageViewer = new ImageViewer(mat, "第一幅图片");
        imageViewer.imshow();
        */

        /**
         * 1. 读取原始图像转换为OpenCV的Mat数据格式
         */

        Mat srcMat = Imgcodecs.imread("E:/srcImage.jpg");  //原始图像


        /**
         * 2. 强原始图像转化为灰度图像
         */
        Mat grayMat = new Mat(); //灰度图像
        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_RGB2GRAY);

        BufferedImage grayImage =  toBufferedImage(grayMat);

        saveJpgImage(grayImage,"E:/grayImage.jpg");

        System.out.println("保存灰度图像！");


        /**
         * 3、对灰度图像进行二值化处理
         */
        Mat binaryMat = new Mat(grayMat.height(),grayMat.width(),CvType.CV_8UC1);
        Imgproc.threshold(grayMat, binaryMat, 20, 255, Imgproc.THRESH_BINARY);
        BufferedImage binaryImage =  toBufferedImage(binaryMat);
        saveJpgImage(binaryImage,"E:/binaryImage.jpg");
        System.out.println("保存二值化图像！");


        /**
         * 4、图像腐蚀---腐蚀后变得更加宽,粗.便于识别--使用3*3的图片去腐蚀
         */
        Mat destMat = new Mat(); //腐蚀后的图像
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.erode(binaryMat,destMat,element);
        BufferedImage destImage =  toBufferedImage(destMat);
        saveJpgImage(destImage,"E:/destImage.jpg");
        System.out.println("保存腐蚀化后图像！");


        /**
         * 5 图片切割
         */

        //获取截图的范围--从第一行开始遍历,统计每一行的像素点值符合阈值的个数,再根据个数判断该点是否为边界
        //判断该行的黑色像素点是否大于一定值（此处为150）,大于则留下,找到上边界,下边界后立即停止
        int a =0, b=0, state = 0;
        for (int y = 0; y < destMat.height(); y++)//行
        {
            int count = 0;
            for (int x = 0; x < destMat.width(); x++) //列
            {
                //得到该行像素点的值
                byte[] data = new byte[1];
                destMat.get(y, x, data);
                if (data[0] == 0)
                    count = count + 1;
            }
            if (state == 0)//还未到有效行
            {
                if (count >= 150)//找到了有效行
                {//有效行允许十个像素点的噪声
                    a = y;
                    state = 1;
                }
            }
            else if (state == 1)
            {
                if (count <= 150)//找到了有效行
                {//有效行允许十个像素点的噪声
                    b = y;
                    state = 2;
                }
            }
        }
        System.out.println("过滤下界"+Integer.toString(a));
        System.out.println("过滤上界"+Integer.toString(b));


        //参数,坐标X,坐标Y,截图宽度,截图长度
        Rect rect = new Rect(0,a,destMat.width(),b - a);
        Mat resMat = new Mat(destMat,rect);
        BufferedImage resImage =  toBufferedImage(resMat);
        saveJpgImage(resImage,"E:/resImage.jpg");
        System.out.println("保存切割后图像！");


        /**
         * 识别-
         */
       /* try {
            Process  pro = Runtime.getRuntime().exec(new String[]{"D:/Program Files (x86)/Tesseract-OCR/tesseract.exe", "E:/resImage.jpg","E:/result"});
            pro.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        try {
            String result =  TesseractOCRUtil.recognizeText(new File("E:/resImage.jpg"));
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 将Mat图像格式转化为 BufferedImage
     * @param matrix  mat数据图像
     * @return BufferedImage
     */
    private static BufferedImage toBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer); // 获取所有的像素点
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }


    /**
     * 将BufferedImage内存图像保存为图像文件
     * @param image BufferedImage
     * @param filePath  文件名
     */
    private static void saveJpgImage(BufferedImage image, String filePath) {

        try {
            ImageIO.write(image, "jpg", new File(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}
