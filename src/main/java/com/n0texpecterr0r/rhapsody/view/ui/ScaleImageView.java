package com.n0texpecterr0r.rhapsody.view.ui;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_DOWN;
import static android.view.MotionEvent.ACTION_POINTER_UP;
import static android.view.MotionEvent.ACTION_UP;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * @author Created by Nullptr
 * @date 2018/8/4 19:12
 * @describe 可缩放的ImageView
 */
@SuppressLint("AppCompatCustomView")
public class ScaleImageView extends ImageView {

    // 缩放模式
    private static final int MODE_SCALE = 0x564897;
    // 平移模式
    private static final int MODE_FLING = 0x8845485;
    // 静止模式
    private static final int MODE_FREE = 0x545764;
    // 外部矩阵，用于表示手势缩放效果后的矩阵
    private Matrix mOuterMatrix = new Matrix();
    // 最大缩放比
    private float mMaxScaleValue = 4F;
    // 双击缩放比
    private float mMidScaleValue = 2F;
    // 上一次移动的点(双指下是中点)
    private PointF mLastMovePoint = new PointF();
    // 缩放中心点
    private PointF mScaleCenter = new PointF();
    // 缩放动画
    private ScaleAnimator mScaleAnimator;
    // 滑动惯性动画
    private FlingAnimator mFlingAnimator;
    // 初始缩放比例，乘上两指距离即为要缩放的比例
    private float mScaleBaseValue = 0;
    // 当前模式，缩放/移动/静止
    private int mCurrentMode = MODE_FREE;

