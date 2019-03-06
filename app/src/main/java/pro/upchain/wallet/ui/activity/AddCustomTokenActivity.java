package pro.upchain.wallet.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.Address;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.viewmodel.AddTokenViewModel;
import pro.upchain.wallet.viewmodel.AddTokenViewModelFactory;
import com.gyf.barlibrary.ImmersionBar;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny熊
 * WeiXin: xlbxiong
 */

public class AddCustomTokenActivity extends BaseActivity {

    // 0xB8c77482e45F1F44dE1745F52C74426C631bDD52  BNB 18

    protected AddTokenViewModelFactory addTokenViewModelFactory;
    private AddTokenViewModel viewModel;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.address)
    TextView address;

    @BindView(R.id.symbol)
    TextView symbol;

    @BindView(R.id.decimals)
    TextView decimals;

    @BindView(R.id.common_toolbar)
    Toolbar commonToolbar;

    @BindView(R.id.save)
    TextView save;

    @BindView(R.id.address_input_layout)
    TextInputLayout addressLayout;

    @BindView(R.id.symbol_input_layout)
    TextInputLayout symbolLayout;

    @BindView(R.id.decimal_input_layout)
    TextInputLayout decimalsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_add_token;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.add_new_property_title);
    }

    @Override
    public void initDatas() {


        addTokenViewModelFactory = new AddTokenViewModelFactory();
        viewModel = ViewModelProviders.of(this, addTokenViewModelFactory)
                .get(AddTokenViewModel.class);
//        viewModel.progress().observe(this, systemView::showProgress);
        viewModel.error().observe(this, this::onError);
        viewModel.result().observe(this, this::onSaved);

    }

    private void onError(ErrorEnvelope errorEnvelope) {
//        showDialog("出错~");

    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .titleBar(commonToolbar, false)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .navigationBarColor(R.color.white)
                .init();
    }


    private void onSave() {
        boolean isValid = true;
        String address = this.address.getText().toString();
        String symbol = this.symbol.getText().toString();
        String rawDecimals = this.decimals.getText().toString();
        int decimals = 0;

        if (TextUtils.isEmpty(address)) {
            addressLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(symbol)) {
            symbolLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        if (TextUtils.isEmpty(rawDecimals)) {
            decimalsLayout.setError(getString(R.string.error_field_required));
            isValid = false;
        }

        try {
            decimals = Integer.valueOf(rawDecimals);
        } catch (NumberFormatException ex) {
            decimalsLayout.setError(getString(R.string.error_must_numeric));
            isValid = false;
        }

        if (!Address.isAddress(address)) {
            addressLayout.setError(getString(R.string.error_invalid_address));
            isValid = false;
        }

        if (isValid) {
            LogUtils.d("viewModel.save");
            viewModel.save(address, symbol, decimals);
        }
    }

    private void onSaved(boolean result) {
        if (result) {
            viewModel.showTokens(this);

            Intent intent = new Intent(this, MainActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivity(intent);

            finish();
        }
    }


    @OnClick({R.id.save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                onSave();
                break;
        }
    }

}
