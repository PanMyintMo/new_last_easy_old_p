
package com.gawilive.main.activity;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.Constants;
import com.gawilive.common.HtmlConfig;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.activity.WebViewActivity;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.event.LoginChangeEvent;
import com.gawilive.common.http.CommonHttpConsts;
import com.gawilive.common.http.CommonHttpUtil;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.http.JsonBean;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.mob.LoginData;
import com.gawilive.common.mob.MobBean;
import com.gawilive.common.mob.MobCallback;
import com.gawilive.common.mob.MobLoginUtil;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.L;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.main.R;
import com.gawilive.main.adapter.LoginTypeAdapter;
import com.gawilive.main.dialog.LoginForbiddenDialogFragment;
import com.gawilive.main.event.RegSuccessEvent;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;


/**
 * Created by cxf on 2018/9/17.
 */
public class LoginActivity extends AbsActivity implements OnItemClickListener<MobBean> {

    private View mRoot;
    private ImageView mBg, google, apple, facebook;
    private ObjectAnimator mAnimator;
    private EditText mEditPhone;
    private EditText mEditPwd;
    private View mBtnLogin;
    private RecyclerView mRecyclerView;
    private MobLoginUtil mLoginUtil;
    private boolean mFirstLogin;//是否是第一次登录
    private String mLoginType = Constants.MOB_PHONE;//登录方式
    private TextView mCountryCode;
    private boolean mUseCountryCode;//是否能选择国家代号
    private ImageView mLoginCheckBox;
    private boolean mChecked;
    private Drawable mCheckedDrawable;
    private Drawable mUnCheckedDrawable;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    CallbackManager callbackManager;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return true;
    }

    @Override
    protected void main() {
        mRoot = findViewById(R.id.root);
        mCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_login_check_1);
        mUnCheckedDrawable = ContextCompat.getDrawable(mContext, R.mipmap.bg_login_check_0);
        mLoginCheckBox = findViewById(R.id.btn_login_check);
        mLoginCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChecked = !mChecked;
                mLoginCheckBox.setImageDrawable(mChecked ? mCheckedDrawable : mUnCheckedDrawable);
            }
        });
        mBg = findViewById(R.id.bg);
        mBg.post(new Runnable() {
            @Override
            public void run() {
                if (mBg != null && mRoot != null) {
                    int bgHeight = mBg.getHeight();
                    int rootHeight = mRoot.getHeight();
                    int dy = bgHeight - rootHeight;
                    if (dy > 0) {
                        mAnimator = ObjectAnimator.ofFloat(mBg, "translationY", 0, -dy);
                        mAnimator.setInterpolator(new LinearInterpolator());
                        mAnimator.setDuration(3000);
                        mAnimator.setRepeatCount(-1);
                        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
                        mAnimator.start();
                    }
                }
            }
        });
        mCountryCode = findViewById(R.id.country_code);
        mEditPhone = findViewById(R.id.edit_phone);
        mEditPwd = findViewById(R.id.edit_pwd);
        google = findViewById(R.id.google);
        facebook = findViewById(R.id.facebook);
        apple = findViewById(R.id.apple);
        mBtnLogin = findViewById(R.id.btn_login);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = mEditPhone.getText().toString();
                String pwd = mEditPwd.getText().toString();
                mBtnLogin.setEnabled(!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(pwd));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
        mEditPhone.addTextChangedListener(textWatcher);
        mEditPwd.addTextChangedListener(textWatcher);
        String tip = getIntent().getStringExtra(Constants.TIP);
        if (!TextUtils.isEmpty(tip)) {
            DialogUitl.showSimpleTipDialog(mContext, tip);
        }


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken("604315513301-g7hoq005a6rus6ce4hrmqt1d2pjbgai6.apps.googleusercontent.com")
                .requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        L.e("hello","success");
                        L.e("hello",loginResult.toString());

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        L.e("hello","cancel");

                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        L.e("hello","error");
                        L.e("hello",exception.toString());

                    }
                });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByGoogle();
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInByFaceBook();
            }
        });

        EventBus.getDefault().register(this);
        MainHttpUtil.getLoginInfo(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    mUseCountryCode = obj.getIntValue("sendcode_type") == 1;
                    String[] loginTypeArray = JSON.parseObject(obj.getString("login_type"), String[].class);
                    if (loginTypeArray != null && loginTypeArray.length > 0) {
                        List<MobBean> list = MobBean.getLoginTypeList(loginTypeArray);


                        View otherLoginTip = findViewById(R.id.other_login_tip);
                        if (otherLoginTip != null) {
                            otherLoginTip.setVisibility(View.VISIBLE);
                        }


                        mRecyclerView = findViewById(R.id.recyclerView);
                        mRecyclerView.setHasFixedSize(true);
                        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
                        LoginTypeAdapter adapter = new LoginTypeAdapter(mContext, list);
                        adapter.setOnItemClickListener(LoginActivity.this);
                        mRecyclerView.setAdapter(adapter);
                        mLoginUtil = new MobLoginUtil();
                    }
                    TextView loginTipTextView = findViewById(R.id.login_tip);
                    if (loginTipTextView != null) {
                        JSONObject loginInfo = obj.getJSONObject("login_alert");
                        String loginTip = loginInfo.getString("login_title");
                        if (TextUtils.isEmpty(loginTip)) {
                            return;
                        }
                        SpannableString spannableString = new SpannableString(loginTip);
                        JSONArray msgArray = JSON.parseArray(loginInfo.getString("message"));
                        for (int i = 0, size = msgArray.size(); i < size; i++) {
                            final JSONObject msgItem = msgArray.getJSONObject(i);
                            String title = msgItem.getString("title");
                            int startIndex = loginTip.indexOf(title);
                            if (startIndex >= 0) {
                                ClickableSpan clickableSpan = new ClickableSpan() {

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        super.updateDrawState(ds);
                                        ds.setColor(0xff3399ee);
                                        ds.setUnderlineText(false);
                                    }

                                    @Override
                                    public void onClick(View widget) {
                                        WebViewActivity.forward(mContext, "https://yszc.ezwel.live/");
                                    }
                                };
                                int endIndex = startIndex + title.length();
                                spannableString.setSpan(clickableSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                        }
                        loginTipTextView.setText(spannableString);
                        loginTipTextView.setMovementMethod(LinkMovementMethod.getInstance());//不设置 没有点击事件
                        loginTipTextView.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明
                    }
                }
            }

        });
    }

    private void signInByGoogle() {
        Intent signIntent = gsc.getSignInIntent();

        startActivityForResult(signIntent, 1000);

    }
    private void signInByFaceBook() {
        LoginManager.getInstance().logOut();
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this,Arrays.asList("email","public_profile"));

    }

    public void loginClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_login) {
            login();


        } else if (i == R.id.btn_register) {
            register();

        } else if (i == R.id.btn_forget_pwd) {
            forgetPwd();
        } else if (i == R.id.btn_choose_country) {
            chooseCountryCode();
        }
    }

    //注册
    private void register() {
        Intent intent = new Intent(mContext, RegisterActivity.class);
        intent.putExtra(Constants.TIP, mUseCountryCode);
        startActivity(intent);
    }

    //忘记密码
    private void forgetPwd() {
        Intent intent = new Intent(mContext, FindPwdActivity.class);
        intent.putExtra(Constants.FROM_LOGIN, true);
        intent.putExtra(Constants.TIP, mUseCountryCode);
        startActivity(intent);
    }

    //选择国家代号
    private void chooseCountryCode() {
        if (mUseCountryCode) {
            // startActivityForResult(new Intent(mContext, ChooseCountryActivity.class), 101);
        }
    }


    //手机号密码登录
    private void login() {
        if (!mChecked) {
            ToastUtil.show(R.string.login_check_tip);
            return;
        }
        String phoneNum = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.show(R.string.login_input_phone);
            mEditPhone.requestFocus();
            return;
        }
