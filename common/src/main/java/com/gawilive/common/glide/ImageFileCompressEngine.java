package com.gawilive.common.glide;

import android.content.Context;
import android.net.Uri;
import com.luck.lib.camerax.utils.DateUtils;
import com.luck.picture.lib.engine.CompressFileEngine;
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener;
import com.gawilive.common.CommonAppContext;
import top.zibin.luban.Luban;
import top.zibin.luban.OnNewCompressListener;
import top.zibin.luban.OnRenameListener;

import java.io.File;
import java.util.ArrayList;

public class ImageFileCompressEngine implements CompressFileEngine {
    @Override
    public void onStartCompress(Context context, ArrayList<Uri> source, OnKeyValueResultCallbackListener call) {
        Luban.with(CommonAppContext.getInstance())
                .ignoreBy(100)//8k以下不压缩
                .setRenameListener(new OnRenameListener() {
                    @Override
                    public String rename(String filePath) {
                        int indexOf = filePath.lastIndexOf(".");
                        String postfix = (indexOf != -1) ? filePath.substring(indexOf) : ".jpg";
                        String name = DateUtils.getCreateFileName("CMP_") + postfix;
                        return name;
                    }
                })
                .setCompressListener(new OnNewCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(String source, File compressFile) {
                        call.onCallback(source, compressFile.getAbsolutePath());
                    }

                    @Override
                    public void onError(String source, Throwable e) {
                        call.onCallback(source, null);
                    }


                }).launch();
    }
}
