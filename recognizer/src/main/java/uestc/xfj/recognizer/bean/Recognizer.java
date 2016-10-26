package uestc.xfj.recognizer.bean;

import java.io.Serializable;

/**
 * Created by byhieg on 16-10-26.
 * Mail byhieg@gmail.com
 */

public class Recognizer implements Serializable{

    /**
     * resultCode : 0
     * resultMsg : 识别成功
     * output : EASYWEATHER


     */

    private int resultCode;
    private String resultMsg;
    private String output;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
