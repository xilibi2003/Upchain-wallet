package pro.upchain.wallet.ui.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Collections;
import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.domain.VerifyMnemonicWordTag;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class VerifyBackupMnemonicWordsAdapter extends BaseQuickAdapter<VerifyMnemonicWordTag, BaseViewHolder> {

    public VerifyBackupMnemonicWordsAdapter(int layoutResId, @Nullable List<VerifyMnemonicWordTag> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VerifyMnemonicWordTag verifyMnemonicWordTag) {
        if (verifyMnemonicWordTag.isSelected()) {
            helper.setBackgroundColor(R.id.lly_tag, mContext.getResources().getColor(R.color.search_ico_upload_token));
            helper.setTextColor(R.id.tv_mnemonic_word,mContext.getResources().getColor(R.color.white));
        } else {
            helper.setBackgroundColor(R.id.lly_tag, mContext.getResources().getColor(R.color.item_divider_bg_color));
            helper.setTextColor(R.id.tv_mnemonic_word,mContext.getResources().getColor(R.color.discovery_application_text_color));
        }
        helper.setText(R.id.tv_mnemonic_word, verifyMnemonicWordTag.getMnemonicWord());
    }

    public boolean setSelection(int position) {

        VerifyMnemonicWordTag verifyMnemonicWordTag = getData().get(position);
        if (verifyMnemonicWordTag.isSelected()) {
            return false;
        }
        verifyMnemonicWordTag.setSelected(true);
        Collections.shuffle(getData());
        notifyDataSetChanged();
        return true;
    }

    public boolean setUnselected(int position) {

        VerifyMnemonicWordTag verifyMnemonicWordTag = getData().get(position);
        if (!verifyMnemonicWordTag.isSelected()) {
            return false;
        }
        verifyMnemonicWordTag.setSelected(false);
        Collections.shuffle(getData());
        notifyDataSetChanged();
        return true;
    }

}
