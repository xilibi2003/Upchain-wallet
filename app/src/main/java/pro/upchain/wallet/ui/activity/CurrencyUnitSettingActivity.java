package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class CurrencyUnitSettingActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    TextView tvBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;
    @BindView(R.id.iv_cny)
    ImageView ivCNY;
    @BindView(R.id.iv_usd)
    ImageView ivUSD;
    // 0为CNY 1 USD
    private int currencyUnit = 0;

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
