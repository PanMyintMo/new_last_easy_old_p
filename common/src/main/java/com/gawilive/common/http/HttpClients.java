package com.gawilive.common.http;

import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.CommonAppContext;
import okhttp3.OkHttpClient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * Created by cxf on 2018/9/17.
 */

public class HttpClients {

    private static HttpClients sInstance;
    private String mLanguage;//语言
    private final String mUrl;
    private final OkHttpClient mOkHttpClient;
    private final OkHttpBuilder mBuilder;

    private HttpClients() {
        mUrl = CommonAppConfig.HOST + "/api.php?act=";
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

    public static HttpClients getInstance() {
        if (sInstance == null) {
            synchronized (HttpClients.class) {
                if (sInstance == null) {
                    sInstance = new HttpClients();
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
        return mBuilder.req2(mUrl + serviceName, tag, JsonBean.class)
                .params(CommonHttpConsts.LANGUAGE, mLanguage);
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
