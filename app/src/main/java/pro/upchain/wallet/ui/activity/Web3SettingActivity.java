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


public class Web3SettingActivity extends BaseActivity implements View.OnClickListener {
    TextView tvTitle;
    LinearLayout rlBtn;
    EditText etWalletServiceUrl;
    TextView tvResetUrl;
    TextView tvBtn;
    private TextView ivBtn;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        ivBtn = findViewById(R.id.iv_btn);
        rlBtn = findViewById(R.id.rl_btn);
        etWalletServiceUrl = findViewById(R.id.et_wallet_service_url);
        tvResetUrl = findViewById(R.id.tv_reset_url);
    }

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
        tvResetUrl.setOnClickListener(this);
        rlBtn.setOnClickListener(this);
    }

    @Override
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
