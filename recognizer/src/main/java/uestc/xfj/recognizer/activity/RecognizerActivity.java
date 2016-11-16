package uestc.xfj.recognizer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.byhieglibrary.Activity.BaseActivity;
import com.example.byhieglibrary.Net.HttpUtils;
import com.example.byhieglibrary.Net.ResultCallback;
import com.orhanobut.logger.Logger;
import com.roger.catloadinglibrary.CatLoadingView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import butterknife.Bind;
import info.hoang8f.widget.FButton;
import okhttp3.Request;
import uestc.xfj.recognizer.R;
import uestc.xfj.recognizer.bean.Recognizer;

public class RecognizerActivity extends BaseActivity {

    @Bind(R.id.back)
    public ImageView back;
    @Bind(R.id.image)
    public ImageView image;
    @Bind(R.id.copy_left)
    public TextView copyLeft;
    @Bind(R.id.chinese_button)
    public FButton chineseButton;
    @Bind(R.id.english_button)
    public FButton englishButton;
    @Bind(R.id.mix_button)
    public FButton mixButton;

    private File imageFile = null;

    public CatLoadingView catLoadingView;
    private int count;
    private Bitmap bitmap;
    private String url;
    Bitmap[] newBitmaps = new Bitmap[4];

    @Override
    public int getLayoutId() {
        return R.layout.activity_recognizer;
    }

    @Override
    public void initData() {
        count = 0;
        catLoadingView = new CatLoadingView();
        String path = getIntent().getBundleExtra("data").getString("path");
        if (path != null) {
            imageFile = new File(path);
        } else {
            showToast("文件有问题");
        }
        url = "http://123.206.6.222/recognizer/recog";

        bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        Matrix[] matrixs = new Matrix[4];
        for (int i = 0; i < 4; i++) {
            matrixs[i] = new Matrix();
            matrixs[i].setRotate(90 * i);
        }
        if (bitmap == null) {
            showToast("没有图片");
            return;
        }
        for(int i = 0 ;i < 4;i++){
            newBitmaps[i] = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrixs[i], true);
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
                count++;
                image.setImageBitmap(newBitmaps[count % 4]);
            }
        });

        chineseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catLoadingView.show(getSupportFragmentManager(), "");
                saveFile(newBitmaps[count % 4]);
                recognizeChinese();
            }
        });

        englishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catLoadingView.show(getSupportFragmentManager(), "");
                saveFile(newBitmaps[count % 4]);
                recognizeEnglish();
            }
        });

        mixButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                catLoadingView.show(getSupportFragmentManager(), "");
                saveFile(newBitmaps[count % 4]);
                recognizeMix();
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

    private void recognizeChinese() {
        doPost(imageFile,"chi_sim");
    }

    private void recognizeEnglish() {
        doPost(imageFile,"eng");
    }

    private void recognizeMix() {
        doPost(imageFile,"chi_sim+eng");
    }

    private void doPost(File file, String lang) {
        String fileName = file.getName();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        Map<String, String> params = new HashMap<>();
        params.put("format", prefix);
        params.put("lang", lang);
        url = HttpUtils.url(url, null, params);
        Logger.d(url);
        HttpUtils.postFile(url, new ResultCallback<Recognizer>() {
            @Override
            public void onResponse(Recognizer response) {
                Logger.d(response);
                int resultCode = response.getResultCode();
                if(resultCode != 0){
                    showToast("图片有问题");
                    catLoadingView.dismiss();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("result", response.getOutput());
                Intent intent = new Intent();
                intent.putExtra("data", bundle);
                startActivity(ResultActivity.class, bundle);
                catLoadingView.dismiss();
                finish();
            }

            @Override
            public void onError(Request request, IOException e) {
                Logger.d("唉唉唉");
            }
        }, file, "file");
    }

    private void saveFile(Bitmap bitmap){
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(imageFile);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
