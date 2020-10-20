package com.example.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
        public static void sendOkHttpRequest(String address,okhttp3.Callback callback)
        {
            OkHttpClient client=new OkHttpClient(); //先创建一个OkHttpClient的实例
            Request request=new Request.Builder().url(address).build();
            client.newCall(request).enqueue(callback);
        }
}
