# -MVP-
持续优化代码量少,精简,可复用的mvp


1:通用的View层(和主流的一样)

我的做法是在activity回调里解析成javabean,可以搭配dangger2等注解框架灵活应用.回调方法有三个,成功,失败,异常.

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/7/19.
 * Author Administrator
 * Mail sgu_lind@163.com
 */

public interface PViews {
    public void onLoadOk( Response<ResponseBody> response,int requestCode) ;
    public void onError(Exception e,int requestCode);
    public void onFaild(Throwable t,int requestCode);
}

2:p层(可复用,可以与activity/fragment生命周期绑定,可以取消请求,call对象引用)
   与网上别人分享的封装方式有些不同,这种封装方式不用写那么多的p层,全局一个就够了,一个界面可以允许无限多个请求,通过请求码和响应码区分,不会发生耦合.
极大的减少了代码量.以往的mvp封装方式几乎每个界面都得对应一个p层,使得mvp只适合团队开发,不适合个人.但是我这种封装方式适合团队和个人,具有mvc的便捷,易用性,
还保持了mvp的逻辑性清晰,易于维护的特点.还有好的一点是可以在adapt中使用,甚至还可以搭配Rxjava使用.
    我这里是通过自己调用方法与activiy生命周期进行绑定,也可以通过lifecycle进行生命周期绑定.

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

import cn.beanpop.userapp.model.PViews;
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
                    pViews.onLoadOk(response, requestCode);
                    if (response.code() == 200 && response.body() != null) {
                        try {
                            Log.d(pViews.getClass().getSimpleName() + "----OK:http_ssr", response.toString());
                        } catch (Exception e) {
                            Log.d(pViews.getClass().getSimpleName() + "----Exception:http_ssr", e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        if (response.message() != null) {
                            Log.d(pViews.getClass().getSimpleName() + "----OK:http_ssr", response.code() + "---" + response.message());
                        }
                    }
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

3:data处理(根据个人需求进行配置)

public class ServiceGenerator {

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(AppConfig.API_SERVER)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass, final Context context) {
        Retrofit retrofit = builder.client(
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        PrefBase pref = new PrefBase(context);

                        // tempcode 로그인 적용한 후에 밑에 두줄 삭제해야 함
                        // pref.put(PrefBase.ACCESS_TOKEN, "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjVkNDhlMWEyMWQ1NDExNDg3OTA0NzI0NTRkM2U3NWFmODExMDc3ZWRkY2NlZWVlMmFmM2YwZmRmMTE4YzY3ZjAzNzM0ZWRjOTA5MTJlZjg3In0.eyJhdWQiOiIyIiwianRpIjoiNWQ0OGUxYTIxZDU0MTE0ODc5MDQ3MjQ1NGQzZTc1YWY4MTEwNzdlZGRjY2VlZWUyYWYzZjBmZGYxMThjNjdmMDM3MzRlZGM5MDkxMmVmODciLCJpYXQiOjE1MjE1MzI3NDUsIm5iZiI6MTUyMTUzMjc0NSwiZXhwIjoxNTIxNjE5MTQ1LCJzdWIiOiIxMSIsInNjb3BlcyI6WyIqIl19.fEhUoDJ7dG1cHY3-AeWivA-b4lQLCD6rVkGiHb6kwV4M2SjTMlqTA3uFP7QUhaptGu9rM16oBG59AnYqktqKnsLA8Oe2m17serwKq0_gK4b9B_ifi9_zYogu3viNaBUy7Vr7kZEOwsays_a1e-ZoUfrusQXg6xHL0ufLXgpgNtbTaBvrITV_wB4P75k5adUVeOcf5IeF1n1JcZixhhHM0EWkZECY-xRgg5nx32VLQ2frAumgPrLcvkg3KCPAdrpszXlV8ZMvqbmWYjxxVItQAf5J9ZlcvbjuGZHqDdbmohIK5KfvDulTlt5oAmexmUZIU8H6Pkn5MHB3Grr7PuScx43ljKZOxDZfJIualzTsU07OwWqtGH93SRQ6bjc3qt8fGzrlsNat3ocwk5M213T_T7kU4QPelA6F-92QcvLLQxpvc0KCyms9DcNF0TFO8Bfdwbn9MctVa7QR_8kVz8KFDrNYjDxro6pC5pPUbRG4MC1mFXVZEPwq-x0UPZ0_A-Lx7YHpqGJbTnbFBe2wE0Z0roXZJuIm26xmtaeBxaB6yFue8lnMh73RgoLj5te-3ZzYEOMgFgV2RDAmu53YAL7qWT-WK9rN2oGPad3af84VwVKGpNuKG3W38QMU9fKAJnXDnAPL2vjL6sRKIQpcdBpccEXLZVeCqTIIQbf45kk5dXM");
                        Lggr.d("ACCESS_TOKEN: "+pref.getValue(PrefBase.ACCESS_TOKEN, ""));

                        Request original = chain.request();
                        Request request = original.newBuilder()
                                //.header("User-Agent", "okhttp/3.3.0")
                                .header("Accept", "application/json")
                                .header("Authorization", "Bearer "+pref.getValue(PrefBase.ACCESS_TOKEN, ""))
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

public interface AlbatrossService {
    //*request change addr*/
    @PUT("v2/beanpop-event-winnings/{seq}/request")
    Call<ResponseBody> changeaddr(@Path("seq") int seq);
    /*get china provinces*/
    @GET("countries/1/provinces")
    Call<ResponseBody> getSheng();
    @GET("countries/1/provinces/{s}/cities")
    Call<ResponseBody> getCity(@Path("s") int shengCody);
    @GET("countries/1/provinces/{s}/cities/{city}/areas")
    Call<ResponseBody> getAreas(@Path("s") int shengCode,@Path("city") int cityCode);

    /*set default addr*/
    @PUT("v2/user-shipping-addresses/{seq}/default")
    Call<ResponseBody> setdefaultAddr(@Path("seq") int seq);

    /*updater addr*/
    
    
    注意:service放在baseactivity/basefragment中就可以,或者在创建出来




