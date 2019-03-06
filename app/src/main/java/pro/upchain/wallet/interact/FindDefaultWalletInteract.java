package pro.upchain.wallet.interact;


import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.repository.WalletRepository;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class FindDefaultWalletInteract {

    private final WalletRepository walletRepository;

    public FindDefaultWalletInteract(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    public Single<ETHWallet> find() {
        return walletRepository
                .getDefaultWallet()
                .onErrorResumeNext(walletRepository
                        .fetchWallets()
                        .to(single -> Flowable.fromIterable(single.blockingGet()))
                        .firstOrError()
                        .flatMapCompletable(walletRepository::setDefaultWallet)
                        .andThen(walletRepository.getDefaultWallet()))
                .observeOn(AndroidSchedulers.mainThread());   // 订阅者 在哪运行
    }
}
