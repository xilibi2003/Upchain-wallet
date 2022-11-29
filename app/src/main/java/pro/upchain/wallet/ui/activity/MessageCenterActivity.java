package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.ui.adapter.MessageCenterAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class MessageCenterActivity extends BaseActivity {
    TextView tvTitle;
    LinearLayout rlBtn;
    ListView lvMessageCenter;
    private List<String> strings;
    private MessageCenterAdapter drawerWalletAdapter;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        rlBtn = findViewById(R.id.rl_btn);
        lvMessageCenter = findViewById(R.id.lv_message_center);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_message_center;
    }

    @Override
    public void initToolBar() {
        rlBtn.setVisibility(View.VISIBLE);
        tvTitle.setText(R.string.news_center_title);
    }

    @Override
    public void initDatas() {
        strings = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            strings.add(String.valueOf(i));
        }
        drawerWalletAdapter = new MessageCenterAdapter(this, strings, R.layout.list_item_news_center);
        lvMessageCenter.setAdapter(drawerWalletAdapter);
    }

    @Override
    public void configViews() {

    }

}
