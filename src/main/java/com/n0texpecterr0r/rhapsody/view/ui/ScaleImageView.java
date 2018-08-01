package com.n0texpecterr0r.rhapsody.view.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.R;

/**
 * @author Created by Nullptr
 * @date 2018/7/30 20:32
 * @describe 可缩放和自由移动的ImageView
 */
@SuppressLint("AppCompatCustomView")
public class ScaleImageView extends ImageView implements OnGlobalLayoutListener,
        OnScaleGestureListener, OnTouchListener {

    private boolean isScaling;          // 是否正在放大
    private int mTouchSlop = 0;         // 最小滑动距离
    private boolean mHasInitialed;      // 是否已经初始化过
    private float mOriginScale;         // 原始缩放比例
    private float mMidScaleValue;       // 双击缩放相对于原始的比例
    private float mMidScale;            // 双击缩放比例
    private float mMaxScaleValue;       // 最大缩放相对于原始的比例
    private float mMaxScale;            // 最大缩放比例
    private ScaleGestureDetector mScaleGestureDetector; // 监听缩放
    private GestureDetector mGestureDetector;           // 监听双击
    private Matrix mMatrix;             // 图像变换矩阵
    private int mLastPointerCount;      // 上一次的手势个数
    private float mLastX;
    private float mLastPointerX;        // 上一次手势中心点X
    private float mLastPointerY;        // 上一次手势中心点Y
    private boolean canMove;            // 能否移动

    public ScaleImageView(Context context) {
        super(context);
        init(context, null);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScaleImageView);
        mMaxScaleValue = typedArray.getFloat(R.styleable.ScaleImageView_maxScale, 4);
        mMidScaleValue = typedArray.getFloat(R.styleable.ScaleImageView_midScale, 2);
        typedArray.recycle();   // 回收

        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        setOnTouchListener(this);

        // 系统触发的最小滑动距离
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        // 设置双击的放大缩小事件
        mGestureDetector = new GestureDetector(context, new SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent event) {
                if (isScaling) {
                    return true;
                }
                //以双击点为缩放中心
                float x = event.getX();
                float y = event.getY();

                if (getScaleValue() < mMidScale) {
                    // 需要放大
                    postDelayed(new AutoScaleTask(mMidScale, x, y), 4);
                    isScaling = true;
                } else {
                    // 需要缩小
                    postDelayed(new AutoScaleTask(mOriginScale, x, y), 4);
                    isScaling = true;
                }
                return true;
            }
        });
    }

    /**
     * 自动放大缩小
     */
    private class AutoScaleTask implements Runnable {

        // 缩放的目标值
        private float mTargetScale;
        // 缩放的中心点
        private float x;
        private float y;

        // 放大与缩小的变化值
        private final float deltaMagnfy = 1.1F;
        private final float deltaMinfy = 0.9F;

        // 此次的缩放值
        private float currentScale;

        public AutoScaleTask(float mTargetScale, float x, float y) {
            this.mTargetScale = mTargetScale;
            this.x = x;
            this.y = y;
            if (getScaleValue() < mTargetScale) {
                // 比目标值大,放大
                currentScale = deltaMagnfy;
            }
            if (getScaleValue() > mTargetScale) {
                // 比目标值小，缩小
                currentScale = deltaMinfy;
            }
        }

        @Override
        public void run() {
            // 进行缩放
            mMatrix.postScale(currentScale, currentScale, x, y);
            checkBorder();
            setImageMatrix(mMatrix);

            float currentScale = getScaleValue();
            if ((this.currentScale > 1.0f && currentScale < mTargetScale)
                    || (this.currentScale < 1.0f && currentScale > mTargetScale)) {
                // 仍未到达目标大小
                // 继续缩放
                postDelayed(this, 4);
            } else {
                // 到达目标值
                isScaling = false;
                // 由于可能会超过目标值，再计算一次
                float scale = mTargetScale / currentScale;
                mMatrix.postScale(scale, scale, x, y);
                checkBorder();
                setImageMatrix(mMatrix);
            }
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mScaleGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 注册onGlobalLayoutListener
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 移除onGlobalLayoutListener
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    /**
     * 用于捕获图片加载完成事件
     */
    @Override
    public void onGlobalLayout() {
        // 为保证对缩放仅仅进行一次，初始化操作仅进行一次
        if (!mHasInitialed) {
            // 得到屏幕宽高
            int width = getWidth();
            int height = getHeight();

            // 获取图片的宽和高
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();

            float scale = 1.0F; // 缩放比例

            // 如果图片比控件宽度小，高度大
            if (width >= drawableWidth && height <= drawableHeight) {
                scale = height * 1.0F / drawableHeight;    // 计算缩放比例
            }
            // 如果图片比控件高度小，宽度大
            if (width <= drawableWidth && height >= drawableHeight) {
                scale = width * 1.0F / drawableWidth;      // 计算缩放比例
            }

            // 计算缩放比例
            if ((width <= drawableWidth && height <= drawableHeight) ||
                    (width >= drawableWidth && height >= drawableHeight)) {
                scale = Math.min(width * 1.0F / drawableWidth, height * 1.0F / drawableHeight);
            }

            // 设置限定缩放比例
            mOriginScale = scale;                  // 初始比例
            mMidScale = scale * mMidScaleValue;    // 双击比例
            mMaxScale = scale * mMaxScaleValue;    // 最大比例

            // 图片移动到控件中心
            int deltaX = width / 2 - drawableWidth / 2;    // 计算需要移动的宽度
            int deltaY = height / 2 - drawableHeight / 2;  // 计算需要移动的高度

            // 移动图像
            mMatrix.postTranslate(deltaX, deltaY);
            // 以控件中心为中心缩放
            mMatrix.postScale(mOriginScale, mOriginScale, width / 2, height / 2);
            // 应用矩阵
            setImageMatrix(mMatrix);

            mHasInitialed = true;
        }
    }

    /**
     * 获取图片当前的缩放比例
     *
     * @return 图片的缩放比例
     */
    public float getScaleValue() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // 获取当前缩放比例
        float scale = getScaleValue();
        // 捕获多点触控，计算缩放比例
        float scaleFactor = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }
        // 控制缩放最大最小值
        if ((scale < mMaxScale && scaleFactor > 1.0f) || (scale > mOriginScale && scaleFactor < 1.0f)) {

            if (scale * scaleFactor > mMaxScale) {
                scaleFactor = mMaxScale / scale;
            }
            if (scale * scaleFactor < mOriginScale) {
                scaleFactor = mOriginScale / scale;
            }
            mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            //不断检测 控制白边和中心位置
            checkBorder();
            setImageMatrix(mMatrix);
        }
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        isScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        isScaling = false;
    }

    /**
     * 获得图片放大或缩小之后的宽和高 以及 left top right bottom的坐标点
     *
     * @return 包含这些信息的RectF
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mMatrix;
        RectF rect = new RectF();
        Drawable drawable = getDrawable();
        if (null != drawable) {
            rect.set(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * 进行边界检测，避免移动或缩放时超过边界
     */
    private void checkBorder() {
        RectF rect = getMatrixRectF();
        float delatX = 0;
        float delatY = 0;
        // 控件的宽和高
        int width = getWidth();
        int height = getHeight();

        // 如果图片是放大状态
        if (rect.width() >= width) {
            // 检查左右是否超过边界
            if (rect.left > 0) {
                delatX = -rect.left;
            }
            if (rect.right < width) {
                delatX = width - rect.right;
            }
        }
        // 如果图片是放大状态
        if (rect.height() >= height) {
            // 检查上下是否超过边界
            if (rect.top > 0) {
                delatY = -rect.top;
            }
            if (rect.bottom < height) {
                delatY = height - rect.bottom;
            }
        }

        // 如果图片的宽和高小于控件的宽和高 让其居中
        if (rect.width() < width) {
            delatX = width / 2 - rect.right + rect.width() / 2f;
        }
        if (rect.height() < height) {
            delatY = height / 2 - rect.bottom + rect.height() / 2f;
        }
        mMatrix.postTranslate(delatX, delatY);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        RectF rectF = getMatrixRectF();  // 用于获取当前图片大小，计算是否要父容器拦截
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mLastX;
                if (rectF.width() > getWidth() + 1 || rectF.height() > getHeight() + 1) {
                    //// 放大情况下
                    //if (rectF.left == 0 && deltaX > 0) {
                    //    getParent().requestDisallowInterceptTouchEvent(false);
                    //} else if (rectF.right == getWidth() && deltaX < 0) {
                    //    getParent().requestDisallowInterceptTouchEvent(false);
                    //}
                    getParent().requestDisallowInterceptTouchEvent(true);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        mLastX = x;
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 双击放大与缩小事件传递给GestureDetector，防止双击时发生移动的事件响应
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        // 将手势传递给ScaleGestureDetector
        mScaleGestureDetector.onTouchEvent(event);

        // 上面的都不接收，则为滑动事件
        float pointerX = 0;
        float pointerY = 0;
        // 计算触控中心点的坐标
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            pointerX += event.getX(i);
            pointerY += event.getY(i);
        }
        pointerX /= pointerCount;
        pointerY /= pointerCount;

        if (mLastPointerCount != pointerCount) {
            // 手指发生改变时,重新判断是否能够移动
            canMove = false;
            mLastPointerX = pointerX;
            mLastPointerY = pointerY;
        }
        mLastPointerCount = pointerCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                // 手指离开，lastCount置为0
                mLastPointerCount = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                // 计算移动距离
                float deltaX = pointerX - mLastPointerX;
                float deltaY = pointerY - mLastPointerY;

                if (!canMove) {
                    // 如果之前不能移动，判断当前是否能移动
                    canMove = Math.pow(deltaX, 2) + Math.pow(deltaY, 2) > Math.pow(mTouchSlop, 2);
                }
                if (canMove) {
                    RectF rect = getMatrixRectF();  // 获取图片缩放后信息
                    if (getDrawable() != null) {
                        if (rect.width() < getWidth()) {
                            // 宽度小于控件宽度，禁止左右移动
                            deltaX = 0;
                        }
                        if (rect.height() < getHeight()) {
                            // 宽度小于控件高度，禁止上下移动
                            deltaY = 0;
                        }
                        mMatrix.postTranslate(deltaX, deltaY);
                        // 检查是否越界
                        checkBorder();
                        setImageMatrix(mMatrix);
                    }
                }
                mLastPointerX = pointerX;
                mLastPointerY = pointerY;
                break;
        }
        return true;
    }
}
