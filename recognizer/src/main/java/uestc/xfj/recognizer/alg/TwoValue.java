package uestc.xfj.recognizer.alg;

import android.os.Environment;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import uestc.xfj.recognizer.MyApp;

/**
 * Created by byhieg on 16-10-22.
 */
public class TwoValue {

    private String testImageName;

    public String getTestImageName() {
        return testImageName;
    }

    public void setTestImageName(String testImageName) {
        this.testImageName = testImageName;
    }



    /**
     * 得到二值化的照片
     * @throws IOException
     */
    public File binaryImage(String path) throws IOException{
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
        for(int i= 0 ; i < width ; i++){
            for(int j = 0 ; j < height; j++){
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }

        File dir = MyApp.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(dir.exists()){
            dir.mkdirs();
        }

        File newFile = new File(dir,System.currentTimeMillis() + ".jpg");
        ImageIO.write(grayImage, "jpg", newFile);
        return newFile;
    }

    /**
     * 得到灰度化的图片
     * @throws IOException
     */
    public File grayImage(String path) throws IOException {
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);//重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY
        for(int i= 0 ; i < width ; i++){
            for(int j = 0 ; j < height; j++){
                int rgb = image.getRGB(i, j);
                grayImage.setRGB(i, j, rgb);
            }
        }
        File dir = MyApp.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(dir.exists()){
            dir.mkdirs();
        }

        File newFile = new File(dir,System.currentTimeMillis() + ".jpg");
        ImageIO.write(grayImage, "jpg", newFile);
        return newFile;
    }

}
