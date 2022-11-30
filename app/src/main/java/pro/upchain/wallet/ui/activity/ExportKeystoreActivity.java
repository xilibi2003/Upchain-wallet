package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
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
import pro.upchain.wallet.ui.adapter.DeriveKeystorePageFragmentAdapter;
import pro.upchain.wallet.ui.fragment.ExportKeystoreQRCodeFragment;
import pro.upchain.wallet.ui.fragment.ExportKeystoreStringFragment;
import pro.upchain.wallet.utils.UUi;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ExportKeystoreActivity extends BaseActivity {
    TextView tvTitle;
    ScrollIndicatorView indicatorView;
    ViewPager vpLoadWallet;

    private List<BaseFragment> fragmentList = new ArrayList<>();
    private IndicatorViewPager indicatorViewPager;

    private DeriveKeystorePageFragmentAdapter deriveKeystorePageFragmentAdapter;

    private ExportKeystoreStringFragment deriveKeystoreStringFragment;
    private ExportKeystoreQRCodeFragment deriveKeystoreQRCodeFragment;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        indicatorView = findViewById(R.id.indicator_view);
        vpLoadWallet = findViewById(R.id.vp_load_wallet);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_import_wallet;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.derive_keystore_title);
    }

    @Override
    public void initDatas() {
        Intent intent = getIntent();
        String walletKeystore = intent.getStringExtra("walletKeystore");
        deriveKeystoreStringFragment = new ExportKeystoreStringFragment();
        deriveKeystoreQRCodeFragment = new ExportKeystoreQRCodeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("walletKeystore",walletKeystore);//这里的values就是我们要传的值
        deriveKeystoreStringFragment.setArguments(bundle);
        deriveKeystoreQRCodeFragment.setArguments(bundle);
        fragmentList.add(deriveKeystoreStringFragment);
        fragmentList.add(deriveKeystoreQRCodeFragment);
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
        deriveKeystorePageFragmentAdapter = new DeriveKeystorePageFragmentAdapter(this, getSupportFragmentManager(), fragmentList);
        indicatorViewPager.setAdapter(deriveKeystorePageFragmentAdapter);
        indicatorViewPager.setCurrentItem(0, false);
        vpLoadWallet.setOffscreenPageLimit(4);
    }

}
