package uestc.xfj.recognizer.activity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.byhieglibrary.Activity.BaseActivity;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import info.hoang8f.widget.FButton;
import uestc.xfj.recognizer.R;
import uestc.xfj.recognizer.alg.TwoValue;

public class RecognizerActivity extends BaseActivity {

    @Bind(R.id.back)
    public ImageView back;
    @Bind(R.id.image)
    public ImageView image;
    @Bind(R.id.recognizer_button)
    public FButton recognizer;
    @Bind(R.id.copy_left)
    public TextView copyLeft;
    @Bind(R.id.gray_button)
    public FButton gray;
    @Bind(R.id.two_value_button)
    public FButton two;

    private File imageFile = null;

    public CatLoadingView catLoadingView;
    private int count = 1;
    private String path;

    @Override
    public int getLayoutId() {
        return R.layout.activity_recognizer;
    }

    @Override
    public void initData() {
        catLoadingView = new CatLoadingView();
        path = getIntent().getBundleExtra("data").getString("path");
        if(path != null){
            imageFile = new File(path);
        }else{
            showToast("文件有问题");
        }

    }

    @Override
    public void initEvent() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recognizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catLoadingView.show(getSupportFragmentManager(), "");
            }
        });

        copyLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RecognizerActivity.this).
                        setTitle("作者").
                        setMessage("Android文字识别分别由王晓雪，陈君，石琦峰三人合力做成").
                        setPositiveButton("嗯", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                Matrix matrix  = new Matrix();
                matrix.setRotate(90 * count);
                Bitmap newBitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                image.setImageBitmap(newBitmap);
                count++;
            }
        });

        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TwoValue twoValue = new TwoValue();
                try {
                    image.setImageURI(Uri.fromFile(twoValue.grayImage(path)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void initView() {
        image.setImageURI(Uri.fromFile(imageFile));
    }

    @Override
    public void initTheme() {

    }
}
