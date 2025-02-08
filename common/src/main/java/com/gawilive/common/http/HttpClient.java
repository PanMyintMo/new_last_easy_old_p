package com.gawilive.common.http;

import android.util.Log;

import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.CommonAppContext;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

/**
 * Created by cxf on 2018/9/17.
 */

public class HttpClient {

    private static HttpClient sInstance;
    private String mLanguage;//语言
    private final String mUrl;
    private final OkHttpClient mOkHttpClient;
    private final OkHttpBuilder mBuilder;

    private HttpClient() {
        mUrl = CommonAppConfig.HOST + "/appapi/?service=";
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        mBuilder = new OkHttpBuilder() {
            @Override
            public void setParams(OkHttpClient.Builder builder) {
//                builder.proxy(Proxy.NO_PROXY);//防止抓包
                HttpsUtils.SSLParams sslParams1 = HttpsUtils.getSslSocketFactory();
                builder.sslSocketFactory(sslParams1.sSLSocketFactory, sslParams1.trustManager);
                builder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
//                        if ("data.facegl.com".equals(hostname)) {
//                            return true;
//                        }
                        return true;
                    }
                });
            }
        };
        mOkHttpClient = mBuilder
                .setHost(CommonAppConfig.HOST)
                .setTimeout(10000)
                .setLoggingInterceptor(loggingInterceptor)
                .build(CommonAppContext.getInstance());
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }


    public GetRequest<JsonBean> get(String serviceName, String tag) {
        return mBuilder.req1(mUrl + serviceName, tag, JsonBean.class)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);

    }

    public PostRequest<JsonBean> post(String serviceName, String tag) {

        Log.d("URL", "Making POST request to: "+mUrl + serviceName + tag);
        return mBuilder.req2(mUrl + serviceName, tag, JsonBean.class)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);
    }


    public PostRequest<JsonBean> postRequest(String serviceName, String tag) {
        // Set the correct URL directly for the backend endpoint
        String url = "https://ezwel.live/" + serviceName;  // serviceName should be "google/login"

        // Check if the URL is correct (log or print it)
        //Log.d("URL", "Making POST request to: " + url);

        // Make the POST request with the ID token parameter
        return mBuilder.req2(url, tag, JsonBean.class)
                .params(CommonHttpConsts.ID_TOKEN, CommonHttpConsts.ID_TOKEN); // Add ID token as parameter
    }


    public void cancel(String tag) {
        mBuilder.cancel(mOkHttpClient, tag);
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }
    public String getLanguage() {
        return mLanguage;
    }

}
