package com.gawilive.main.activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.CommonAppContext;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.event.UpdateFieldEvent;
import com.gawilive.common.glide.GlideEngine;
import com.gawilive.common.glide.ImageFileCompressEngine;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.ActivityResultCallback;
import com.gawilive.common.interfaces.CommonCallback;
import com.gawilive.common.interfaces.ImageResultCallback;
import com.gawilive.common.upload.UploadBean;
import com.gawilive.common.upload.UploadCallback;
import com.gawilive.common.upload.UploadStrategy;
import com.gawilive.common.upload.UploadUtil;
import com.gawilive.common.utils.*;
import com.gawilive.main.R;
import com.gawilive.main.http.MainHttpConsts;
import com.gawilive.main.http.MainHttpUtil;

import com.gawilive.mall.activity.AreaSelectActivity;
import com.gawilive.mall.bean.SelectAreaModel;
import com.google.gson.Gson;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;

/**
 * Created by cxf on 2018/9/29.
 * 我的 编辑资料
 */

public class EditProfileActivity extends AbsActivity {

    private ImageView mAvatar;
    private TextView mName;
    private TextView mSign;
    private TextView mBirthday;
    private TextView mSex;
    private TextView mCity;
    private UserBean mUserBean;
    private String mProvinceVal;
    private String mCityVal;
    private String mZoneVal;
    private final ImageResultCallback mImageResultCallback = new ImageResultCallback() {
        @Override
        public void beforeCamera() {

        }

        @Override
        public void onSuccess(final File file) {
            if (file != null) {

            }
        }

        @Override
        public void onFailure() {
        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_profile;
    }

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.edit_profile));
        mAvatar = findViewById(R.id.avatar);
        mName = findViewById(R.id.name);
        mSign = findViewById(R.id.sign);
        mBirthday = findViewById(R.id.birthday);
        mSex = findViewById(R.id.sex);
        mCity = findViewById(R.id.city);
        mUserBean = CommonAppConfig.getInstance().getUserBean();
        if (mUserBean != null) {
            showData(mUserBean);
        } else {
            MainHttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                @Override
                public void callback(UserBean u) {
                    mUserBean = u;
                    showData(u);
                }
            });
        }
    }


    public void editProfileClick(View v) {
        if (!canClick()) {
            return;
        }
        int i = v.getId();
        if (i == R.id.btn_avatar) {
            editAvatar();

        } else if (i == R.id.btn_name) {
            forwardName();

        } else if (i == R.id.btn_sign) {
            forwardSign();

        } else if (i == R.id.btn_birthday) {
            editBirthday();

        } else if (i == R.id.btn_sex) {
            forwardSex();

        } else if (i == R.id.btn_city) {
            chooseCity();
        }
    }

    private void editAvatar() {
        chooseImage();
    }

    // 选择图片
    private void chooseImage() {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setCompressEngine(new ImageFileCompressEngine())
                .setImageEngine(GlideEngine.createGlideEngine())
                .setMaxSelectNum(1)
                .setMinSelectNum(1) // Set minimum selection quantity
                .setImageSpanCount(4) //Set the number of images displayed in each row
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        for (LocalMedia localMedia : result) {
                            String filePath = localMedia.getAvailablePath();
                            if (PictureMimeType.isContent(filePath)) {
                                if (localMedia.isCut() || localMedia.isCompressed()) {
                                    modifyUserHead(new File(filePath));
                                } else {
                                    Uri uri = Uri.parse(filePath);
                                    String path =
                                            ImageUriPathUtil.getPathFromUri(CommonAppContext.getInstance(), uri);
                                    modifyUserHead(new File(path));
                                }
                            }
                        }

                    }

                    @Override
                    public void onCancel() {

                    }
                });
    }

    private void modifyUserHead(File file) {
        ImgLoader.display(mContext, file, mAvatar);
        UploadUtil.startUpload(new CommonCallback<UploadStrategy>() {
            @Override
            public void callback(UploadStrategy strategy) {
                List<UploadBean> list = new ArrayList<>();
                list.add(new UploadBean(file, UploadBean.IMG));
                strategy.upload(list, true, new UploadCallback() {
                    @Override
                    public void onFinish(List<UploadBean> list, boolean success) {
                        if (success) {
                            MainHttpUtil.updateAvatar(list.get(0).getRemoteFileName(), new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 0 && info.length > 0) {
                                        ToastUtil.show(R.string.edit_profile_update_avatar_success);
                                        UserBean bean = CommonAppConfig.getInstance().getUserBean();
                                        if (bean != null) {
                                            JSONObject obj = JSON.parseObject(info[0]);
                                            bean.setAvatar(obj.getString("avatar"));
                                            bean.setAvatarThumb(obj.getString("avatarThumb"));
                                        }
                                        EventBus.getDefault().post(new UpdateFieldEvent());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    private void forwardName() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditNameActivity.class);
        intent.putExtra(Constants.NICK_NAME, mUserBean.getUserNiceName());
        ActivityResultUtil.startActivityForResult(this, intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String name = intent.getStringExtra(Constants.NICK_NAME);
                    mUserBean.setUserNiceName(name);
                    mName.setText(name);
                    EventBus.getDefault().post(new UpdateFieldEvent());
                }
            }
        });
    }


    private void forwardSign() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditSignActivity.class);
        intent.putExtra(Constants.SIGN, mUserBean.getSignature());
        ActivityResultUtil.startActivityForResult(this, intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String sign = intent.getStringExtra(Constants.SIGN);
                    mUserBean.setSignature(sign);
                    mSign.setText(sign);
                    EventBus.getDefault().post(new UpdateFieldEvent());
                }
            }

        });
    }

    private void editBirthday() {
        if (mUserBean == null) {
            return;
        }
        DialogUitl.showDatePickerDialog(mContext, new DialogUitl.DataPickerCallback() {
            @Override
            public void onConfirmClick(final String date) {
                MainHttpUtil.updateFields("{\"birthday\":\"" + date + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            if (info.length > 0) {
                                ToastUtil.show(JSON.parseObject(info[0]).getString("msg"));
                                mUserBean.setBirthday(date);
                                mBirthday.setText(date);
                                EventBus.getDefault().post(new UpdateFieldEvent());
                            }
                        } else {
                            ToastUtil.show(msg);
                        }
                    }
                });
            }
        });
    }

    private void forwardSex() {
        if (mUserBean == null) {
            return;
        }
        Intent intent = new Intent(mContext, EditSexActivity.class);
        intent.putExtra(Constants.SEX, mUserBean.getSex());
        ActivityResultUtil.startActivityForResult(this, intent, new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    int sex = intent.getIntExtra(Constants.SEX, 0);
                    if (sex == 1) {
                        mSex.setText(R.string.sex_male);
                        mUserBean.setSex(sex);
                    } else if (sex == 2) {
                        mSex.setText(R.string.sex_female);
                        mUserBean.setSex(sex);
                    }
                    EventBus.getDefault().post(new UpdateFieldEvent());
                }
            }

        });
    }


    @Override
    protected void onDestroy() {
        UploadUtil.cancelUpload();
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_AVATAR);
        MainHttpUtil.cancel(MainHttpConsts.UPDATE_FIELDS);
        super.onDestroy();
    }

    private void showData(UserBean u) {
        ImgLoader.displayAvatar(mContext, u.getAvatar(), mAvatar);
        mName.setText(u.getUserNiceName());
        mSign.setText(u.getSignature());
        mBirthday.setText(u.getBirthday());
        mSex.setText(u.getSex() == 1 ? R.string.sex_male : R.string.sex_female);
        mCity.setText(u.getLocation());
    }

    private String selectResult = "";

    /**
     * 选择城市
     */
    private void chooseCity() {
        AreaSelectActivity.start(this,selectResult);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 999) {
            selectResult = data.getStringExtra("selectResult");
            SelectAreaModel model = new Gson().fromJson(selectResult, SelectAreaModel.class);
            mProvinceVal = model.getProvince();
            mCityVal = model.getCity();
            mZoneVal = model.getArea();
            final String location = StringUtil.contact(mProvinceVal, mCityVal, mZoneVal);
            mCity.setText(location);
            MainHttpUtil.updateFields("{\"location\":\"" + location + "\"}", new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        JSONObject obj = JSON.parseObject(info[0]);
                        if (info.length > 0) {
                            UserBean u = CommonAppConfig.getInstance().getUserBean();
                            if (u != null) {
                                u.setLocation(location);
                            }
                            EventBus.getDefault().post(new UpdateFieldEvent());
                        }
                        ToastUtil.show(obj.getString("msg"));
                    }
                }
            });
        }
    }


    /**
     * 选择城市
     */
    private void showChooseCityDialog(ArrayList<Province> list) {
        String province = mProvinceVal;
        String city = mCityVal;
        String district = mZoneVal;
        if (TextUtils.isEmpty(province)) {
            province = CommonAppConfig.getInstance().getProvince();
        }
        if (TextUtils.isEmpty(city)) {
            city = CommonAppConfig.getInstance().getCity();
        }
        if (TextUtils.isEmpty(district)) {
            district = CommonAppConfig.getInstance().getDistrict();
        }
        DialogUitl.showCityChooseDialog(this, list, province, city, district, new AddressPicker.OnAddressPickListener() {
            @Override
            public void onAddressPicked(Province province, final City city, County county) {
                String provinceName = province.getAreaName();
                String cityName = city.getAreaName();
                String zoneName = county.getAreaName();
                mProvinceVal = provinceName;
                mCityVal = cityName;
                mZoneVal = zoneName;
                final String location = StringUtil.contact(mProvinceVal, mCityVal, mZoneVal);
                if (mCity != null) {
                    mCity.setText(location);
                }

                MainHttpUtil.updateFields("{\"location\":\"" + location + "\"}", new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0) {
                            JSONObject obj = JSON.parseObject(info[0]);
                            if (info.length > 0) {
                                UserBean u = CommonAppConfig.getInstance().getUserBean();
                                if (u != null) {
                                    u.setLocation(location);
                                }
                                EventBus.getDefault().post(new UpdateFieldEvent());
                            }
                            ToastUtil.show(obj.getString("msg"));
                        }
                    }
                });


            }
        });
    }

}
