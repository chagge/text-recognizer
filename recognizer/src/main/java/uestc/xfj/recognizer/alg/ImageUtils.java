package uestc.xfj.recognizer.alg;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

import java.util.Arrays;

/**
 * Created by byhieg on 16-10-24.
 * Mail byhieg@gmail.com
 */

public class ImageUtils {

    private int count;

    public Bitmap bitmap2Gray(Bitmap bmSrc) {
        // 得到图片的长和宽
        int width = bmSrc.getWidth();
        int height = bmSrc.getHeight();
        // 创建目标灰度图像
        Bitmap bmpGray = null;
        bmpGray = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        // 创建画布
        Canvas c = new Canvas(bmpGray);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmSrc, 0, 0, paint);
        return bmpGray;
    }

    public Bitmap lineGrey(Bitmap image)
    {
        //得到图像的宽度和长度
        int width = image.getWidth();
        int height = image.getHeight();
        //创建线性拉升灰度图像
        Bitmap linegray = null;
        linegray = image.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到每点的像素值
                int col = image.getPixel(i, j);
                int alpha = col & 0xFF000000;
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 增加了图像的亮度
                red = (int) (1.1 * red + 30);
                green = (int) (1.1 * green + 30);
                blue = (int) (1.1 * blue + 30);
                //对图像像素越界进行处理
                if (red >= 255)
                {
                    red = 255;
                }

                if (green >= 255) {
                    green = 255;
                }

                if (blue >= 255) {
                    blue = 255;
                }
                // 新的ARGB
                int newColor = alpha | (red << 16) | (green << 8) | blue;
                //设置新图像的RGB值
                linegray.setPixel(i, j, newColor);
            }
        }
        return linegray;
    }

    // 该函数实现对图像进行二值化处理
    public Bitmap gray2Binary(Bitmap graymap) {
        //得到图形的宽度和长度
        int width = graymap.getWidth();
        int height = graymap.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = graymap.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);

                //对图像进行二值化处理
                if (gray <= 95) {
                    gray = 0;
                } else {
                    gray = 255;
                    count++;
                }
                    // 新的ARGB
                    int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                    //设置新图像的当前像素值
                    binarymap.setPixel(i, j, newColor);


            }
        }
        if (count == width * height) {
            binarymap = doReverse(graymap);
        }
        return binarymap;
    }

    /**
     * 正常的二值化处理，图片可能会出现都是白色的情况，这里我做了反转处理，将字的部分变为黑色，背景为白色
     * @param graymap
     * @return
     */
    private Bitmap doReverse(Bitmap graymap) {
        int width = graymap.getWidth();
        int height = graymap.getHeight();
        //创建二值化图像
        Bitmap binarymap = null;
        binarymap = graymap.copy(Bitmap.Config.ARGB_8888, true);
        //依次循环，对图像的像素进行处理
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //得到当前像素的值
                int col = binarymap.getPixel(i, j);
                //得到alpha通道的值
                int alpha = col & 0xFF000000;
                //得到图像的像素RGB的值
                int red = (col & 0x00FF0000) >> 16;
                int green = (col & 0x0000FF00) >> 8;
                int blue = (col & 0x000000FF);
                // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);

                //对图像进行二值化处理
                if (gray < 220) {
                    gray = 255;
                } else {
                    gray = 0;
                }
                // 新的ARGB
                int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                //设置新图像的当前像素值
                binarymap.setPixel(i, j, newColor);
            }
        }
        return binarymap;
    }

    /**
     * 中值滤波
     */
    public Bitmap medianFiltering(Bitmap denosingBitmap) {
        int w = denosingBitmap.getWidth();
        int h = denosingBitmap.getHeight();
        Bitmap bitmap = null;
        bitmap = denosingBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int[] pix = new int[w*h];
        denosingBitmap.getPixels(pix,0,w,0,0,w,h);
        bitmap.setPixels(doFilter(pix, w, h),
                0,w,0,0,w,h);
        return bitmap;
    }
    /**
     * 中值滤波
     * @param pix 像素矩阵数组
     * @param w 矩阵的宽
     * @param h 矩阵的高
     * @return 处理后的数组
     */
    public int[] doFilter(int pix[], int w, int h) {
        int newpix[] = new int[ w * h];
        int[] temp = new int[9];
        int r = 0;
        for(int y = 0; y < h; y ++) {
            for(int x = 0; x < w; x ++) {
                if(x != 0 && x != w-1 && y !=0 && y !=h-1) {
                    //g = median[(x-1,y-1) + f(x,y-1)+ f(x+1,y-1)
                    //  + f(x-1,y) + f(x,y) + f(x+1,y)
                    //  + f(x-1,y+1) + f(x,y+1) + f(x+1,y+1)]
                    temp[0] = (pix[x-1+(y-1)*w] & 0x00FF0000) >> 16;
                    temp[1] = (pix[x+(y-1)*w] & 0x00FF0000) >> 16;
                    temp[2] = (pix[x+1+(y-1)*w] & 0x00FF0000) >> 16;
                    temp[3] = (pix[x-1+(y)*w] & 0x00FF0000) >> 16;
                    temp[4] = (pix[x+(y)*w] & 0x00FF0000) >> 16;
                    temp[5] = (pix[x+1+(y)*w] & 0x00FF0000) >> 16;
                    temp[6] = (pix[x-1+(y+1)*w] & 0x00FF0000) >> 16;
                    temp[7] = (pix[x+(y+1)*w] & 0x00FF0000) >> 16;
                    temp[8] = (pix[x+1+(y+1)*w] & 0x00FF0000) >> 16;
                    Arrays.sort(temp);
                    r = temp[4];
                    newpix[y*w+x] = 255<<24 | r<<16 | r<<8 | r;
                } else {
                    newpix[y*w+x] = pix[y*w+x];
                }
            }
        }
        return newpix;
    }


    public Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap;

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
//      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        //其实是无效的,大家尽管尝试
        return bitmap;
    }

}
