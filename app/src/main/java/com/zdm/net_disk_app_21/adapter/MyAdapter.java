package com.zdm.net_disk_app_21.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zdm.net_disk_app_21.R;
import com.zdm.net_disk_app_21.entity.FileInfo;
import com.zdm.net_disk_app_21.util.RetrofitUtils;
import com.zdm.net_disk_app_21.viewmodel.FileViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 利用泛型指定使用自定义的Holder
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private final boolean userCardView;
    private FileViewModel fileViewModel;
    private List<String> downloadListTemp = new ArrayList<>();
    List<FileInfo> fileInfos = new ArrayList<>(); // 名称应该是filesInfo 手残 打错了



    public MyAdapter(boolean userCardView, FileViewModel fileViewModel) {
        this.userCardView = userCardView;
        this.fileViewModel = fileViewModel;
    }

    public void setFiles(List<FileInfo> fileInfos) {
        this.fileInfos = fileInfos;
    }

    public void setDownloadListTemp(List<String> downloadListTemp) {
        this.downloadListTemp = downloadListTemp;
        fileViewModel.setDownloadList(this.downloadListTemp); // 同步到viewModel
    }

    public void setFileViewModel(FileViewModel fileViewModel) {
        this.fileViewModel = fileViewModel;
    }

    /**
     * 创建 holder 时 call to code our logic
     */
    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        // 从layout中加载view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView;

        if (userCardView) {
            itemView = inflater.inflate(R.layout.cell_card_file, parent, false);
        } else {
            itemView = inflater.inflate(R.layout.cell_normal_plus, parent, false);
        }

        MyViewHolder holder = new MyViewHolder(itemView, parent.getContext());

        // 勾选 logic
        holder.checkBox_file.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                downloadListTemp.add(holder.textView_fileName.getText().toString());
                fileViewModel.setDownloadList(downloadListTemp);
                fileViewModel.setUsePop(true);
            } else {
                fileViewModel.setUsePop(false);

                // 更新本地adapter数据
                Iterator<String> iterator = downloadListTemp.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().equals(holder.textView_fileName.getText().toString())) {
                        iterator.remove();
                    }
                }
                fileViewModel.setDownloadList(downloadListTemp); // 更新viewModel
            }
        });

        // item click logic with implement lambda 使用浏览器预览文件资源
        holder.itemView.setOnClickListener(v -> {

            if (holder.textView_fileSize.getText().toString().equals("")) {
                fileViewModel.getFileListFromServer();
            } else {
                Uri uri = Uri.parse(RetrofitUtils.BASE_URL + RetrofitUtils.BASE_PATH + holder.textView_fileName.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }

        });

        return holder;
    }

    /**
     * holder binding the recycleView when to call
     * 数据和view的绑定
     */
    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {

        FileInfo fileInfo = fileInfos.get(position);

        holder.textView_fileName.setText(fileInfo.getFileName());
        holder.textView_fileCreateTime.setText(fileInfo.getCreateTime());
        if (fileInfo.getFileSize().equals("0")) {
            holder.imageView_file_type.setImageResource(R.drawable.ic_folder_24);
            holder.textView_fileSize.setText("");

        } else {
            holder.imageView_file_type.setImageResource(R.drawable.ic_file_24);
            holder.textView_fileSize.setText(fileInfo.getFileSize());
        }
    }

    /**
     * get total num of the list
     */
    @Override
    public int getItemCount() {
        return fileInfos.size();
    }

    /**
     * static防止内存泄漏
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final Context context;

        TextView  textView_fileName, textView_fileCreateTime, textView_fileSize;
        CheckBox checkBox_file;
        ImageView imageView_file_type;

        /**
         * 自定义构造方法 主要是为了 context
         */
        public MyViewHolder(@NonNull @NotNull View itemView, Context context) {
            super(itemView);
            this.context = context;
            imageView_file_type = itemView.findViewById(R.id.imageView_file_type);
            textView_fileName = itemView.findViewById(R.id.textView_explorer_fileName);
            textView_fileCreateTime = itemView.findViewById(R.id.textView_fileCreateTime);
            textView_fileSize = itemView.findViewById(R.id.textView_fileSize);
            checkBox_file = itemView.findViewById(R.id.checkBox_file);
        }
    }
}
