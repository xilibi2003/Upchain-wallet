package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class Web3SettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.et_wallet_service_url)
    EditText etWalletServiceUrl;
    @BindView(R.id.tv_reset_url)
    TextView tvResetUrl;
    @BindView(R.id.iv_btn)
    TextView tvBtn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_web3_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_language);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.tv_reset_url, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_reset_url:
                etWalletServiceUrl.setText(R.string.web3_setting_wallet_service_url_hint);
                break;
            case R.id.rl_btn:// 设置语言并保存
                finish();
                break;
        }
    }

}
