package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.entity.TokenInfo;
import pro.upchain.wallet.ui.adapter.AddTokenListAdapter;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.viewmodel.AddTokenViewModel;
import pro.upchain.wallet.viewmodel.AddTokenViewModelFactory;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;

/**
 * Created by Tiny熊
 * 微信: xlbxiong
 */

public class AddTokenActivity extends BaseActivity {

    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;

    protected AddTokenViewModelFactory addTokenViewModelFactory;
    private AddTokenViewModel addTokenViewModel;

    private static final int SEARCH_ICO_TOKEN_REQUEST = 1000;
    TextView tvTitle;
    ListView tokenList;
    Toolbar commonToolbar;
    LinearLayout rlBtn;


    List<TokenItem> mItems = new ArrayList<TokenItem>();

    private AddTokenListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        rlBtn = findViewById(R.id.rl_btn);
        commonToolbar = findViewById(R.id.common_toolbar);
        tokenList = findViewById(R.id.lv_ico);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_add_new_property;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.add_new_property_title);
        rlBtn.setVisibility(View.VISIBLE);
    }

    public static class TokenItem {
        public final TokenInfo tokenInfo;
        public boolean added;
        public int iconId;

        public TokenItem(TokenInfo tokenInfo, boolean added, int id) {
            this.tokenInfo = tokenInfo;
            this.added = added;
            this.iconId = id;
        }
    }

    @Override
    public void initDatas() {

        // TODO 写死了几个热门的ERC20 （ 主网地址）
        mItems.add(new TokenItem(new TokenInfo("", "BNB", "BNB", 18), true, R.drawable.wallet_logo_demo));
//        mItems.add(new TokenItem(new TokenInfo("0xB8c77482e45F1F44dE1745F52C74426C631bDD52", "", "BNB", 18), false, R.drawable.wallet_logo_demo));
        mItems.add(new TokenItem(new TokenInfo("0xA0b86991c6218b36c1d19D4a2e9Eb0cE3606eB48", "USD Coin", "USDC", 6), false, R.drawable.wallet_logo_demo));
        mItems.add(new TokenItem(new TokenInfo("0x9f8f72aa9304c8b593d555f12ef6589cc3a579a2", "Maker", "MKR", 18), false, R.drawable.wallet_logo_demo));
        mItems.add(new TokenItem(new TokenInfo("0xd850942ef8811f2a866692a623011bde52a462c1", "VeChain", "VEN", 18), false, R.drawable.wallet_logo_demo));
        mItems.add(new TokenItem(new TokenInfo("0x0000000000085d4780B73119b644AE5ecd22b376", "TrueUSD", "TUSD", 18), false, R.drawable.wallet_logo_demo));
        mItems.add(new TokenItem(new TokenInfo("0x613552479cC8133b0C7864EDb457658D63432682", "BNB", "BNB", 18), false, R.drawable.wallet_logo_demo));


        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this, tokensViewModelFactory)
                .get(TokensViewModel.class);
        tokensViewModel.tokens().observe(this, this::onTokens);

        tokensViewModel.prepare();

        addTokenViewModelFactory = new AddTokenViewModelFactory();
        addTokenViewModel = ViewModelProviders.of(this, addTokenViewModelFactory)
                .get(AddTokenViewModel.class);


    }

    private void onTokens(Token[] tokens) {

        for (TokenItem item : mItems) {
            for (Token token : tokens) {
                if (item.tokenInfo.address.equals(token.tokenInfo.address)) {
                    item.added = true;
                }
            }
        }

        // TODO:  Add missed for tokens

        mAdapter = new AddTokenListAdapter(this, mItems, R.layout.list_item_add_ico_property);
        tokenList.setAdapter(mAdapter);
    }

    public void onCheckedChanged(CompoundButton btn, boolean checked) {
        TokenItem info = (TokenItem) btn.getTag();
        info.added = checked;
        LogUtils.d(info.toString() + ", checked:" + checked);

        if (checked) {
            addTokenViewModel.save(info.tokenInfo.address, info.tokenInfo.symbol, info.tokenInfo.decimals);
        }


    }

    ;

    @Override
    public void configViews() {

    }

    @OnClick({R.id.rl_btn})
    public void onClick(View view) {
        if (view.getId() == R.id.rl_btn) {
            Intent intent = new Intent(this, AddCustomTokenActivity.class);
            startActivityForResult(intent, SEARCH_ICO_TOKEN_REQUEST);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
