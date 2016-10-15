package uestc.xfj.recognizer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

import uestc.xfj.recognizer.Constants;
import uestc.xfj.recognizer.MyApp;
import uestc.xfj.recognizer.R;

import static uestc.xfj.recognizer.R.id.img_show;

public class MainActivity extends AppCompatActivity {


    public Toolbar toolbar;
    public ImageView imageView;
    private static final String  TAG  = "uestc.xfj.recognizer.activity.MainActivity";
    private File currentImageFile = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView = (ImageView) findViewById(img_show);
        toolbar.setTitle("文字识别");
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.take_pic:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                                Logger.d("还是没有权限啊");
                                requestPermissions(new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        Constants.PERMISSION);
                            }else{
                                Logger.d("开始了相机调用");
                                doCamera();
                            }
                        }else{
                            Logger.d("已经有权限了");
                            doCamera();
                        }
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void doCamera(){
        File dir =  MyApp.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(dir.exists()){
            dir.mkdirs();
        }
        currentImageFile = new File(dir,System.currentTimeMillis() + ".jpg");
        if(!currentImageFile.exists()){
            try {
                currentImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentImageFile));
        startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);
        startActivityForResult(it, Activity.DEFAULT_KEYS_DIALER);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch(requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case Constants.PERMISSION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.d("已经获取到权限了");
                    doCamera();
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                }
                else {
                    // 没有获取到权限，做特殊处理
                    Logger.d("获取权限失败");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Activity.DEFAULT_KEYS_DIALER){
            Logger.d("到底执行没执行？？？");
            imageView.setImageURI(Uri.fromFile(currentImageFile));
        }
    }
}
