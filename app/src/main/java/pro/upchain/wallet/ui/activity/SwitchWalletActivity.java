package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.ui.adapter.SwitchWalletAdapter;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SwitchWalletActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.lv_wallet)
    ListView lvWallet;
    private SwitchWalletAdapter switchWalletAdapter;
    private List<String> strings;

    @Override
    public int getLayoutId() {
        return R.layout.activity_switch_activity;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.switch_wallet_title);
    }

    @Override
    public void initDatas() {
        strings = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            strings.add(String.valueOf(i));
        }
        switchWalletAdapter = new SwitchWalletAdapter(this, strings, R.layout.list_item_switch_wallet);
        lvWallet.setAdapter(switchWalletAdapter);

    }

    @Override
    public void configViews() {
        lvWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switchWalletAdapter.setSelection(position);
            }
        });
    }

}