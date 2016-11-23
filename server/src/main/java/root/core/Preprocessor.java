package root.core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.util.Arrays;

/**
 * 预处理
 */
public class Preprocessor {
    
    /**
     * 灰度化图像（加权法）
     *
     * @param img
     * @return
     * @throws Exception
     */
    public static BufferedImage grayImage(BufferedImage img) throws Exception {
        
        int width = img.getWidth();
        int height = img.getHeight();
        
        BufferedImage grayBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // 计算灰度值
                final int color = img.getRGB(x, y);
                final int r = (color >> 16) & 0xff;
                final int g = (color >> 8) & 0xff;
                final int b = color & 0xff;
                int gray = (int) (0.3 * r + 0.59 * g + 0.11 * b);
                int newPixel = colorToRGB(255, gray, gray, gray);
                grayBufferedImage.setRGB(x, y, newPixel);
            }
        }
        
        return grayBufferedImage;
    }
    
    
    /**
     * 二值化图像（灰度平均值法）
     *
     * @param img
     * @return
     */
    public static BufferedImage binarizeImage(BufferedImage img) {
        int h = img.getHeight(); //获取图像的高  
        int w = img.getWidth();  //获取图像的宽  
        
        //图像的灰度值数组
        int[][] gray = new int[w][h];
        
        long total = 0;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                gray[x][y] = new Color(img.getRGB(x, y)).getRed(); //因为假设前面已经灰度化了，所以灰度值=red=green=blue
                total += gray[x][y];
            }
        }
        
        //图像的灰度平均值
        int avgGray = (int) (total / w / h);
        
        BufferedImage nbi = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (gray[x][y] > avgGray) {
                    int max = new Color(255, 255, 255).getRGB();
                    nbi.setRGB(x, y, max);
                } else {
                    int min = new Color(0, 0, 0).getRGB();
                    nbi.setRGB(x, y, min);
                }
            }
        }
        
        return nbi;
    }
    
    
    /**
     * 中值滤波图像
     * @param img
     * @return
     */
    public static BufferedImage medianFiltering(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[] pix = new int[w * h];
        
        img.getRGB(0, 0, w, h, pix, 0, w);
        int newpix[] = medianFiltering(pix, w, h);
        
        img.setRGB(0, 0, w, h, newpix, 0, w);
        return img;
    }
    
    
    /**
     * 中值滤波算法
     * @param pix
     * @param w
     * @param h
     * @return
     */
    private static int[] medianFiltering(int pix[], int w, int h) {
        int newpix[] = new int[w * h];
        int[] temp = new int[9];
        ColorModel cm = ColorModel.getRGBdefault();
        int r;
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if (x != 0 && x != w - 1 && y != 0 && y != h - 1) {
                    temp[0] = cm.getRed(pix[x - 1 + (y - 1) * w]);
                    temp[1] = cm.getRed(pix[x + (y - 1) * w]);
                    temp[2] = cm.getRed(pix[x + 1 + (y - 1) * w]);
                    temp[3] = cm.getRed(pix[x - 1 + (y) * w]);
                    temp[4] = cm.getRed(pix[x + (y) * w]);
                    temp[5] = cm.getRed(pix[x + 1 + (y) * w]);
                    temp[6] = cm.getRed(pix[x - 1 + (y + 1) * w]);
                    temp[7] = cm.getRed(pix[x + (y + 1) * w]);
                    temp[8] = cm.getRed(pix[x + 1 + (y + 1) * w]);
                    Arrays.sort(temp);
                    r = temp[4];
                    newpix[y * w + x] = 255 << 24 | r << 16 | r << 8 | r;
                } else {
                    newpix[y * w + x] = pix[y * w + x];
                }
            }
        }
        
        return newpix;
    }
    
    
    /**
     * 颜色分量转换为RGB值
     *
     * @param alpha
     * @param red
     * @param green
     * @param blue
     * @return
     */
    private static int colorToRGB(int alpha, int red, int green, int blue) {
        
        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;
        
        return newPixel;
        
    }
}