//        if (!ValidatePhoneUtil.validateMobileNumber(phoneNum)) {
//            mEditPhone.setError(WordUtil.getString(R.string.login_phone_error));
//            mEditPhone.requestFocus();
//            return;
//        }
        String pwd = mEditPwd.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            ToastUtil.show(R.string.login_input_pwd);
            mEditPwd.requestFocus();
            return;
        }
        mLoginType = Constants.MOB_PHONE;
        String countryCode = mCountryCode.getText().toString();
        if (!TextUtils.isEmpty(countryCode) && countryCode.startsWith("+")) {
            countryCode = countryCode.substring(1);
        }
        MainHttpUtil.login(phoneNum, pwd, countryCode, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }


        });
    }

    //登录即代表同意服务和隐私条款
    private void forwardTip() {
        WebViewActivity.forward(mContext, HtmlConfig.LOGIN_PRIVCAY);
    }

    //登录成功！
    private void onLoginSuccess(int code, String msg, String[] info) {
        if (code == 0) {
            // Log.d("LOGBYGOOGLE", "<====== Code is ===> " + code);
            if (info.length > 0) {
                //   Log.d("LOGBYGOOGLE", "<====== info length is ===>" + info.length);
                JSONObject obj = JSON.parseObject(info[0]);
                String uid = obj.getString("id");
                String token = obj.getString("token");
                mFirstLogin = obj.getIntValue("isreg") == 1;
                CommonAppConfig.getInstance().setLoginInfo(uid, token, true);
                getBaseUserInfo();
                //友盟统计登录
                //  MobclickAgent.onProfileSignIn(mLoginType, uid);

                // 登录腾讯云
                CommonAppConfig.loginIm(this, uid);
            }
        } else if (code == 1002) {
            if (info.length > 0) {
                LoginForbiddenDialogFragment fragment = new LoginForbiddenDialogFragment();
                JSONObject obj = JSON.parseObject(info[0]);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TIP, obj.getString("ban_reason"));
                bundle.putString(Constants.UID, obj.getString("ban_tip"));
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(), "LoginForbiddenDialogFragment");
            }
        } else {
            ToastUtil.show(msg);
        }
    }

    /**
     * 获取用户信息
     */
    private void getBaseUserInfo() {
        MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                if (mFirstLogin) {
                    RecommendActivity.forward(mContext, mFirstLogin);
                } else {
                    EventBus.getDefault().post(new LoginChangeEvent(true, false));
                }
                finish();
            }
        });
    }

    /**
     * 三方登录
     */
    private void loginBuyThird(LoginData data) {
        if (!mChecked) {
            ToastUtil.show(R.string.login_check_tip);
            return;
        }
        mLoginType = data.getType();
        MainHttpUtil.loginByThird(data.getOpenID(), data.getAccessToken(), data.getNickName(), data.getAvatar(), data.getType(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                onLoginSuccess(code, msg, info);
            }

        });
    }

    @Override
    public void onItemClick(MobBean bean, int position) {
        if (mLoginUtil == null) {
            return;
        }
        if (!mChecked) {
            ToastUtil.show(R.string.login_check_tip);
            return;
        }
        final Dialog dialog = DialogUitl.loginAuthDialog(mContext);
        dialog.show();
        mLoginUtil.execute(bean.getType(), new MobCallback() {
            @Override
            public void onSuccess(Object data) {
                if (data != null) {
                    loginBuyThird((LoginData) data);
                }
            }

            @Override
            public void onError() {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFinish() {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegSuccessEvent(RegSuccessEvent e) {
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mAnimator = null;
        EventBus.getDefault().unregister(this);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN);
        CommonHttpUtil.cancel(CommonHttpConsts.GET_QQ_LOGIN_UNION_ID);
        MainHttpUtil.cancel(MainHttpConsts.LOGIN_BY_THIRD);
        MainHttpUtil.cancel(MainHttpConsts.GET_BASE_INFO);
        MainHttpUtil.cancel(MainHttpConsts.GET_LOGIN_INFO);
        if (mLoginUtil != null) {
            mLoginUtil.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            String code = data.getStringExtra(Constants.TO_NAME);
            if (mCountryCode != null) {
                mCountryCode.setText(StringUtil.contact("+", code));
            }
        }

        if (requestCode == 1000) {


            GoogleSignIn.getClient(this, gso).signOut();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);

                if (account != null) {
                    // Successfully signed in
                    String idToken = account.getIdToken();
                    // Log.d("TOKEN", "ID Token: " + idToken);
                    sendIdTokenToServer(idToken);
                }
            } catch (ApiException e) {
//                Log.e("TOKEN", "Error Code: " + e.getStatusCode());
//                Log.e("TOKEN", "Error Message: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Google Login Failed.", Toast.LENGTH_SHORT).show();
                // e.printStackTrace();
            }
        }
    }


    private void sendIdTokenToServer(String idToken) {
        if (idToken == null || idToken.isEmpty()) {
            Toast.makeText(this, "Failed to get ID token.", Toast.LENGTH_SHORT).show();
            return;
        }

        //Log.d("TOKEN", "Sending ID token to server: " + idToken);

        MainHttpUtil.loginByGoogle("google",idToken, new HttpCallback() {

            @Override
            public void onSuccess(int code, String msg, String[] info) {
                // Toast.makeText(mContext, "Navigating to MainActivity.", Toast.LENGTH_SHORT).show();
                onLoginSuccess(code, msg, info);
            }


            @Override
            public void onError(Response<JsonBean> response) {
                //     Log.e("TOKEN", "Error Response: Code=" + response.code() + ", Message=" + response.message());
                if (response.getException() != null) {
                    //   Log.e("TOKEN", "Exception: ", response.getException());
                }
            }
        });
    }

}