package com.viger.myokhttp;

import java.io.IOException;
import java.util.ArrayList;

public class Call {

    Request request;
    OkHttpClient client;
    boolean executed; //是否执行过
    boolean canceled; //是否取消

    public Call(Request request, OkHttpClient client) {
        this.request = request;
        this.client = client;
    }

    public Call enqueue(Callback callback) {
        //不能重复执行
        synchronized (this) {
            if(executed) {
                throw new IllegalStateException("already execute");
            }
            executed = true;
        }
        client.dispatcher().enqueue(new AsyncCall(callback));
        return this;
    }

    public void cancel() {
        canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public OkHttpClient client() {
        return client;
    }

    public Request request() {
        return request;
    }

    final class AsyncCall implements Runnable{

        private Callback callback;

        public AsyncCall(Callback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            //是否已经通知过callback了
            boolean signalledCallback = false;
            try {
                //开始请求链
                Response response = getResponse();
                if(canceled) {
                    //如果取消了
                    signalledCallback = true;
                    callback.onFailure(Call.this, new IOException("Canceled"));
                }else {
                    signalledCallback = true;
                    callback.onResponse(Call.this, response);
                }
            }catch (IOException e) {
                if(!signalledCallback) {
                    //如果没有通知过就发生了异常
                    callback.onFailure(Call.this, e);
                }
            }finally {
                client.dispatcher().finished(this);
            }
        }

        //获取当前call的请求主机名
        public String host() {
            return request.url().getHost();
        }

    }

    //拦截链开始
    private Response getResponse()throws IOException {
        //添加拦截器
        ArrayList<Interceptor> interceptors = new ArrayList<>();
        interceptors.addAll(client.interceptors());
        interceptors.add(new RetryInterception()); //重试拦截器
        interceptors.add(new HeadersInterceptor()); //添加heder
        interceptors.add(new ConnectionInterceptor()); //连接
        interceptors.add(new CallServiceInterceptor()); //请求
        InterceptorChain chain = new InterceptorChain(interceptors, 0, this, null);
        return chain.proceed();
    }
}
