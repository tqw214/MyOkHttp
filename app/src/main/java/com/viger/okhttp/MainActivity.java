package com.viger.okhttp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.viger.myokhttp.Call;
import com.viger.myokhttp.Callback;
import com.viger.myokhttp.Interceptor;
import com.viger.myokhttp.InterceptorChain;
import com.viger.myokhttp.OkHttpClient;
import com.viger.myokhttp.Request;
import com.viger.myokhttp.Response;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyInterceptor myInterceptor = new MyInterceptor();
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                        .retrys(3)
                                        .addInterceptor(myInterceptor)
                                        .build();
        String url = "http://www.baidu.com";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback(){

            @Override
            public void onFailure(Call call, Throwable throwable) {

            }

            @Override
            public void onResponse(Call call, Response response) {

            }
        });

    }

    class MyInterceptor implements Interceptor {
        @Override
        public Response intercept(InterceptorChain chain) throws IOException {

            return null;
        }
    }

}
