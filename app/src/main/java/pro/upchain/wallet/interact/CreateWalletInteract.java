package pro.upchain.wallet.interact;

import java.util.Arrays;

import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CreateWalletInteract {


    public CreateWalletInteract() {
    }

    public Single<ETHWallet> create(final String name, final String pwd, String confirmPwd, String pwdReminder) {
        return Single.fromCallable(() -> {
            ETHWallet ethWallet = ETHWalletUtils.generateMnemonic(name, pwd);
            WalletDaoUtils.insertNewWallet(ethWallet);
            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<ETHWallet> loadWalletByKeystore(final String keystore, final String pwd) {
        return Single.fromCallable(() -> {
            ETHWallet ethWallet = ETHWalletUtils.loadWalletByKeystore(keystore, pwd);
            if (ethWallet != null) {
                WalletDaoUtils.insertNewWallet(ethWallet);
            }

            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<ETHWallet> loadWalletByPrivateKey(final String privateKey, final String pwd) {
        return Single.fromCallable(() -> {

                    ETHWallet ethWallet = ETHWalletUtils.loadWalletByPrivateKey(privateKey, pwd);
                    if (ethWallet != null) {
                        WalletDaoUtils.insertNewWallet(ethWallet);
                    }
                    return ethWallet;
                }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<ETHWallet> loadWalletByMnemonic(final String bipPath, final String mnemonic, final String pwd) {
        return Single.fromCallable(() -> {
            ETHWallet ethWallet = ETHWalletUtils.importMnemonic(bipPath
                    , Arrays.asList(mnemonic.split(" ")), pwd);
            if (ethWallet != null) {
                WalletDaoUtils.insertNewWallet(ethWallet);
            }
            return ethWallet;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());


    }

}
