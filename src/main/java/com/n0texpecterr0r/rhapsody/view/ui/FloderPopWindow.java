package com.n0texpecterr0r.rhapsody.view.ui;

import static android.graphics.Color.WHITE;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import com.n0texpecterr0r.rhapsody.R;

import com.n0texpecterr0r.rhapsody.adapter.base.BaseAdapter.OnItemClickListener;
import com.n0texpecterr0r.rhapsody.adapter.FloderAdapter;
import com.n0texpecterr0r.rhapsody.bean.Floder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Nullptr
 * @date 2018/7/27 19:43
 * @describe 文件夹列表弹出菜单
 */
public class FloderPopWindow extends PopupWindow {

    private int popupWidth;     // 宽度
    private int popupHeight;    // 高度
    private FloderAdapter mAdapter;     // 文件夹列表Adapter

    public FloderPopWindow(Activity context) {

        // 获取FloderPopWindow的View
        // 展示的view
        View contentView = LayoutInflater.from(context).inflate(R.layout.pop_floder_list, null);
        Point size = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(size);
        // 获取屏幕宽高
        popupWidth = size.x;
        popupHeight = size.y / 2 + 300;
        // 设置FloderPopWindow的View
        this.setContentView(contentView);
        // 设置FloderPopWindow弹出窗体的宽
        this.setWidth(popupWidth);
        // 设置FloderPopWindow弹出窗体的高
        this.setHeight(popupHeight);
        // 设置FloderPopWindow弹出窗体可以点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 设置全白背景
        this.setBackgroundDrawable(new ColorDrawable(WHITE));
        // 刷新
        this.update();
        // 设置弹出动画
        this.setAnimationStyle(R.style.pop_anim_style);

        // 文件夹列表
        RecyclerView rvList = contentView.findViewById(R.id.pop_rv_floder);
        rvList.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new FloderAdapter(new ArrayList<Floder>(),R.layout.item_floder);
        rvList.setAdapter(mAdapter);
    }

    /**
     * 更改文件夹列表
     * @param floderList 文件夹列表
     */
    public void setDatas(List<Floder> floderList){
        mAdapter.setDatas(floderList);
    }

    /**
     * 将popupWindow显示在view上方(以view的左边距为开始位置)
     * @param view 要显示在上面的控件
     */
    public void showUp(View view) {
        // 获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        // 在控件上方显示
        showAtLocation(view, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }

}
