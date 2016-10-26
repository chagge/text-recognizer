package uestc.xfj.recognizer.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.byhieglibrary.Activity.BaseActivity;
import com.example.byhieglibrary.Net.HttpUtils;
import com.example.byhieglibrary.Net.ResultCallback;
import com.hanks.htextview.HTextView;
import com.orhanobut.logger.Logger;
import com.roger.catloadinglibrary.CatLoadingView;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import info.hoang8f.widget.FButton;
import okhttp3.Request;
import uestc.xfj.recognizer.MyApp;
import uestc.xfj.recognizer.R;
import uestc.xfj.recognizer.alg.ImageUtils;
import uestc.xfj.recognizer.bean.Recognizer;

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
    @Bind(R.id.denoising_button)
    public FButton denoisingButton;

    private File imageFile = null;

    public CatLoadingView catLoadingView;
    private int count = 1;
    private String path;
    private String currentPath;
    private ImageUtils imageUtils;
    private Bitmap grayBitmap;
    private Bitmap twoBitmap;
    private Bitmap nosingBitmap;
    private Handler handler;
    private Bitmap bitmap;
    private ViewGroup layout;
    private String url;
    private File[] postFile = new File[4];
    private int choose;


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
        imageUtils = new ImageUtils();
        handler = new Handler();
        currentPath = path;
        layout = (ViewGroup)findViewById(R.id.activity_preview);
        url =  "http://junwork.cn/recognizer/recog";
        postFile[0] = imageFile;
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
                File temp = postFile[0];
                if (nosingBitmap == null) {
                    Snackbar.make(layout,"你还没有去噪",Snackbar.LENGTH_LONG).setAction("点我去噪", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            cancelNosing();
                        }
                    }).show();
                    return;
                }
//                showPopupWindow();
//                switch (choose) {
//                    case 0:
//                        temp = postFile[0];
//                        break;
//                    case 1:
//                        temp = postFile[1];
//                        break;
//                    case 2:
//                        temp = postFile[2];
//                        break;
//                    case 3:
//                        temp = postFile[3];
//                        break;
//                    default:
//                        showToast("请选择上传的图片");
//                        return;
//                }
                String fileName = temp.getName();
                String prefix= fileName.substring(fileName.lastIndexOf(".")+1);
                catLoadingView.show(getSupportFragmentManager(), "");
                Map<String, String> params = new HashMap<>();
                params.put("format",prefix);
                params.put("lang", "chi_sim");
                url = HttpUtils.url(url, null,params);
                HttpUtils.postFile(url, new ResultCallback<Recognizer>() {
                    @Override
                    public void onResponse(Recognizer response) {
                        Logger.d(response);
                        Bundle bundle = new Bundle();
                        bundle.putString("result",response.getOutput());
                        Intent intent = new Intent();
                        intent.putExtra("data", bundle);
                        startActivity(ResultActivity.class, bundle);
                        finish();
                        catLoadingView.dismiss();
                    }

                    @Override
                    public void onError(Request request, IOException e) {
                        Logger.d("唉唉唉");
                    }
                },temp,"file");
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
                if (currentPath != null) {
                  bitmap = BitmapFactory.decodeFile(currentPath);
                }else if(grayBitmap != null){
                    bitmap = grayBitmap;
                }else if(twoBitmap != null){
                    bitmap = twoBitmap;
                } else if (nosingBitmap != null) {
                    bitmap = nosingBitmap;
                } else {
                    showToast("当前没有图片");
                }
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
                doGray();
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doTwoValue();
            }
        });

        denoisingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelNosing();
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

    private void doGray(){
        if (path == null && grayBitmap == null) {
            Snackbar.make(layout,"当前图片为空",Snackbar.LENGTH_LONG).setAction("回到上页选择图片", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(MainActivity.class);
                    finish();
                }
            }).show();
            return;
        } else if (path == null && grayBitmap != null) {
            showToast("已经灰度化了，请进行下一步");
            return;
        }
        catLoadingView.show(getSupportFragmentManager(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                grayBitmap = imageUtils.lineGrey(bitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        image.setImageBitmap(grayBitmap);
                        try {
                            postFile[1] = generatePostFile(grayBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        currentPath = null;
                        twoBitmap = null;
                        nosingBitmap = null;
                        catLoadingView.dismiss();
                    }
                });
            }
        }).start();

    }

    private void doTwoValue(){
        if (grayBitmap == null && twoBitmap == null) {
            Snackbar.make(layout,"你还没有灰度化",Snackbar.LENGTH_LONG).setAction("点我进行灰度化", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doGray();
                }
            }).show();
            return;
        }else if(grayBitmap == null && twoBitmap != null){
            showToast("你已经二值化了，请进行下一步");
            return;
        }
        catLoadingView.show(getSupportFragmentManager(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                twoBitmap = imageUtils.gray2Binary(grayBitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        grayBitmap = null;
                        currentPath = null;
                        nosingBitmap = null;
                        image.setImageBitmap(twoBitmap);
                        try {
                            postFile[2] = generatePostFile(twoBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        catLoadingView.dismiss();
                    }
                });
            }
        }).start();

    }

    private void cancelNosing(){
        if (twoBitmap == null && nosingBitmap == null) {
            Snackbar.make(layout,"你还没有二值化",Snackbar.LENGTH_LONG).setAction("点我进行二值化", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doTwoValue();
                }
            }).show();
            return;
        }else if(twoBitmap == null && nosingBitmap != null){
            showToast("已经去噪了,请进行下一步");
            return;
        }
        catLoadingView.show(getSupportFragmentManager(), "");
        new Thread(new Runnable() {
            @Override
            public void run() {
                nosingBitmap = imageUtils.medianFiltering(twoBitmap);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        grayBitmap = null;
                        currentPath = null;
                        twoBitmap = null;
                        image.setImageBitmap(nosingBitmap);
                        try {
                            postFile[3] = generatePostFile(nosingBitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        catLoadingView.dismiss();
                    }
                });
            }
        }).start();
    }

    private File generatePostFile(Bitmap bitmap) throws IOException {
        File dir = MyApp.getAppContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if(dir.exists()){
            dir.mkdirs();
        }
        File tempFile = null;
        tempFile = new File(dir, System.currentTimeMillis() + ".jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tempFile));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        return tempFile;
    }


    private void showPopupWindow() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_popup, null);
        HTextView origin = (HTextView) contentView.findViewById(R.id.origin);
        HTextView gray = (HTextView) contentView.findViewById(R.id.gray);
        HTextView two = (HTextView) contentView.findViewById(R.id.two);
        HTextView noise = (HTextView) contentView.findViewById(R.id.noise);
        final PopupWindow popupWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
                true);

        popupWindow.setTouchable(true);
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.transparent));
        popupWindow.showAtLocation(image, Gravity.CENTER, 0, 0);
        origin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose = 0;
                popupWindow.dismiss();
            }
        });
        gray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose = 1;
                popupWindow.dismiss();

            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose = 2;
                popupWindow.dismiss();
            }
        });
        noise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose = 3;
                popupWindow.dismiss();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (catLoadingView != null) {
            catLoadingView.dismiss();
        }
    }
}
