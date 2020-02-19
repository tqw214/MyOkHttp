package com.viger.myokhttp;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocketFactory;

public class HttpConnection {

    static final String HTTPS = "https";
    Socket socket;
    InputStream is;
    OutputStream os;
    Request request;
    long lastUsetime;

    public Request getRequest(){
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public void closeQuietly() {
        if(null != socket) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isSameAddress(String host, int port) {
        if(null == socket) {
            return false;
        }
        return (TextUtils.equals(socket.getInetAddress().getHostName(), host)
                && port == socket.getPort());
    }

    private void createSocket() throws IOException {
        if(null == socket || socket.isClosed()) {
            HttpUrl url = request.url();
            if(url.getProtocol().equalsIgnoreCase(HTTPS)) {
                socket = SSLSocketFactory.getDefault().createSocket();
            }else {
                socket = new Socket();
            }
            InetSocketAddress address = new InetSocketAddress(url.getHost(),
                    url.getPort());
            socket.connect(address);
            is = socket.getInputStream();
            os = socket.getOutputStream();
        }
    }

    public InputStream call(HttpCodec httpCodec) throws IOException{
        try {
            createSocket();
            httpCodec.writeRequest(os, request);
            return is;
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    public void updateLastUseTime() {
        //更新最后使用时间
        lastUsetime = System.currentTimeMillis();
    }

}
