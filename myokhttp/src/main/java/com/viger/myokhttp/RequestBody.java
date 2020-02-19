package com.viger.myokhttp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 提交body
 */
public class RequestBody {

    private static final String CONTENT_TYPE = "application/x-www-form-urlencoded";
    private static final String CHARSET = "utf-8";

    private Map<String, String> encodeBodys = new HashMap<>();

    /**
     * 表单提交 使用url encoded编码
     */

    public String contentType() {
        return CONTENT_TYPE;
    }

    public long contentLength() {
        return body().getBytes().length;
    }

    public String body() {
        StringBuffer sb = new StringBuffer();
        for(Map.Entry<String, String> entry : encodeBodys.entrySet()) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        if(sb.length() != 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    public RequestBody add(String name, String value) {
        try {
            encodeBodys.put(URLEncoder.encode(name, CHARSET),
                    URLEncoder.encode(value,CHARSET));
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return this;
    }

}
