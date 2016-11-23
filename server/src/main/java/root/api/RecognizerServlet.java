package root.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import root.core.Recognizer;
import root.util.ServletUtil;
import root.bean.Response;
import root.bean.Task;

/**
 * 识别接口
 * 请求方法：Post
 */
@WebServlet(name = "RecognizerServlet")
@MultipartConfig
public class RecognizerServlet extends HttpServlet {
    private static final File TEMP_DIR = new File( FileUtils.getTempDirectoryPath() + "/recognizer/" );
    
    private Gson gson = new GsonBuilder().serializeNulls().create();
    
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        Response rspObj = new Response();
    
        //创建一个识别任务
        Task task = new Task();
        
        //提取请求参数
        task.setLang( ServletUtil.getParameter(request, "lang", "eng") );
        task.setDoPreprocess( ServletUtil.getParameter(request, "doPreprocess") );
        
        Part filePart = request.getPart("file");
        if (filePart != null) {
            //先保存图片
            File file = File.createTempFile("input_img_", "." + ServletUtil.getParameter(request, "format", "jpg"), TEMP_DIR);
            FileUtils.copyInputStreamToFile(filePart.getInputStream(), file);
            System.out.println("保存了一张图片至：" + file.getAbsolutePath());
            
            //添加图片文件到task中
            task.setInputFile(file);
            
            try {
                //识别
                String output = Recognizer.recognize(task);
    
                //保存结果
                rspObj.setResultCode(0);
                rspObj.setResultMsg("识别成功");
                rspObj.setOutput(output);
            } catch (Exception e) {
                e.printStackTrace();
                rspObj.setResultMsg("识别模块出现异常");
            }
        } else {
            rspObj.setResultMsg("无图片数据");
        }
        
        //响应客户端
        response.getWriter().println(gson.toJson(rspObj));
    }

}
