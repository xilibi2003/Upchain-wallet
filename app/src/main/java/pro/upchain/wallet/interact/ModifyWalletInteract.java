package pro.upchain.wallet.interact;

import io.reactivex.Single;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.utils.ETHWalletUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class ModifyWalletInteract {


    public ModifyWalletInteract() {
    }


    public Single<Boolean> modifyWalletName(long walletId, String name) {
        return Single.fromCallable(() -> {
            WalletDaoUtils.updateWalletName(walletId, name);
            return true;
        });

    }


    public Single<ETHWallet> modifyWalletPwd(final long walletId, final String walletName, final String oldPassword, final String newPassword) {

        return Single.fromCallable(() -> {
            return ETHWalletUtils.modifyPassword(walletId, walletName, oldPassword, newPassword);
                }

        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<String>  deriveWalletPrivateKey(final long walletId, final String password) {

        return Single.fromCallable(() -> {
                return ETHWalletUtils.derivePrivateKey(walletId, password);
        } ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public  Single<String>  deriveWalletKeystore(final long walletId, final String password) {

        return Single.fromCallable(() -> {
                return ETHWalletUtils.deriveKeystore(walletId, password);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        // mView.showDeriveKeystore(keystore);

    }


    public Single<Boolean> deleteWallet(final long walletId) {
        return Single.fromCallable(() -> {
               return ETHWalletUtils.deleteWallet(walletId);
            }
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
