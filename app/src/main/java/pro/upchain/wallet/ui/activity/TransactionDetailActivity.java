package pro.upchain.wallet.ui.activity;

import static pro.upchain.wallet.C.Key.TRANSACTION;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.viewmodel.TransactionDetailViewModel;
import pro.upchain.wallet.viewmodel.TransactionDetailViewModelFactory;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class TransactionDetailActivity extends BaseActivity implements View.OnClickListener {

    TextView tvTitle;

    TextView amount;

    TextView tvFrom;

    TextView tvTo;

    TextView tvGasFee;

    TextView tvTxHash;

    TextView tvTxTime;
    TextView tvBlockNumber;

    private Transaction transaction;

    TransactionDetailViewModelFactory transactionDetailViewModelFactory;
    private TransactionDetailViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        amount = findViewById(R.id.amount);
        tvFrom = findViewById(R.id.from);
        tvTo = findViewById(R.id.to);
        tvGasFee = findViewById(R.id.gas_fee);
        tvTxHash = findViewById(R.id.txn_hash);
        tvTxTime = findViewById(R.id.txn_time);
        tvBlockNumber = findViewById(R.id.block_number);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_transaction_detail;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.subject_transaction_detail);
    }

    @Override
    public void initDatas() {

        transaction = getIntent().getParcelableExtra(TRANSACTION);
        if (transaction == null) {
            finish();
            return;
        }

        BigInteger gasFee = new BigInteger(transaction.gasUsed).multiply(new BigInteger(transaction.gasPrice));

        tvFrom.setText(transaction.from);
        tvTo.setText(transaction.to);
        tvGasFee.setText(BalanceUtils.weiToEth(gasFee).toPlainString());
        tvTxHash.setText(transaction.hash);
        tvTxTime.setText(getDate(transaction.timeStamp));
        tvBlockNumber.setText(transaction.blockNumber);

        transactionDetailViewModelFactory  = new TransactionDetailViewModelFactory();

        viewModel = ViewModelProviders.of(this, transactionDetailViewModelFactory)
                .get(TransactionDetailViewModel.class);
        viewModel.defaultNetwork().observe(this, this::onDefaultNetwork);
        viewModel.defaultWallet().observe(this, this::onDefaultWallet);

    }


    private void onDefaultNetwork(NetworkInfo networkInfo) {
        findViewById(R.id.more_detail).setVisibility(
                TextUtils.isEmpty(networkInfo.etherscanUrl) ? View.GONE : View.VISIBLE);
    }

    private void onDefaultWallet(ETHWallet wallet) {
        boolean isSent = transaction.from.toLowerCase().equals(wallet.getAddress().toLowerCase());
        String rawValue;
        String symbol;
        long decimals = 18;
        NetworkInfo networkInfo = viewModel.defaultNetwork().getValue();
        if (transaction.operations == null || transaction.operations.length == 0) {
            rawValue = transaction.value;
            symbol = networkInfo == null ? "" : networkInfo.symbol;
        } else {
            rawValue = transaction.operations[0].value;
            decimals = transaction.operations[0].contract.decimals;
            symbol = transaction.operations[0].contract.symbol;
        }

        amount.setTextColor(ContextCompat.getColor(this, isSent ? R.color.red : R.color.green));
        if (rawValue.equals("0")) {
            rawValue = "0 " + symbol;
        } else {
            rawValue = (isSent ? "-" : "+") + getScaledValue(rawValue, decimals) + " " + symbol;
        }
        amount.setText(rawValue);
    }


    private String getDate(long timeStampInSec) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timeStampInSec * 1000);
        return DateFormat.getLongDateFormat(this).format(cal.getTime());
    }


    private String getScaledValue(String valueStr, long decimals) {
        // Perform decimal conversion
        BigDecimal value = new BigDecimal(valueStr);
        value = value.divide(new BigDecimal(Math.pow(10, decimals)));
        int scale = 3 - value.precision() + value.scale();
        return value.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }

    @Override
    public void configViews() {
        findViewById(R.id.more_detail).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        viewModel.showMoreDetails(v.getContext(), transaction);
    }

}
