package pro.upchain.wallet.ui.adapter;

import static pro.upchain.wallet.C.ETHER_DECIMALS;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.entity.TransactionOperation;
import pro.upchain.wallet.utils.LogUtils;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class TransactionsAdapter  extends BaseQuickAdapter<Transaction, BaseViewHolder> {

    private final List<Transaction> items = new ArrayList<>();

    private static final int SIGNIFICANT_FIGURES = 3;

    private String symbol;
    private String defaultAddress;


    public TransactionsAdapter(int layoutResId, @Nullable List<Transaction> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Transaction transaction) {
        LogUtils.d(TAG, "convert: helper:" + helper + ", transaction:" + transaction);

        boolean isSent = transaction.from.toLowerCase().equals(defaultAddress.toLowerCase());
        boolean isCreateContract = TextUtils.isEmpty(transaction.to);

        if (isSent) {
            if (isCreateContract) {
                helper.setText(R.id.type, R.string.create);
            } else {
                helper.setText(R.id.type, R.string.sent);
            }
        }
        else {
            helper.setText(R.id.type, R.string.received);
        }


        if (!TextUtils.isEmpty(transaction.error)) {
            helper.setImageResource(R.id.type_icon, R.drawable.ic_error_outline_black_24dp);
        } else if (isSent) {
            helper.setImageResource(R.id.type_icon, R.drawable.ic_arrow_upward_black_24dp);
        } else {
            helper.setImageResource(R.id.type_icon, R.drawable.ic_arrow_downward_black_24dp);
        }

        if (isCreateContract) {
            helper.setText(R.id.address, transaction.contract);
        } else {
            helper.setText(R.id.address, isSent ? transaction.to : transaction.from);
        }


        helper.setTextColor(R.id.value, ContextCompat.getColor(mContext, isSent ? R.color.red : R.color.green));


        String valueStr = "";


        // If operations include token transfer, display token transfer instead
        TransactionOperation operation = transaction.operations == null
                || transaction.operations.length == 0 ? null : transaction.operations[0];

        if (operation == null || operation.contract == null) {  // default to ether transaction
            valueStr = transaction.value;

            if (valueStr.equals("0")) {
                valueStr = "0 " + symbol;
            } else {
                valueStr = (isSent ? "-" : "+") +  getScaledValue(valueStr, ETHER_DECIMALS) + " " + symbol;
            }

        } else {
            valueStr = operation.value;

            if (valueStr.equals("0")) {
                valueStr = "0 " + symbol;
            } else {
                valueStr = (isSent ? "-" : "+") +  getScaledValue(valueStr, operation.contract.decimals) + " " + symbol;
            }
        }


        helper.setText(R.id.value, valueStr);


    }


    private String getScaledValue(String valueStr, long decimals) {
        // Perform decimal conversion
        BigDecimal value = new BigDecimal(valueStr);
        value = value.divide(new BigDecimal(Math.pow(10, decimals)));
        int scale = SIGNIFICANT_FIGURES - value.precision() + value.scale();
        return value.setScale(scale, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    }


    public void addTransactions(List<Transaction> transactions, String walletAddress, String symbol) {
        setNewData(transactions);
        this.defaultAddress = walletAddress;
        this.symbol = symbol;
    }


}
