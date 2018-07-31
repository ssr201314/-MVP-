package com.lind.mvp_forssr;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/7/19.
 * Author Administrator
 * Mail sgu_lind@163.com
 */

public interface PViews {
    public void onLoadOk(Response<ResponseBody> response, int requestCode) ;
    public void onError(Exception e, int requestCode);
    public void onFaild(Throwable t, int requestCode);
}
