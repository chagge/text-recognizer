package uestc.xfj.recognizer;

import android.app.Application;
import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import uestc.xfj.recognizer.listener.PicassoPauseOnScrollListener;
import uestc.xfj.recognizer.utils.GlideImageLoader;

/**
 * Created by byhieg on 16-10-15.
 * Mail byhieg@gmail.com
 */

public class MyApp extends Application {

    private static MyApp mcontext;
    public static FunctionConfig functionConfig;


    @Override
    public void onCreate() {
        super.onCreate();
        mcontext = this;
        Logger.init()                 // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2);         //default AndroidLogAdapter


        ThemeConfig themeConfig = new ThemeConfig.Builder().
                setIconCamera(0).
                setIconPreview(0).
                setTitleBarBgColor(ContextCompat.getColor(mcontext,R.color.maincolor)).
                setFabNornalColor(ContextCompat.getColor(mcontext,R.color.maincolor)).
                setFabPressedColor(ContextCompat.getColor(mcontext,R.color.maincolor)).
                build();
        functionConfig = new FunctionConfig.Builder().
                setEnableCamera(true).
                setEnableEdit(true).
                setEnableCrop(true).
                setEnableRotate(true).
                setCropSquare(true).
                setEnablePreview(true).build();
        ImageLoader imageloader = new GlideImageLoader();

        CoreConfig coreConfig = new CoreConfig.Builder(mcontext, imageloader, themeConfig).
                setFunctionConfig(functionConfig).
                setPauseOnScrollListener(new PicassoPauseOnScrollListener(false, true)).
                build();
        GalleryFinal.init(coreConfig);

    }

    public static Context getAppContext() {
        return mcontext;
    }


}