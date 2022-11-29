package pro.upchain.wallet.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.interact.FetchWalletInteract;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new FetchWalletInteract().fetch()
                .observeOn(AndroidSchedulers
                        .mainThread())
                .delay(2, TimeUnit.SECONDS)
                .subscribe(this::onWalltes, this::onError);

    }

    public void onWalltes(List<ETHWallet> ethWallets) {

        if (ethWallets.size() == 0) {
            Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SplashActivity.this.startActivity(intent);
        }


    }


    public void onError(Throwable throwable) {
        Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SplashActivity.this.startActivity(intent);
    }

}
