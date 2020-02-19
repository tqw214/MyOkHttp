package com.viger.myokhttp;

import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URL;

public class HttpUrl {

    private String protocol;//协议http  https
    private String host;//192.6.2.3
    private String file;// 文件地址
    private int port;//端口

    /**
     * scheme://host:port/path?query#fragment
     * @param url
     * @throws MalformedURLException
     */
    public HttpUrl(String urlStr) throws MalformedURLException {
        URL url = new URL(urlStr);
        host = url.getHost();
        protocol = url.getProtocol();
        file = url.getFile();
        file = TextUtils.isEmpty(file) ? "/" : file;
        port = url.getPort();
        port = port == -1 ? url.getDefaultPort() : port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public int getPort() {
        return port;
    }

}
