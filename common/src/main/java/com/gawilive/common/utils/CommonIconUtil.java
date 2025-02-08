package com.gawilive.common.utils;

import android.util.SparseIntArray;

import com.gawilive.common.Constants;
import com.gawilive.common.R;

/**
 * Created by cxf on 2018/10/11.
 */

public class CommonIconUtil {
    private static final SparseIntArray sCashTypeMap;//提现图片

    static {
        sCashTypeMap = new SparseIntArray();
        sCashTypeMap.put(Constants.CASH_ACCOUNT_ALI, R.mipmap.icon_cash_ali);
        sCashTypeMap.put(Constants.CASH_ACCOUNT_WX, R.mipmap.icon_cash_wx);
        sCashTypeMap.put(Constants.CASH_ACCOUNT_BANK, R.mipmap.icon_cash_bank);
    }

    public static int getSexIcon(int key) {
        return key == 1 ? R.mipmap.icon_sex_male_1 : R.mipmap.icon_sex_female_1;
    }

    public static int getCashTypeIcon(int key) {
        return sCashTypeMap.get(key);
    }

}
