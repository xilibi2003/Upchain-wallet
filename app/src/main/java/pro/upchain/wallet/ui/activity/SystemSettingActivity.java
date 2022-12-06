package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SystemSettingActivity extends BaseActivity implements View.OnClickListener {
    TextView tvTitle;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
    }

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
        findViewById(R.id.rl_language).setOnClickListener(this);
        findViewById(R.id.rl_currency).setOnClickListener(this);
        findViewById(R.id.rl_net).setOnClickListener(this);
        findViewById(R.id.rl_gesture).setOnClickListener(this);

    }

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
