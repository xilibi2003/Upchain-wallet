package pro.upchain.wallet.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.Address;
import pro.upchain.wallet.entity.ConfirmationType;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.view.ConfirmTransactionView;
import pro.upchain.wallet.view.InputPwdView;
import pro.upchain.wallet.viewmodel.ConfirmationViewModel;
import pro.upchain.wallet.viewmodel.ConfirmationViewModelFactory;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SendActivity extends BaseActivity implements View.OnClickListener {

    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;

    TextView tvTitle;

    ImageView ivBtn;
    LinearLayout rlBtn;

    EditText etTransferAddress;

    EditText amountText;

    LinearLayout llyContacts;
    SeekBar seekbar;

    TextView tvGasCost;

    TextView tvGasPrice;

    LinearLayout llyGas;
    EditText etHexData;
    LinearLayout llyAdvanceParam;


    Switch advancedSwitch;

    EditText customGasPrice;

    EditText customGasLimit;
    private LinearLayout llyBack;
    private Toolbar commonToolbar;
    private EditText sendAmount;
    private TextView btnNext;

    @Override
    public void initView() {
        amountText = findViewById(R.id.send_amount);
        tvTitle = findViewById(R.id.tv_title);
        ivBtn = findViewById(R.id.iv_btn);
        rlBtn = findViewById(R.id.rl_btn);
        etTransferAddress = findViewById(R.id.et_transfer_address);
        llyContacts = findViewById(R.id.lly_contacts);
        tvGasCost = findViewById(R.id.tv_gas_cost);
        seekbar = findViewById(R.id.seekbar);
        tvGasPrice = findViewById(R.id.gas_price);
        llyGas = findViewById(R.id.lly_gas);
        customGasPrice = findViewById(R.id.custom_gas_price);
        customGasLimit = findViewById(R.id.custom_gas_limit);
        etHexData = findViewById(R.id.et_hex_data);
        llyAdvanceParam = findViewById(R.id.lly_advance_param);
        advancedSwitch = findViewById(R.id.advanced_switch);


        llyBack = findViewById(R.id.lly_back);
        commonToolbar = findViewById(R.id.common_toolbar);
        sendAmount = findViewById(R.id.send_amount);
        btnNext = findViewById(R.id.btn_next);
    }

    private String walletAddr;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;

    private String netCost;
    private BigInteger gasPrice;
    private BigInteger gasLimit;


    private boolean sendingTokens = false;

    private Dialog dialog;

    private static final int QRCODE_SCANNER_REQUEST = 1100;

    private static final double miner_min = 5;
    private static final double miner_max = 55;

    private String scanResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_transfer;
    }

    @Override
    public void initToolBar() {
        ivBtn.setImageResource(R.drawable.ic_transfer_scanner);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {


        Intent intent = getIntent();
        walletAddr = intent.getStringExtra(C.EXTRA_ADDRESS);

        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);

        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.ETH_SYMBOL : symbol;

        tvTitle.setText(symbol + getString(R.string.transfer_title));

        confirmationViewModelFactory = new ConfirmationViewModelFactory();
        viewModel = ViewModelProviders.of(this, confirmationViewModelFactory)
                .get(ConfirmationViewModel.class);

        viewModel.sendTransaction().observe(this, this::onTransaction);
        viewModel.gasSettings().observe(this, this::onGasSettings);
        viewModel.progress().observe(this, this::onProgress);
        viewModel.error().observe(this, this::onError);

        // 首页直接扫描进入
        scanResult = intent.getStringExtra("scan_result");
        if (!TextUtils.isEmpty(scanResult)) {
            parseScanResult(scanResult);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.prepare(this, sendingTokens ? ConfirmationType.ETH : ConfirmationType.ERC20);
    }

    @Override
    public void configViews() {
        advancedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llyAdvanceParam.setVisibility(View.VISIBLE);
                    llyGas.setVisibility(View.GONE);

                    customGasPrice.setText(Convert.fromWei(new BigDecimal(gasPrice), Convert.Unit.GWEI).toString());
                    customGasLimit.setText(gasLimit.toString());

                } else {
                    llyAdvanceParam.setVisibility(View.GONE);
                    llyGas.setVisibility(View.VISIBLE);

                }
            }
        });


        final DecimalFormat gasformater = new DecimalFormat();
        //保留几位小数
        gasformater.setMaximumFractionDigits(2);
        //模式  四舍五入
        gasformater.setRoundingMode(RoundingMode.CEILING);


        final String etherUnit = getString(R.string.transfer_ether_unit);


        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                double p = progress / 100f;
                double d = (miner_max - miner_min) * p + miner_min;

                gasPrice = BalanceUtils.gweiToWei(BigDecimal.valueOf(d));
                tvGasPrice.setText(gasformater.format(d) + " " + C.GWEI_UNIT);

                updateNetworkFee();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekbar.setProgress(10);
        try {
            netCost = BalanceUtils.weiToEth(gasPrice.multiply(gasLimit), 4) + etherUnit;
        } catch (Exception e) {
        }

        customGasPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    return;
                }
                gasPrice = BalanceUtils.gweiToWei(new BigDecimal(s.toString()));

                try {
                    netCost = BalanceUtils.weiToEth(gasPrice.multiply(gasLimit), 4) + etherUnit;
                    tvGasCost.setText(String.valueOf(netCost));
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        customGasLimit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                gasLimit = new BigInteger(s.toString());

                updateNetworkFee();
            }
        });
        rlBtn.setOnClickListener(this);
        btnNext.setOnClickListener(this);

    }

    private void updateNetworkFee() {

        try {
            netCost = BalanceUtils.weiToEth(gasPrice.multiply(gasLimit), 4) + " " + C.ETH_SYMBOL;
            tvGasCost.setText(String.valueOf(netCost));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void onGasSettings(GasSettings gasSettings) {
        gasPrice = gasSettings.gasPrice;
        gasLimit = gasSettings.gasLimit;

    }

    private boolean verifyInfo(String address, String amount) {

        try {
            new Address(address);
        } catch (Exception e) {
            ToastUtils.showToast(R.string.addr_error_tips);
            return false;
        }

        try {
            String wei = BalanceUtils.EthToWei(amount);
            return wei != null;
        } catch (Exception e) {
            ToastUtils.showToast(R.string.amount_error_tips);

            return false;
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                Intent intent = new Intent(SendActivity.this, QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                break;
            case R.id.btn_next:

                // confirm info;
                String toAddr = etTransferAddress.getText().toString().trim();
                String amount = amountText.getText().toString().trim();

                if (verifyInfo(toAddr, amount)) {
                    ConfirmTransactionView confirmView = new ConfirmTransactionView(this, this::onClick);
                    confirmView.fillInfo(walletAddr, toAddr, " - " + amount + " " + symbol, netCost, gasPrice, gasLimit);

                    dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                    dialog.setContentView(confirmView);
                    dialog.setCancelable(true);
                    dialog.setCanceledOnTouchOutside(true);
                    dialog.show();
                }

                break;
            case R.id.confirm_button:
                // send
                dialog.hide();

                InputPwdView pwdView = new InputPwdView(this, pwd -> {
                    Log.i("CONFIRM_BUTTON", "onClick: 开始交易:sendingTokens->" + sendingTokens);
                  new Thread(()->{
                      if (sendingTokens) {
                          viewModel.createTokenTransfer(pwd,
                                  etTransferAddress.getText().toString().trim(),
                                  contractAddress,
                                  BalanceUtils.tokenToWei(new BigDecimal(amountText.getText().toString().trim()), decimals).toBigInteger(),
                                  gasPrice,
                                  gasLimit
                          );
                      } else {
                          viewModel.createTransaction(pwd, etTransferAddress.getText().toString().trim(),
                                  Convert.toWei(amountText.getText().toString().trim(), Convert.Unit.ETHER).toBigInteger(),
                                  gasPrice,
                                  gasLimit);
                      }
                  }).start();
                });

                dialog = new BottomSheetDialog(this);
                dialog.setContentView(pwdView);
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();

                break;
        }
    }


    private void onProgress(boolean shouldShowProgress) {
        hideDialog();
        if (shouldShowProgress) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.title_dialog_sending)
                    .setView(new ProgressBar(this))
                    .setCancelable(false)
                    .create();
            dialog.show();
        }
    }

    private void onError(ErrorEnvelope error) {
        hideDialog();
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error_transaction_failed)
                .setMessage(error.message)
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    // Do nothing
                })
                .create();
        dialog.show();
    }

    private void onTransaction(String hash) {
        hideDialog();
        dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.transaction_succeeded)
                .setMessage(hash)
                .setPositiveButton(R.string.button_ok, (dialog1, id) -> {
                    finish();
                })
                .setNeutralButton(R.string.copy, (dialog1, id) -> {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("transaction hash", hash);
                    clipboard.setPrimaryClip(clip);
                    finish();
                })
                .create();
        dialog.show();
    }

    private void fillAddress(String addr) {
        try {
            new Address(addr);
            etTransferAddress.setText(addr);
        } catch (Exception e) {
            ToastUtils.showToast(R.string.addr_error_tips);
        }
    }

    private void parseScanResult(String result) {
        if (result.contains(":") && result.contains("?")) {  // 符合协议格式
            String[] urlParts = result.split(":");
            if (urlParts[0].equals("ethereum")) {
                urlParts = urlParts[1].split("\\?");

                fillAddress(urlParts[0]);

                // ?contractAddress=0xdxx & decimal=1 & value=100000
//                 String[] params = urlParts[1].split("&");
//                for (String param : params) {
//                    String[] keyValue = param.split("=");
//                }

            }


        } else {  // 无格式， 只有一个地址
            fillAddress(result);
        }


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                // 对扫描结果进行处理
                parseScanResult(scanResult);
//                ToastUtils.showLongToast(scanResult);
            }
        }
    }

}
