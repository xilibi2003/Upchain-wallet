package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputLayout;
import com.gyf.barlibrary.ImmersionBar;

import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.Address;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.viewmodel.AddTokenViewModel;
import pro.upchain.wallet.viewmodel.AddTokenViewModelFactory;

/**
 * Created by Tiny熊
 * WeiXin: xlbxiong
 */

public class AddCustomTokenActivity extends BaseActivity {

    // 0xB8c77482e45F1F44dE1745F52C74426C631bDD52  BNB 18

    protected AddTokenViewModelFactory addTokenViewModelFactory;
    private AddTokenViewModel viewModel;

    TextView tvTitle;

    TextView address;

    TextView symbol;

    TextView decimals;

    Toolbar commonToolbar;

    TextView save;

    TextInputLayout addressLayout;

    TextInputLayout symbolLayout;

    TextInputLayout decimalsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        tvTitle = findViewById(R.id.tv_title);
        address = findViewById(R.id.address);
        symbol = findViewById(R.id.symbol);
        decimals = findViewById(R.id.decimals);
        save = findViewById(R.id.save);
        commonToolbar = findViewById(R.id.common_toolbar);
        addressLayout = findViewById(R.id.address_input_layout);
        symbolLayout = findViewById(R.id.symbol_input_layout);
        decimalsLayout = findViewById(R.id.decimal_input_layout);
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
