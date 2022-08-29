package pro.upchain.wallet.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.Single;
import pro.upchain.wallet.R;
import pro.upchain.wallet.base.BaseFragment;
import pro.upchain.wallet.utils.GlideImageLoader;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ExportKeystoreQRCodeFragment extends BaseFragment {
    @BindView(R.id.iv_keystore)
    ImageView ivKeystore;

    String walletKeystore;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_derive_keystore_qrcode;
    }

    @Override
    public void attachView() {
        Bundle arguments = getArguments();
        walletKeystore = arguments.getString("walletKeystore");
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {

        ivKeystore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Single.fromCallable(
                        () -> {
                            return QRCodeEncoder.syncEncodeQRCode(walletKeystore, BGAQRCodeUtil.dp2px(getSupportActivity()
                                    , 240), Color.parseColor("#000000"));
                        }
                ).subscribe(bitmap -> GlideImageLoader.loadBmpImage(ivKeystore, bitmap, -1));
            }
        });

    }

}