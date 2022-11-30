package pro.upchain.wallet.ui.activity;

import static pro.upchain.wallet.C.PRUNE_ACTIVITY;
import static pro.upchain.wallet.entity.ConfirmationType.WEB3TRANSACTION;
import static pro.upchain.wallet.view.AWalletAlertDialog.ERROR;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.ConfirmationType;
import pro.upchain.wallet.entity.ErrorEnvelope;
import pro.upchain.wallet.entity.FinishReceiver;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.entity.TransactionData;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.view.AWalletAlertDialog;
import pro.upchain.wallet.view.GasAdvanceSettingsView;
import pro.upchain.wallet.view.InputPwdView;
import pro.upchain.wallet.viewmodel.ConfirmationViewModel;
import pro.upchain.wallet.viewmodel.ConfirmationViewModelFactory;
import pro.upchain.wallet.web3.entity.Web3Transaction;


public class ConfirmationActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = ConfirmationActivity.class.getSimpleName();

    AWalletAlertDialog dialog;
    Dialog bottomSheetDialog;

    ConfirmationViewModelFactory confirmationViewModelFactory;
    ConfirmationViewModel viewModel;

    private FinishReceiver finishReceiver;

    private TextView fromAddressText;
    private TextView toAddressText;
    private TextView valueText;
    private TextView symbolText;
    private TextView gasPriceText;
    private TextView gasLimitText;
    private TextView networkFeeText;
    private TextView contractAddrText;
    private TextView contractAddrLabel;
    private TextView websiteLabel;
    private TextView websiteText;
    private Button sendButton;
    private TextView title;

    private BigDecimal amount;
    private int decimals;
    private String contractAddress;
    private String amountStr;
    private String toAddress;

    private ConfirmationType confirmationType;
    private byte[] transactionBytes = null;
    private Web3Transaction transaction;
    private boolean isMainNet;
    private String networkName;

    Toolbar commonToolbar;

    TextView tvTitle;

    ImageView ivBtn;
    LinearLayout rlBtn;

    private GasSettings gasSettings;
    private Button btnNext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        ivBtn = findViewById(R.id.iv_btn);
        rlBtn = findViewById(R.id.rl_btn);
        commonToolbar = findViewById(R.id.common_toolbar);

        btnNext = findViewById(R.id.btn_next);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_confirm;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText("");

        ivBtn.setImageResource(R.drawable.ic_mine_setting);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {

        confirmationViewModelFactory = new ConfirmationViewModelFactory();
        viewModel = ViewModelProviders.of(this, confirmationViewModelFactory)
                .get(ConfirmationViewModel.class);


        viewModel.defaultWallet().observe(this, this::onDefaultWallet);
        viewModel.gasSettings().observe(this, this::onGasSettings);

        viewModel.sendTransaction().observe(this, this::onTransaction);
        viewModel.sendDappTransaction().observe(this, this::onDappTransaction);

        viewModel.progress().observe(this, this::onProgress);
        viewModel.error().observe(this, this::onError);

        finishReceiver = new FinishReceiver(this);

    }

    @Override
    public void configViews() {

        fromAddressText = findViewById(R.id.text_from);
        toAddressText = findViewById(R.id.text_to);
        valueText = findViewById(R.id.text_value);
        symbolText = findViewById(R.id.text_symbol);
        gasPriceText = findViewById(R.id.text_gas_price);
        gasLimitText = findViewById(R.id.text_gas_limit);
        networkFeeText = findViewById(R.id.text_network_fee);

        contractAddrText = findViewById(R.id.text_contract);
        contractAddrLabel = findViewById(R.id.label_contract);
        websiteLabel = findViewById(R.id.label_website);
        websiteText = findViewById(R.id.text_website);
        title = findViewById(R.id.title_confirm);


        transaction = getIntent().getParcelableExtra(C.EXTRA_WEB3TRANSACTION);

        toAddress = getIntent().getStringExtra(C.EXTRA_TO_ADDRESS);
        contractAddress = getIntent().getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        confirmationType = ConfirmationType.values()[getIntent().getIntExtra(C.TOKEN_TYPE, 0)];
        LogUtils.d(TAG, confirmationType);

        String ensName = getIntent().getStringExtra(C.EXTRA_ENS_DETAILS);
        amountStr = getIntent().getStringExtra(C.EXTRA_AMOUNT);
        decimals = getIntent().getIntExtra(C.EXTRA_DECIMALS, -1);
        String symbol = getIntent().getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.ETH_SYMBOL : symbol;
        String tokenList = getIntent().getStringExtra(C.EXTRA_TOKENID_LIST);
        amount = new BigDecimal(getIntent().getStringExtra(C.EXTRA_AMOUNT));

        String amountString = "";
        switch (confirmationType) {
            case ETH:
                amountString = "-" + BalanceUtils.subunitToBase(amount.toBigInteger(), decimals).toPlainString();
                symbolText.setText(symbol);
                transactionBytes = null;
                break;
            case ERC20:
//                contractAddrText.setVisibility(View.VISIBLE);
//                contractAddrLabel.setVisibility(View.VISIBLE);
//                contractAddrText.setText(contractAddress);
//                amountString = "-" + BalanceUtils.subunitToBase(amount.toBigInteger(), decimals).toPlainString();
//                symbolText.setText(symbol);
//                transactionBytes = TokenRepository.createTokenTransferData(toAddress, amount.toBigInteger());
                break;
            case WEB3TRANSACTION:

                title.setVisibility(View.VISIBLE);
                title.setText(R.string.confirm_dapp_transaction);
                toAddress = transaction.recipient.toString();
                if (transaction.contract != null) {
                    contractAddrText.setVisibility(View.VISIBLE);
                    contractAddrLabel.setVisibility(View.VISIBLE);
                    contractAddrText.setText(transaction.contract.toString());
                } else {
                    BigInteger addr = Numeric.toBigInt(transaction.recipient.toString());
                    if (addr.equals(BigInteger.ZERO)) //constructor
                    {
                        toAddress = getString(R.string.ticket_contract_constructor);
                    }
                }
                String urlRequester = getIntent().getStringExtra(C.EXTRA_CONTRACT_NAME);
                networkName = getIntent().getStringExtra(C.EXTRA_NETWORK_NAME);
                isMainNet = getIntent().getBooleanExtra(C.EXTRA_NETWORK_MAINNET, false);

                if (urlRequester != null) {
                    websiteLabel.setVisibility(View.VISIBLE);
                    websiteText.setVisibility(View.VISIBLE);
                    websiteText.setText(urlRequester);
                }

                BigDecimal ethAmount = Convert.fromWei(transaction.value.toString(10), Convert.Unit.ETHER);
                amountString = getEthString(ethAmount.doubleValue());
                symbolText.setText(C.ETH_SYMBOL);
                transactionBytes = Numeric.hexStringToByteArray(transaction.payload);

                break;

            case ERC721:
            case ERC875:
            case MARKET:
                break;
            default:
                amountString = "-" + BalanceUtils.subunitToBase(amount.toBigInteger(), decimals).toPlainString();
                symbolText.setText(symbol);
                transactionBytes = null;
                break;
        }

        if (ensName != null && ensName.length() > 0) {
            toAddressText.setText(ensName);
        } else {
            toAddressText.setText(toAddress);
        }

        valueText.setText(amountString);
        btnNext.setOnClickListener(this);
        rlBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_next: // show pwd

                LogUtils.d(TAG, "send_button click");

                InputPwdView pwdView = new InputPwdView(this, this::sendTransaction);
                bottomSheetDialog = new BottomSheetDialog(this);
                bottomSheetDialog.setContentView(pwdView);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.show();

                break;

            case R.id.rl_btn:  // gas advance

                GasAdvanceSettingsView gasSetingsView = new GasAdvanceSettingsView(this, (gasPrice, gasLimit) -> {
                    bottomSheetDialog.dismiss();
                    GasSettings settings = new GasSettings(gasPrice, gasLimit);
                    viewModel.overrideGasSettings(settings);
                });

                gasSetingsView.fill(gasSettings.gasPrice, gasSettings.gasLimit);

                bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                bottomSheetDialog.setContentView(gasSetingsView);
                bottomSheetDialog.setCancelable(true);
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.show();

                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.prepare(this, confirmationType);
    }

    private void onProgress(boolean shouldShowProgress) {
        hideDialog();
        if (shouldShowProgress) {
            dialog = new AWalletAlertDialog(this);
            dialog.setProgressMode();
            dialog.setTitle(R.string.title_dialog_sending);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    public void hideDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideDialog();
        unregisterReceiver(finishReceiver);
    }



    // 初始时、获取gas
    private void onGasSettings(GasSettings gasSettings) {
        LogUtils.d(TAG, "onGasSettings:" + gasSettings);
        this.gasSettings = gasSettings;

        String gasPrice = BalanceUtils.weiToGwei(gasSettings.gasPrice) + " " + C.GWEI_UNIT;
        gasPriceText.setText(gasPrice);
        gasLimitText.setText(gasSettings.gasLimit.toString());

        BigDecimal networkFeeBD = new BigDecimal(gasSettings
                .gasPrice.multiply(gasSettings.gasLimit));

        String networkFee = BalanceUtils.weiToEth(networkFeeBD).toPlainString() + " " + C.ETH_SYMBOL;
        networkFeeText.setText(networkFee);

        if (confirmationType == WEB3TRANSACTION) {
            //update amount
            BigDecimal ethValueBD = amount.add(networkFeeBD);

            //convert to ETH
            ethValueBD = Convert.fromWei(ethValueBD, Convert.Unit.ETHER);
            String valueUpdate = getEthString(ethValueBD.doubleValue()) + " ";
            valueText.setText(valueUpdate);
        }
    }


    private void sendTransaction(String pwd) {
        bottomSheetDialog.dismiss();

        switch (confirmationType) {
            case ETH:
                viewModel.createTransaction(
                        fromAddressText.getText().toString(),
                        toAddress,
                        amount.toBigInteger(),
                        gasSettings.gasPrice,
                        gasSettings.gasLimit);
                break;

            case ERC20:
                viewModel.createTokenTransfer(
                        fromAddressText.getText().toString(),
                        toAddress,
                        contractAddress,
                        amount.toBigInteger(),
                        gasSettings.gasPrice,
                        gasSettings.gasLimit);
                break;

            case ERC875:
            case ERC721:
            case MARKET:
                break;

            case WEB3TRANSACTION:
                viewModel.signWeb3DAppTransaction(transaction, gasSettings.gasPrice, gasSettings.gasLimit, pwd);
                break;

            default:
                break;
        }

    }

    private void onDefaultWallet(ETHWallet wallet) {
        fromAddressText.setText(wallet.address);
        switch (confirmationType) {
            case ERC875:
            case ERC721:
                viewModel.calculateGasSettings(transactionBytes, true);
                break;
            default:
                viewModel.calculateGasSettings(transactionBytes, false);
                break;
        }
    }

    private void onTransaction(String hash) {
        hideDialog();
        dialog = new AWalletAlertDialog(this);
        dialog.setTitle(R.string.transaction_succeeded);
        dialog.setMessage(hash);
        dialog.setButtonText(R.string.copy);
        dialog.setButtonListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("transaction hash", hash);
            clipboard.setPrimaryClip(clip);
            dialog.dismiss();
            sendBroadcast(new Intent(PRUNE_ACTIVITY));
        });
        dialog.setOnDismissListener(v -> {
            dialog.dismiss();
//            new HomeRouter().open(this, true);
            finish();
        });
        dialog.show();
    }

    private void onDappTransaction(TransactionData txData) {
        hideDialog();
        dialog = new AWalletAlertDialog(this);
        dialog.setTitle(R.string.transaction_succeeded);
        dialog.setMessage(txData.txHash);
        dialog.setButtonText(R.string.copy);
        dialog.setButtonListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("transaction hash", txData.txHash);
            clipboard.setPrimaryClip(clip);
            dialog.dismiss();
        });
        dialog.setOnDismissListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(C.SIGN_DAPP_TRANSACTION);
            intent.putExtra(C.EXTRA_WEB3TRANSACTION, transaction);
            intent.putExtra(C.EXTRA_HEXDATA, txData.signature);
            intent.putExtra(C.EXTRA_SUCCESS, true);
            sendBroadcast(intent);

            finish();
        });
        dialog.show();
    }


    private void onError(ErrorEnvelope error) {
        hideDialog();
        dialog = new AWalletAlertDialog(this);
        dialog.setTitle(R.string.error_transaction_failed);
        dialog.setMessage(error.message);
        dialog.setIcon(ERROR);
        dialog.setButtonText(R.string.button_ok);
        dialog.setButtonListener(v -> {
            dialog.dismiss();
            if (confirmationType == WEB3TRANSACTION) {
                Intent intent = new Intent(C.SIGN_DAPP_TRANSACTION);
                intent.putExtra(C.EXTRA_WEB3TRANSACTION, transaction);
                intent.putExtra(C.EXTRA_HEXDATA, "0x0000"); //Placeholder signature - transaction failed
                intent.putExtra(C.EXTRA_SUCCESS, false);
                sendBroadcast(intent);
            }
            finish();
        });
        dialog.show();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == GasSettingsViewModel.SET_GAS_SETTINGS) {
//            if (resultCode == RESULT_OK) {

//                //viewModel.gasSettings().postValue(settings);
//            }
//        }
//    }

    public static String getEthString(double ethPrice) {
        DecimalFormat df = new DecimalFormat("0.#####");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(ethPrice);
    }
}
