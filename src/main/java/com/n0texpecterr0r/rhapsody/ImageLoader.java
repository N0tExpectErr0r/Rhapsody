package com.n0texpecterr0r.rhapsody;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author Created by Nullptr
 * @date 2018/7/24 10:37
 * @describe 单例模式图片加载类
 */
public class ImageLoader {

    private static ImageLoader sInstance;

    private LruCache<String,Bitmap> mImgCache;  //用于图片缓存

    private ExecutorService mThreadPool;    //线程池

    private static final int DEFAULT_THREAD_COUNT = 1;  //默认线程数

    private LoadType mLoadType;  //图片加载类型

    //LinkedList可以从尾部和头部取对象
    private LinkedList<Runnable> mTaskQueue;    //任务队列

    private Thread mLoopThread;     //轮询任务队列的轮询线程

    private static final String LOOP_THREAD_NAME = "loop_thread";

    private Handler mLoopThreadHandler; //用于给线程中的MessageQueue发送消息

    private Handler mUIHandler;     //用于回到UI线程

    //加载方式(先进先出，后进先出)
    private enum LoadType{
        FIFO,
        LIFO
    }

    /**
     * 构造方法
     * @param threadCount 线程数
     * @param type 类型
     */
    private ImageLoader(int threadCount,LoadType type) {
        init(threadCount,type);
    }

    /**
     * 获取实例
     * @return 实例
     */
    public static ImageLoader getInstance() {
        //外层判断提高了效率(过滤掉一些代码)
        if (sInstance == null) {
            synchronized (ImageLoader.class) {
                if (sInstance == null) {
                    sInstance = new ImageLoader(DEFAULT_THREAD_COUNT,LoadType.LIFO);
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化变量
     * @param threadCount 线程池的线程数
     * @param type 图片加载类型
     */
    private void init(int threadCount, LoadType type) {
        //初始化后台轮询线程
        mLoopThread = new HandlerThread(LOOP_THREAD_NAME){
            @Override
            public void run() {
                mLoopThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //从线程池取出任务执行
                        mThreadPool.execute(getTask());
                    }
                };
            }
        };
        mLoopThread.start();

        //初始化LruCache
        //将最大内存的1/8作为LruCache的最大内存
        int cacheMemory = (int)Runtime.getRuntime().maxMemory()/8;
        mImgCache = new LruCache<String,Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //计算图片占据的内存
                return value.getRowBytes()*value.getHeight();
            }
        };

        //初始化线程池
        mThreadPool = Executors.newFixedThreadPool(threadCount);

        //初始化TaskQueue
        mTaskQueue = new LinkedList<>();
        mLoadType = type;


    }

    private Runnable getTask() {
        if (mLoadType == LoadType.FIFO){
            return mTaskQueue.removeFirst();    //先进先出
        }else if (mLoadType == LoadType.LIFO){
            return mTaskQueue.removeLast(); //后进先出
        }
        return null;
    }

    /**
     * 根据path设置图片
     * @param path 路径
     * @param imageView ImageView
     */
    public void loadImage(String path, final ImageView imageView){
        imageView.setTag(path);

        //UIHandler的初始化
        mUIHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                ImageHolder holder = (ImageHolder) msg.obj;
                Bitmap bitmap = holder.bitmap;
                ImageView imgView = holder.imageView;
                String path = holder.path;
                //防止错乱，比较tag后再进行设置
                if (imgView.getTag().toString().equals(path)){
                    imgView.setImageBitmap(bitmap);
                }
            }
        };

        final Bitmap bitmap = getBitmapFromLruCache(path);



        if (bitmap!=null){
            // 需要通知handleMessage具体是哪个ImageView及Bitmap
            Message message = Message.obtain();
            message.obj = new ImageHolder(bitmap,imageView,path);
            mUIHandler.sendMessage(message);
        }else{
            addTask(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    }

    private void addTask(Runnable runnable){
        mTaskQueue.add(runnable);
        mLoopThreadHandler.sendEmptyMessage(1);

    }

    private class ImageHolder{
        Bitmap bitmap;
        ImageView imageView;
        String path;

        public ImageHolder(Bitmap bitmap, ImageView imageView, String path) {
            this.bitmap = bitmap;
            this.imageView = imageView;
            this.path = path;
        }
    }

    /**
     * 根据path在缓存中获取bitmap
     * @param path 路径
     * @return 获取的Bitmap
     */
    private Bitmap getBitmapFromLruCache(String path) {
        return mImgCache.get(path);
    }

}
