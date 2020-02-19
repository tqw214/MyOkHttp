package com.viger.myokhttp;

import android.util.Log;

import java.io.IOException;
import java.util.Map;

public class HeadersInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("interceprot","Http头拦截器....");
        Request request = chain.call.request;
        Map<String, String> headers = request.headers();
        headers.put(HttpCodec.HEAD_HOST, request.url().getHost());
        headers.put(HttpCodec.HEAD_CONNECTION, HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        if(null != request.body()) {
            String contentType = request.body().contentType();
            if(contentType != null) {
                headers.put(HttpCodec.HEAD_CONTENT_TYPE, contentType);
            }
            long contentLength = request.body().contentLength();
            if(contentLength != -1) {
                headers.put(HttpCodec.HEAD_CONTENT_LENGTH, Long.toString(contentLength));
            }
        }
        return chain.proceed();
    }
}
