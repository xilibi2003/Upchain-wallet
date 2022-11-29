package pro.upchain.wallet.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.ui.adapter.HomePagerAdapter;
import pro.upchain.wallet.ui.fragment.DappBrowserFragment;
import pro.upchain.wallet.ui.fragment.MineFragment;
import pro.upchain.wallet.ui.fragment.PropertyFragment;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.view.NoScrollViewPager;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * 微信: xlbxiong
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    NoScrollViewPager vpHome;
    ImageView ivMall;
    TextView tvMall;
    LinearLayout llyMall;

    ImageView ivDapp;
    TextView tvDapp;
    LinearLayout llyDapp;


    ImageView ivMine;
    TextView tvMine;
    LinearLayout llyMine;

    private HomePagerAdapter homePagerAdapter;

    public static final int DAPP_BARCODE_READER_REQUEST_CODE = 1;

    DappBrowserFragment dappBrowserFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initToolBar() {

    }


    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        ivMall.setSelected(true);
        tvMall.setSelected(true);
        llyMall.setOnClickListener(this);
        llyDapp.setOnClickListener(this);
        llyMine.setOnClickListener(this);

        vpHome.setOffscreenPageLimit(2);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new PropertyFragment());
        dappBrowserFragment = new DappBrowserFragment();
        fragmentList.add(dappBrowserFragment);
        fragmentList.add(new MineFragment());
        homePagerAdapter = new HomePagerAdapter(getSupportFragmentManager(), fragmentList);
        vpHome.setAdapter(homePagerAdapter);
        vpHome.setCurrentItem(0, false);

    }

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            } else {
                finish(); // 退出
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
        setAllUnselected();
        switch (v.getId()) {
            case R.id.lly_mall://
                ivMall.setSelected(true);
                tvMall.setSelected(true);
                vpHome.setCurrentItem(0, false);
                break;
            case R.id.lly_dapp:
                ivDapp.setSelected(true);
                tvDapp.setSelected(true);
                vpHome.setCurrentItem(1, false);
                setupToolbarForDapp();

                break;
            case R.id.lly_mine:// 我的
                ivMine.setSelected(true);
                tvMine.setSelected(true);
                vpHome.setCurrentItem(2, false);
                break;
        }
    }

    private void setupToolbarForDapp() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.empty);

        enableDisplayHomeAsHome(true);
        invalidateOptionsMenu();

    }

    protected void enableDisplayHomeAsHome(boolean active) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(active);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_browser_home);
        }
    }

    private void setAllUnselected() {
        ivMall.setSelected(false);
        tvMall.setSelected(false);
        ivDapp.setSelected(false);
        tvDapp.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (dappBrowserFragment.getUrlIsBookmark()) {
            getMenuInflater().inflate(R.menu.menu_added, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_add_bookmark, menu);
        }

        getMenuInflater().inflate(R.menu.menu_bookmarks, menu);
        setIconsVisible(menu, true);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                dappBrowserFragment.homePressed();
                return true;
            }
            case R.id.action_add_bookmark: {
                dappBrowserFragment.addBookmark();
                invalidateOptionsMenu();
                return true;
            }
            case R.id.action_bookmarks: {
                dappBrowserFragment.viewBookmarks();
                return true;
            }
            case R.id.action_added: {
                dappBrowserFragment.removeBookmark();
                invalidateOptionsMenu();
                return true;
            }
            case R.id.action_reload: {
                dappBrowserFragment.reloadPage();
                return true;
            }
            case R.id.action_share: {
                dappBrowserFragment.share();
                return true;
            }
            case R.id.action_scan: {
                Intent intent = new Intent(this, QRCodeScannerActivity.class);
                startActivityForResult(intent, DAPP_BARCODE_READER_REQUEST_CODE);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 解决不显示menu icon的问题
     *
     * @param menu
     * @param flag
     */
    private void setIconsVisible(Menu menu, boolean flag) {
        //判断menu是否为空
        if (menu != null) {
            try {
                //如果不为空,就反射拿到menu的setOptionalIconsVisible方法
                Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                //暴力访问该方法
                method.setAccessible(true);
                //调用该方法显示icon
                method.invoke(menu, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }





    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void initView() {
        ivMall = findViewById(R.id.iv_mall);
        tvMall = findViewById(R.id.tv_mall);
        llyMall = findViewById(R.id.lly_mall);
        llyDapp = findViewById(R.id.lly_dapp);
        tvDapp=findViewById(R.id.tv_dapp);
        tvMine=findViewById(R.id.tv_mine);
        ivDapp=findViewById(R.id.iv_dapp);
        ivMine=findViewById(R.id.iv_mine);
        llyMine = findViewById(R.id.lly_mine);
        vpHome = findViewById(R.id.vp_home);
    }
}
