package pro.upchain.wallet.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.ui.activity.ContactsActivity;
import pro.upchain.wallet.ui.activity.HelpActivity;
import pro.upchain.wallet.ui.activity.MessageCenterActivity;
import pro.upchain.wallet.ui.activity.SystemSettingActivity;
import pro.upchain.wallet.ui.activity.TransactionsActivity;
import pro.upchain.wallet.ui.activity.WalletMangerActivity;


public class MineFragment extends BaseFragment implements View.OnClickListener {



//    private LinearLayout llyWalletManage;
//    private LinearLayout llyContacts;
//    private LinearLayout llyMsgCenter;
//    private LinearLayout llyAskHelp;
//    private LinearLayout llySystemSetting;
//    private LinearLayout llyTradeRecode;


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void initView() {
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        parentView.findViewById(R.id.lly_wallet_manage).setOnClickListener(this);

        parentView.findViewById(R.id.lly_contacts).setOnClickListener(this);
        parentView.findViewById(R.id.lly_msg_center).setOnClickListener(this);
        parentView.findViewById(R.id.ask_help).setOnClickListener(this);
        parentView.findViewById(R.id.lly_system_setting).setOnClickListener(this);
        parentView.findViewById(R.id.lly_trade_recode).setOnClickListener(this);
    }

    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {

            case R.id.lly_contacts:
                intent = new Intent(getActivity(), ContactsActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_wallet_manage:
                intent = new Intent(getActivity(), WalletMangerActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_msg_center:
                intent = new Intent(getActivity(), MessageCenterActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_system_setting:
                intent = new Intent(getActivity(), SystemSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.lly_trade_recode:
                intent = new Intent(getActivity(), TransactionsActivity.class);
                startActivity(intent);
                break;
            case R.id.ask_help:
                intent = new Intent(getActivity(), HelpActivity.class);
                startActivity(intent);

                break;
        }
    }
}
