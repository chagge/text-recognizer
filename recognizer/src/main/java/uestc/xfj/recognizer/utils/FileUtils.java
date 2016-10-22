package uestc.xfj.recognizer.utils;

import java.io.File;

/**
 * Created by byhieg on 16-10-22.
 * Mail byhieg@gmail.com
 */

public class FileUtils {

    private static File imagePath;

    public static File getImagePath() {
        return imagePath;
    }

    public static void setImagePath(File imagePath) {
        FileUtils.imagePath = imagePath;
    }
}
