package pro.upchain.wallet.ui.fragment;

import static pro.upchain.wallet.C.EXTRA_ADDRESS;
import static pro.upchain.wallet.C.Key.WALLET;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.progresslayout.ProgressLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.Ticker;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.ui.activity.AddTokenActivity;
import pro.upchain.wallet.ui.activity.CreateWalletActivity;
import pro.upchain.wallet.ui.activity.GatheringQRCodeActivity;
import pro.upchain.wallet.ui.activity.PropertyDetailActivity;
import pro.upchain.wallet.ui.activity.QRCodeScannerActivity;
import pro.upchain.wallet.ui.activity.SendActivity;
import pro.upchain.wallet.ui.activity.WalletDetailActivity;
import pro.upchain.wallet.ui.activity.WalletMangerActivity;
import pro.upchain.wallet.ui.adapter.DrawerWalletAdapter;
import pro.upchain.wallet.ui.adapter.TokensAdapter;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;
import pro.upchain.wallet.view.ListViewForScrollView;
import pro.upchain.wallet.viewmodel.TokensViewModel;
import pro.upchain.wallet.viewmodel.TokensViewModelFactory;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class PropertyFragment extends BaseFragment implements View.OnClickListener {

    TokensViewModelFactory tokensViewModelFactory;
    private TokensViewModel tokensViewModel;

    RecyclerView recyclerView;
    Toolbar mToolbar;
    TwinklingRefreshLayout refreshLayout;

    TextView tvPropertToolbaryLabel;

    DrawerLayout drawer;
    ListView lvWallet;


    private ETHWallet currEthWallet;

    List<Token> tokenItems;

    private LinearLayoutManager linearLayoutManager;
    private TokensAdapter recyclerAdapter;
    private View headView;

    private int bannerHeight = 300;
    private View mIv;

    SwipeRefreshLayout swipeRefresh;

    private static final int QRCODE_SCANNER_REQUEST = 1100;
    private static final int CREATE_WALLET_REQUEST = 1101;
    private static final int ADD_NEW_PROPERTY_REQUEST = 1102;
    private static final int WALLET_DETAIL_REQUEST = 1104;

    private DrawerWalletAdapter drawerWalletAdapter;

    FetchWalletInteract fetchWalletInteract;

    private TextView tvWalletName;
    private TextView tvWalletAddress;
    private TextView tvTolalAssetValue;
    private TextView tvTolalAsset;

    private String currency;
    private TextView tvPropertyLabel;
    private LinearLayout llyMenu;
    private Toolbar commonToolbar;
    private FrameLayout frame;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout llyQrcodeScanner;
    private LinearLayout llyCreateWallet;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_property;
    }

    @Override
    public void attachView() {

    }

    @Override
    public void initDatas() {
        if (recyclerView == null) {
            recyclerView = getParentView().findViewById(R.id.recycler_view);
        }

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        //设置布局管理器
        recyclerView.setLayoutManager(linearLayoutManager);

        //设置适配器
        recyclerAdapter = new TokensAdapter(R.layout.list_item_property, new ArrayList<>());  //

        recyclerView.setAdapter(recyclerAdapter);
        recyclerAdapter.setOnItemClickListener((adapter, view, position) -> {

            Intent intent = new Intent(getActivity(), PropertyDetailActivity.class);
            Token token = tokenItems.get(position);

            intent.putExtra(C.EXTRA_BALANCE, token.balance);
            intent.putExtra(EXTRA_ADDRESS, currEthWallet.getAddress());
            intent.putExtra(C.EXTRA_CONTRACT_ADDRESS, token.tokenInfo.address);
            intent.putExtra(C.EXTRA_SYMBOL, token.tokenInfo.symbol);
            intent.putExtra(C.EXTRA_DECIMALS, token.tokenInfo.decimals);

            startActivity(intent);
        });


        fetchWalletInteract = new FetchWalletInteract();
        fetchWalletInteract.fetch().subscribe(this::showDrawerWallets);

        tokensViewModelFactory = new TokensViewModelFactory();
        tokensViewModel = ViewModelProviders.of(this.getActivity(), tokensViewModelFactory)
                .get(TokensViewModel.class);

        tokensViewModel.defaultWallet().observe(this, this::showWallet);

//        tokensViewModel.progress().observe(this, systemView::showProgress);
//        tokensViewModel.error().observe(this, this::onError);

        tokensViewModel.tokens().observe(this, this::onTokens);
        tokensViewModel.prices().observe(this, this::onPrices);

        currency = tokensViewModel.getCurrency();
    }

    private void onTokens(Token[] tokens) {
        tokenItems = Arrays.asList(tokens);
        recyclerAdapter.setTokens(tokenItems);
    }

    private void onPrices(Ticker ticker) {
        BigDecimal sum = new BigDecimal(0);

        for (Token token : tokenItems) {
            if (token.tokenInfo.symbol.equals(ticker.symbol)) {
                if (token.balance == null) {
                    token.value = "0";
                } else {
                    if(currency == "CNY") {
                        token.value = BalanceUtils.ethToUsd(ticker.price_cny, token.balance);
                    } else {
                        token.value = BalanceUtils.ethToUsd(ticker.price_usd, token.balance);
                    }

                }
            }
            if (!TextUtils.isEmpty(token.value)) {
                sum = sum.add(new BigDecimal(token.value));
            }

        }

        if (tvTolalAssetValue != null) {
            tvTolalAssetValue.setText(sum.setScale(2, RoundingMode.CEILING).toString());
        }

        recyclerAdapter.setTokens(tokenItems);
    }

    private void initTinklingLayoutListener() {
        ViewGroup.LayoutParams bannerParams = mIv.getLayoutParams();
        ViewGroup.LayoutParams titleBarParams = mToolbar.getLayoutParams();
        bannerHeight = bannerParams.height - titleBarParams.height - ImmersionBar.getStatusBarHeight(getActivity());
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int totalDy = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalDy += dy;

                ImmersionBar immersionBar = ImmersionBar.with(PropertyFragment.this)
                        .addViewSupportTransformColor(mToolbar, R.color.colorPrimary);


                if (totalDy <= bannerHeight) {
                    float alpha = (float) totalDy / bannerHeight;
                    immersionBar.statusBarAlpha(alpha)
                            .init();
                    // 设置资产文字alpha值
                    if (totalDy >= bannerHeight / 2) {
                        float tvPropertyAlpha = (float) (totalDy - bannerHeight / 2) / (bannerHeight / 2);
                        tvPropertToolbaryLabel.setAlpha(tvPropertyAlpha);
                        int top = (int) (mToolbar.getHeight() - mToolbar.getHeight() * alpha);
                        tvPropertToolbaryLabel.setPadding(0, top, 0, 0);
                    } else {
                        tvPropertToolbaryLabel.setPadding(0, mToolbar.getHeight(), 0, 0);
                        tvPropertToolbaryLabel.setAlpha(0);
                    }
                } else {
                    immersionBar.statusBarAlpha(1.0f).init();
                    tvPropertToolbaryLabel.setAlpha(1.0f);
                }
            }
        });
        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                        mToolbar.setVisibility(View.VISIBLE);
                        ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(false).init();
                    }
                }, 2000);
            }

            @Override
            public void onPullingDown(TwinklingRefreshLayout refreshLayout, float fraction) {
                mToolbar.setVisibility(View.GONE);
                ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(true).init();
            }

            @Override
            public void onPullDownReleasing(TwinklingRefreshLayout refreshLayout, float fraction) {
                if (Math.abs(fraction - 1.0f) > 0) {
                    mToolbar.setVisibility(View.VISIBLE);
                    ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(false).init();
                } else {
                    mToolbar.setVisibility(View.GONE);
                    ImmersionBar.with(PropertyFragment.this).statusBarDarkFont(true).init();
                }
            }
        });
        llyMenu.setOnClickListener(this);
        llyQrcodeScanner.setOnClickListener(this);
        llyCreateWallet.setOnClickListener(this);
    }

    private void addHeaderView() {
        ProgressLayout headerView = new ProgressLayout(getContext());
        refreshLayout.setHeaderView(headerView);
        headView = LayoutInflater.from(getContext()).inflate(R.layout.list_header_item, (ViewGroup) recyclerView.getParent(), false);
        mIv = headView.findViewById(R.id.iv);
        headView.findViewById(R.id.lly_add_token).setOnClickListener(this);
        headView.findViewById(R.id.lly_wallet_address).setOnClickListener(this);
        headView.findViewById(R.id.civ_wallet_logo).setOnClickListener(this);
        tvWalletName =  headView.findViewById(R.id.tv_wallet_name);
        tvWalletAddress =  headView.findViewById(R.id.tv_wallet_address);
        tvTolalAssetValue =  headView.findViewById(R.id.tv_total_value);

        tvTolalAsset =  headView.findViewById(R.id.tv_total_assets);
        if (currency.equals("CNY")) {
            tvTolalAsset.setText(R.string.property_total_assets_cny);
        } else {
            tvTolalAsset.setText(R.string.property_total_assets_usd);
        }


        recyclerAdapter.addHeaderView(headView);
    }

    @Override
    public void onResume() {
        super.onResume();

        ImmersionBar.with(this)
                .titleBar(mToolbar, false)
                .navigationBarColor(R.color.colorPrimary)
                .init();

        tokensViewModel.prepare();

        // 更改货币单位
        if (!currency.equals(tokensViewModel.getCurrency())) {
            currency = tokensViewModel.getCurrency();
            if (currency.equals("CNY")) {
                tvTolalAsset.setText(R.string.property_total_assets_cny);
            } else {
                tvTolalAsset.setText(R.string.property_total_assets_usd);
            }
        }

    }


    @Override
    public void configViews() {
        addHeaderView();
        initTinklingLayoutListener();
        drawer.setScrimColor(getContext().getResources().getColor(R.color.property_drawer_scrim_bg_color));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(false);
            }
        });
    }

