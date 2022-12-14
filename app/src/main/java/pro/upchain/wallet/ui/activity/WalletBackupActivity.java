package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.Md5Utils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.view.InputPwdDialog;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */



// 进行备份提示

public class WalletBackupActivity extends BaseActivity implements View.OnClickListener {
    TextView tvTitle;
    TextView btnBackup;
    TextView btnBackupHelp;

    TextView btnSkip;

    private InputPwdDialog inputPwdDialog;
    private String walletPwd;
    private String walletAddress;
    private String walletName;
    private String walletMnemonic;
    private long walletId;
    private boolean firstAccount;

    @Override
    public void initView() {
        btnSkip=findViewById(R.id.iv_btn);
        tvTitle = findViewById(R.id.tv_title);
        btnBackup = findViewById(R.id.btn_backup);
        btnBackupHelp = findViewById(R.id.btn_backup_help);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.wallet_backup_backup_btn);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletPwd = intent.getStringExtra("walletPwd");
        walletAddress = intent.getStringExtra("walletAddress");
        walletName = intent.getStringExtra("walletName");
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        firstAccount = getIntent().getBooleanExtra("first_account", false);
        LogUtils.d("WalletBackupActivity", "walletMnemonic:" + walletMnemonic);
    }

    @Override
    public void configViews() {
        btnSkip.setText(R.string.wallet_backup_skip_btn);
        btnBackup.setOnClickListener(this);
        btnBackupHelp.setOnClickListener(this);
        btnSkip.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_backup:
                inputPwdDialog = new InputPwdDialog(this);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.isEmpty(pwd)) {
                            ToastUtils.showToast(R.string.input_pwd_dialog_tip);
                            return;
                        }

                        if (TextUtils.equals(pwd, walletPwd)) {

                            if (firstAccount) {
                                Intent intent = new Intent(WalletBackupActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            Intent intent = new Intent(WalletBackupActivity.this, MnemonicBackupActivity.class);
                            intent.putExtra("walletId", walletId);
                            intent.putExtra("walletMnemonic", walletMnemonic);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
                inputPwdDialog.show();
                break;
            case R.id.iv_btn:
                if (firstAccount) {
                    Intent intent = new Intent(WalletBackupActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }

                finish();
                break;
            case R.id.btn_backup_help:
                break;
        }
    }

}
