package pro.upchain.wallet.interact;

import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.repository.WalletRepository;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class FetchWalletsInteract {

    private final WalletRepository walletRepository;

    public FetchWalletsInteract(WalletRepository accountRepository) {
        this.walletRepository = accountRepository;
    }

    public Single<List<ETHWallet>> fetch() {
        return walletRepository
                .fetchWallets()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
