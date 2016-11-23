package root.bean;

import java.io.File;

/**
 * 识别任务封装类
 */
public class Task {
    //输入文件
    private File inputFile;
    //目标语言
    private String lang;
    //是否做预处理
    private boolean doPreprocess;
    
    public Task() {
    
    }
    
    public File getInputFile() {
        return inputFile;
    }
    
    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }
    
    public String getLang() {
        return lang;
    }
    
    public void setLang(String lang) {
        this.lang = lang;
    }
    
    public boolean isDoPreprocess() {
        return doPreprocess;
    }
    
    public void setDoPreprocess(boolean doPreprocess) {
        this.doPreprocess = doPreprocess;
    }
}
