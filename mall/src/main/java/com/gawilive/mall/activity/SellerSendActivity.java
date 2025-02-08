package com.gawilive.mall.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.glide.ImgLoader;
import com.gawilive.common.http.HttpCallback;
import com.gawilive.common.interfaces.OnItemClickListener;
import com.gawilive.common.utils.DpUtil;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.mall.R;
import com.gawilive.mall.adapter.SellerWuliuAdapter;
import com.gawilive.mall.bean.MerchantAddressModel;
import com.gawilive.mall.bean.WuliuBean;
import com.gawilive.mall.http.MallHttpConsts;
import com.gawilive.mall.http.MallHttpUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 卖家发货
 */
public class SellerSendActivity extends AbsActivity implements View.OnClickListener, OnItemClickListener<WuliuBean>, PopupWindow.OnDismissListener {

    public static void forward(Context context, String orderId) {
        Intent intent = new Intent(context, SellerSendActivity.class);
        intent.putExtra(Constants.MALL_ORDER_ID, orderId);
        context.startActivity(intent);
    }

    private String mOrderId;
    private TextView mStatusName;
    private TextView mStatusTip;
    private TextView mOrderNo;//订单编号
    private EditText mWuliuOrderNum;//物流运单号
    private TextView mWuliuName;//物流公司名称
    private View mArrow;//箭头
    private TextView mBuyerName;
    private TextView mBuyerAddress;
    private View mGroupMsg;
    private TextView mBuyerMsg;//买家留言
    private ImageView mGoodsThumb;
    private TextView mGoodsName;
    private TextView mGoodsPrice;
    private TextView mGoodsSpecName;
    private TextView mGoodsNum;
    private TextView mPostage;
    private TextView mMoney;
    private String mMoneySymbol;
    private View mBtnWuLiuChoose;
    private PopupWindow mPopupWindow;
    private SellerWuliuAdapter mWuliuAdapter;
    private String mWuliuId;//物流公司id
    private String mAddressString;

