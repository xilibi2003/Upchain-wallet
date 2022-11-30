package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import pro.upchain.wallet.UpChainWalletApp;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.repository.SharedPreferenceRepository;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class LanguageSettingActivity extends BaseActivity {
    TextView tvTitle;
    TextView tvBtn;
    LinearLayout rlBtn;
    ImageView ivChinese;
    ImageView ivEnglish;
    // 0为简体中文 1 English
    private int language = 0;
    private TextView ivBtn;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        ivBtn = findViewById(R.id.iv_btn);
        rlBtn = findViewById(R.id.rl_btn);
        ivChinese = findViewById(R.id.iv_chinese);
        ivEnglish = findViewById(R.id.iv_English);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_language_setting;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.system_setting_language);
        rlBtn.setVisibility(View.VISIBLE);
        tvBtn.setText(R.string.language_setting_save);
    }

    @Override
    public void initDatas() {
        language = UpChainWalletApp.sp.getCurrencyUnit();
        if (language == 0) {
            ivChinese.setVisibility(View.VISIBLE);
            ivEnglish.setVisibility(View.GONE);
        } else if (language == 1) {
            ivChinese.setVisibility(View.GONE);
            ivEnglish.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_chinese, R.id.rl_english, R.id.rl_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_chinese:
                language = 0;
                ivChinese.setVisibility(View.VISIBLE);
                ivEnglish.setVisibility(View.GONE);
                break;
            case R.id.rl_english:
                ivChinese.setVisibility(View.GONE);
                ivEnglish.setVisibility(View.VISIBLE);
                language = 1;
                break;
            case R.id.rl_btn:// 设置语言并保存
//                SharedPreferencesUtil.getInstance().putInt("language", language);
                finish();
                break;
        }
    }
}
