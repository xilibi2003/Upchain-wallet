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
import pro.upchain.wallet.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ImportKeystoreFragment extends BaseFragment implements View.OnClickListener {

    EditText etKeystore;
    EditText etWalletPwd;
    CheckBox cbAgreement;
    TextView btnLoadWallet;

    CreateWalletInteract createWalletInteract;


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_load_wallet_by_official_wallet;
    }

    @Override
    public void initView() {

        etKeystore = parentView.findViewById(R.id.et_keystore);
        etWalletPwd = parentView.findViewById(R.id.et_wallet_pwd);
        cbAgreement = parentView.findViewById(R.id.cb_agreement);
        btnLoadWallet = parentView.findViewById(R.id.btn_load_wallet);
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
        parentView.findViewById(R.id.btn_load_wallet).setOnClickListener(this);

        cbAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (verifyInfo()) {
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

                String walletPwd = etWalletPwd.getText().toString().trim();
                String keystore = etKeystore.getText().toString().trim();

                if (verifyInfo()) {
                    showDialog(getString(R.string.loading_wallet_tip));
                    createWalletInteract.loadWalletByKeystore(keystore, walletPwd).subscribe(this::loadSuccess, this::onError);
                }
                break;
        }
    }

    private boolean verifyInfo() {

        String walletPwd = etWalletPwd.getText().toString().trim();
        String keystore = etKeystore.getText().toString().trim();

        if (TextUtils.isEmpty(keystore)) {
            ToastUtils.showToast(R.string.load_wallet_by_official_wallet_keystore_input_tip);
            return false;
        }
        // 导入时，可以为空
//        else if (TextUtils.isEmpty(walletPwd)) {
//            ToastUtils.showToast(R.string.create_wallet_pwd_input_tips);
//            // 同时判断强弱
//            return false;
//        }
        return true;
    }

    public void loadSuccess(ETHWallet wallet) {
        ToastUtils.showToast("导入钱包成功");
        dismissDialog();

        getActivity().finish();


    }

    public void onError(Throwable e) {
        ToastUtils.showToast("导入钱包失败");
        dismissDialog();
    }

}
