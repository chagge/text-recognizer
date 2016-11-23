package root.util;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

/**
 * 工具类
 */
public class ServletUtil {
    
    public static String getParameter(HttpServletRequest request, String key, String defaultValue) {
        return request.getParameter(key) != null ? request.getParameter(key) : defaultValue;
    }
    
    public static boolean getParameter(HttpServletRequest request, String key) {
        return new Boolean( request.getParameter(key) );
    }
}
