package pro.upchain.wallet.interact;

import pro.upchain.wallet.domain.ETHWallet;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.upchain.wallet.utils.WalletDaoUtils;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class FetchWalletInteract {


    public FetchWalletInteract() {
    }

    public Single<List<ETHWallet>> fetch() {


        return Single.fromCallable(WalletDaoUtils::loadAll).observeOn(AndroidSchedulers.mainThread());

    }

    public Single<ETHWallet> findDefault() {

        return Single.fromCallable(WalletDaoUtils::getCurrent);

    }
}
