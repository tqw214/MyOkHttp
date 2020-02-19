package com.viger.myokhttp;

import java.util.ArrayDeque;
import java.util.Deque;
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

        }
    }

    //同一host同一请求数
    private int runningCallsForHost(Call.AsyncCall call) {
        int result = 0;
        for(Call.AsyncCall c : runningAsyncCalls) {

        }
        return 0;
    }

    //停止call，移除
    public void finished(Call.AsyncCall asyncCall) {

    }
}
