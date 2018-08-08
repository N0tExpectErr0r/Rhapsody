package com.n0texpecterr0r.rhapsody.loader;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import com.n0texpecterr0r.rhapsody.loader.base.AbsTaskResolver;

/**
 * @author Created by Nullptr
 * @date 2018/8/8 9:50
 * @describe 本地责任链
 */
public class LocalTaskResolver extends AbsTaskResolver {

    private final LoadConfig mConfig;   // 配置
    private Handler mUIHandler;             // 主线程Handler，用于回调
    private TaskDispatcher mDispatcher;     // 线程调度器，分发任务。
    private String mPath;                   // 地址
    private ImageView mImageView;           // 目标Imageview

    public LocalTaskResolver(String path, ImageView imageView){
        mConfig = LoadConfig.getInstance();
        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                ImageHolder holder = (ImageHolder) msg.obj;
                Bitmap bitmap = holder.bitmap;
                ImageView imgView = holder.imageView;
                String path = holder.path;
                //防止错乱，比较tag后再进行设置
                if (imgView.getTag().toString().equals(path)) {
                    imgView.setImageBitmap(bitmap);
                    //加载成功动画
                    ObjectAnimator.ofFloat(imgView, "alpha", 0F, 1F)
                            .setDuration(250)
                            .start();
                    BitmapCache.getInstance().add(path, bitmap);
                }
            }
        };
        mDispatcher = TaskDispatcher.getInstance();
        mPath = path;
        mImageView = imageView;
    }

    @Override
    public void handleTask() {

        // 线程调度器调度任务
        Runnable loadTask = new Runnable() {
            @Override
            public void run() {
                Bitmap localBitmap = BitmapUtil.getBitmapFromPath(mPath,
                        mConfig.mTargetWidth,mConfig.mTargetHeight);
                ImageHolder imageHolder = new ImageHolder(localBitmap, mImageView, mPath);
                Message message = Message.obtain();
                message.obj = imageHolder;
                //回到主线程更新UI
                mUIHandler.sendMessage(message);
            }
        };
        mDispatcher.executeTask(loadTask);
    }

    /**
     * 为了防止图片错乱，在message内判断后再加载图片
     */
    private class ImageHolder {
        Bitmap bitmap;
        ImageView imageView;
        String path;

        ImageHolder(Bitmap bitmap, ImageView imageView, String path) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.path = path;
        }
    }
}
