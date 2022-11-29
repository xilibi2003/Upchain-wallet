package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.ui.adapter.MessageCenterAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class TransactionsActivity extends BaseActivity implements View.OnClickListener{
    private static final int SWITCH_WALLET_REQUEST = 1101;
    TextView tvTitle;
    ImageView ivBtn;
    LinearLayout rlBtn;
    ListView lvTradingRecord;
    SwipeRefreshLayout swipeRefresh;

    private List<String> strings;
    private MessageCenterAdapter drawerWalletAdapter;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        ivBtn = findViewById(R.id.iv_btn);
        rlBtn = findViewById(R.id.rl_btn);
        lvTradingRecord = findViewById(R.id.lv_trading_record);
        swipeRefresh = findViewById(R.id.swipeRefresh);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_trading_record;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.trading_record_title);
        ivBtn.setImageResource(R.drawable.ic_acount_switch);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {
        strings = new ArrayList<>();
        drawerWalletAdapter = new MessageCenterAdapter(this, strings, R.layout.list_item_news_center);
        lvTradingRecord.setAdapter(drawerWalletAdapter);
    }

    @Override
    public void configViews() {
//        swipeRefresh.setRefreshing(true);
        rlBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn:
                Intent intent = new Intent(this,SwitchWalletActivity.class);
                startActivityForResult(intent,SWITCH_WALLET_REQUEST);
                break;
        }
    }
}
