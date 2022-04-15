package com.zdm.net_disk_app_21.customwidget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.zdm.net_disk_app_21.R;


/**
 * 这里没有使用 建造者的设计模式 来设计这个类
 * 以后可以用 建造者 模式进行改写
 *
 * 所以这里不支持链式调用
 *  想链式调用 在setter方法上进行改造
 *      不再返回void而是返回 CustomDialog 对象，那么就可以接着进行 .来调用其他方法
 *      eg:
 *          public CustomDialog setTitle(String title) {
 *              this.title = title;
 *              return this;
 *          }
 */
public class CustomDialog extends Dialog implements View.OnClickListener {
    private TextView textView_custom_dialog_title, textView_cancel_custom_dialog, textView_confirm_custom_dialog;
    private EditText editText_custom_dialog_filename;
    private String title, filename, cancel, confirm;
    private IOnCancelListener cancelListener;
    private IOnConfirmListener confirmListener;

    public CustomDialog(@NonNull Context context) {
        super(context);
    }
    public CustomDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            这里设置视图文件时，虽然布局文件中宽度为匹配父布局
            但是系统在绑定视图与控制器时，会把 匹配 变为 包裹 导致实际显示的dialog很窄

            解决方案
                自己设定宽度
         */
        setContentView(R.layout.layout_custom_dialog);

        // 设定宽度
        WindowManager windowManager = getWindow().getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();

        Point pointSize = new Point(); // 这里使用 pointSize 作为变量名 是因为 display.getSize() 的形参名称为 size
        display.getSize(pointSize);

        layoutParams.width = (int) (pointSize.x * 0.8); // 设置dialog的宽度为当前手机屏幕的 宽度 * 0.8
        getWindow().setAttributes(layoutParams);

        textView_custom_dialog_title = findViewById(R.id.textView_custom_dialog_title);
        editText_custom_dialog_filename = findViewById(R.id.editText_custom_dialog_filename);
        textView_cancel_custom_dialog = findViewById(R.id.textView_cancel_custom_dialog);
        textView_confirm_custom_dialog = findViewById(R.id.textView_confirm_custom_dialog);

        // android.text.TextUtils 工具类可以帮助我们快速判空以及...
        if (!TextUtils.isEmpty(title)) {
            textView_custom_dialog_title.setText(title);
        }

        if (!TextUtils.isEmpty(filename)) {
            editText_custom_dialog_filename.setText(filename);
        }

        if (!TextUtils.isEmpty(cancel)) {
            textView_cancel_custom_dialog.setText(cancel);
        }
        if (!TextUtils.isEmpty(confirm)) {
            textView_confirm_custom_dialog.setText(confirm);
        }

        // 设置按钮点击事件
        textView_cancel_custom_dialog.setOnClickListener(this);
        textView_confirm_custom_dialog.setOnClickListener(this);
    }

    public EditText getEditText_custom_dialog_filename() {
        return editText_custom_dialog_filename;
    }

    // 支持链式调用的写法
    public CustomDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public CustomDialog setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public CustomDialog setCancel(String cancel, IOnCancelListener cancelListener) {
        this.cancel = cancel;
        this.cancelListener = cancelListener;
        return this;
    }

    public CustomDialog setConfirm(String confirm, IOnConfirmListener confirmListener) {
        this.confirm = confirm;
        this.confirmListener = confirmListener;
        return this;
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textView_cancel_custom_dialog:
                if (cancelListener != null) { // 监听 不为空 则调用其回调方法 深入理解回调
                    cancelListener.onCancel(this);
                }
                break;

            case R.id.textView_confirm_custom_dialog:
                if (confirmListener != null) {
                    confirmListener.onConfirm(this);
                }
                break;
        }
    }

    // 类 内部定义接口
    public interface IOnCancelListener {
        void onCancel(CustomDialog dialog);
    }

    public interface IOnConfirmListener {
        void onConfirm(CustomDialog dialog);
    }
}
