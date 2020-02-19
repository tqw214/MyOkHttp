package com.viger.myokhttp;

import java.io.IOException;

public interface Interceptor {
    Response intercept(InterceptorChain chain) throws IOException;
}
