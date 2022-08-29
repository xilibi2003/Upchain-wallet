package pro.upchain.wallet.ui.adapter;

import android.content.Context;

import java.util.List;

import pro.upchain.wallet.base.CommonAdapter;
import pro.upchain.wallet.base.ViewHolder;


public class MessageCenterAdapter extends CommonAdapter<String> {
    public MessageCenterAdapter(Context context, List<String> datas, int layoutId) {
        super(context, datas, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, String s) {

    }
}