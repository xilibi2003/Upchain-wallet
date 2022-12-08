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
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.UUi;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.view.LoadWalletSelectStandardPopupWindow;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ImportMnemonicFragment extends BaseFragment {

    EditText etMnemonic;
    EditText etStandard;
    EditText etWalletPwd;
    EditText etWalletPwdAgain;
    EditText etWalletPwdReminderInfo;
    CheckBox cbAgreement;
    TextView tvAgreement;
    LinearLayout llyWalletAgreement;
    TextView btnLoadWallet;
    LinearLayout llyStandardMenu;

    CreateWalletInteract createWalletInteract;

    private LoadWalletSelectStandardPopupWindow popupWindow;

    private String ethType = ETHWalletUtils.ETH_JAXX_TYPE;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_mnemonic;
    }

    @Override
    public void initView() {

        etMnemonic = parentView.findViewById(R.id.et_mnemonic);
        etStandard = parentView.findViewById(R.id.et_standard);
        etWalletPwd = parentView.findViewById(R.id.et_wallet_pwd);
        etWalletPwdAgain = parentView.findViewById(R.id.et_wallet_pwd_again);
        etWalletPwdReminderInfo = parentView.findViewById(R.id.et_wallet_pwd_reminder_info);
        cbAgreement = parentView.findViewById(R.id.cb_agreement);
        tvAgreement = parentView.findViewById(R.id.tv_agreement);
        llyWalletAgreement = parentView.findViewById(R.id.lly_wallet_agreement);
        btnLoadWallet = parentView.findViewById(R.id.btn_load_wallet);
        llyStandardMenu = parentView.findViewById(R.id.lly_standard_menu);
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
        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {

                    String mnemonic = etMnemonic.getText().toString().trim();
                    String walletPwd = etWalletPwd.getText().toString().trim();
                    String confirmPwd = etWalletPwdAgain.getText().toString().trim();
                    String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
                    boolean verifyWalletInfo = verifyInfo(mnemonic, walletPwd, confirmPwd, pwdReminder);
                    LogUtils.i(verifyWalletInfo);
                    if (verifyWalletInfo) {
                        btnLoadWallet.setEnabled(true);
                    }
                } else {
                    btnLoadWallet.setEnabled(false);
                }
            }
        });


        popupWindow = new LoadWalletSelectStandardPopupWindow(getContext());
        popupWindow.setOnPopupItemSelectedListener(new LoadWalletSelectStandardPopupWindow.OnPopupItemSelectedListener() {
            @Override
            public void onSelected(int selection) {
                switch (selection) {
                    case 0:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard);
                        ethType = ETHWalletUtils.ETH_JAXX_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 1:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_ledger);
                        ethType = ETHWalletUtils.ETH_LEDGER_TYPE;
                        etStandard.setEnabled(false);
                        break;
                    case 2:
                        etStandard.setText(R.string.load_wallet_by_mnemonic_standard_custom);
                        ethType = ETHWalletUtils.ETH_CUSTOM_TYPE;
                        etStandard.setEnabled(true);
                        etStandard.setFocusable(true);
                        etStandard.setFocusableInTouchMode(true);
                        etStandard.requestFocus();
                        break;

                }
            }
        });

        btnLoadWallet.setOnClickListener(view -> {
            String mnemonic = etMnemonic.getText().toString().trim();
            String walletPwd = etWalletPwd.getText().toString().trim();
            String confirmPwd = etWalletPwdAgain.getText().toString().trim();
            String pwdReminder = etWalletPwdReminderInfo.getText().toString().trim();
            boolean verifyWalletInfo = verifyInfo(mnemonic, walletPwd, confirmPwd, pwdReminder);
            if (verifyWalletInfo) {
                showDialog(getString(R.string.loading_wallet_tip));
                createWalletInteract.loadWalletByMnemonic(ethType, mnemonic, walletPwd).subscribe(this::loadSuccess, this::onError);
            }
        });

        llyStandardMenu.setOnClickListener(view -> {
            LogUtils.i("LoadWallet", "lly_standard_menu");
            popupWindow.showAsDropDown(etStandard, 0, UUi.dip2px(10));
        });

    }

    public void loadSuccess(ETHWallet wallet) {

        dismissDialog();
        ToastUtils.showToast("导入钱包成功");

        getActivity().finish();

    }

    public void onError(Throwable e) {
        ToastUtils.showToast("导入钱包失败");
        dismissDialog();
    }



    private boolean verifyInfo(String mnemonic, String walletPwd, String confirmPwd, String pwdReminder) {
        if (TextUtils.isEmpty(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (!WalletDaoUtils.isValid(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_by_mnemonic_input_tip);
            return false;
        } else if (WalletDaoUtils.checkRepeatByMenmonic(mnemonic)) {
            ToastUtils.showToast(R.string.load_wallet_already_exist);
            return false;
        }
//        else if (TextUtils.isEmpty(walletPwd)) {
//            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
//            // 同时判断强弱
//            return false;
//        }
        else if (TextUtils.isEmpty(confirmPwd) || !TextUtils.equals(confirmPwd, walletPwd)) {
            ToastUtils.showToast(R.string.create_wallet_pwd_confirm_input_tips);
            return false;
        }
        return true;
    }

}
