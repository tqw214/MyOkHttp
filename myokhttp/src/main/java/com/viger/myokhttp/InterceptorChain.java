package com.viger.myokhttp;

import java.io.IOException;
import java.util.List;

//拦截器链处理类
public class InterceptorChain {

    final List<Interceptor> interceptors;
    final int index;
    final Call call;
    final HttpConnection connection;
    final HttpCodec httpCodec = new HttpCodec();

    public InterceptorChain(List<Interceptor> interceptors, int index, Call call, HttpConnection connection) {
        this.interceptors = interceptors;
        this.index = index;
        this.call = call;
        this.connection = connection;
    }

    public Response proceed() throws IOException {
        return proceed(connection);
    }

    public Response proceed(HttpConnection connection) throws IOException {
        Interceptor interceptor = interceptors.get(index);
        InterceptorChain next = new InterceptorChain(interceptors, index + 1, call, connection);
        Response response = interceptor.intercept(next);
        return response;
    }

}
