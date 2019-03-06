package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.interact.ModifyWalletInteract;
import pro.upchain.wallet.utils.Md5Utils;
import pro.upchain.wallet.utils.TKeybord;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.view.InputPwdDialog;
import pro.upchain.wallet.view.PrivateKeyDerivetDialog;
import com.gyf.barlibrary.ImmersionBar;


import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong

 */
public class WalletDetailActivity extends BaseActivity {

    private static final int WALLET_DETAIL_RESULT = 2201;
    private static final int MODIFY_PASSWORD_REQUEST = 1102;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lly_back)
    LinearLayout llyBack;
    @BindView(R.id.iv_btn)
    TextView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.civ_wallet)
    CircleImageView civWallet;
    @BindView(R.id.tv_eth_balance)
    TextView tvEthBalance;
    @BindView(R.id.lly_wallet_property)
    LinearLayout llyWalletProperty;
    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;
    @BindView(R.id.et_wallet_name)
    EditText etWalletName;
    @BindView(R.id.rl_modify_pwd)
    RelativeLayout rlModifyPwd;
    @BindView(R.id.rl_derive_private_key)
    RelativeLayout rlDerivePrivateKey;
    @BindView(R.id.rl_derive_keystore)
    RelativeLayout rlDeriveKeystore;
    @BindView(R.id.btn_delete_wallet)
    TextView btnDeleteWallet;
    @BindView(R.id.btn_mnemonic_backup)
    TextView btnMnemonicBackup;
    private long walletId;
    private String walletPwd;
    private String walletAddress;
    private String walletName;
    private boolean walletIsBackup;
    private InputPwdDialog inputPwdDialog;
    private String walletMnemonic;
    private PrivateKeyDerivetDialog privateKeyDerivetDialog;
    private boolean fromList;
    private ModifyWalletInteract modifyWalletInteract;


    @Override
    public int getLayoutId() {
        return R.layout.activity_wallet_detail;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {
        modifyWalletInteract = new ModifyWalletInteract();
        inputPwdDialog = new InputPwdDialog(this);
        Intent intent = getIntent();
        walletId = intent.getLongExtra("walletId", -1);
        walletPwd = intent.getStringExtra("walletPwd");
        walletAddress = intent.getStringExtra("walletAddress");
        walletName = intent.getStringExtra("walletName");
        walletIsBackup = intent.getBooleanExtra("walletIsBackup", false);
        walletMnemonic = intent.getStringExtra("walletMnemonic");
        fromList = intent.getBooleanExtra("fromList", false);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();

        tvTitle.setText(walletName);
        etWalletName.setText(walletName);
        tvWalletAddress.setText(walletAddress);

    }


    @Override
    protected void onResume() {
        super.onResume();

        walletIsBackup = WalletDaoUtils.getWalletById(walletId).isBackup();

        if (walletIsBackup) {
            btnMnemonicBackup.setVisibility(View.GONE);
        } else {
            btnMnemonicBackup.setVisibility(View.VISIBLE);
        }
    }

    public void modifySuccess(boolean s) {

    }

    public void modifyPwdSuccess(ETHWallet wallet) {

    }

    public void showDerivePrivateKeyDialog(String privateKey) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                dismissDialog();
            }
        }, 1000);
        privateKeyDerivetDialog = new PrivateKeyDerivetDialog(WalletDetailActivity.this);
        privateKeyDerivetDialog.show();
        privateKeyDerivetDialog.setPrivateKey(privateKey);
    }

    public void showDeriveKeystore(String keystore) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                dismissDialog();
            }
        }, 1000);
        Intent intent = new Intent(this, ExportKeystoreActivity.class);
        intent.putExtra("walletKeystore", keystore);
        startActivity(intent);
    }

    public void deleteSuccess(boolean isDelete) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                //execute the task
                dismissDialog();
            }
        }, 2000);
        if (fromList) {
            setResult(WALLET_DETAIL_RESULT, new Intent());
            TKeybord.closeKeybord(etWalletName);
        }
        finish();
    }


    @OnClick({R.id.rl_btn, R.id.rl_modify_pwd, R.id.btn_mnemonic_backup
            , R.id.btn_delete_wallet, R.id.rl_derive_private_key
            , R.id.rl_derive_keystore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                String name = etWalletName.getText().toString().trim();
                if (!TextUtils.equals(this.walletName, name)) {
                    modifyWalletInteract.modifyWalletName(walletId, name).subscribe(this::modifySuccess);
                }
                showDialog(getString(R.string.saving_wallet_tip));
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
                        dismissDialog();
                        ToastUtils.showToast(R.string.wallet_detail_save_success);
                        setResult(WALLET_DETAIL_RESULT, new Intent());
                        TKeybord.closeKeybord(etWalletName);
                        finish();
                    }
                }, 2000);

                break;
            case R.id.rl_modify_pwd:// 修改密码
                Intent intent = new Intent(mContext, ModifyPasswordActivity.class);
                intent.putExtra("walletId", walletId);
                intent.putExtra("walletPwd", walletPwd);
                intent.putExtra("walletAddress", walletAddress);
                intent.putExtra("walletName", walletName);
                intent.putExtra("walletMnemonic", walletMnemonic);
                intent.putExtra("walletIsBackup", walletIsBackup);
                startActivityForResult(intent, MODIFY_PASSWORD_REQUEST);
                break;
            case R.id.rl_derive_private_key:// 导出明文私钥
                inputPwdDialog.show();
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        inputPwdDialog.dismiss();
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deriving_wallet_tip));
                            modifyWalletInteract.deriveWalletPrivateKey(walletId, pwd).subscribe(WalletDetailActivity.this::showDerivePrivateKeyDialog);

                        } else {
                            ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);
                        }
                    }
                });
                break;
            case R.id.rl_derive_keystore:// 导出明文keystore
                inputPwdDialog.show();
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        inputPwdDialog.dismiss();
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deriving_wallet_tip));
                            modifyWalletInteract.deriveWalletKeystore(walletId, pwd).subscribe(WalletDetailActivity.this::showDeriveKeystore);
                        } else {
                            ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);
                        }
                    }
                });
                break;
            case R.id.btn_mnemonic_backup:
                inputPwdDialog.show();
                inputPwdDialog.setDeleteAlertVisibility(false);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            Intent intent = new Intent(WalletDetailActivity.this, MnemonicBackupActivity.class);
                            intent.putExtra("walletId", walletId);
                            intent.putExtra("walletMnemonic", walletMnemonic);
                            startActivity(intent);
                        } else {
                            ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);
                        }
                        inputPwdDialog.dismiss();
                    }
                });
                break;

            case R.id.btn_delete_wallet:
                inputPwdDialog.show();
                inputPwdDialog.setDeleteAlertVisibility(true);
                inputPwdDialog.setOnInputDialogButtonClickListener(new InputPwdDialog.OnInputDialogButtonClickListener() {
                    @Override
                    public void onCancel() {
                        inputPwdDialog.dismiss();
                    }

                    @Override
                    public void onConfirm(String pwd) {
                        if (TextUtils.equals(walletPwd, Md5Utils.md5(pwd))) {
                            showDialog(getString(R.string.deleting_wallet_tip));
                            modifyWalletInteract.deleteWallet(walletId).subscribe(WalletDetailActivity.this::deleteSuccess);
                        } else {
                            ToastUtils.showToast(R.string.wallet_detail_wrong_pwd);
                            inputPwdDialog.dismiss();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MODIFY_PASSWORD_REQUEST) {
            if (data != null) {
                walletPwd = data.getStringExtra("newPwd");
            }
        }
    }
}
