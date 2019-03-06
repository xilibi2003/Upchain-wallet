package pro.upchain.wallet.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;

import pro.upchain.wallet.R;
import pro.upchain.wallet.base.ViewHolder;
import pro.upchain.wallet.ui.activity.AddTokenActivity;

import java.util.List;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class AddTokenListAdapter extends BaseAdapter {
    private AddTokenActivity mActivity;
    private int layoutId;
    private List<AddTokenActivity.TokenItem> items;

    public AddTokenListAdapter(AddTokenActivity context, List<AddTokenActivity.TokenItem> tokenItems , int layoutId) {

        this.mActivity = context;
        this.layoutId = layoutId;
        this.items = tokenItems;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = ViewHolder.get(mActivity, convertView, parent,
                layoutId, position);
        if (position == 0) {
            holder.getView(R.id.lly_item).setBackgroundColor(mActivity.getResources().getColor(R.color.white));
            holder.setVisible(R.id.add_switch, false);
        } else {
            holder.getView(R.id.lly_item).setBackgroundColor(mActivity.getResources().getColor(R.color.add_property_gray_bg_color));
            holder.setVisible(R.id.add_switch, true);
            holder.setTag(R.id.add_switch,  items.get(position));
            ((Switch)holder.getView(R.id.add_switch)).setOnCheckedChangeListener(mActivity::onCheckedChanged);

        }

        holder.setText(R.id.tv_ico_name, items.get(position).tokenInfo.symbol);
        holder.setImageResource(R.id.civ_ico_logo, items.get(position).iconId);

        if (items.get(position).added) {
            holder.setChecked(R.id.add_switch, true);
        } else {
            holder.setChecked(R.id.add_switch, false);
        }

        return holder.getConvertView();
    }


}
