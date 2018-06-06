package com.example.asus.weather.unit;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.asus.weather.R;

/**
 * 自定义弹出弹窗
 * Created by ASUS on 2018/6/2.
 */

public class MyPopupWindow extends PopupWindow {

    private View mView;
    TextView textViewLocation;
    TextView textViewShared;

    public MyPopupWindow(Context context, int width, int height, View.OnClickListener onClick) {
        super(context);

        mView = LayoutInflater.from(context).inflate(R.layout.popup_window, null);
        textViewLocation = (TextView) mView.findViewById(R.id.text_location);
        textViewShared = (TextView) mView.findViewById(R.id.text_shared);

        textViewLocation.setOnClickListener(onClick);
        textViewShared.setOnClickListener(onClick);

        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(width);
        //设置PopupWindow弹出窗体的高
        this.setHeight(height);
        // 设置外部可点击
        this.setOutsideTouchable(true);
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.PopupWindow);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xfff);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

    }

    /**
     * 让弹出窗显示在某控件上面
     * @param v 某控件
     */
    public void showOnView(View v){
        //PopupWindows获取自身的长宽高
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int popupWidth = mView.getMeasuredWidth();
        int popupHeight = mView.getMeasuredHeight();
        int[] location = new int[2];
        v.getLocationOnScreen(location);//获取控件在屏幕上的位置
        this.showAtLocation(v, Gravity.NO_GRAVITY, 0, location[1] - popupHeight);
    }

}
