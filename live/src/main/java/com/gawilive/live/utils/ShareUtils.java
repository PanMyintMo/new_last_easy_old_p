package com.gawilive.live.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.gawilive.common.Constants;
import com.gawilive.live.R;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Author: Mario Velasco Casquero
 * Date: 02/12/2015
 */
public class ShareUtils {


    public static void shareSocial(Context context,String type){
        switch (type) {
//            case Constants.MOB_QQ:
//                bean.setIcon1(com.gawilive.common.R.mipmap.icon_share_qq_1);
//                bean.setIcon2(com.gawilive.common.R.mipmap.icon_share_qq_2);
//                break;
//            case Constants.MOB_QZONE:
//                bean.setIcon1(com.gawilive.common.R.mipmap.icon_share_qzone_1);
//                bean.setIcon2(com.gawilive.common.R.mipmap.icon_share_qzone_2);
//                break;
//            case Constants.MOB_WX:
//                bean.setIcon1(com.gawilive.common.R.mipmap.icon_share_wx_1);
//                bean.setIcon2(com.gawilive.common.R.mipmap.icon_share_wx_2);
//                break;
//            case Constants.MOB_WX_PYQ:
//                bean.setIcon1(com.gawilive.common.R.mipmap.icon_share_pyq_1);
//                bean.setIcon2(com.gawilive.common.R.mipmap.icon_share_pyq_2);
//                break;
            case Constants.MOB_FACEBOOK:
                shareFacebook(context,"https://ezlive.asia/");
                break;
            case Constants.MOB_TWITTER:
//                shareTwitter(context,"https://ezlive.asia/");
                break;
            case Constants.MOB_VIBER:
                shareViber(context,"https://ezlive.asia/");
                break;
            case Constants.MOB_TELEGRAM:
                shareTelegram(context,"https://ezlive.asia/");
                break;
            case Constants.MOB_WHATSAPP:
                shareWhatsapp(context,"https://ezlive.asia/");
                break;
        }
    }
    /**
     * Share on Facebook. Using Facebook app if installed or web link otherwise.
     *
     * @param context activity which launches the intent
     * @param url      url to share
     */
    public static void shareFacebook(Context context,String url) {
        boolean facebookAppFound = false;
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.packageName).contains("com.facebook.katana")) {
                final ActivityInfo activityInfo = app.activityInfo;
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setComponent(name);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            String sharerUrl = "https://www.facebook.com/sharer/sharer.php?u=" + url;
            shareIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sharerUrl));
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.getApplicationContext().startActivity(shareIntent);
    }
    public static void shareViber(Context context,String url){
        boolean facebookAppFound = false;
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ez LIVE \n" + url);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.packageName).contains("com.viber.voip")) {
                final ActivityInfo activityInfo = app.activityInfo;
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setComponent(name);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.viber.voip");
            shareIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.getApplicationContext().startActivity(shareIntent);
    }
    public static void shareTelegram(Context context,String url){
        boolean facebookAppFound = false;
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ez LIVE \n" + url);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(url));
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.packageName).contains("org.telegram.messenger")) {
                final ActivityInfo activityInfo = app.activityInfo;
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setComponent(name);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            Uri marketUri = Uri.parse("market://details?id="
                    + "org.telegram.messenger");
            shareIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.getApplicationContext().startActivity(shareIntent);
    }

    /**
     * Share on Twitter. Using Twitter app if installed or web link otherwise.
     *
     * @param activity activity which launches the intent
     * @param text     text to share
     * @param url      url to share
     * @param via      twitter username without '@' who shares
     * @param hashtags hashtags for tweet without '#' and separated by ','
     */
    public static void shareTwitter(Activity activity, String text, String url, String via, String hashtags) {
        StringBuilder tweetUrl = new StringBuilder("https://twitter.com/intent/tweet?text=");
        tweetUrl.append(TextUtils.isEmpty(text) ? urlEncode(" ") : urlEncode(text));
        if (!TextUtils.isEmpty(url)) {
            tweetUrl.append("&url=");
            tweetUrl.append(urlEncode(url));
        }
        if (!TextUtils.isEmpty(via)) {
            tweetUrl.append("&via=");
            tweetUrl.append(urlEncode(via));
        }
        if (!TextUtils.isEmpty(hashtags)) {
            tweetUrl.append("&hastags=");
            tweetUrl.append(urlEncode(hashtags));
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl.toString()));
        List<ResolveInfo> matches = activity.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith("com.twitter")) {
                intent.setPackage(info.activityInfo.packageName);
            }
        }
        activity.startActivity(intent);
    }

    /**
     * Share on Whatsapp (if installed)
     *
     * @param url      url to share
     */
    public static void shareWhatsapp(Context context, String url) {
        boolean facebookAppFound = false;
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Ez LIVE \n" + url);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.packageName).contains("com.whatsapp")) {
                final ActivityInfo activityInfo = app.activityInfo;
                final ComponentName name = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setComponent(name);
                facebookAppFound = true;
                break;
            }
        }
        if (!facebookAppFound) {
            Uri marketUri = Uri.parse("market://details?id="
                    + "com.telegram.messenger");
            shareIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.getApplicationContext().startActivity(shareIntent);
//        PackageManager pm = activity.getPackageManager();
//        try {
//
//            Intent waIntent = new Intent(Intent.ACTION_SEND);
//            waIntent.setType("text/plain");
//
//            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
//            //Check if package exists or not. If not then code
//            //in catch block will be called
//            waIntent.setPackage("com.whatsapp");
//
//            waIntent.putExtra(Intent.EXTRA_TEXT, "Ez LIVE \n" + " " + url);
////            activity.startActivity(Intent
////                    .createChooser(waIntent, activity.getString(R.string.share_intent_title)));
//
//        } catch (PackageManager.NameNotFoundException e) {
////            Toast.makeText(activity, activity.getString(R.string.share_whatsapp_not_instaled),
////                    Toast.LENGTH_SHORT).show();
//        }
    }


    /**
     * Convert to UTF-8 text to put it on url format
     *
     * @param s text to be converted
     * @return text on UTF-8 format
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.wtf("wtf", "UTF-8 should always be supported", e);
            throw new RuntimeException("URLEncoder.encode() failed for " + s);
        }
    }
}