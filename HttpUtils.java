package com.lind.mvp_forssr.http;

import android.annotation.SuppressLint;
import android.util.Log;

import com.lind.mvp_forssr.PViews;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/7/19.
 * Author Administrator
 * Mail sgu_lind@163.com
 */

public class HttpUtils {
    //      AlbatrossService service = ServiceGenerator.createService(AlbatrossService.class, getApplicationContext());
    Call<ResponseBody> call;

    @SuppressLint("LongLogTag")
    public void getDateFromService(final PViews pViews, Call<ResponseBody> call, final int requestCode) {
        this.call = call;
        try {

            call.enqueue(new Callback<ResponseBody>() {
                @SuppressLint("LongLogTag")
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    // ResponseBody rstemp = response.body().string();
                    //  pViews.onLoadOk(response, requestCode);
                    if (response.code() == 200 && response.body() != null) {
                        try {
                            Log.d(pViews.getClass().getSimpleName() + "----OK:http_ssr", showLog(response));
                        } catch (Exception e) {
                            Log.d(pViews.getClass().getSimpleName() + "----Exception:http_ssr", e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        if (response.message() != null) {
                            Log.d(pViews.getClass().getSimpleName() + "----OK:http_ssr", response.code() + "---" + response.message());
                        }
                    }
                    pViews.onLoadOk(response, requestCode);
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d(pViews.getClass().getSimpleName() + "----Fail:http_ssr", t.getMessage());
                    pViews.onFaild(t, requestCode);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.d(pViews.getClass().getSimpleName() + "-----Exception:http_ssr", e.getMessage());
            pViews.onError(e, requestCode);
            e.printStackTrace();
        }
    }

    //cancel httprequest,release Resources
    public void cancel() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }

    //print service response string
    private String showLog(Response<ResponseBody> response) {
        ResponseBody responseBody = response.body();
        BufferedSource source = responseBody.source();
        try {
            source.request(Long.MAX_VALUE); // request the entire body.
        } catch (IOException e) {
            e.printStackTrace();
        }
        Buffer buffer = source.buffer();
        // clone buffer before reading from it
        String responseBodyString = buffer.clone().readString(Charset.forName("UTF-8"));
        return responseBodyString;
    }

    public void onCreate() {

    }


    public void onStart() {

    }


    public void onResume() {

    }


    public void onPause() {

    }


    public void onStop() {

    }

    public void onDestroy() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
}
