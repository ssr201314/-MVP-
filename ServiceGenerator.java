package com.lind.mvp_forssr.http;

import android.content.Context;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl("")
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass, final Context context) {
        Retrofit retrofit = builder.client(
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                       // PrefBase pref = new PrefBase(context);


                        Request original = chain.request();
                        Request request = original.newBuilder()
                                //.header("User-Agent", "okhttp/3.3.0")
                                .header("Accept", "application/json")
                                .method(original.method(), original.body())
                                .build();

                        return chain.proceed(request);
                    }
                })
                        .connectTimeout(2, TimeUnit.MINUTES)
                        .readTimeout(2, TimeUnit.MINUTES)
                        .writeTimeout(2, TimeUnit.MINUTES)
                        .build()
        ).build();
        return retrofit.create(serviceClass);
    }
}