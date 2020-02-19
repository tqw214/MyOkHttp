package com.viger.myokhttp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

public class CallServiceInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("interceprot", "通信拦截器....");
        HttpCodec httpCodec = chain.httpCodec;
        HttpConnection connection = chain.connection;
        //向socket写出数据
        InputStream is = connection.call(httpCodec);
        //从socket读取数据
        String readLine = httpCodec.readLine(is);
        return null;
    }
}
