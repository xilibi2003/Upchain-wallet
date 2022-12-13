package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.repository.SharedPreferenceRepository;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class CurrencyUnitSettingActivity extends BaseActivity implements View.OnClickListener  {
    TextView tvTitle;
    TextView tvBtn;
    LinearLayout rlBtn;
    ImageView ivCNY;
    ImageView ivUSD;
    // 0为CNY 1 USD
    private int currencyUnit = 0;

    @Override
    public void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvBtn = findViewById(R.id.iv_btn);
        rlBtn = findViewById(R.id.rl_btn);
        ivCNY = findViewById(R.id.iv_cny);
        ivUSD = findViewById(R.id.iv_usd);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_currency_unit_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_currency);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
    }

    @Override
    public void initDatas() {
        currencyUnit = SharedPreferenceRepository.instance(this.mContext).getCurrencyUnit();
        if (currencyUnit == 0) {
            ivCNY.setVisibility(View.VISIBLE);
            ivUSD.setVisibility(View.GONE);
        } else if (currencyUnit == 1) {
            ivCNY.setVisibility(View.GONE);
            ivUSD.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {
        ivCNY.setOnClickListener(this);
        ivUSD.setOnClickListener(this);
        rlBtn.setOnClickListener(this);

        findViewById(R.id.rl_cny).setOnClickListener(this);
        findViewById(R.id.rl_usd).setOnClickListener(this);
        findViewById(R.id.rl_btn).setOnClickListener(this);
    }

    @OnClick({R.id.rl_cny, R.id.rl_usd, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_cny:
                currencyUnit = 0;
                ivCNY.setVisibility(View.VISIBLE);
                ivUSD.setVisibility(View.GONE);
                break;
            case R.id.rl_usd:
                currencyUnit = 1;
                ivCNY.setVisibility(View.GONE);
                ivUSD.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_btn:// 保存
                SharedPreferenceRepository.instance(this.mContext).setCurrencyUnit(currencyUnit);
                finish();
                break;
        }
    }
}
