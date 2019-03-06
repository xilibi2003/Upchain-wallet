package pro.upchain.wallet.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.ui.adapter.TransactionsAdapter;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.viewmodel.TransactionsViewModel;
import pro.upchain.wallet.viewmodel.TransactionsViewModelFactory;
import com.gyf.barlibrary.ImmersionBar;

import java.util.Arrays;
import java.util.List;


import butterknife.BindView;
import butterknife.OnClick;

import static pro.upchain.wallet.C.EXTRA_ADDRESS;
import static pro.upchain.wallet.C.Key.TRANSACTION;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class PropertyDetailActivity extends BaseActivity {

    TransactionsViewModelFactory transactionsViewModelFactory;
    private TransactionsViewModel viewModel;

    private TransactionsAdapter adapter;

    private String currWallet;
    private String contractAddress;
    private int decimals;
    private String balance;
    private String symbol;


    SwipeRefreshLayout refreshLayout;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_amount)
    TextView tvAmount;

    List<Transaction> transactionLists;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_property_detail;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void initDatas() {

        Intent intent = getIntent();
        currWallet = intent.getStringExtra(C.EXTRA_ADDRESS);
        balance = intent.getStringExtra(C.EXTRA_BALANCE);
        contractAddress = intent.getStringExtra(C.EXTRA_CONTRACT_ADDRESS);
        decimals = intent.getIntExtra(C.EXTRA_DECIMALS, C.ETHER_DECIMALS);
        symbol = intent.getStringExtra(C.EXTRA_SYMBOL);
        symbol = symbol == null ? C.ETH_SYMBOL : symbol;

        tvTitle.setText(symbol);

        tvAmount.setText(balance);

        transactionsViewModelFactory = new TransactionsViewModelFactory();
        viewModel = ViewModelProviders.of(this, transactionsViewModelFactory)
                .get(TransactionsViewModel.class);

        viewModel.transactions().observe(this, this::onTransactions);
        viewModel.progress().observe(this, this::onProgress);

    }

    @Override
    protected void onResume() {
        super.onResume();

        LogUtils.d("contractAddress " + contractAddress);

        if (!TextUtils.isEmpty(contractAddress)) {
            viewModel.prepare(contractAddress);
        } else {
            viewModel.prepare(null);
        }

    }



    private void onTransactions(List<Transaction> transactions) {
        LogUtils.d("onTransactions", "size: " + transactions.size());
        transactionLists = transactions;
        adapter.addTransactions(transactionLists, currWallet, symbol);
    }

    @Override
    public void configViews() {
        ImmersionBar.with(this)
                .transparentStatusBar()
                .statusBarDarkFont(true, 1f)
                .init();

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        RecyclerView list = (RecyclerView) findViewById(R.id.list);

        list.setLayoutManager(new LinearLayoutManager(this));


        adapter = new TransactionsAdapter(R.layout.list_item_transaction, null );
        list.setAdapter(adapter);

        adapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            Transaction t = transactionLists.get(position);

            Intent intent = new Intent(this, TransactionDetailActivity.class);
            intent.putExtra(TRANSACTION, t);
            startActivity(intent);
        });

        refreshLayout.setOnRefreshListener(viewModel::fetchTransactions);

    }


    private void onProgress(boolean shouldShow) {
        if (shouldShow && refreshLayout != null && refreshLayout.isRefreshing()) {
            return;
        }

        if (shouldShow) {
//            if (transactionLists.size() > 0) {
//                refreshLayout.setRefreshing(true);
//            }

        } else {
            refreshLayout.setRefreshing(false);
        }
    }


    @OnClick({R.id.lly_back, R.id.lly_transfer, R.id.lly_gathering})
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.lly_back:
                finish();
                break;
            case R.id.lly_transfer:

                intent = new Intent(mContext, SendActivity.class);

                intent.putExtra(C.EXTRA_BALANCE, balance);
                intent.putExtra(C.EXTRA_ADDRESS, currWallet);
                intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, contractAddress);
                intent.putExtra(C.EXTRA_SYMBOL, symbol);
                intent.putExtra(C.EXTRA_DECIMALS, decimals);

                startActivity(intent);
                break;
            case R.id.lly_gathering:
                intent = new Intent(mContext, GatheringQRCodeActivity.class);
                ETHWallet wallet = WalletDaoUtils.getCurrent();

                intent.putExtra(EXTRA_ADDRESS, wallet.getAddress());
                intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, contractAddress);
                intent.putExtra(C.EXTRA_SYMBOL, symbol);
                intent.putExtra(C.EXTRA_DECIMALS, decimals);

                startActivity(intent);
                break;
        }
    }
}
