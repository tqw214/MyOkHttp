package com.viger.myokhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * //拼接头部的工具类
 */
public class HttpCodec {

    public static final String SPACE = " ";
    public static final String VERSION = "HTTP/1.1";
    public static final String CRLF = "\r\n";
    public static final String COLON = ":";

    public static final String HEAD_HOST = "Host";

    public static final String HEAD_CONNECTION = "Connection";
    public static final String HEAD_CONTENT_TYPE = "Content-Type";
    public static final String HEAD_CONTENT_LENGTH = "Content-Length";


    public static final String HEAD_VALUE_KEEP_ALIVE = "Keep-Alive";

    ByteBuffer byteBuffer;

    public HttpCodec() {
        //申请足够大的内存记录读取的数据 (一行)
        byteBuffer = ByteBuffer.allocate(10 * 1024);
    }

    public void writeRequest(OutputStream os, Request request) throws IOException {
        StringBuffer protocol = new StringBuffer();
        //写请求行
        // GET /fiddler2/updatecheck.asp?isBeta=False HTTP/1.1
        protocol.append(request.method());
        protocol.append(SPACE);
        protocol.append(request.url().getFile());
        protocol.append(SPACE);
        protocol.append(VERSION);
        protocol.append(CRLF);

        //写请求头
        /**
         * Host: www.baidu.com
         * Connection: Keep-Alive
         * Accept: *
         */
        Map<String, String> headers = request.headers();
        for(Map.Entry<String,String> entry : headers.entrySet()) {
            protocol.append(entry.getKey());
            protocol.append(COLON);
            protocol.append(SPACE);
            protocol.append(entry.getValue());
            protocol.append(CRLF);
        }
        protocol.append(CRLF);

        //http请求体，如果存在的话
        RequestBody body = request.body();
        if(null != body) {
            protocol.append(body.body());
        }

        //向socket写出
        os.write(protocol.toString().getBytes());
        os.flush();
    }

    public String readLine(InputStream is) {
        byte b;
        byteBuffer.clear();
        byteBuffer.mark();
        //todo
        return null;
    }
}
