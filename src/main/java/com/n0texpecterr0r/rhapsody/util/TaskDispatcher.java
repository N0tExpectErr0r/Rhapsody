package com.n0texpecterr0r.rhapsody.util;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * @author Created by Nullptr
 * @date 2018/7/24 17:08
 * @describe 线程调度工具类，可以配置为LIFO及FIFO两种调度模式
 */
public class TaskDispatcher {

    private LinkedList<Runnable> mTaskList;         // 任务队列（LinkedList可以取头和尾）
    private ExecutorService mThreadPool;            // 线程池
    private Thread mPollingThead;            // 轮询线程
    private static final String POLLING_THREAD_NAME = "polling_thread"; // 轮询线程的默认名称
    private Handler mPollingHandler;                 // 轮询线程中的Handler
    private static final int DEFAULT_THREAD_COUNT = 1;  // 默认线程数量
    private Type mType = Type.LIFO;                 // 队列的调度方式，默认为LIFO
    //用volatile保证可见性
    private volatile Semaphore mPollingSemaphore;   // 信号量，由于线程池内部也有一个阻塞线程，若加入任务的速度过快，LIFO效果不明显
    private volatile Semaphore mSemaphore = new Semaphore(0);  // 信号量，防止mPoolThreadHandler未初始化完成
    //用volatile保证可见性
    private volatile static TaskDispatcher sInstance;

    public enum Type { FIFO, LIFO }                 //两种调度模式

    /**
     * 懒汉式单例获取实例对象
     * @return 实例对象
     */
    public static TaskDispatcher getInstance() {
        if (sInstance == null) {
            synchronized (TaskDispatcher.class) {
                if (sInstance == null) {
                    sInstance = new TaskDispatcher(DEFAULT_THREAD_COUNT, Type.LIFO);
                }
            }
        }
        return sInstance;
    }

    /**
     * 懒汉式单例获得实例对象的重载
     * @param threadCount 线程池的线程数量
     * @param type 队列的调度方式
     * @return 实例对象
     */
    public static TaskDispatcher getInstance(int threadCount, Type type) {
        if (sInstance == null) {
            synchronized (TaskDispatcher.class) {
                if (sInstance == null) {
                    sInstance = new TaskDispatcher(threadCount, type);
                }
            }
        }
        return sInstance;
    }

    /**
     * 构造方法
     * @param threadCount 线程池的线程数量
     * @param type 队列的调度方式
     */
    private TaskDispatcher(int threadCount, Type type) {
        init(threadCount, type);
    }

    /**
     * 初始化
     * @param threadCount 线程池的线程数量
     * @param type 队列的调度方式
     */
    private void init(int threadCount, Type type) {

        mThreadPool = Executors.newFixedThreadPool(threadCount);
        mPollingSemaphore = new Semaphore(threadCount);
        mTaskList = new LinkedList<>();
        mType = type == null ? Type.LIFO : type;

        // 开启轮询线程
        mPollingThead = new Thread(POLLING_THREAD_NAME) {
            @Override
            public void run() {
                Looper.prepare();
                mPollingHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mThreadPool.execute(getTask());
                        try {
                            mPollingSemaphore.acquire();    // 请求轮询信号量，在doTask中释放
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSemaphore.release();   // 释放一个信号量
                Looper.loop();
            }
        };
        mPollingThead.start();
    }

    /**
     * 添加任务
     * @param task 任务
     */
    private synchronized void addTask(Runnable task) {
        try {
            // mPollingHandler为空时，请求信号量，因为mPollingHandler创建完成会释放一个信号量
            if (mPollingHandler == null) mSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mTaskList.add(task);

        mPollingHandler.sendEmptyMessage(1);
    }

    /**
     * 取出一个任务
     * @return 需要执行的任务
     */
    private synchronized Runnable getTask() {
        if (mType == Type.LIFO) {
            return mTaskList.removeLast();  // 如果是LIFO模式，则从尾部取(当做栈用)
        } else if (mType == Type.FIFO) {
            return mTaskList.removeFirst(); // 如果是FIFO模式，则从头取(当做队列)
        }
        return null;
    }

    /**
     * 给外部调用的方法
     * @param task 要执行的task
     */
    public void executeTask(final Runnable task) {
        addTask(new Runnable() {
            @Override
            public void run() {
                task.run();
                mPollingSemaphore.release();    //调用完成，释放
            }
        });
    }
}