package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseActivity;
import pro.upchain.wallet.domain.VerifyMnemonicWordTag;
import pro.upchain.wallet.ui.adapter.VerifyBackupMnemonicWordsAdapter;
import pro.upchain.wallet.ui.adapter.VerifyBackupSelectedMnemonicWordsAdapter;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.ToastUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


// 验证 助记词是否 正确界面

public class VerifyMnemonicBackupActivity extends BaseActivity implements View.OnClickListener{
    private static final int VERIFY_SUCCESS_RESULT = 2202;
    TextView tvTitle;
    RecyclerView rvSelected;
    RecyclerView rvMnemonic;
    private String walletMnemonic;

    private List<VerifyMnemonicWordTag> mnemonicWords;

    private List<String> selectedMnemonicWords;

    private VerifyBackupMnemonicWordsAdapter verifyBackupMenmonicWordsAdapter;
    private VerifyBackupSelectedMnemonicWordsAdapter verifyBackupSelectedMnemonicWordsAdapter;
    private long walletId;
    private TextView btnConfirm;

    @Override
    public void initView() {

        tvTitle = findViewById(R.id.tv_title);
        rvSelected = findViewById(R.id.rv_selected);
        rvMnemonic = findViewById(R.id.rv_mnemonic);

        btnConfirm = findViewById(R.id.btn_confirm);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_verify_mnemonic_backup;
    }

    @Override
    public void initToolBar() {
        tvTitle.setText(R.string.mnemonic_backup_title);
    }

    @Override
    public void initDatas() {
        walletId = getIntent().getLongExtra("walletId", -1);
        walletMnemonic = getIntent().getStringExtra("walletMnemonic");

        LogUtils.d("VerifyMnemonicBackUp", "walletMnemonic:" + walletMnemonic);

        String[] words = walletMnemonic.split("\\s+");
        mnemonicWords = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            VerifyMnemonicWordTag verifyMnemonicWordTag = new VerifyMnemonicWordTag();
            verifyMnemonicWordTag.setMnemonicWord(words[i]);
            mnemonicWords.add(verifyMnemonicWordTag);
        }
        // 乱序
        Collections.shuffle(mnemonicWords);

        // 未选中单词
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        layoutManager.setAlignItems(AlignItems.STRETCH);
        rvMnemonic.setLayoutManager(layoutManager);
        verifyBackupMenmonicWordsAdapter = new VerifyBackupMnemonicWordsAdapter(R.layout.list_item_mnemoic, mnemonicWords);
        rvMnemonic.setAdapter(verifyBackupMenmonicWordsAdapter);


        // 已选中单词
        FlexboxLayoutManager layoutManager2 = new FlexboxLayoutManager(this);
        layoutManager2.setFlexWrap(FlexWrap.WRAP);
        layoutManager2.setAlignItems(AlignItems.STRETCH);
        rvSelected.setLayoutManager(layoutManager2);
        selectedMnemonicWords = new ArrayList<>();
        verifyBackupSelectedMnemonicWordsAdapter = new VerifyBackupSelectedMnemonicWordsAdapter(R.layout.list_item_mnemoic_selected, selectedMnemonicWords);
        rvSelected.setAdapter(verifyBackupSelectedMnemonicWordsAdapter);
    }

    @Override
    public void configViews() {
        verifyBackupMenmonicWordsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String mnemonicWord = verifyBackupMenmonicWordsAdapter.getData().get(position).getMnemonicWord();
                if (verifyBackupMenmonicWordsAdapter.setSelection(position)) {
                    verifyBackupSelectedMnemonicWordsAdapter.addData(mnemonicWord);
                }
            }
        });
        verifyBackupSelectedMnemonicWordsAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                List<VerifyMnemonicWordTag> datas = verifyBackupMenmonicWordsAdapter.getData();
                for (int i = 0; i < datas.size(); i++) {
                    if (TextUtils.equals(datas.get(i).getMnemonicWord(), verifyBackupSelectedMnemonicWordsAdapter.getData().get(position))) {
                        verifyBackupMenmonicWordsAdapter.setUnselected(i);
                        break;
                    }
                }
                verifyBackupSelectedMnemonicWordsAdapter.remove(position);
            }

        });
        btnConfirm.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                LogUtils.d("VerifyMnemonicBackUp", "Click!!");
                List<String> data = verifyBackupSelectedMnemonicWordsAdapter.getData();
                int size = data.size();
                if (size == 12) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < size; i++) {
                        stringBuilder.append(data.get(i));
                        if (i != size - 1) {
                            stringBuilder.append(" ");
                        }
                    }
                    String verifyMnemonic = stringBuilder.toString();
                    String trim = verifyMnemonic.trim();
                    LogUtils.d("VerifyMnemonicBackUp", "verifyMnemonic:" + verifyMnemonic);
                    LogUtils.d("VerifyMnemonicBackUp", "trim:" + trim);
                    if (TextUtils.equals(trim, walletMnemonic)) {
                        // TODO 修改该钱包备份标识
                        WalletDaoUtils.setIsBackup(walletId);
//                        AppManager.getAppManager().finishActivity(MnemonicBackupActivity.class);
                        setResult(VERIFY_SUCCESS_RESULT, new Intent());
                        finish();
                    } else {
                        ToastUtils.showToast(R.string.verify_backup_failed);
                    }
                } else {
                    ToastUtils.showToast(R.string.verify_backup_failed);
                }
                break;
        }
    }
}
