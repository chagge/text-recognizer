package uestc.xfj.recognizer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.byhieglibrary.Activity.BaseActivity;
import com.orhanobut.logger.Logger;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import me.shaohui.advancedluban.Luban;
import me.shaohui.advancedluban.OnCompressListener;
import uestc.xfj.recognizer.Constants;
import uestc.xfj.recognizer.MyApp;
import uestc.xfj.recognizer.R;

import static uestc.xfj.recognizer.Constants.CROP_PHOTO;
import static uestc.xfj.recognizer.Constants.TAKE_PHOTO;

public class MainActivity extends BaseActivity {

    @Bind(R.id.take_pic)
    public ImageView takePic;
    @Bind(R.id.choose_pic)
    public ImageView choosePic;
    @Bind(R.id.img_show)
    public ImageView imageView;

    private File currentImageFile = null;
    private CatLoadingView mView;
    private Uri imageUri;

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

        //对Android6.0以上的手机申请拍照权限。
        takePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Logger.d("还是没有权限啊");
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                Constants.PERMISSION);
                    } else {
                        Logger.d("开始了相机调用");
                        doCamera();
                    }
                } else {
                    Logger.d("已经有权限了");
                    doCamera();
                }
            }
        });
        //选择相册
        choosePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryFinal.openGallerySingle(Constants.REQUEST_CODE_GALLERY, MyApp.functionConfig, mOnHanlderResultCallback);
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
               final Drawable drawable = imageView.getDrawable();
                new AlertDialog.Builder(MainActivity.this).setMessage("是否对图片进行压缩").setPositiveButton("恩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (drawable != null) {
                            Luban.get(MainActivity.this).load(currentImageFile).putGear(Luban.THIRD_GEAR).launch(new OnCompressListener() {
                                @Override
                                public void onStart() {
                                    mView.show(getSupportFragmentManager(), "");
                                }

                                @Override
                                public void onSuccess(File file) {
                                    Intent intent = new Intent(MainActivity.this, RecognizerActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("path", file.getAbsolutePath());
                                    intent.putExtra("data",bundle);
                                    mView.dismiss();
                                    startActivity(intent);
                                }
                                @Override
                                public void onError(Throwable e) {
                                    Logger.d(e.getMessage());
                                }
                            });

                        } else {
                            showToast("图片框没有图片，请选择图片");
                        }

                    }
                }).setNegativeButton("不压缩", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (drawable != null) {
                            Intent intent = new Intent(MainActivity.this, RecognizerActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("path", currentImageFile.getAbsolutePath());
                            intent.putExtra("data",bundle);
                            startActivity(intent);
                        } else {
                            showToast("图片框没有图片，请选择图片");
                        }
                    }
                }).show();
//
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
                currentImageFile = new File(resultList.get(0).getPhotoPath());
                try {
                    Bitmap choosePic = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(Uri.fromFile(currentImageFile)));
                    imageView.setImageDrawable(new BitmapDrawable(choosePic));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void doCamera() {
        File dir = MyApp.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir != null && dir.exists()) {
            if(dir.mkdirs()){
                Logger.d("创建成功");
            }else {
                Logger.d("创建失败");
            }
        }
        currentImageFile = new File(dir, System.currentTimeMillis() + ".jpg");
        if (!currentImageFile.exists()) {
            try {
                boolean flag = currentImageFile.createNewFile();
                if (flag) {
                    Logger.d("创建文件成功");
                } else {
                    Logger.d("创建文件失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        imageUri = Uri.fromFile(currentImageFile);
        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        it.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(it, TAKE_PHOTO);

    }

    protected void doCropPhoto(){
        Intent intent = getCropImageIntent();
        startActivityForResult(intent, CROP_PHOTO);
    }

    public Intent getCropImageIntent() {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHOTO:
                doCropPhoto();
                break;

            case CROP_PHOTO:
                try {
                    Bitmap cropPhoto = BitmapFactory.decodeStream(getContentResolver()
                            .openInputStream(imageUri));
                    if(cropPhoto == null){
                        showToast("没有拍摄图片");
                        return;
                    }
                    imageView.setImageDrawable(new BitmapDrawable(cropPhoto));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            // requestCode即所声明的权限获取码，在checkSelfPermission时传入
            case Constants.PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.d("已经获取到权限了");
                    doCamera();
                    // 获取到权限，作相应处理（调用定位SDK应当确保相关权限均被授权，否则可能引起定位失败）
                } else {
                    // 没有获取到权限，做特殊处理
                    Logger.d("获取权限失败");
                }
                break;
            default:
                break;
        }
    }
}
