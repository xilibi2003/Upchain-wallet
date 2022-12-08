package pro.upchain.wallet.ui.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.interact.CreateWalletInteract;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.Hex;
import pro.upchain.wallet.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ImportPrivateKeyFragment extends BaseFragment implements View.OnClickListener  {
    EditText etPrivateKey;
    EditText etWalletPwd;
    EditText etWalletPwdAgain;
    EditText etWalletPwdReminderInfo;
    CheckBox cbAgreement;
    TextView tvAgreement;
    LinearLayout llyWalletAgreement;
    TextView btnLoadWallet;
    TextView btnHelp;

    CreateWalletInteract createWalletInteract;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_private_key;
    }

    @Override
    public void initView() {

        etPrivateKey = parentView. findViewById(R.id.et_private_key);
        etWalletPwd = parentView. findViewById(R.id.et_wallet_pwd);
        etWalletPwdAgain = parentView. findViewById(R.id.et_wallet_pwd_again);
        etWalletPwdReminderInfo = parentView. findViewById(R.id.et_wallet_pwd_reminder_info);
        cbAgreement =parentView.  findViewById(R.id.cb_agreement);
        tvAgreement = parentView. findViewById(R.id.tv_agreement);
        llyWalletAgreement = parentView. findViewById(R.id.lly_wallet_agreement);
        btnLoadWallet = parentView. findViewById(R.id.btn_load_wallet);
        btnHelp = parentView. findViewById(R.id.btn_help);
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        createWalletInteract = new CreateWalletInteract();
    }

    @Override
    public void configViews() {
        btnLoadWallet.setOnClickListener(this);

        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    String privateKey = etPrivateKey.getText().toString().trim();
                    String walletPwd = etWalletPwd.getText().toString().trim();
                    String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                    String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                    boolean verifyWalletInfo = verifyInfo(privateKey, walletPwd, confirmPwd, pwdReminder);
                    if (verifyWalletInfo) {
                        btnLoadWallet.setEnabled(true);
                    }
                } else {
                    btnLoadWallet.setEnabled(false);
                }
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_load_wallet:
                String privateKey = etPrivateKey.getText().toString().trim();
                String walletPwd = etWalletPwd.getText().toString().trim();
                String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                boolean verifyWalletInfo = verifyInfo(privateKey, walletPwd, confirmPwd, pwdReminder);
                if (verifyWalletInfo) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    createWalletInteract.loadWalletByPrivateKey(privateKey, walletPwd).subscribe(this::loadSuccess, this::onError);
                }
                break;
        }
    }

    private boolean verifyInfo(String privateKey, String walletPwd, String confirmPwd, String pwdReminder) {
        if (TextUtils.isEmpty(privateKey) || ETHWalletUtils.isTooSimplePrivateKey(privateKey)) {
            ToastUtils.showToast(R.string.load_wallet_by_private_key_input_tip);
            return false;
        } else if (TextUtils.isEmpty(walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
            // 同时判断强弱
            return false;
        } else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }

    public void loadSuccess(ETHWallet wallet) {
        ToastUtils.showToast("导入钱包成功");
        dismissDialog();

        getActivity().finish();

    }

    private void onError(Throwable error) {
        ToastUtils.showToast(R.string.load_wallet_by_private_key_input_tip);
        dismissDialog();
    }


}
