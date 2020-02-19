package com.viger.myokhttp;

import java.util.ArrayList;
import java.util.List;

public class OkHttpClient {
    //分发器
    private Dispatcher dispatcher;
    private ConnectionPool connectionPool;
    private int retrys;
    private List<Interceptor> interceptors;

    public OkHttpClient(Builder builder) {
        this.dispatcher = builder.dispatcher;
        this.connectionPool = builder.connectionPool;
        this.retrys = builder.retrys;
        this.interceptors = builder.interceptors;
    }

    public static final class Builder{
        Dispatcher dispatcher = new Dispatcher();
        ConnectionPool connectionPool = new ConnectionPool();
        int retrys = 3;
        List<Interceptor> interceptors = new ArrayList<>();
        public Builder retrys(int retrys) {
            this.retrys = retrys;
            return this;
        }
        public Builder addInterceptor(Interceptor interceptor) {
            interceptors.add(interceptor);
            return this;
        }
        public OkHttpClient build() {
            return new OkHttpClient(this);
        }
    }

    public Call newCall(Request request) {
        return new Call(request, this);
    }

    public int retrys() {
        return retrys;
    }

    public Dispatcher dispatcher() {
        return dispatcher;
    }

    public ConnectionPool connectionPool() {
        return connectionPool;
    }

    public List<Interceptor> interceptors() {
        return interceptors;
    }

}
