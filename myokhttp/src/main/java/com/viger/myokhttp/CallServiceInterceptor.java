package com.viger.myokhttp;

import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class CallServiceInterceptor implements Interceptor {
    @Override
    public Response intercept(InterceptorChain chain) throws IOException {
        Log.d("interceprot", "通信拦截器....");
        HttpCodec httpCodec = chain.httpCodec;
        HttpConnection connection = chain.connection;
        //向socket写出数据
        InputStream is = connection.call(httpCodec);
        //从socket读取数据
        //读取响应行数据
        String readLine = httpCodec.readLine(is);
        //读取响应头数据
        Map<String, String> map = httpCodec.readHeaders(is);
        //是否保持连接
        boolean isKeepAlive = false;
        if(map.containsKey(HttpCodec.HEAD_CONNECTION)) {
            isKeepAlive = map.get(HttpCodec.HEAD_CONNECTION)
                    .equalsIgnoreCase(HttpCodec.HEAD_VALUE_KEEP_ALIVE);
        }
        int contentLength = -1;
        if(map.containsKey(HttpCodec.HEAD_CONTENT_LENGTH)) {
            //响应数据的长度
            contentLength = Integer.valueOf(map.get(HttpCodec.HEAD_CONTENT_LENGTH));
        }
        boolean isChunked = false;
        if(map.containsKey(HttpCodec.HEAD_TRANSFER_ENCODING)) {
            isChunked = map.get(HttpCodec.HEAD_TRANSFER_ENCODING)
                    .equalsIgnoreCase(HttpCodec.HEAD_VALUE_CHUNKED);
        }
        //读取响应体数据
        String body = null;
        if(contentLength > 0) {
            byte[] bytes = httpCodec.readBytes(is, contentLength);
            body = new String(bytes);
        }else if(isChunked) {
            body = httpCodec.readChunked(is);
        }
        String[] split = readLine.split(" ");
        connection.updateLastUseTime();
        return new Response(Integer.valueOf(split[1]),
                contentLength, map, body, isKeepAlive);
    }
}
