package com.zdm.net_disk_app_21.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zdm.net_disk_app_21.R;
import com.zdm.net_disk_app_21.callback.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class FileExplorerAdapter extends RecyclerView.Adapter<FileExplorerAdapter.FileExplorerViewHolder> {

    private List<Map<String, Object>> list;

    // 1. 创建私有 recyclerView 提供重写方法实现其赋值与注销
    private RecyclerView recyclerView;

    // 2. 使用定义好的自定义接口
    private OnItemClickListener onItemClickListener;

    // 3. 提供 setter
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public FileExplorerAdapter(List<Map<String, Object>> list) {
        this.list = list;
    }

    @NonNull
    @NotNull
    @Override
    public FileExplorerViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.cell_file_explorer, parent, false);
        FileExplorerViewHolder holder = new FileExplorerViewHolder(itemView);

        // 勾选框 logic
        holder.checkBox_explorer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
//                Toast.makeText(parent.getContext(), "已选中文件: " + holder.textView_explorer_fileName.getText(), Toast.LENGTH_SHORT).show();

            }
        });

        // 4. item点击：实则使用自定义的onclick logic
        holder.itemView.setOnClickListener(v -> {
            // 获得当前itemView得位置
            int position = recyclerView.getChildAdapterPosition(v);
            if (onItemClickListener != null) {
                onItemClickListener.OnItemClick(recyclerView, v, holder.checkBox_explorer, position, list.get(position).get("fileName").toString());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FileExplorerViewHolder holder, int position) {
        Map<String, Object> map = list.get(position);
        holder.textView_explorer_fileName.setText(String.valueOf(map.get("fileName")));
        Log.v("fileName: ", map.get("fileName").toString());
        holder.imageView_explorer.setImageResource((Integer) map.get("icon"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull @NotNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    public class FileExplorerViewHolder extends RecyclerView.ViewHolder{
        TextView textView_explorer_fileName;
        ImageView imageView_explorer;
        CheckBox checkBox_explorer;
        public FileExplorerViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView_explorer_fileName = itemView.findViewById(R.id.textView_explorer_fileName);
            imageView_explorer = itemView.findViewById(R.id.imageView_explorer);
            checkBox_explorer = itemView.findViewById(R.id.checkBox_file_explorer);
        }
    }
}
