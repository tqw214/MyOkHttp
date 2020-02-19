package com.viger.myokhttp;

import android.text.TextUtils;
import java.util.HashMap;
import java.util.Map;

public class Request {

    //请求头
    private Map<String, String> headers;
    //解析url成httpurl对象
    private HttpUrl url;
    //请求方式 get/post
    private String method;
    //请求体
    private RequestBody body;

    public Request(Builder builder) {
        this.headers = builder.headers;
        this.url = builder.url;
        this.method = builder.method;
        this.body = builder.body;
    }

    public String method() {
        return method;
    }

    public HttpUrl url() {
        return url;
    }

    public RequestBody body() {
        return body;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public static class Builder {

        private HttpUrl url;
        private Map<String, String> headers = new HashMap<>();
        private String method;
        private RequestBody body;

        public Builder url(String url) {
            try {
                this.url = new HttpUrl(url);
                return this;
            }catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException("Failed Http Url");
            }
        }

        public Builder addHeader(String name, String value) {
            headers.put(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            headers.remove(name);
            return this;
        }

        public Builder get() {
            method = "GET";
            return this;
        }

        public Builder post(RequestBody body) {
            method = "POST";
            this.body = body;
            return this;
        }

        public Request build() {
            if(url == null) {
                throw new IllegalStateException("url is null");
            }
            if(TextUtils.isEmpty(method)) {
                method = "GET";
            }
            return new Request(this);
        }
    }

}
