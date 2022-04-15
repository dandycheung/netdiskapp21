package com.zdm.net_disk_app_21.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zdm.net_disk_app_21.R;
import com.zdm.net_disk_app_21.entity.FileInfo;
import com.zdm.net_disk_app_21.util.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FileTransferAdapter extends RecyclerView.Adapter<FileTransferAdapter.FileTransferHolder> {
    List<FileInfo> transferFiles = new ArrayList<>();

    public void setTransferFiles(List<FileInfo> transferFiles) {
        this.transferFiles = transferFiles;
    }

    @NonNull
    @NotNull
    @Override
    public FileTransferHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_normal_transfer, parent, false);
        FileTransferHolder holder = new FileTransferHolder(itemView);

        // 设置监听 选中弹出 pop 进行删除
        holder.checkBox_file_transfer.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                ToastUtil.showMsg(parent.getContext(), holder.textView_transfer_fileName.getText().toString() + "记录已选择");
            }
        }));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FileTransferHolder holder, int position) {
        FileInfo fileInfo = transferFiles.get(position);
        // Log.v("MyTag", transferFiles.get(position).toString());

        if (fileInfo.getFileName().equals("没有传输中的文件")) {
            holder.textView_seqNum_transfer.setVisibility(View.GONE);

            holder.textView_transfer_fileName.setText(fileInfo.getFileName());
            holder.textView_transfer_fileName.setTextSize(24);
            holder.textView_transfer_fileName.setGravity(Gravity.CENTER);


            holder.textView_transferTime.setVisibility(View.GONE);
            holder.textView_fileSize_transfer.setVisibility(View.GONE);
            holder.checkBox_file_transfer.setVisibility(View.GONE);
        } else {
            holder.textView_seqNum_transfer.setText(String.valueOf(position+1));
            holder.textView_transfer_fileName.setText(fileInfo.getFileName());
            holder.textView_transferTime.setText(fileInfo.getCreateTime());
            holder.textView_fileSize_transfer.setText(fileInfo.getFileSize());
        }




    }

    @Override
    public int getItemCount() {
        return transferFiles.size();
    }

    class FileTransferHolder extends RecyclerView.ViewHolder{
        TextView textView_seqNum_transfer, textView_transfer_fileName, textView_transferTime, textView_fileSize_transfer;
        CheckBox checkBox_file_transfer;

        public FileTransferHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            /* bug logs:
                1. 当itemView和其他RecyclerView的itemView的控件id相似时，极有可能造成寻找控件错位 导致 NullPointerException
                2. 控件名称容易写错、写混

             */
            textView_seqNum_transfer = itemView.findViewById(R.id.textView_seqNum_transfer);
            textView_transfer_fileName = itemView.findViewById(R.id.textView_transfer_fileName);
            textView_transferTime = itemView.findViewById(R.id.textView_transferTime);
            textView_fileSize_transfer = itemView.findViewById(R.id.textView_fileSize_transfer);
            checkBox_file_transfer = itemView.findViewById(R.id.checkBox_file_transfer);
        }
    }
}
