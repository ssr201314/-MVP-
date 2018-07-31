package com.lind.mvp_forssr;

import android.os.Bundle;

import com.lind.mvp_forssr.http.HttpUtils;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements PViews {
HttpUtils mHttpUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHttpUtils=new HttpUtils();
        mHttpUtils.getDateFromService(this, service.getSheng(), 0);
        mHttpUtils.getDateFromService(this, service.changeaddr(1), 1);
    }

    @Override
    public void onLoadOk(Response<ResponseBody> response, int requestCode) {
        if (requestCode==0){

        }else if (requestCode==1){

        }

    }

    @Override
    public void onError(Exception e, int requestCode) {

    }

    @Override
    public void onFaild(Throwable t, int requestCode) {

    }
}
