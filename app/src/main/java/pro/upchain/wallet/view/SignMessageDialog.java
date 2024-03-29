package pro.upchain.wallet.view;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import pro.upchain.wallet.R;
import pro.upchain.wallet.web3.entity.Message;


public class SignMessageDialog extends Dialog {
    //    private LinearLayout container;
    private TextView message;
    private TextView requester;
    private TextView address;
    private TextView value;
    private TextView valueLabel;
    private TextView messageLabel;
    private TextView title;
    private LinearLayout valueLayout;
    private TextView valueUSD;
    private TextView usdLabel;
    private Button btnReject;
    private LinearLayout layoutBtnApprove;
    private Context context;

    public SignMessageDialog(@NonNull Activity activity) {
        super(activity);
        this.context = activity;

        setContentView(R.layout.dialog_sign_message);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCanceledOnTouchOutside(true);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        title = findViewById(R.id.dialog_main_text);
        message = findViewById(R.id.message);
        requester = findViewById(R.id.requester);
        address = findViewById(R.id.address);
        value = findViewById(R.id.value);
        valueLabel = findViewById(R.id.value_label);
        messageLabel = findViewById(R.id.message_label);
        valueLayout = findViewById(R.id.value_layout);
        valueUSD = findViewById(R.id.value_usd);
        usdLabel = findViewById(R.id.usd_label);
        btnReject = findViewById(R.id.btn_reject);
        layoutBtnApprove = findViewById(R.id.button_container);
        btnReject.setOnClickListener(v -> dismiss());
    }

    public SignMessageDialog(Activity activity, Message<String> message) {
        this(activity);

        setMessage(message.value);
        setRequester(message.url);
    }

    public void setMessage(CharSequence message) {
        this.message.setText(message);
    }

    public void setRequester(CharSequence requester) {
        this.requester.setText(requester);
    }

    public void setAddress(CharSequence address) {
        this.address.setText(address);
    }

    public void setValue(CharSequence value, CharSequence dollarValue, String networkName)
    {
        title.setText(R.string.dialog_title_sign_transaction);

        this.message.setVisibility(View.GONE);
        this.messageLabel.setVisibility(View.GONE);

        this.valueLayout.setVisibility(View.VISIBLE);
        this.valueLabel.setVisibility(View.VISIBLE);

        this.valueUSD.setText(dollarValue);
        this.value.setText(value);

        if (networkName.length() > 0)
        {
            usdLabel.setVisibility(View.VISIBLE);
            usdLabel.setText(networkName);
        }
    }

    public void setOnApproveListener(View.OnClickListener listener) {
        layoutBtnApprove.setOnClickListener(listener);
        //btnApprove.setOnClickListener(listener);
    }

    public void setOnRejectListener(View.OnClickListener listener) {
        btnReject.setOnClickListener(listener);

    }
}
