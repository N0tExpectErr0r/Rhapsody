package com.n0texpecterr0r.rhapsody.view.ui;

import android.animation.ValueAnimator;
import android.graphics.Matrix;

/**
 * @author Created by Nullptr
 * @date 2018/8/4 19:50
 * @describe 缩放动画
 */
class ScaleAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

    // 初始矩阵
    private float[] mSrcMatrix = new float[9];
    // 目标矩阵
    private float[] mDstMatrix = new float[9];


    public ScaleAnimator(Matrix srcMatrix, Matrix dstMatrix) {
        super();
        // 设置数值更新事件
        addUpdateListener(this);
        setFloatValues(0, 1.0F);
        setDuration(200);
        // 起始矩阵值数组
        srcMatrix.getValues(mSrcMatrix);
        // 目标矩阵值数组
        dstMatrix.getValues(mDstMatrix);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        // 当前动画进度
        float currentValue = (float) animation.getAnimatedValue();
            
    }
}
