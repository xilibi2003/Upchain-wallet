package pro.upchain.wallet.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import pro.upchain.wallet.R;


public class LoadWalletSelectStandardPopupWindow extends PopupWindow implements OnClickListener {

    private View contentView;
    private final TextView tvJaxx;
    private final TextView tvLedger;
    private final TextView tvCustom;
    private final ImageView ivJaxx;
    private final ImageView ivLedger;
    private final ImageView ivCustom;
    private int selection = 0;

    public void setOnPopupItemSelectedListener(OnPopupItemSelectedListener onPopupItemSelectedListener) {
        this.onPopupItemSelectedListener = onPopupItemSelectedListener;
    }

    private OnPopupItemSelectedListener onPopupItemSelectedListener;

    public LoadWalletSelectStandardPopupWindow(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.popup_load_wallet_select_standard, null);
        this.setContentView(contentView);
        contentView.findViewById(R.id.rl_jaxx).setOnClickListener(this);
        contentView.findViewById(R.id.rl_ledger).setOnClickListener(this);
        contentView.findViewById(R.id.rl_custom).setOnClickListener(this);
        tvJaxx = (TextView)contentView.findViewById(R.id.tv_standard_jaxx);
        tvJaxx.setSelected(true);
        tvLedger = (TextView)contentView.findViewById(R.id.tv_standard_ledger);
        tvCustom = (TextView)contentView.findViewById(R.id.tv_standard_custom);
        ivJaxx = (ImageView)contentView.findViewById(R.id.iv_standard_jaxx);
        ivLedger = (ImageView)contentView.findViewById(R.id.iv_standard_ledger);
        ivCustom = (ImageView)contentView.findViewById(R.id.iv_standard_custom);
        setProperty();
    }

    private void initSelection() {
        tvJaxx.setSelected(false);
        ivJaxx.setVisibility(View.GONE);
        tvLedger.setSelected(false);
        ivLedger.setVisibility(View.GONE);
        tvCustom.setSelected(false);
        ivCustom.setVisibility(View.GONE);
    }

    private void setProperty() {
        setAnimationStyle(R.style.AnimBottom);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.update();
        ColorDrawable dw = new ColorDrawable(0xff000000);
        setBackgroundDrawable(dw);
    }


    @Override
    public void onClick(View v) {
        initSelection();

        switch (v.getId()) {
            case R.id.rl_jaxx:
                selection = 0;
                tvJaxx.setSelected(true);
                ivJaxx.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_ledger:
                selection = 1;
                tvLedger.setSelected(true);
                ivLedger.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_custom:
                selection = 2;
                tvCustom.setSelected(true);
                ivCustom.setVisibility(View.VISIBLE);
                break;
        }
        dismiss();
        if (onPopupItemSelectedListener != null)
            onPopupItemSelectedListener.onSelected(selection);
    }

    public interface OnPopupItemSelectedListener {
        void onSelected(int selection);
    }
}
