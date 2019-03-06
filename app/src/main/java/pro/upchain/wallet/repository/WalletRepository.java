package pro.upchain.wallet.repository;

import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.utils.LogUtils;
import pro.upchain.wallet.utils.WalletDaoUtils;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * 微信: xlbxiong
 */



public class WalletRepository  {
    public static WalletRepository sSelf;
    private final SharedPreferenceRepository spRepository;

    public static WalletRepository init(SharedPreferenceRepository sf) {
        if (sSelf == null) {
            sSelf = new WalletRepository(sf);
        }
        return sSelf;
    }

    private WalletRepository(SharedPreferenceRepository preferenceRepositoryType) {
        this.spRepository = preferenceRepositoryType;
    }

    public Single<List<ETHWallet>> fetchWallets() {

        return Single.fromCallable(() -> {
            List<ETHWallet> ethWallets = WalletDaoUtils.loadAll();
            return ethWallets;
        }) .subscribeOn(Schedulers.io());

    }

    public Single<ETHWallet> findWallet(String address) {
        LogUtils.d("Wallet: " + address);
        return fetchWallets()
                .flatMap(accounts -> {
                    for (ETHWallet wallet : accounts) {
                        if (wallet.getAddress().equals(address)) {
                            return Single.just(wallet);
                        }
                    }
                    return null;
                });
    }

    public Completable setDefaultWallet(ETHWallet wallet) {
        return Completable.fromAction(() -> spRepository.setCurrentWalletAddress(wallet.getAddress()));
    }

    public Single<ETHWallet> getDefaultWallet() {
        return Single.fromCallable(spRepository::getCurrentWalletAddress)
                .flatMap(this::findWallet);
    }

}