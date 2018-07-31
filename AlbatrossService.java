package com.lind.mvp_forssr.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface AlbatrossService {
    //*request change addr*/
    @PUT("v2/beanpop-event-winnings/{seq}/request")
    Call<ResponseBody> changeaddr(@Path("seq") int seq);
    /*get china provinces*/
    @GET("countries/1/provinces")
    Call<ResponseBody> getSheng();

}

