package root.core;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import root.bean.Task;

/**
 * 识别器（基于tesseract）
 * 支持并发识别
 */
public class Recognizer {
    
    public static String recognize(Task task) throws Exception {
        //预处理
        if ( task.isDoPreprocess() ) {
            File processedFile = doPreprocess( task.getInputFile() );
            task.setInputFile(processedFile);
        }
        
        //创建输出文件
        File outputFile = File.createTempFile("output_", ".txt", task.getInputFile().getParentFile());
        
        //设置执行进程参数
        ProcessBuilder prb = new ProcessBuilder("tesseract", task.getInputFile().getName(), FilenameUtils.removeExtension(outputFile.getName()), "-l", task.getLang());
        //图片文件所在目录为进程工作目录
        prb.directory(task.getInputFile().getParentFile());
        
        //执行
        if (prb.start().waitFor() != 0) {
            throw new Exception("Tesseract执行出错！");
        }
        
        //返回结果
        String output = FileUtils.readFileToString(outputFile, "UTF-8");
        
        //对英文单词做矫正
        if ("eng".equals(task.getLang())) {
            output = WordsCorrector.correct(output);
        }
    
        return output;
    }
    
    
    /**
     * 做预处理
     * @param inputImage
     * @return
     * @throws Exception
     */
    private static File doPreprocess(File inputImage) throws Exception {
        BufferedImage bimg = ImageIO.read(inputImage);
        
        //灰度化
        bimg = Preprocessor.grayImage(bimg);
        //二值化
        bimg = Preprocessor.binarizeImage(bimg);
        //中值滤波去噪
        bimg = Preprocessor.medianFiltering(bimg);
    
        String extension = FilenameUtils.getExtension(inputImage.getName());
        String outputFilename = FilenameUtils.removeExtension(inputImage.getName()) + "_preprocessed." + extension;
        File outputFile = new File(inputImage.getParent(), outputFilename);
    
        ImageIO.write(bimg, extension, outputFile);
        
        return outputFile;
    }
}
