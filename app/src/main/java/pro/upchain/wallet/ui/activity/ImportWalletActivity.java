package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.ScrollIndicatorView;
import com.shizhefei.view.indicator.slidebar.TextWidthColorBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.ui.adapter.LoadWalletPageFragmentAdapter;
import pro.upchain.wallet.ui.fragment.ImportKeystoreFragment;
import pro.upchain.wallet.ui.fragment.ImportMnemonicFragment;
import pro.upchain.wallet.ui.fragment.ImportPrivateKeyFragment;
import pro.upchain.wallet.utils.UUi;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ImportWalletActivity extends BaseActivity {

    TextView tvTitle;
    ImageView ivBtn;
    LinearLayout rlBtn;
    ScrollIndicatorView indicatorView;
    ViewPager vpLoadWallet;

    boolean firstAccount;

    private List<BaseFragment> fragmentList = new ArrayList<>();
    private LoadWalletPageFragmentAdapter loadWalletPageFragmentAdapter;
    private IndicatorViewPager indicatorViewPager;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        ivBtn = findViewById(R.id.iv_btn);
        rlBtn = findViewById(R.id.rl_btn);
        indicatorView = findViewById(R.id.indicator_view);
        vpLoadWallet = findViewById(R.id.vp_load_wallet);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_wallet;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.load_wallet_title);
        ivBtn.setImageResource(R.drawable.ic_transfer_scanner);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {

        firstAccount = getIntent().getBooleanExtra("first_account", false);

        fragmentList.add(new ImportMnemonicFragment());
        fragmentList.add(new ImportKeystoreFragment());
        fragmentList.add(new ImportPrivateKeyFragment());
    }

    @Override
    public void configViews() {
        indicatorView.setSplitAuto(true);
        indicatorView.setOnTransitionListener(new OnTransitionTextListener()
                .setColor(getResources().getColor(R.color.transfer_advanced_setting_help_text_color), getResources().getColor(R.color.discovery_application_item_name_color))
                .setSize(14, 14));
        indicatorView.setScrollBar(new TextWidthColorBar(this, indicatorView, getResources().getColor(R.color.transfer_advanced_setting_help_text_color), UUi.dip2px(2)));
        indicatorView.setScrollBarSize(50);
        indicatorViewPager = new IndicatorViewPager(indicatorView, vpLoadWallet);
        loadWalletPageFragmentAdapter = new LoadWalletPageFragmentAdapter(this, getSupportFragmentManager(), fragmentList);
        indicatorViewPager.setAdapter(loadWalletPageFragmentAdapter);
        indicatorViewPager.setCurrentItem(0, false);
        vpLoadWallet.setOffscreenPageLimit(4);
    }

    @Override
    public void finish() {
        super.finish();

        if (firstAccount) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        // TODO
    }
}
