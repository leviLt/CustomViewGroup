package com.android.viewgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.support.v4.content.ContextCompat.getColor;

/**
 * Created by luotao
 * 2018/7/6
 * emil:luotaosc@foxmail.com
 * qq:751423471
 *
 * @author 罗涛
 */
public class CustomViewGroup extends ViewGroup {
    private static final String TAG = "CustomViewGroup";
    //城市名字数组
    private String[] arrays;

    public CustomViewGroup(Context context) {
        super(context);
        initView(context, null);
    }


    public CustomViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public CustomViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    /**
     * 第一步获取城市列表，并添加View到ViewGroup
     *
     * @param context
     * @param attrs
     */
    @SuppressLint("ResourceType")
    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null && context != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomViewGroup);
            //获取自定义的属性城市名字
            String[] stringArray = getResources().getStringArray(R.array.name);
            //            arrays = new String[stringArray.length];
            //            for (int i = 0; i < stringArray.length; i++) {
            //                int resourceId = typedArray.getResourceId(i, 0);
            //                arrays[i] = getResources().getString(resourceId);
            //            }
            if (arrays == null) {
                arrays = context.getResources().getStringArray(R.array.name);
            }
            //取出城市的名字，并添加到TextView中
            for (String array : arrays) {
                TextView tvName = new TextView(context);
                tvName.setPadding(8, 4, 8, 4);
                tvName.setText(array);
                tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                tvName.setTextColor(getColor(context, R.color.colorAccent));
                tvName.setBackgroundResource(R.color.colorPrimary);
                //添加View到ViewGroup中
                addView(tvName);
            }
            //释放掉资源
            typedArray.recycle();
        }

    }

    /**
     * 确定我们的子View的摆放位置
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childTop = 0;
        int childLeft = 0;
        int childRight = 0;
        int childBottom = 0;

        //记录已使用的宽高
        int usedWidth = 0;
        //ViewGroup的宽度
        int layoutWidth = getMeasuredWidth();
        Log.e(TAG, "layoutWidth: " + layoutWidth);
        Log.e(TAG, "getChildCount: "+getChildCount());
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            if (layoutWidth - usedWidth < childWidth) {
                childLeft = 0;
                usedWidth = 0;
                childTop += childHeight;
                childRight = childWidth;
                childBottom = childTop + childHeight;
                childView.layout(childLeft, childTop, childRight, childBottom);
                usedWidth += childWidth;
                childLeft = childWidth;
                continue;
            }
            childRight = childLeft + childWidth;
            childBottom = childTop + childHeight;
            childView.layout(childLeft, childTop, childRight, childBottom);
            childLeft = childLeft + childWidth;
            usedWidth += childWidth;
        }
    }

    /**
     * 第二步获取到的子View的尺寸，确定ViewGroup的宽度和高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        //已使用的宽度
        int usedWidth = 0;
        //剩余可用宽度
        int remaining = 0;
        //总高度
        int totalHeight = 0;
        //当前行高
        int lineHeight = 0;

        for (int i = 0; i < getChildCount(); i++) {
            TextView childView = (TextView) getChildAt(i);
            //量测子View的尺寸
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
            //计算当前的剩余宽度
            remaining = wSize - usedWidth;
            //子View宽度大于剩余宽度
            if (childView.getMeasuredWidth() > remaining) {
                //换一行，那么已使用的宽度为0
                usedWidth = 0;

                //高度也相应的增加一行
                totalHeight = totalHeight + lineHeight;
            }
            //累加已使用的宽度
            usedWidth = usedWidth + childView.getMeasuredWidth();
            //当前View的行高
            lineHeight = childView.getMeasuredHeight();
        }
        if (hMode == MeasureSpec.AT_MOST) {
            hSize = totalHeight;
        }
        Log.e(TAG, "wSize=="+wSize);
        Log.e(TAG, "hSize=="+hSize);
        Log.e(TAG, "totalHeight=="+totalHeight);
        //设置保存的高度
        setMeasuredDimension(wSize, hSize);
    }
}
