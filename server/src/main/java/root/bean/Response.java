package root.bean;

import java.io.Serializable;

/**
 * Servlet响应的结果类
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int resultCode = -1;
    private String resultMsg;
    
    private String output;
    
    public Response() {
        
    }
    
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
