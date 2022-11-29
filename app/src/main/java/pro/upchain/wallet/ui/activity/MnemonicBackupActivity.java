package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.view.MnemonicBackupAlertDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


// 提示 抄下钱包助记词 界面

public class MnemonicBackupActivity extends BaseActivity implements View.OnClickListener {
    private static final int VERIFY_MNEMONIC_BACKUP_REQUEST = 1101;
    TextView tvTitle;
    TextView tvMnemonic;
    private String walletMnemonic;

    private long walletId;
    private TextView btnNext;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        tvMnemonic = findViewById(R.id.tv_mnemonic);
        btnNext = findViewById(R.id.btn_next);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mnemonic_backup_title);
    }

    @Override
    public void initDatas() {
        MnemonicBackupAlertDialog mnemonicBackupAlertDialog = new MnemonicBackupAlertDialog(this);
        mnemonicBackupAlertDialog.show();
        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        tvMnemonic.setText(walletMnemonic);

    }

    @Override
    public void configViews() {
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, VerifyMnemonicBackupActivity.class);
        intent.putExtra("walletId", walletId);
        intent.putExtra("walletMnemonic", walletMnemonic);
        startActivityForResult(intent, VERIFY_MNEMONIC_BACKUP_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VERIFY_MNEMONIC_BACKUP_REQUEST) {
            if (data != null) {
                finish();
            }
        }
    }


}

