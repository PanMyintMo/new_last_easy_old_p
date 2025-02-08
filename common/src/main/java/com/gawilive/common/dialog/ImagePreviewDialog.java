package com.gawilive.common.dialog;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.gawilive.common.CommonAppConfig;
import com.gawilive.common.R;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.adapter.ImagePreviewAdapter;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.utils.BitmapUtil;
import com.gawilive.common.utils.DialogUitl;
import com.gawilive.common.utils.DownloadUtil;
import com.gawilive.common.utils.PermissionUtil;
import com.gawilive.common.utils.StringUtil;
import com.gawilive.common.utils.ToastUtil;

import java.io.File;
import java.util.List;

/**
 * Created by cxf on 2018/11/28.
 * 图片预览弹窗
 */

public class ImagePreviewDialog extends AbsDialogFragment implements View.OnClickListener {

    private View mBg;
    private RecyclerView mRecyclerView;
    private ValueAnimator mAnimator;
    private int mPosition;
    private int mPageCount;
    private ActionListener mActionListener;
    private TextView mCount;
    private ImagePreviewAdapter mAdapter;
    private boolean mNeedDelete;
    private DownloadUtil mDownloadUtil;

    @Override
    protected int getLayoutId() {
        return R.layout.view_preview_image;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBg = mRootView.findViewById(R.id.bg);
        mCount = findViewById(R.id.count);
        findViewById(R.id.btn_close).setOnClickListener(this);
        if (mNeedDelete) {
            View btnDelete = findViewById(R.id.btn_delete);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(this);
        }
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(150);
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                mBg.setAlpha(v);
            }
        });
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mRecyclerView != null && mPageCount > 0) {
                    ImagePreviewAdapter adapter = new ImagePreviewAdapter(mContext, mPageCount);
                    mAdapter = adapter;
                    adapter.setActionListener(new ImagePreviewAdapter.ActionListener() {

                        @Override
                        public void onPageChanged(int position) {
                            if (mCount != null) {
                                mCount.setText(StringUtil.contact(String.valueOf(position + 1), "/", String.valueOf(mPageCount)));
                            }
                        }

                        @Override
                        public void loadImage(ImageView imageView, int position) {
                            if (mActionListener != null) {
                                mActionListener.loadImage(imageView, position);
                            }
                        }

                        @Override
                        public void saveImage(int position) {
                            if (mActionListener != null) {
                                final String imgUrl = mActionListener.getImageUrl(position);
                                if (TextUtils.isEmpty(imgUrl) || mContext == null) {
                                    return;
                                }
                                DialogUitl.showStringArrayDialog(mContext, new Integer[]{R.string.save_image_album}, new DialogUitl.StringArrayDialogCallback() {
                                    @Override
                                    public void onItemClick(String text, int tag) {
                                        PermissionUtil.request((AbsActivity) mContext,
                                                new PermissionCallback() {
                                                    @Override
                                                    public void onAllGranted(){
                                                        if (mDownloadUtil == null) {
                                                            mDownloadUtil = new DownloadUtil();
                                                        }
                                                        mDownloadUtil.download("save_img", CommonAppConfig.CAMERA_IMAGE_PATH, StringUtil.generateFileName() + ".png", imgUrl, new DownloadUtil.Callback() {
                                                            @Override
                                                            public void onSuccess(File file) {
                                                                BitmapUtil.saveImageInfo(file);
                                                                ToastUtil.show(R.string.save_success);
                                                            }

                                                            @Override
                                                            public void onProgress(int progress) {

                                                            }

                                                            @Override
                                                            public void onError(Throwable e) {

                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onDenied(List<String> deniedPermissions) {

                                                    }
                                                },
                                                Manifest.permission.READ_MEDIA_VIDEO,
                                                Manifest.permission.READ_MEDIA_IMAGES,
                                                Manifest.permission.READ_MEDIA_AUDIO,
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                    }
                                });
                            }
                        }
                    });
                    mRecyclerView.setAdapter(adapter);
                    if (mPosition >= 0 && mPosition < mPageCount) {
                        adapter.setCurPosition(mPosition);
                        mRecyclerView.scrollToPosition(mPosition);
                    }
                }
            }
        });
        mAnimator.start();
    }

    public void setImageInfo(int pageCount, int position, boolean needDelete, ActionListener actionListener) {
        mActionListener = actionListener;
        mPageCount = pageCount;
        mPosition = position;
        mNeedDelete = needDelete;
    }


    @Override
    public void onDestroy() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mContext = null;
        mActionListener = null;
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_close) {
            dismiss();
        } else if (i == R.id.btn_delete) {
            delete();
        }
    }

    private void delete() {
        if (mAdapter != null && mActionListener != null) {
            mActionListener.onDeleteClick(mAdapter.getCurPosition());
        }
        dismiss();
    }


    public interface ActionListener {
        void loadImage(ImageView imageView, int position);

        void onDeleteClick(int position);

        String getImageUrl(int position);
    }

}
