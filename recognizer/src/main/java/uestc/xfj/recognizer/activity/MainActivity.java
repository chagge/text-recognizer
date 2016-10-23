package uestc.xfj.recognizer.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.byhieglibrary.Activity.BaseActivity;
import com.orhanobut.logger.Logger;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import uestc.xfj.recognizer.Constants;
import uestc.xfj.recognizer.MyApp;
import uestc.xfj.recognizer.R;
import uestc.xfj.recognizer.utils.FileUtils;

public class MainActivity extends BaseActivity {

    @Bind(R.id.take_pic)
    public ImageView takePic;
    @Bind(R.id.choose_pic)
    public ImageView choosePic;
    @Bind(R.id.img_show)
    public ImageView imageView;

    private File currentImageFile = null;
    private CatLoadingView mView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        mView = new CatLoadingView();
    }

    @Override
    public void initEvent() {
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFinal.openGallerySingle(Constants.REQUEST_CODE_GALLERY, MyApp.functionConfig, mOnHanlderResultCallback);
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                new AlertDialog.Builder(MainActivity.this).setMessage("是否识别该图片的文字").setPositiveButton("嗯", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, RecognizerActivity.class);
                        Bundle bundle = new Bundle();
                        if (currentImageFile != null) {
                            bundle.putString("path",currentImageFile.getAbsolutePath());
                            intent.putExtra("data", bundle);
                            startActivity(intent);
                        }else{
                            showToast("图片框没有图片，请选择图片");
                        }

                    }
                }).setNegativeButton("不要", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mView.show(getSupportFragmentManager(), "");
                    }
                }).show();
                return true;
            }
        });

    }
    @Override
    public void initView() {

    }

    @Override
    public void initTheme() {

    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                Logger.d(resultList.get(0).getPhotoPath());
                FileUtils.setImagePath(new File(resultList.get(0).getPhotoPath()));
                imageView.setImageURI(getUri());
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Activity.DEFAULT_KEYS_DIALER){
            imageView.setImageURI(Uri.fromFile(currentImageFile));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private Uri getUri(){
        currentImageFile = FileUtils.getImagePath();
        return Uri.fromFile(currentImageFile);
    }

    private void doCamera(){
        File dir = MyApp.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(dir.exists()){
            dir.mkdirs();
        }
        FileUtils.setImagePath(new File(dir,System.currentTimeMillis() + ".jpg"));
        currentImageFile = FileUtils.getImagePath();
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
}
