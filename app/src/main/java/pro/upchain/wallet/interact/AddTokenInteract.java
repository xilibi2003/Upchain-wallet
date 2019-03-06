package pro.upchain.wallet.interact;


import pro.upchain.wallet.repository.TokenRepositoryType;
import pro.upchain.wallet.repository.WalletRepository;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AddTokenInteract {
    private final TokenRepositoryType tokenRepository;
    private final WalletRepository walletRepository;

    public AddTokenInteract(
            WalletRepository walletRepository, TokenRepositoryType tokenRepository) {
        this.walletRepository = walletRepository;
        this.tokenRepository = tokenRepository;
    }

    public Completable add(String address, String symbol, int decimals) {
        return walletRepository
                .getDefaultWallet()
                .flatMapCompletable(wallet -> tokenRepository
                        .addToken(wallet.address, address, symbol, decimals)
                        .observeOn(AndroidSchedulers.mainThread()));
    }
}
