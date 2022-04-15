package com.zdm.net_disk_app_21.callback;

import android.view.View;
import android.widget.CheckBox;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemClickListener {
    void OnItemClick(RecyclerView parent, View itemView, CheckBox checkBox, int position, String data);
}
