package com.viger.myokhttp;

import android.util.Log;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 分发器
 */
public class Dispatcher {

    //最多同时请求
    private int maxRequests;
    //同一个主机最多同时请求数
    private int maxRequestsPerHost;
    //线程池,发送异步请求
    private ExecutorService executorService;

    //等待执行的队列
    private final Deque<Call.AsyncCall> readyAsyncCalls = new ArrayDeque<>();

    //正在执行的队列
    private final Deque<Call.AsyncCall> runningAsyncCalls = new ArrayDeque<>();

    public Dispatcher() {
        this(64, 2);
    }

    public Dispatcher(int maxRequests, int maxRequestsPerHost) {
        this.maxRequests = maxRequests;
        this.maxRequestsPerHost = maxRequestsPerHost;
    }

    /**
     * SynchronousQueue是一个内部只能包含一个元素的队列。
     * 插入元素到队列的线程被阻塞，直到另一个线程从队列中获取了队列中存储的元素。
     * 同样，如果线程尝试获取元素并且当前不存在任何元素，则该线程将被阻塞，
     * 直到线程将元素插入队列。
     * @return
     */
    private synchronized ExecutorService executorService() {
        if(executorService == null) {
            ThreadFactory threadFactory = new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread result = new Thread(r, "myokhttp Dispatcher");
                    return result;
                }
            };
            executorService = new ThreadPoolExecutor(0,
                    Integer.MAX_VALUE, 60,
                    TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), threadFactory);
        }
        return executorService;
    }

    //异步
    public void enqueue(Call.AsyncCall call) {
        if(runningAsyncCalls.size() < maxRequests &&
            runningCallsForHost(call) < maxRequestsPerHost) {
            Log.d("Dispatcher","提交执行...");
            runningAsyncCalls.add(call);
            executorService().execute(call);
        }else {
            Log.d("Dispatcher","等待执行...");
            readyAsyncCalls.add(call);
        }
    }

    //同一host同一请求数
    private int runningCallsForHost(Call.AsyncCall call) {
        int result = 0;
        for(Call.AsyncCall c : runningAsyncCalls) {
            if(c.host().equals(call.host())) {
                result++;
            }
        }
        return result;
    }

    /*
     *请求结束 移出正在运行队列
     *并判断是否执行等待队列中的请求
     */
    public void finished(Call.AsyncCall asyncCall) {
        synchronized (this) {
            runningAsyncCalls.remove(asyncCall);
            //判断是否执行等待队列中的请求
            promoteCalls();
        }
    }

    /**
     *
     * 判断是否执行等待队列中的请求
     * 如果需要，执行下一个任务
     */
    private void promoteCalls() {
        if(runningAsyncCalls.size() >= maxRequests) {
            //还有没有执行完的任务
            return;
        }
        if(readyAsyncCalls.isEmpty()) {
            //没有正在执行的任务 和 准备执行的任务
            return;
        }
        //有(或者没有)正在执行的任务，准备队列不为空的情况
        for (Iterator<Call.AsyncCall> i = readyAsyncCalls.iterator(); i.hasNext(); ) {
            Call.AsyncCall call = i.next();
            if(runningCallsForHost(call) < maxRequestsPerHost) {
                i.remove();
                runningAsyncCalls.add(call);
                executorService().execute(call);
            }
            if(runningAsyncCalls.size() > maxRequests) {
                return;
            }
        }
    }
}
