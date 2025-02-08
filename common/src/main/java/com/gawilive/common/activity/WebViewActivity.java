package com.gawilive.common.activity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.RequiresApi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.R;
import com.gawilive.common.http.HttpClient;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.utils.*;
import okhttp3.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by cxf on 2018/9/25.
 */

public class WebViewActivity extends AbsActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private final int CHOOSE = 100;//Android 5.0以下的
    private final int CHOOSE_ANDROID_5 = 200;//Android 5.0以上的
    private ValueCallback<Uri> mValueCallback;
    private ValueCallback<Uri[]> mValueCallback2;
    private final int CODE_AUTH = 8001;//我的认证
    private final int CODE_FAMILY = 8002;//我的家族

    private TextView saveImage;

    private static boolean isSaveImg = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void main() {
        String url = getIntent().getStringExtra(Constants.URL);
       // L.e("H5--->" + url);
        LinearLayout rootView = findViewById(R.id.rootView);
        mProgressBar = findViewById(R.id.progressbar);
        saveImage = findViewById(R.id.tvSaveImage);
        mWebView = findViewById(R.id.webView);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        params.topMargin = DpUtil.dp2px(1);
//        mWebView.setLayoutParams(params);
//        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
//        rootView.addView(mWebView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
              //  L.e("H5-------->" + url);
                if (url.startsWith(Constants.COPY_PREFIX)) {
                    String content = url.substring(Constants.COPY_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        copy(content);
                    }
                } else if (url.startsWith(Constants.TEL_PREFIX)) {
                    String phoneNum = url.substring(Constants.TEL_PREFIX.length());
                    if (!TextUtils.isEmpty(phoneNum)) {
                        callPhone(phoneNum);
                    }
                } else if (url.startsWith(Constants.AUTH_PREFIX)) {
                    RouteUtil.forwardAuth(WebViewActivity.this, CODE_AUTH);
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                    if (isSaveImg) {
                        saveImage.setVisibility(View.VISIBLE);
                    }
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }

            //The following is how WebView calls the file picker in various Android versions
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                openImageChooserActivity(valueCallback);
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                openImageChooserActivity(valueCallback);
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback,
                                        String acceptType, String capture) {
                openImageChooserActivity(valueCallback);
            }

            // For Android >= 5.0
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                mValueCallback2 = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, CHOOSE_ANDROID_5);
                return true;
            }

        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.loadUrl(url);

        // Call js method
        mWebView.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public void queryOrder(String orderNo) {
                getOrder(orderNo);
            }

            @JavascriptInterface
            public void escPay() {
                finish();
            }


        }, "AndroidBridge");

        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = getViewScreenshot(mWebView);
                saveImage(bitmap);
            }
        });
    }


    private void saveImage(Bitmap bitmap) {
        PermissionUtil.request(this, new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        QRCodeUtils.saveImageToGallery(WebViewActivity.this, bitmap, "payCode.jpg");
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                },   Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static Bitmap getViewScreenshot(View view) {
        // Creates an empty Bitmap of the same size as the given View
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //Use Canvas to draw Bitmap
        Canvas canvas = new Canvas(bitmap);
        // Draw View on Canvas
        view.draw(canvas);
        return bitmap;
    }

    private void getOrder(String orderNo) {
        Map<String, String> map = new HashMap<>();
        map.put("order_sn", orderNo);
        OkHttp.postAsync(CommonAppConfig.HOST2 + "?act=find_order", map, new OkHttp.DataCallBack() {
            @Override
            public void requestSuccess(String result) throws Exception {
                JsonObject object = new JsonParser().parse(result).getAsJsonObject();
                int code = object.get("code").getAsInt();
                if (code == 0) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.TransferSuccess");
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.putExtra("title", "充值成功");
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void requestFailure(Request request, IOException e) {

            }
        });
    }

    private void openImageChooserActivity(ValueCallback<Uri> valueCallback) {
        mValueCallback = valueCallback;
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT < 19) {
            intent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            intent.setAction(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, WordUtil.getString(R.string.choose_flie)), CHOOSE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case CHOOSE://5.0以下选择图片后的回调
                processResult(resultCode, intent);
                break;
            case CHOOSE_ANDROID_5://5.0以上选择图片后的回调
                processResultAndroid5(resultCode, intent);
                break;
            case CODE_AUTH://我的认证
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
            case CODE_FAMILY://我的家族
                if (resultCode == RESULT_OK) {
                    finish();
                }
                break;
        }
    }

    private void processResult(int resultCode, Intent intent) {
        if (mValueCallback == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            Uri result = intent.getData();
            mValueCallback.onReceiveValue(result);
        } else {
            mValueCallback.onReceiveValue(null);
        }
        mValueCallback = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processResultAndroid5(int resultCode, Intent intent) {
        if (mValueCallback2 == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            mValueCallback2.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
        } else {
            mValueCallback2.onReceiveValue(null);
        }
        mValueCallback2 = null;
    }

    protected boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    @Override
    public void onBackPressed() {
        if (isNeedExitActivity()) {
            finish();
        } else {
            if (canGoBack()) {
                mWebView.goBack();
            } else {
                finish();
            }
        }
    }


    private boolean isNeedExitActivity() {
        if (mWebView != null) {
            String url = mWebView.getUrl();
            if (!TextUtils.isEmpty(url)) {
                return url.contains("appapi/Auth/succ");//身份认证成功页面

            }
        }
        return false;
    }

    public static void forward(Context context, String url, boolean addArgs) {
        if (addArgs) {
            if (!url.contains("?")) {
                url = StringUtil.contact(url, "?");
            }
            url = StringUtil.contact(url, "&uid=", CommonAppConfig.getInstance().getUid(), "&token=", CommonAppConfig.getInstance().getToken(),
                    "&lang=", HttpClient.getInstance().getLanguage());
        }
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void loadingPayCode(Context context, String url, boolean addArgs, boolean mIsSaveImg) {
        if (addArgs) {
            if (!url.contains("?")) {
                url = StringUtil.contact(url, "?");
            }
            url = StringUtil.contact(url, "&uid=", CommonAppConfig.getInstance().getUid(), "&token=", CommonAppConfig.getInstance().getToken(),
                    "&lang=", HttpClient.getInstance().getLanguage());
        }
        isSaveImg = mIsSaveImg;
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void forward(Context context, String url) {
        forward(context, url, true);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.destroy();
        }
        super.onDestroy();
    }

    /**
     * copy to clipboard
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }

    /**
     * Make a call
     */
    private void callPhone(String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        startActivity(intent);
    }
}
