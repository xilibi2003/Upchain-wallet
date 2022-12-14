package pro.upchain.wallet.ui.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ExportKeystoreStringFragment extends BaseFragment {
    TextView tvKeystore;
    TextView btnCopy;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_derive_keystore_string;
    }

    @Override
    public void initView() {
        tvKeystore = parentView.findViewById(R.id.tv_keystore);
        btnCopy = parentView.findViewById(R.id.btn_copy);
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        Bundle arguments = getArguments();
        String walletKeystore = arguments.getString("walletKeystore");
        tvKeystore.setText(walletKeystore);
    }

    @Override
    public void configViews() {
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyText();
            }
        });
    }

    public void copyText() {
        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (cm != null) {
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", tvKeystore.getText().toString());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }
        btnCopy.setText(R.string.derive_private_key_already_copy_btn);
    }

}
