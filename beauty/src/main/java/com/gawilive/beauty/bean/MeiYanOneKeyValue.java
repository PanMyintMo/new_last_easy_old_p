package com.gawilive.beauty.bean;

public class MeiYanOneKeyValue {

    private final Value mMeiBai;
    private final Value mMoPi;
    private final Value mHongRun;
    private final Value mDaYan;
    private final Value mMeiMao;
    private final Value mYanJu;
    private final Value mYanJiao;
    private final Value mShouLian;
    private final Value mZuiXing;
    private final Value mShouBi;
    private final Value mXiaBa;
    private final Value mETou;
    private final Value mChangBi;
    private final Value mXueLian;
    private final Value mKaiYanJiao;

    public MeiYanOneKeyValue(
            Value meiBai,
            Value moPi,
            Value hongRun,
            Value daYan,
            Value meiMao,
            Value yanJu,
            Value yanJiao,
            Value shouLian,
            Value zuiXing,
            Value shouBi,
            Value xiaBa,
            Value eTou,
            Value changBi,
            Value xueLian,
            Value kaiYanJiao) {
        mMeiBai = meiBai;
        mMoPi = moPi;
        mHongRun = hongRun;
        mDaYan = daYan;
        mMeiMao = meiMao;
        mYanJu = yanJu;
        mYanJiao = yanJiao;
        mShouLian = shouLian;
        mZuiXing = zuiXing;
        mShouBi = shouBi;
        mXiaBa = xiaBa;
        mETou = eTou;
        mChangBi = changBi;
        mXueLian = xueLian;
        mKaiYanJiao = kaiYanJiao;
    }

    public void calculateValue(MeiYanValueBean resultValue, float rate) {
        resultValue.setMeiBai(mMeiBai.getValue(rate));
        resultValue.setMoPi(mMoPi.getValue(rate));
        resultValue.setHongRun(mHongRun.getValue(rate));
        resultValue.setDaYan(mDaYan.getValue(rate));
        resultValue.setMeiMao(mMeiMao.getValue(rate));
        resultValue.setYanJu(mYanJu.getValue(rate));
        resultValue.setYanJiao(mYanJiao.getValue(rate));
        resultValue.setShouLian(mShouLian.getValue(rate));
        resultValue.setZuiXing(mZuiXing.getValue(rate));
        resultValue.setShouBi(mShouBi.getValue(rate));
        resultValue.setXiaBa(mXiaBa.getValue(rate));
        resultValue.setETou(mETou.getValue(rate));
        resultValue.setChangBi(mChangBi.getValue(rate));
        resultValue.setXueLian(mXueLian.getValue(rate));
        resultValue.setKaiYanJiao(mKaiYanJiao.getValue(rate));
    }


    public static class Value {
        private final int minValue;
        private final int maxValue;

        public Value(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }

        int getValue(float rate) {
            return minValue + (int) ((maxValue - minValue) * rate);
        }
    }


}
