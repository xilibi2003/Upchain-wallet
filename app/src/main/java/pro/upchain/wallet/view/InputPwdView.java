package pro.upchain.wallet.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import pro.upchain.wallet.R;


public class InputPwdView extends FrameLayout {
    private EditText password;
    private onConfirmSend onConfirmSender;

    public interface onConfirmSend {
        void sendTransaction(String pwd);
    }

    public InputPwdView(@NonNull Context context, onConfirmSend l) {
        super(context);
        onConfirmSender = l;

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_input_password, this, true);
        password = (EditText)findViewById(R.id.password);

        findViewById(R.id.send_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onConfirmSender.sendTransaction(getPassword());
            }
        });
    }



    public String getPassword() {
        return password.getText().toString();
    }

    public void showKeyBoard() {
        password.requestFocus();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showKeyBoard();
    }
}
