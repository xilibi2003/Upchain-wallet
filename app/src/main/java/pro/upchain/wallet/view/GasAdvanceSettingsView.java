package pro.upchain.wallet.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;

import java.math.BigDecimal;
import java.math.BigInteger;

import pro.upchain.wallet.C;
import pro.upchain.wallet.R;
import pro.upchain.wallet.utils.BalanceUtils;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */
public class GasAdvanceSettingsView extends FrameLayout {

    private TextView gasPriceText;
    private TextView gasLimitText;
    private TextView networkFeeText;
    private TextView gasPriceInfoText;
    private TextView gasLimitInfoText;

    AppCompatSeekBar gasPriceSlider ;
    AppCompatSeekBar gasLimitSlider ;

    private BigInteger gasPrice;
    private BigInteger gasLimit;

    public interface onGasCustom {
        void gasCustom(BigInteger gasPrice, BigInteger gasLimit);
    }


    public GasAdvanceSettingsView(@NonNull Context context, onGasCustom onGasCustomLister) {
        super(context);

        LayoutInflater.from(getContext())
                .inflate(R.layout.layout_gas_settings, this, true);



        gasPriceSlider = findViewById(R.id.gas_price_slider);
        gasLimitSlider = findViewById(R.id.gas_limit_slider);
        gasPriceText = findViewById(R.id.gas_price_text);
        gasLimitText = findViewById(R.id.gas_limit_text);
        networkFeeText = findViewById(R.id.text_network_fee);
        gasPriceInfoText = findViewById(R.id.gas_price_info_text);
        gasLimitInfoText = findViewById(R.id.gas_limit_info_text);


        findViewById(R.id.button_gas_save).setOnClickListener(view -> {
            onGasCustomLister.gasCustom(gasPrice, gasLimit);
        });
    }

    public void fill(BigInteger gasPrice, BigInteger gasLimit) {
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;

        BigInteger gasLimitMin = BigInteger.valueOf(C.GAS_LIMIT_MIN);
        BigInteger gasLimitMax = BigInteger.valueOf(C.GAS_LIMIT_MAX);
        BigInteger gasPriceMin = BigInteger.valueOf(C.GAS_PRICE_MIN);
        BigInteger networkFeeMax = BigInteger.valueOf(C.NETWORK_FEE_MAX);
        BigDecimal  gasPriceMaxGwei = BalanceUtils
                .weiToGweiBI(networkFeeMax.divide(gasLimitMax));
        BigInteger granularity = BalanceUtils.gweiToWei(gasPriceMaxGwei.divide(BigDecimal.valueOf(100)));
        gasPriceSlider.setMax(99);


        gasPriceSlider.setProgress(((gasPrice.subtract(gasPriceMin)).divide(granularity)).intValue());
        gasPriceSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                    {
                        BigInteger scaledProgress = BigInteger.valueOf(progress).multiply(granularity);

                        GasAdvanceSettingsView.this.gasPrice = scaledProgress.add(gasPriceMin);


                        updateNetworkFee();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

        gasLimitSlider.setMax(gasLimitMax.subtract(gasLimitMin).intValue());
        gasLimitSlider.setProgress(gasLimit.subtract(gasLimitMin).intValue());
        gasLimitSlider.refreshDrawableState();
        gasLimitSlider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress = progress / 100;
                        progress = progress * 100;

                        GasAdvanceSettingsView.this.gasLimit = BigInteger.valueOf(progress).add(gasLimitMin);
                        updateNetworkFee();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });

    }

    private void updateNetworkFee() {
        String priceStr = BalanceUtils.weiToGwei(gasPrice) + " " + C.GWEI_UNIT;
        gasPriceText.setText(priceStr);

        gasLimitText.setText(gasLimit.toString());

        String fee = BalanceUtils.weiToEth(gasPrice.multiply(gasLimit)).toPlainString() + " " + C.ETH_SYMBOL;
        networkFeeText.setText(fee);
    }

}
