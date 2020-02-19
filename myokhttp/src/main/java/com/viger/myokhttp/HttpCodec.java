package com.viger.myokhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * //拼接头部的工具类
 */
public class HttpCodec {

    public static final String SPACE = " ";
    public static final String VERSION = "HTTP/1.1";
    public static final String CRLF = "\r\n";
    public static final String COLON = ":";
    public static final String HEAD_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEAD_VALUE_CHUNKED = "chunked";
    private static final int CR = 13; //ASCII码:CR回车键
    private static final int LF = 10; //ASCII码:LF换行键

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

    public String readLine(InputStream is) throws IOException {
        try {
            byte b;
            boolean isMabeyEofLine = false;
            byteBuffer.clear();
            byteBuffer.mark();
            while((b = (byte)is.read()) != -1) {
                byteBuffer.put(b);
                //判断读到\r则记录，判断下一个字节是否为\n
                if(b == CR) { //回车键
                    isMabeyEofLine = true;
                } else if(isMabeyEofLine) {
                    //上一个字节是/r 并且本次读取到/n
                    if(b==LF) { //换行键
                        //获得目前读取的所有字节
                        byte[] lineBytes = new byte[byteBuffer.position()];
                        //返回标记位置
                        byteBuffer.reset();
                        byteBuffer.get(lineBytes);
                        //清空所有index并重新标记
                        byteBuffer.clear();
                        byteBuffer.mark();
                        String line = new String(lineBytes);
                        return line;
                    }
                    isMabeyEofLine = false;
                }
            }
        }catch (IOException e) {
            throw new IOException(e);
        }
        throw new IOException("Response Read Line.");
    }

    public Map<String, String> readHeaders(InputStream is) throws IOException {
        Map<String, String> headers = new HashMap<>();
        while(true){
            String line = readLine(is);
            //读取到空行，则下面的为body
            if(isEmptyLine(line)) {
                break;
            }
            int index = line.indexOf(":");
            if(index > 0) {
                String name = line.substring(0, index);
                // ": "移动两位到 总长度减去两个("\r\n")
                String value = line.substring(index+2,line.length()-2);
                headers.put(name, value);
            }
        }
        return headers;
    }

    private boolean isEmptyLine(String line) {
        return line.equals("\r\n");
    }

    public byte[] readBytes(InputStream is, int contentLength) throws IOException{
        byte[] bytes = new byte[contentLength];
        int readNum = 0;
        while (true) {
            readNum += is.read(bytes, readNum, contentLength - readNum);
            if(readNum == contentLength) {
                return bytes;
            }
        }
    }

    //读取分块传输数据
    public String readChunked(InputStream is) throws IOException {
        int len = -1;
        boolean isEmptyData = false;
        StringBuffer chunked = new StringBuffer();
        while (true) {
            //解析下一个chunk长度
            if(len < 0) {
                String line = readLine(is);
                line = line.substring(0, line.length() - 2);
                len = Integer.valueOf(line, 16); //16进制
                //chunk编码的数据最后一段为 0\r\n\r\n
                isEmptyData = len == 0;
            }else {
                //块长度不包括\r\n  所以+2将 \r\n 读走
                byte[] bytes = readBytes(is, len + 2);
                chunked.append(new String(bytes));
                len = -1;
                if(isEmptyData) {
                    return chunked.toString();
                }
            }
        }
    }
}
