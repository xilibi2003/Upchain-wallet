package pro.upchain.wallet.ui.activity;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class HelpActivity extends BaseActivity {

    TextView tvTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mine_help_center);
    }

    @Override
    public void initDatas() {
    }

    @Override
    public void configViews() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        tvTitle=findViewById(R.id.tv_title);
    }


}
