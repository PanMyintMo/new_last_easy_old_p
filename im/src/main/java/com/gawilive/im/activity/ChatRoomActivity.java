package com.gawilive.im.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.ViewGroup;

import com.gawilive.common.Constants;
import com.gawilive.common.activity.AbsActivity;
import com.gawilive.common.bean.UserBean;
import com.gawilive.common.interfaces.ActivityResultCallback;
import com.gawilive.common.interfaces.ImageResultCallback;
import com.gawilive.common.interfaces.PermissionCallback;
import com.gawilive.common.utils.ActivityResultUtil;
import com.gawilive.common.utils.FloatWindowHelper;
import com.gawilive.common.utils.KeyBoardUtil;
import com.gawilive.common.utils.MediaUtil;
import com.gawilive.common.utils.PermissionUtil;
import com.gawilive.common.utils.ToastUtil;
import com.gawilive.common.utils.WordUtil;
import com.gawilive.im.R;
import com.gawilive.im.dialog.ChatVoiceInputDialog;
import com.gawilive.im.interfaces.ChatRoomActionListener;
import com.gawilive.im.utils.ImMessageUtil;
import com.gawilive.im.views.ChatRoomViewHolder;

import java.io.File;
import java.util.List;

/**
 * Created by cxf on 2018/10/24.
 */

public class ChatRoomActivity extends AbsActivity implements KeyBoardUtil.KeyBoardHeightListener {

    public static void forward(Context context, UserBean userBean, boolean following, boolean fromUserHome) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(Constants.USER_BEAN, userBean);
        intent.putExtra(Constants.FOLLOW, following);
        intent.putExtra(Constants.IM_FROM_HOME, fromUserHome);
        context.startActivity(intent);
    }

    private ViewGroup mRoot;
    private ViewGroup mContianer;
    private ChatRoomViewHolder mChatRoomViewHolder;
    private boolean mFromUserHome;
    private KeyBoardUtil mKeyBoardUtil;
    private final ImageResultCallback mImageResultCallback = new ImageResultCallback() {
        @Override
        public void beforeCamera() {

        }

        @Override
        public void onSuccess(File file) {
            if (file != null && file.exists() && mChatRoomViewHolder != null) {
                mChatRoomViewHolder.sendImage(file.getAbsolutePath());
            }
        }

        @Override
        public void onFailure() {

        }
    };


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_room;
    }


    @Override
    protected void main() {
        Intent intent = getIntent();
        UserBean userBean = intent.getParcelableExtra(Constants.USER_BEAN);
        if (userBean == null) {
            return;
        }
        boolean following = intent.getBooleanExtra(Constants.FOLLOW, false);
        mFromUserHome = intent.getBooleanExtra(Constants.IM_FROM_HOME, false);
        mRoot = findViewById(R.id.root);
        mContianer = findViewById(R.id.container);
        mChatRoomViewHolder = new ChatRoomViewHolder(mContext, mContianer, userBean, following);
        mChatRoomViewHolder.setActionListener(new ChatRoomActionListener() {
            @Override
            public void onCloseClick() {
                superBackPressed();
            }

            @Override
            public void onChooseImageClick() {
                PermissionUtil.request(ChatRoomActivity.this,
                        new PermissionCallback() {
                            @Override
                            public void onAllGranted() {
                                forwardChooseImage();
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

            @Override
            public void onCameraClick() {
                MediaUtil.getImageByCamera(ChatRoomActivity.this, false, mImageResultCallback);
            }

            @Override
            public void onVoiceInputClick() {
                if (!FloatWindowHelper.checkVoice(false)) {
                    return;
                }
                PermissionUtil.request(ChatRoomActivity.this, new PermissionCallback() {
                            @Override
                            public void onAllGranted() {
                                openVoiceInputDialog();
                            }

                            @Override
                            public void onDenied(List<String> deniedPermissions) {

                            }
                        },
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO
                );
            }

            @Override
            public void onLocationClick() {
                PermissionUtil.request(ChatRoomActivity.this, new PermissionCallback() {
                    @Override
                    public void onAllGranted() {
                        forwardLocation();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                }, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION);
            }

            @Override
            public ViewGroup getImageParentView() {
                return mRoot;
            }


        });
        mChatRoomViewHolder.addToParent();
        mChatRoomViewHolder.loadData();
        mKeyBoardUtil = new KeyBoardUtil(mRoot,this);
        ImMessageUtil.getInstance().setOpenChatActivity(true);
    }

    @Override
    public void onKeyBoardHeightChanged(int keyboardHeight) {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onKeyBoardHeightChanged(keyboardHeight);
        }
    }

    @Override
    public void onBackPressed() {
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.back();
        } else {
            superBackPressed();
        }
    }

    private void release() {
        if (mKeyBoardUtil != null) {
            mKeyBoardUtil.release();
        }
        mKeyBoardUtil = null;
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.refreshLastMessage();
            mChatRoomViewHolder.release();
        }
        mChatRoomViewHolder = null;
        ImMessageUtil.getInstance().setOpenChatActivity(false);
    }

    public void superBackPressed() {
        release();
        super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
    }


    /**
     * 前往选择图片页面
     */
    private void forwardChooseImage() {
        ActivityResultUtil.startActivityForResult(this, new Intent(mContext, ChatChooseImageActivity.class), new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    String imagePath = intent.getStringExtra(Constants.SELECT_IMAGE_PATH);
                    if (mChatRoomViewHolder != null) {
                        mChatRoomViewHolder.sendImage(imagePath);
                    }
                }
            }
        });
//        MediaUtil.getImageByAlumb(this, false, mImageResultCallback);
    }


    /**
     * 前往发送位置页面
     */
    private void forwardLocation() {
        ActivityResultUtil.startActivityForResult(this, new Intent(mContext, LocationActivity.class), new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                if (intent != null) {
                    double lat = intent.getDoubleExtra(Constants.LAT, 0);
                    double lng = intent.getDoubleExtra(Constants.LNG, 0);
                    int scale = intent.getIntExtra(Constants.SCALE, 0);
                    String address = intent.getStringExtra(Constants.ADDRESS);
                    if (lat > 0 && lng > 0 && scale > 0 && !TextUtils.isEmpty(address)) {
                        if (mChatRoomViewHolder != null) {
                            mChatRoomViewHolder.sendLocation(lat, lng, scale, address);
                        }
                    } else {
                        ToastUtil.show(WordUtil.getString(R.string.im_get_location_failed));
                    }
                }
            }
        });
    }


    /**
     * 打开语音输入窗口
     */
    private void openVoiceInputDialog() {
        ChatVoiceInputDialog fragment = new ChatVoiceInputDialog();
        fragment.setChatRoomViewHolder(mChatRoomViewHolder);
        fragment.show(getSupportFragmentManager(), "ChatVoiceInputDialog");
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mChatRoomViewHolder != null) {
            mChatRoomViewHolder.onResume();
        }
    }

    public boolean isFromUserHome() {
        return mFromUserHome;
    }
}
