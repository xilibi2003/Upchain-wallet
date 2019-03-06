package pro.upchain.wallet.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;

import butterknife.BindView;

 /**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class ContactsActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_btn)
    ImageView ivBtn;
    @BindView(R.id.rl_btn)
    LinearLayout rlBtn;

    @Override
    public int getLayoutId() {
        return R.layout.activity_contacts;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mine_contracts);
        ivBtn.setImageResource(R.drawable.ic_add);
        rlBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

    }

}
