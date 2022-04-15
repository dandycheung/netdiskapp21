package com.zdm.net_disk_app_21.adapter;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zdm.net_disk_app_21.R;
import com.zdm.net_disk_app_21.util.ToastUtil;
import com.zdm.net_disk_app_21.viewmodel.TransferFileViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FileTransferProgressAdapter extends RecyclerView.Adapter<FileTransferProgressAdapter.FileTransferHolder> {
    private List<String> transferFiles = new ArrayList<>();
    private TransferFileViewModel viewModel;

    public void setTransferFiles(List<String> transferFiles) {
        this.transferFiles = transferFiles;
    }

    public void setViewModel(TransferFileViewModel viewModel) {
        this.viewModel = viewModel;
        this.transferFiles = viewModel.getTransferList().getValue();
    }

    @NonNull
    @NotNull
    @Override
    public FileTransferHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_normal_transfer_progress, parent, false);
        FileTransferHolder holder = new FileTransferHolder(itemView);

        // 设置监听 选中弹出 pop 进行删除
        holder.checkBox_file_transfer_progress.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                ToastUtil.showMsg(parent.getContext(), holder.textView_transfer_fileName_progress.getText().toString() + "记录已选择");
            }
        }));

        Handler handler = new Handler();
        for (int i = 1; i < 101; i++) {


//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            int finalI = i;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    holder.progressBar_download.setProgress(finalI);
                }
            }, 1000);
            // holder.progressBar_download.setProgress(i);
        }

        if (holder.progressBar_download.getProgress() == 100) {
            holder.progressBar_download.setVisibility(View.GONE);
//            viewModel
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FileTransferHolder holder, int position) {
        String filename = transferFiles.get(position);
        // Log.v("MyTag", transferFiles.get(position).toString());

        if (filename.equals("没有传输中的文件")) {
            holder.textView_seqNum_transfer_progress.setVisibility(View.GONE);

            holder.textView_transfer_fileName_progress.setText(filename);
            holder.textView_transfer_fileName_progress.setTextSize(24);
            holder.textView_transfer_fileName_progress.setGravity(Gravity.CENTER);
            holder.checkBox_file_transfer_progress.setVisibility(View.GONE);
            holder.progressBar_download.setVisibility(View.GONE);
        } else {
            if (holder.progressBar_download.getProgress() == 100) {
                holder.progressBar_download.setVisibility(View.GONE);
            } else {
                holder.progressBar_download.setVisibility(View.VISIBLE);
            }
            holder.textView_seqNum_transfer_progress.setText(String.valueOf(position+1));
            holder.textView_transfer_fileName_progress.setText(filename);

        }

    }

    @Override
    public int getItemCount() {
        return transferFiles.size();
    }

    class FileTransferHolder extends RecyclerView.ViewHolder{
        TextView textView_seqNum_transfer_progress, textView_transfer_fileName_progress;
        CheckBox checkBox_file_transfer_progress;
        ProgressBar progressBar_download;

        public FileTransferHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            /* bug logs:
                1. 当itemView和其他RecyclerView的itemView的控件id相似时，极有可能造成寻找控件错位 导致 NullPointerException
                2. 控件名称容易写错、写混

             */
            textView_seqNum_transfer_progress = itemView.findViewById(R.id.textView_seqNum_transfer_progress);
            textView_transfer_fileName_progress = itemView.findViewById(R.id.textView_transfer_fileName_progress);
            checkBox_file_transfer_progress = itemView.findViewById(R.id.checkBox_file_transfer_progress);
            progressBar_download = itemView.findViewById(R.id.progressBar_download);
        }
    }
}
