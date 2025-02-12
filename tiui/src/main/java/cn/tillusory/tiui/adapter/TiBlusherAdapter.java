package cn.tillusory.tiui.adapter;

import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import cn.tillusory.sdk.TiSDK;
import cn.tillusory.tiui.R;
import cn.tillusory.tiui.TiPanelLayout;
import cn.tillusory.tiui.model.RxBusAction;
import cn.tillusory.tiui.model.TiMakeupConfig;
import cn.tillusory.tiui.model.TiMakeupText;
import cn.tillusory.tiui.model.TiSelectedPosition;
import com.bumptech.glide.Glide;
import com.hwangjr.rxbus.RxBus;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.dispatcher.DownloadDispatcher;
import com.liulishuo.okdownload.core.listener.DownloadListener2;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Anko on 2019-09-08.
 * Copyright (c) 2018-2020 拓幻科技 - tillusory.cn. All rights reserved.
 */
public class TiBlusherAdapter extends RecyclerView.Adapter<TiMakeupItemViewHolder> {

    private int selectedPosition = TiSelectedPosition.POSITION_BLUSHER;

    private List<TiMakeupConfig.TiMakeup> list;
    private List<TiMakeupText> textList;

    private Handler handler = new Handler();

    private Map<String, String> downloadingMakeups = new ConcurrentHashMap<>();

    public TiBlusherAdapter(List<TiMakeupConfig.TiMakeup> list, List<TiMakeupText> textList) {
        this.list = list;
        this.textList = textList;

        DownloadDispatcher.setMaxParallelRunningCount(5);
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        TiSelectedPosition.POSITION_BLUSHER = selectedPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TiMakeupItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ti_makeup, parent, false);
        return new TiMakeupItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final TiMakeupItemViewHolder holder, int position) {
        final TiMakeupConfig.TiMakeup tiMakeup = list.get(holder.getAdapterPosition());

//        if (position == 0) {
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
//            p.setMargins((int) (holder.itemView.getContext().getResources().getDisplayMetrics().density * 16 + 0.5f), 0, 0, 0);
//            holder.itemView.requestLayout();
//        } else {
//            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();
//            p.setMargins(0, 0, 0, 0);
//            holder.itemView.requestLayout();
//        }

        if (selectedPosition == position) {
            holder.itemView.setSelected(true);
        } else {
            holder.itemView.setSelected(false);
        }

        //显示封面
        if (tiMakeup == TiMakeupConfig.TiMakeup.NO_MAKEUP) {
            holder.thumbIV.setImageResource(R.drawable.ic_ti_none);
        } else {
            Glide.with(holder.itemView.getContext())
                .load(list.get(position).getThumb())
                .into(holder.thumbIV);
        }
        holder.nameTV.setText(textList.get(position).getString(holder.itemView.getContext()));
        if (TiPanelLayout.isFullRatio) {
            holder.nameTV.setTextColor(holder.itemView.getContext().getResources().getColorStateList(R.color.color_ti_selector_full));
        } else {
            holder.nameTV.setTextColor(holder.itemView.getContext().getResources().getColorStateList(R.color.color_ti_selector_not_full));
        }

        //判断是否已经下载
        if (tiMakeup.isDownloaded()) {
            holder.downloadIV.setVisibility(View.GONE);
            holder.loadingIV.setVisibility(View.GONE);
            holder.stopLoadingAnimation();
        } else {
            //判断是否正在下载，如果正在下载，则显示加载动画
            if (downloadingMakeups.containsKey(tiMakeup.getName())) {
                holder.downloadIV.setVisibility(View.GONE);
                holder.loadingIV.setVisibility(View.VISIBLE);
                holder.startLoadingAnimation();
            } else {
                holder.downloadIV.setVisibility(View.VISIBLE);
                holder.loadingIV.setVisibility(View.GONE);
                holder.stopLoadingAnimation();
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果没有下载，则开始下载到本地
                if (!tiMakeup.isDownloaded()) {

                    //如果已经在下载了，则不操作
                    if (downloadingMakeups.containsKey(tiMakeup.getName())) {
                        return;
                    }

                    if (!new File(TiSDK.getMakeupPath(holder.itemView.getContext()) + File.separator + tiMakeup.getType() + File.separator + tiMakeup.getName()).exists()) {
                        if (!new File(TiSDK.getMakeupPath(holder.itemView.getContext()) + File.separator + tiMakeup.getType() + File.separator + tiMakeup.getName()).mkdirs()) {
                            return;
                        }
                    }

                    new DownloadTask.Builder(tiMakeup.getUrl(), new File(TiSDK.getMakeupPath(holder.itemView.getContext()) + File.separator + tiMakeup.getType() + File.separator + tiMakeup.getName()))
                            .setMinIntervalMillisCallbackProcess(30)
                            .build()
                            .enqueue(new DownloadListener2() {
                                @Override
                                public void taskStart(@NonNull DownloadTask task) {
                                    downloadingMakeups.put(tiMakeup.getName(), tiMakeup.getUrl());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyDataSetChanged();
                                        }
                                    });
                                }

                                @Override
                                public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable final Exception realCause) {
                                    if (cause == EndCause.COMPLETED) {
                                        downloadingMakeups.remove(tiMakeup.getName());

//                                        File targetDir = new File(TiSDK.getMakeupPath(holder.itemView.getContext()));
//                                        File file = task.getFile();
//                                        try {
//                                            //解压到礼物目录
//                                            TiUtils.unzip(file, targetDir);
//                                            if (file != null) {
//                                                file.delete();
//                                            }

                                            //修改内存与文件
                                        tiMakeup.setDownloaded(true);
                                        tiMakeup.downloaded();

                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyDataSetChanged();
                                                }
                                            });

//                                        } catch (Exception e) {
//                                            if (file != null) {
//                                                file.delete();
//                                            }
//                                        }
                                    } else {
                                        downloadingMakeups.remove(tiMakeup.getName());

                                        handler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                notifyDataSetChanged();
                                                if (realCause != null) {
                                                    Toast.makeText(holder.itemView.getContext(), realCause.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });

                } else {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        //切换选中背景
                        int lastPosition = selectedPosition;
                        selectedPosition = holder.getAdapterPosition();
                        TiSelectedPosition.POSITION_BLUSHER = selectedPosition;
                        notifyItemChanged(selectedPosition);
                        notifyItemChanged(lastPosition);
                    }

                    //如果已经下载了，则让美妆生效
                    RxBus.get().post(RxBusAction.ACTION_BLUSHER, list.get(selectedPosition).getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
}