//    @OnClick({R.id.lly_menu, R.id.lly_qrcode_scanner, R.id.lly_create_wallet})
    @Override
    public void onClick(View view) {
        Intent intent = null;
        ETHWallet wallet = null;
        switch (view.getId()) {
            case R.id.lly_menu:
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_qrcode_scanner:// 二维码扫描
                intent = new Intent(mContext, QRCodeScannerActivity.class);
                startActivityForResult(intent, QRCODE_SCANNER_REQUEST);
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_create_wallet:// 创建钱包
                intent = new Intent(mContext, CreateWalletActivity.class);
                startActivityForResult(intent, CREATE_WALLET_REQUEST);
                openOrCloseDrawerLayout();
                break;
            case R.id.lly_add_token:// 跳转添加资产
//                tokensViewModel.showAddToken(this.getApplicationContext());

                intent = new Intent(mContext, AddTokenActivity.class);
                intent.putExtra(WALLET, currEthWallet.getAddress());
                startActivityForResult(intent, ADD_NEW_PROPERTY_REQUEST);

                break;

            case R.id.lly_wallet_address:  // 跳转收款码
                intent = new Intent(mContext, GatheringQRCodeActivity.class);
                wallet = WalletDaoUtils.getCurrent();
                if (wallet == null) {
                    return;
                }
                System.out.printf("钱包地址 wallet:"+wallet.getAddress());

                intent.putExtra(EXTRA_ADDRESS, wallet.getAddress());
                startActivity(intent);
                break;

            case R.id.civ_wallet_logo:// 跳转钱包详情
                intent = new Intent(mContext, WalletDetailActivity.class);
                wallet = WalletDaoUtils.getCurrent();
                if (wallet == null) {
                    return;
                }
                intent.putExtra("walletId", wallet.getId());
                intent.putExtra("walletPwd", wallet.getPassword());
                intent.putExtra("walletAddress", wallet.getAddress());
                intent.putExtra("walletName", wallet.getName());
                intent.putExtra("walletMnemonic", wallet.getMnemonic());
                intent.putExtra("walletIsBackup", wallet.getIsBackup());
                startActivityForResult(intent, WALLET_DETAIL_REQUEST);
                break;

        }

    }

    // 打开关闭DrawerLayout
    private void openOrCloseDrawerLayout() {
        boolean drawerOpen = drawer.isDrawerOpen(GravityCompat.END);
        if (drawerOpen) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QRCODE_SCANNER_REQUEST) {
            if (data != null) {
                String scanResult = data.getStringExtra("scan_result");
                // 对扫描结果进行处理
                ToastUtils.showLongToast(scanResult);

                Intent intent = new Intent(mContext, SendActivity.class);
                intent.putExtra("scan_result", scanResult);

                startActivity(intent);

            }
        } else if (requestCode == WALLET_DETAIL_REQUEST) {
            if (data != null) {
//                mPresenter.loadAllWallets();
                startActivity(new Intent(mContext, WalletMangerActivity.class));
            }
        }
    }

    public void showWallet(ETHWallet wallet) {
        currEthWallet = wallet;
        tvWalletName.setText(wallet.getName());
        tvWalletAddress.setText(wallet.getAddress());

        //       openOrCloseDrawerLayout();
    }

    public void showDrawerWallets(final List<ETHWallet> ethWallets) {
        for (int i = 0; i < ethWallets.size(); i++) {
            LogUtils.i("PropertyFragment", "Ethwallets" + ethWallets.get(i).toString());
        }
        drawerWalletAdapter = new DrawerWalletAdapter(getContext(), ethWallets, R.layout.list_item_drawer_property_wallet);
        lvWallet.setAdapter(drawerWalletAdapter);
        lvWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                drawerWalletAdapter.setCurrentWalletPosition(position);

                ETHWallet wallet = drawerWalletAdapter.getDatas().get(position);

                tokensViewModel.updateDefaultWallet(wallet.getId());

                openOrCloseDrawerLayout();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return super.onCreateView(inflater, container, state);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    @Override
    public void initView() {
        recyclerView = parentView.findViewById(R.id.recycler_view);
        mToolbar = parentView.findViewById(R.id.common_toolbar);
        refreshLayout = parentView.findViewById(R.id.refresh_layout);
        tvPropertToolbaryLabel = parentView.findViewById(R.id.tv_property_label);
        drawer = parentView.findViewById(R.id.drawer);
        lvWallet = parentView.findViewById(R.id.lv_wallet);
        swipeRefresh = parentView.findViewById(R.id.swipe_refresh_layout);


        tvPropertyLabel = parentView.findViewById(R.id.tv_property_label);
        llyMenu = parentView.findViewById(R.id.lly_menu);
        commonToolbar = parentView.findViewById(R.id.common_toolbar);
        frame = parentView.findViewById(R.id.frame);
        swipeRefreshLayout = parentView.findViewById(R.id.swipe_refresh_layout);
        llyQrcodeScanner =parentView. findViewById(R.id.lly_qrcode_scanner);
        llyCreateWallet =parentView. findViewById(R.id.lly_create_wallet);
    }
}
