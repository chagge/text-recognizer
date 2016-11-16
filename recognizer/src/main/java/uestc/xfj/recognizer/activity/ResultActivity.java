package uestc.xfj.recognizer.activity;

import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.byhieglibrary.Activity.BaseActivity;

import butterknife.Bind;
import uestc.xfj.recognizer.R;


public class ResultActivity extends BaseActivity {


    @Bind(R.id.back)
    public ImageView back;
    @Bind(R.id.recognizer_result)
    public TextView mTextView;


    private String result;

    @Override
    public int getLayoutId() {
        return R.layout.activity_result;
    }

    @Override
    public void initData() {
        result = getIntent().getExtras().getString("result");
        String temp;
        temp = result.replaceAll("\n|\r","");
        temp = temp.trim();
        if("".equals(temp)){
            result = "图片资源有问题，暂无文字在上面";
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
    }

    @Override
    public void initView() {
        mTextView.setText(result);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    @Override
    public void initTheme() {

    }
}
