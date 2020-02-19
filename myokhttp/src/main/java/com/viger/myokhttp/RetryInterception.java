package com.viger.myokhttp;

import android.util.Log;

import java.io.IOException;

/**
 * 重试拦截器
 */
public class RetryInterception implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("interceptor","重试拦截器....");
        Call call = chain.call;
        IOException exception = null;
        for(int i=0; i < call.client.retrys(); i++) {
            if(call.isCanceled()) {
                throw new IOException("Canceled");
            }
            try {
                Response response = chain.proceed();
                return response;
            }catch(IOException e) {
                exception = e;
            }
        }
        throw exception;
    }
}
