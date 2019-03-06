package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SystemSettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_system_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_title);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_language, R.id.rl_currency, R.id.rl_net, R.id.rl_gesture})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.rl_language:
                intent = new Intent(this, LanguageSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_currency:
                intent = new Intent(this, CurrencyUnitSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_net:
                intent = new Intent(this, NetSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_gesture:
                intent = new Intent(this, WalletMangerActivity.class);
                startActivity(intent);
                break;
        }
    }
}
