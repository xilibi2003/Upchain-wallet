package pro.upchain.wallet.ui.adapter;

import android.util.Log;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import pro.upchain.wallet.R;
import pro.upchain.wallet.entity.Token;

public class TokensAdapter extends BaseQuickAdapter<Token, BaseViewHolder> {

    private final List<Token> items = new ArrayList<>();


    public TokensAdapter(int layoutResId, @Nullable List<Token> data) {
        super(layoutResId, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Token token) {
        Log.d(TAG, "convert: helper:" + helper + ", token:" + token);
        helper.setText(R.id.symbol, token.tokenInfo.symbol);
        helper.setText(R.id.balance, token.balance);
        helper.setText(R.id.tv_property_cny, token.value);

    }

    public void setTokens(List<Token> tokens) {
        setNewData(tokens);
    }


}