    private TextView tvNameOrPhone, tvSendDetailsAddress, btn_modify_address;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_seller_send;
    }

    private JSONObject orderInfo;

    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.mall_233));
        mOrderId = getIntent().getStringExtra(Constants.MALL_ORDER_ID);
        mStatusName = findViewById(R.id.status_name);
        mStatusTip = findViewById(R.id.status_tip);

        mOrderNo = findViewById(R.id.order_no);//订单编号
        mWuliuOrderNum = findViewById(R.id.wuliu_order_num);//物流运单号
        mWuliuName = findViewById(R.id.wuliu_name);//物流公司名称
        mArrow = findViewById(R.id.arrow);//箭头
        mBuyerName = findViewById(R.id.buyer_name);
        mBuyerAddress = findViewById(R.id.buyer_address);
        mGroupMsg = findViewById(R.id.group_msg);
        mBuyerMsg = findViewById(R.id.buyer_msg);//买家留言
        mGoodsThumb = findViewById(R.id.goods_thumb);
        mGoodsName = findViewById(R.id.goods_name);
        mGoodsPrice = findViewById(R.id.goods_price);
        mGoodsSpecName = findViewById(R.id.goods_spec_name);
        mGoodsNum = findViewById(R.id.goods_num);
        mPostage = findViewById(R.id.postage);
        mMoney = findViewById(R.id.money);
        mMoneySymbol = WordUtil.getString(R.string.money_symbol);
        mBtnWuLiuChoose = findViewById(R.id.btn_wuliu_choose);
        tvNameOrPhone = findViewById(R.id.tvNameOrPhone);
        tvSendDetailsAddress = findViewById(R.id.tvSendDetailsAddress);
        btn_modify_address = findViewById(R.id.btn_modify_address);
        mBtnWuLiuChoose.setOnClickListener(this);
        btn_modify_address.setOnClickListener(this);

        findViewById(R.id.btn_copy_address).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        MallHttpUtil.getSellerOrderDetail(mOrderId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    orderInfo = obj.getJSONObject("order_info");
                    if (mStatusName != null) {
                        mStatusName.setText(orderInfo.getString("status_name"));
                    }
                    if (mStatusTip != null) {
                        mStatusTip.setText(orderInfo.getString("status_desc"));
                    }
                    if (mOrderNo != null) {
                        mOrderNo.setText(String.format(WordUtil.getString(R.string.mall_232), orderInfo.getString("orderno")));
                    }
                    if (mBuyerName != null) {
                        mBuyerName.setText(StringUtil.contact(orderInfo.getString("username"), " ", orderInfo.getString("phone")));
                    }
                    if (mBuyerAddress != null) {
                        mAddressString = orderInfo.getString("address_format");
                        mBuyerAddress.setText(mAddressString);
                    }
                    String msgContent = orderInfo.getString("message");
                    if (TextUtils.isEmpty(msgContent)) {
//                        if (mGroupMsg != null && mGroupMsg.getVisibility() != View.GONE) {
//                            mGroupMsg.setVisibility(View.GONE);
//                        }
                    } else {
//                        if (mGroupMsg != null && mGroupMsg.getVisibility() != View.VISIBLE) {
//                            mGroupMsg.setVisibility(View.VISIBLE);
//                        }
                        if (mBuyerMsg != null) {
                            mBuyerMsg.setText(msgContent);
                        }
                    }
                    if (mGoodsThumb != null) {
                        ImgLoader.display(mContext, orderInfo.getString("spec_thumb_format"), mGoodsThumb);
                    }
                    if (mGoodsName != null) {
                        mGoodsName.setText(orderInfo.getString("goods_name"));
                    }
                    if (mGoodsPrice != null) {
                        mGoodsPrice.setText(StringUtil.contact(mMoneySymbol, orderInfo.getString("price")));
                    }
                    if (mGoodsSpecName != null) {
                        mGoodsSpecName.setText(orderInfo.getString("spec_name"));
                    }
                    if (mGoodsNum != null) {
                        mGoodsNum.setText(StringUtil.contact("x", orderInfo.getString("nums")));
                    }
                    if (mPostage != null) {
                        mPostage.setText(StringUtil.contact(mMoneySymbol, orderInfo.getString("postage")));
                    }
                    if (mMoney != null) {
                        mMoney.setText(StringUtil.contact(mMoneySymbol, orderInfo.getString("total")));
                    }

                    // 查询商家信息
                    getShopUserInfo();
                    // 查询发货地址
                    getAddress();
                }
            }
        });


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_wuliu_choose) {
            chooseWuLiu();
        } else if (id == R.id.btn_submit) {
            if (TextUtils.isEmpty(mWuliuName.getText().toString().trim())) {
                ToastUtil.show(R.string.mall_226);
                return;
            }
            if (TextUtils.isEmpty(sendProvince)) {
                ToastUtil.show(getString(R.string.str_009));
                return;
            }

            commitOrder();
        } else if (id == R.id.btn_copy_address) {

            copyAddress();
        } else if (id == R.id.btn_modify_address) {
            SellerAddressEditNewActivity.start(this);
        }
    }

    private void getAddress() {
        MallHttpUtil.getMerchantAddress(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<MerchantAddressModel> list = JSON.parseArray(Arrays.toString(info), MerchantAddressModel.class);
                    if (list.size() > 0) {
                        btn_modify_address.setText(getString(R.string.str_008));
                        MerchantAddressModel model = list.get(0);
                        sendName = model.getUsername();
                        sendAddress = model.getAddress();
                        sendArea = model.getArea();
                        sendCity = model.getCity();
                        sendProvince = model.getProvince();
                        sendPhone = model.getPhone();
                        tvNameOrPhone.setText(model.getUsername() + " " + model.getPhone());
                        tvSendDetailsAddress.setText(sendProvince + "  " + sendCity + "  " + sendArea + "  " + model.getAddress());
                    } else {
                        btn_modify_address.setText(getString(R.string.str_007));
                    }
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    // 提交订单
    private void commitOrder() {
        String reProvince = orderInfo.getString("province");
        String reCity = orderInfo.getString("city");
        String reArea = orderInfo.getString("area");
        String reAddress = orderInfo.getString("address");
        String goodsName = orderInfo.getString("goods_name");
        String rePhone = orderInfo.getString("phone");
        String reName = orderInfo.getString("username");
        String total = orderInfo.getString("total");
        MallHttpUtil.openBuildOrder(sendName, sendPhone, sendProvince, sendCity, sendArea, sendAddress, reName, rePhone, replaceStr(reProvince), replaceStr(reCity), replaceStr(reArea + reAddress), goodsName, total, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    String billNo = obj.getString("id");
                    submit(billNo);
                } else {
                    ToastUtil.show(msg);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 789) {
            getAddress();
        }
    }

    private String sendProvince;
    private String sendCity;

    private String sendArea;

    private String sendPhone;

    private String sendName;
    private String sendAddress;

    private void getShopUserInfo() {
        MallHttpUtil.shopUserInfo(orderInfo.getString("shop_uid"), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    sendProvince = obj.getString("province");
                    sendCity = obj.getString("city");
                }
            }
        });
    }

    /**
     * 把空字符串替换为固定
     *
     * @param str
     * @return
     */
    public String replaceStr(String str) {
        return TextUtils.isEmpty(str) ? "unknown" : str;
    }

    /**
     * 复制地址
     */
    private void copyAddress() {
        if (TextUtils.isEmpty(mAddressString)) {
            return;
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", mAddressString);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(R.string.copy_success);
    }


    /**
     * 选择物流公司
     */
    private void chooseWuLiu() {
        if (mWuliuAdapter == null) {
            MallHttpUtil.getWuliuList(new HttpCallback() {
                @Override
                public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0) {
                        List<WuliuBean> list = JSON.parseArray(Arrays.toString(info), WuliuBean.class);
                        mWuliuAdapter = new SellerWuliuAdapter(mContext, list);
                        mWuliuAdapter.setOnItemClickListener(SellerSendActivity.this);
                        showWuliuDialog();
                    }
                }
            });
        } else {
            showWuliuDialog();
        }
    }

    private void showWuliuDialog() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.dialog_seller_wuliu, null);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mWuliuAdapter);
        mPopupWindow = new PopupWindow(v, ViewGroup.LayoutParams.MATCH_PARENT, DpUtil.dp2px(250), true);
        mPopupWindow.setOnDismissListener(this);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.showAsDropDown(mBtnWuLiuChoose);
        if (mArrow != null) {
            mArrow.setRotation(-90);
        }
    }


    @Override
    public void onItemClick(WuliuBean bean, int position) {
        mWuliuId = bean.getId();
        if (mWuliuName != null) {
            mWuliuName.setText(bean.getName());
        }
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    private void submit(String wuliuOrderNum) {
        if (TextUtils.isEmpty(mOrderId)) {
            return;
        }
//        String wuliuOrderNum = mWuliuOrderNum.getText().toString().trim();
//        if (TextUtils.isEmpty(wuliuOrderNum)) {
//            ToastUtil.show(R.string.mall_225);
//            return;
//        }

        MallHttpUtil.sellerSendOrder(mOrderId, mWuliuId, wuliuOrderNum, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    finish();
                }
                ToastUtil.show(msg);
            }
        });
    }


    @Override
    protected void onDestroy() {
        MallHttpUtil.cancel(MallHttpConsts.GET_SELLER_ORDER_DETAIL);
        MallHttpUtil.cancel(MallHttpConsts.GET_WULIU_LIST);
        MallHttpUtil.cancel(MallHttpConsts.SELLER_SEND_ORDER);
        if (mPopupWindow != null) {
            mPopupWindow.setOnDismissListener(null);
            mPopupWindow.dismiss();
        }
        mPopupWindow = null;
        super.onDestroy();
    }


    @Override
    public void onDismiss() {
        if (mArrow != null) {
            mArrow.setRotation(90);
        }
    }
}
