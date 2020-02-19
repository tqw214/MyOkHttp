package com.viger.myokhttp;

import android.util.Log;

import java.io.IOException;

public class ConnectionInterceptor implements Interceptor {

    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("interceprot","连接拦截器....");
        Request request = chain.call.request();
        OkHttpClient client = chain.call.client();
        HttpUrl url = request.url();
        String host = url.getHost();
        int port = url.getPort();
        HttpConnection httpConnection = client.connectionPool().get(host,port);
        if(httpConnection == null) {
            httpConnection = new HttpConnection();
        }else {
            Log.d("ConnectionInterceptor","使用连接池...");
        }
        httpConnection.setRequest(request);
        Response response = chain.proceed(httpConnection);
        if(response.isKeepAlive()) {
            client.connectionPool().put(httpConnection);
        }
        return response;
    }

}