    public ScaleImageView(Context context) {
        super(context);
        initView();
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        super.setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        // 禁止外部改变ScaleType
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isReady()) {
            Matrix matrix = new Matrix();
            setImageMatrix(getCurrentMatrix());
        }
        super.onDraw(canvas);
    }

    /**
     * 获取当前矩阵 当前矩阵为内部矩阵组合上外部矩阵
     *
     * 内部矩阵：计算图片填满屏幕的状态的矩阵 外部矩阵：计算图片手势变换后效果的矩阵
     */
    private Matrix getCurrentMatrix() {
        // 计算内部矩阵
        Matrix matrix = getInnerMatrix();
        // 将内部矩阵组合外部矩阵即可得到当前矩阵
        matrix.postConcat(mOuterMatrix);
        return matrix;
    }

    /**
     * 获取内部矩阵
     *
     * @return 内部矩阵
     */
    private Matrix getInnerMatrix() {
        Matrix matrix = new Matrix();
        if (isReady()) {
            // 原图尺寸
            RectF drawableRect = new RectF(0, 0,
                    getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            // 控件尺寸
            RectF viewRect = new RectF(0, 0,
                    getWidth(), getHeight());
            // 计算图片缩放到控件大小后的矩阵
            matrix.setRectToRect(drawableRect, viewRect, ScaleToFit.CENTER);
        }
        return matrix;
    }

    /**
     * 获取带有缩放后尺寸信息的RectF
     *
     * @return 缩放后的尺寸信息
     */
    private RectF getCurrentRect() {
        RectF rectF = new RectF();
        if (isReady()) {
            // 获取当前缩放后的matrix
            Matrix matrix = getCurrentMatrix();
            // 应用于RectF后，得到当前的矩阵
            rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
            matrix.mapRect(rectF);
            return rectF;
        } else {
            return rectF;
        }
    }

    /**
     * 判断当前情况是否能执行手势相关计算
     */
    private boolean isReady() {
        return getDrawable() != null                        // 图片不为空
                && getDrawable().getIntrinsicWidth() > 0     // 图片可以获取宽度
                && getDrawable().getIntrinsicHeight() > 0    // 图片可以获取高度
                && getWidth() > 0                            // 控件有宽度
                && getHeight() > 0;                          // 控件有高度
    }

    private GestureDetector mGestureDetector = new GestureDetector(ScaleImageView.this.getContext(),
            new GestureDetector.SimpleOnGestureListener() {

                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    //只有在单指模式结束之后才允许执行fling
                    if (mCurrentMode == MODE_FREE && !(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                        fling(velocityX, velocityY);
                    }
                    return true;
                }

                public void onLongPress(MotionEvent e) {
                }

                public boolean onDoubleTap(MotionEvent e) {
                    // 如果是滑动模式且没有在放大才能双击放大
                    if (mCurrentMode == MODE_FLING && !(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                        doubleTap(new PointF(e.getX(), e.getY()));
                    }
                    return true;
                }

                public boolean onSingleTapConfirmed(MotionEvent e) {
                    return true;
                }
            });

    /**
     * 双击的处理
     *
     * @param tapPoint 双击的点
     */
    private void doubleTap(PointF tapPoint) {
        if (!isReady()) {
            return;
        }
        // 获取内部矩阵
        Matrix innerMatrix = getInnerMatrix();
        // 获取当前缩放比例
        float innerScale = caculateMatrixScale(innerMatrix);
        float outerScale = caculateMatrixScale(mOuterMatrix);
        // 当前缩放比例
        float currentScale = innerScale * outerScale;
        // 控件的大小
        float width = getWidth();
        float height = getHeight();
        float nextScale = caculateNextScale(innerScale, outerScale);
        //如果接下来放大大于最大值或者小于fit center值，则取边界
        if (nextScale > mMidScaleValue) {
            nextScale = mMidScaleValue;
        }
        if (nextScale < innerScale) {
            nextScale = innerScale;
        }
        // 计算缩放后矩阵
        Matrix scaleMatrix = new Matrix(mOuterMatrix);
        // 缩放
        scaleMatrix.postScale(nextScale / currentScale, nextScale / currentScale, tapPoint.x, tapPoint.y);
        // 将图片平移到中心
        scaleMatrix.postTranslate(width / 2F - tapPoint.x, height / 2F - tapPoint.y);
        // 结合缩放
        Matrix finalMatrix = new Matrix(innerMatrix);
        finalMatrix.postConcat(scaleMatrix);
        // 获取边界
        RectF bound = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        finalMatrix.mapRect(bound);
        // 修正位置
        float postX = 0;
        float postY = 0;
        if (bound.right - bound.left < width) {
            postX = width / 2f - (bound.right + bound.left) / 2f;
        } else if (bound.left > 0) {
            postX = -bound.left;
        } else if (bound.right < width) {
            postX = width - bound.right;
        }
        if (bound.bottom - bound.top < height) {
            postY = height / 2f - (bound.bottom + bound.top) / 2f;
        } else if (bound.top > 0) {
            postY = -bound.top;
        } else if (bound.bottom < height) {
            postY = height - bound.bottom;
        }
        // 修正
        scaleMatrix.postTranslate(postX, postY);
        // 结束动画
        cancelAllAnimator();
        // 开启动画
        mScaleAnimator = new ScaleAnimator(mOuterMatrix, scaleMatrix);
        mScaleAnimator.start();
    }

    /**
     * 下一个缩放比例
     *
     * @param innerScale 内部缩放率
     * @param outerScale 外部缩放率
     * @return 下一个缩放率
     */
    private float caculateNextScale(float innerScale, float outerScale) {
        float tempScale = innerScale * outerScale;
        if (tempScale < mMidScaleValue) {
            // 如果是需要放大，返回缩放后大小
            return mMidScaleValue;
        } else {
            // 如果是需要缩小,返回初始大小
            return innerScale;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        // 处理多点触控事件
        int action = event.getAction() & MotionEvent.ACTION_MASK;

        if (action == ACTION_UP || action == ACTION_CANCEL) {
            // 抬起最后一个手指时
            if (mCurrentMode == MODE_SCALE) {
                // 如果当前是缩放模式，停止缩放，并处理回弹等事件
                endScale();
            }
            // 切换回静止模式
            mCurrentMode = MODE_FREE;
        } else if (action == ACTION_POINTER_UP) {
            if (event.getPointerCount() > 2) {
                // 如果还有两个以上的手指
                if (event.getAction() >> 8 == 0) {
                    // 如果抬起的是第1个手指，保存 2 3 两个手指的状态
                    saveScaleStatus(new PointF(event.getX(1), event.getY(1)),
                            new PointF(event.getX(2), event.getY(2)));
                } else if (event.getAction() >> 8 == 1) {
                    // 如果抬起的是第2个手指，保存 1 3 两个手指的状态
                    saveScaleStatus(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(2), event.getY(2)));
                }
            }
        } else if (action == ACTION_DOWN) {
            // 按下第一个点，开启滑动模式
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                // 如果没有在进行放大动画
                // 取消所有动画
                cancelAllAnimator();
                // 切换滑动模式
                mCurrentMode = MODE_FLING;
                // 保存当前点
                mLastMovePoint.set(event.getX(), event.getY());
            }
        } else if (action == ACTION_POINTER_DOWN) {
            // 并非是按下第一个点，则取消滑动模式、
            // 停止所有动画
            cancelAllAnimator();
            // 切换缩放模式
            mCurrentMode = MODE_SCALE;
            // 保存缩放的 1 2 手指下的状态
            saveScaleStatus(new PointF(event.getX(0), event.getY(0)),
                    new PointF(event.getX(1), event.getY(1)));
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (!(mScaleAnimator != null && mScaleAnimator.isRunning())) {
                if (mCurrentMode == MODE_FLING) {
                    // 手指在滑动模式下移动
                    // 滑动对应距离
                    scrollBy(event.getX() - mLastMovePoint.x, event.getY() - mLastMovePoint.y);
                    // 记录新的点
                    mLastMovePoint.set(event.getX(), event.getY());
                } else if (mCurrentMode == MODE_SCALE && event.getPointerCount() > 1) {
                    // 在缩放模式并且手指不低于2
                    // 计算缩放点距离
                    float distance = caculateDistance(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));
                    // 计算缩放中心点
                    PointF centerPoint = caculateCenterPoint(new PointF(event.getX(0), event.getY(0)),
                            new PointF(event.getX(1), event.getY(1)));
                    mLastMovePoint = centerPoint;
                    // 执行缩放
                    doScale(mScaleCenter, mScaleBaseValue, distance, mLastMovePoint);
                }
            }
        }
        // 处理外部手势
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    /**
     * 结束缩放，处理回弹及边界处理
     */
    private void endScale() {
        if (!isReady()) {
            return;
        }
        // 是否改变位置
        boolean isChanged = false;
        // 获取当前变换矩阵
        Matrix matrix = getCurrentMatrix();
        // 计算当前缩放比例
        float currentScale = caculateMatrixScale(matrix);
        // 计算外部矩阵缩放比例
        float outerScale = caculateMatrixScale(mOuterMatrix);
        // 控件的大小
        float width = getWidth();
        float height = getHeight();
        // 最大缩放比例
        float maxScale = mMaxScaleValue;
        // 修正比例
        float postScale = 1F;
        // 修正位置
        float postX = 0F;
        float postY = 0F;
        // 如果比例大于最大比例，缩放修正
        if (currentScale > maxScale) {
            postScale = maxScale / currentScale;
        }
        // 如果修正导致了图片未填满，重新修正
        if (outerScale * postScale < 1.0F) {
            postScale = 1.0F / outerScale;
        }
        // 缩放修正不为1，则进行了修正
        if (postScale != 1.0F) {
            isChanged = true;
        }
        // 进行缩放修正
        Matrix postMatrix = new Matrix(matrix);
        // 以移动中心缩放
        postMatrix.postScale(postScale, postScale, mLastMovePoint.x, mLastMovePoint.y);
        // 获取缩放后rect
        RectF rect = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        postMatrix.mapRect(rect);
        // 如果出界，进行位置修正
        if (rect.right - rect.left < width) {
            postX = width / 2.0F - (rect.right + rect.left) / 2.0F;
        } else if (rect.left > 0) {
            postX = -rect.left;
        } else if (rect.right < width) {
            postX = width - rect.right;
        }
        if (rect.bottom - rect.top < height) {
            postY = height / 2.0F - (rect.bottom + rect.top) / 2.0F;
        } else if (rect.top > 0) {
            postY = -rect.top;
        } else if (rect.bottom < height) {
            postY = height - rect.bottom;
        }
        // 如果位置修正不为0，说明进行了修正
        if (postX != 0 || postY != 0) {
            isChanged = true;
        }
        if (isChanged) {
            // 改变了，则执行修正动画
            // 计算结束后的矩阵
            Matrix finalMatrix = new Matrix(mOuterMatrix);
            finalMatrix.postScale(postScale, postScale, mLastMovePoint.x, mLastMovePoint.y);
            finalMatrix.postTranslate(postX, postY);
            // 结束当前正在执行的动画
            cancelAllAnimator();
            // 执行矩阵动画
            mScaleAnimator = new ScaleAnimator(mOuterMatrix, finalMatrix);
            mScaleAnimator.start();
        }
    }

    /**
     * 结束所有正在执行的动画
     */
    private void cancelAllAnimator() {
        if (mScaleAnimator != null) {
            mScaleAnimator.cancel();
            mScaleAnimator = null;
        }
        if (mFlingAnimator != null) {
            mFlingAnimator.cancel();
            mFlingAnimator = null;
        }
    }

    /**
     * 滑动
     *
     * @param vx x方向
     * @param vy y方向
     */
    private void fling(float vx, float vy) {
        if (!isReady()) {
            return;
        }
        //清理当前可能正在执行的动画
        cancelAllAnimator();
        //创建惯性动画
        //FlingAnimator单位为 像素/帧,一秒60帧
        mFlingAnimator = new FlingAnimator(vx / 60f, vy / 60f);
        mFlingAnimator.start();
    }

    /**
     * 让图片移动一段距离m,同时防止超界
     *
     * @param dx x方向移动距离
     * @param dy y方向移动距离
     * @return 位置是否改变
     */
    private boolean scrollBy(float dx, float dy) {
        if (!isReady()) {
            return false;
        }
        // 获取当前图片的大小及边界
        RectF drawableRect = getCurrentRect();
        // 当前控件大小
        float width = getWidth();
        float height = getHeight();

        // 边界检测
        if (drawableRect.width() < width) {
            // 图片比控件窄，禁止移动
            dx = 0;
        } else if (drawableRect.left + dx > 0) {
            // 移动后如果左边超界
            if (drawableRect.left < 0) {
                // 移动之前没有超界的话，则最多移动到边界
                dx = -drawableRect.left;
            } else {
                // 移动前就已经到达边界，则不移动
                dx = 0;
            }
        } else if (drawableRect.right + dx < width) {
            // 移动后右边超界
            if (drawableRect.right > width) {
                // 移动之前没有超界的话，则最多移动到边界
                dx = width - drawableRect.right;
            } else {
                // 移动前就已经到达边界，则不移动
                dx = 0;
            }
        }
        // 判断纵向是否超界
        if (drawableRect.height() < height) {
            // 图片比控件低，禁止移动
            dy = 0;
        } else if (drawableRect.top + dy > 0) {
            // 移动后如果上边超界
            if (drawableRect.top < 0) {
                // 移动之前没有超界的话，则最多移动到边界
                dy = -drawableRect.top;
            } else {
                // 移动前就已经到达边界，则不移动
                dy = 0;
            }
        } else if (drawableRect.bottom + dy < height) {
            // 移动后下边超界
            if (drawableRect.bottom > height) {
                // 移动之前没有超界的话，则最多移动到边界
                dy = height - drawableRect.bottom;
            } else {
                // 移动前就已经到达边界，则不移动
                dy = 0;
            }
        }
        // 应用移动变换
        mOuterMatrix.postTranslate(dx, dy);
        // 重绘
        invalidate();
        // 返回是否发生变化
        if (dx == 0 && dy == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 保存缩放状态，以便超界时恢复
     */
    private void saveScaleStatus(PointF point1, PointF point2) {
        // 根据图像变换部分知识，矩阵的左上和左中的值决定了缩放的x和y方向
        // 由于我们是等比缩放，所以只用获取一个方向即可
        // 保存基础缩放比例
        mScaleBaseValue = caculateMatrixScale(mOuterMatrix) / caculateDistance(point1, point2);
        // 保存不缩放状态下的缩放中心点
        mScaleCenter = inverseMatrixPoint(caculateCenterPoint(point1, point2), mOuterMatrix);
    }

    /**
     * 计算映射到原来状态的中点(不做放大缩小变换的)
     */
    private PointF inverseMatrixPoint(PointF pointF, Matrix outerMatrix) {
        PointF resultPoint = new PointF();
        // 计算逆矩阵
        Matrix inverseMatrix = new Matrix();
        // invert方法:求矩阵的逆矩阵，简而言之就是计算与之前相反的矩阵
        // 如果之前是平移200px，则求的矩阵为反向平移200px
        // 如果之前是缩小到0.5f，则结果是放大到2倍
        outerMatrix.invert(inverseMatrix);
        float[] srcPoint = {pointF.x, pointF.y};
        float[] dstPoint = new float[2];
        // mapPoints方法：计算一组点基于当前Matrix变换后的位置
        inverseMatrix.mapPoints(dstPoint, srcPoint);
        return new PointF(dstPoint[0], dstPoint[1]);
    }

    /**
     * 计算中心点
     *
     * @param point1 点1
     * @param point2 点2
     * @return 包含中心点信息的PointF
     */
    private PointF caculateCenterPoint(PointF point1, PointF point2) {
        return new PointF((point1.x + point2.x) / 2.0F, (point1.y + point2.y) / 2.0F);
    }

    /**
     * 计算矩阵的缩放比例 由于等比缩放，所以0 4 两个位置的缩放比例相同
     */
    private float caculateMatrixScale(Matrix matrix) {
        float[] value = new float[9];
        matrix.getValues(value);
        return value[0];
    }

    /**
     * 计算两点之间的距离
     *
     * @param point1 点1
     * @param point2 点2
     * @return 两点的距离
     */
    private float caculateDistance(PointF point1, PointF point2) {
        float dx = point1.x - point2.x;
        float dy = point1.y - point2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 执行缩放
     *
     * @param scaleCenter 缩放中心
     * @param scaleBaseValue 缩放初始值
     * @param distance 两指距离
     * @param lineCenter 两指中点
     */
    private void doScale(PointF scaleCenter, float scaleBaseValue, float distance, PointF lineCenter) {
        if (!isReady()) {
            return;
        }
        // 计算图片从不缩放的状态到目标状态的缩放比例
        float scale = scaleBaseValue * distance;
        Matrix matrix = new Matrix();
        // 从缩放中心缩放
        matrix.postScale(scale, scale, scaleCenter.x, scaleCenter.y);
        // 跟随手指中心移动
        matrix.postTranslate(lineCenter.x - scaleCenter.x, lineCenter.y - scaleCenter.y);
        // 为外部矩阵应用变换
        mOuterMatrix.set(matrix);
        // 根据外部矩阵的值重绘
        invalidate();
    }

    /**
     * 缩放动画
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
            float[] resultMatrix = new float[9];
            // 插值动画计算出减速效果(算法借鉴而来)
            for (int i = 0; i < 9; i++) {
                // 改变矩阵的每一个值
                resultMatrix[i] = mSrcMatrix[i] + (mDstMatrix[i] - mSrcMatrix[i]) * currentValue;
            }
            // 为外部矩阵设定数值
            mOuterMatrix.setValues(resultMatrix);
            // 根据外部矩阵数值重绘
            invalidate();
        }
    }

    /**
     * 滑动惯性动画 算法学习而来
     */
    class FlingAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener {

        private float[] mVector;    // 速度向量

        public FlingAnimator(float vectorX, float vectorY) {
            super();
            setFloatValues(0, 1f);
            setDuration(1000000);
            addUpdateListener(this);
            mVector = new float[]{vectorX, vectorY};
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            // 移动图像并给出结果
            boolean result = scrollBy(mVector[0], mVector[1]);
            // 每次衰减9/10
            mVector[0] *= 0.9F;
            mVector[1] *= 0.9F;
            //速度太小或者不能移动了则结束
            if (!result || caculateDistance(new PointF(0, 0), new PointF(mVector[0], mVector[1])) < 1.0F) {
                animation.cancel();
            }
        }
    }

    /**
     * 与ViewPager结合时判断是否能左右滚动
     */
    @Override
    public boolean canScrollHorizontally(int direction) {
        if (mCurrentMode == MODE_SCALE) {
            // 如果是缩放模式，可以
            return true;
        }
        RectF bound = getCurrentRect();
        if (bound == null) {
            return false;
        }
        if (bound.isEmpty()) {
            return false;
        }
        if (direction > 0) {
            // 如果方向为左且右边没有到边界,则可以
            return bound.right > getWidth();
        } else {
            // 如果方向为右且左边没有到边界,则可以
            // 如果方向为右且左边没有到边界,则可以
            return bound.left < 0;
        }
    }
}
