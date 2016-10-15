package uestc.xfj.recognizer;

import android.app.Application;
import android.content.Context;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

/**
 * Created by byhieg on 16-10-15.
 * Mail byhieg@gmail.com
 */

public class MyApp extends Application {

    private static MyApp mcontext;

    @Override
    public void onCreate() {
        super.onCreate();
        mcontext = this;
        Logger.init()                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);         //default AndroidLogAdapter
    }



    public static Context getAppContext() {
        return mcontext;
    }


}