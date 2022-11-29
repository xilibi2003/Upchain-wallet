package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.ui.adapter.SwitchWalletAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class SwitchWalletActivity extends BaseActivity {
    TextView tvTitle;
    ListView lvWallet;
    private SwitchWalletAdapter switchWalletAdapter;
    private List<String> strings;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        lvWallet = findViewById(R.id.lv_wallet);
    }

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
