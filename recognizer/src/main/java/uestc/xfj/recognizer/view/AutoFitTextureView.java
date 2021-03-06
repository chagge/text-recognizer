package uestc.xfj.recognizer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * Created by byhieg on 16-10-15.
 * Mail byhieg@gmail.com
 */

public class AutoFitTextureView extends TextureView{

    private int mRatioWidth = 0;
    private int mRatioHeight = 0;


    public AutoFitTextureView(Context context) {
        super(context);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setAspectRatio(int width, int height) {
        mRatioHeight = height;
        mRatioWidth = width;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (0 == mRatioHeight || 0 == mRatioWidth) {
            setMeasuredDimension(width,height);
        }else{
            if (width < height * mRatioWidth / mRatioHeight) {
                setMeasuredDimension(width,width * mRatioHeight / mRatioWidth);
            }else{
                setMeasuredDimension(height * mRatioWidth / mRatioHeight,height);
            }
        }
    }
}
