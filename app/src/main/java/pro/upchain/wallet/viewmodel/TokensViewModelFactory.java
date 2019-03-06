package pro.upchain.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import pro.upchain.wallet.UpChainWalletApp;
import pro.upchain.wallet.interact.FetchTokensInteract;
import pro.upchain.wallet.interact.FindDefaultWalletInteract;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.repository.WalletRepository;


public class TokensViewModelFactory implements ViewModelProvider.Factory {

    private final FetchTokensInteract fetchTokensInteract;
    private final EthereumNetworkRepository ethereumNetworkRepository;
    private final WalletRepository walletRepository;

    public TokensViewModelFactory() {

        RepositoryFactory rf = UpChainWalletApp.repositoryFactory();
        fetchTokensInteract = new FetchTokensInteract(rf.tokenRepository);
        ethereumNetworkRepository = rf.ethereumNetworkRepository;
        walletRepository = rf.walletRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TokensViewModel(
                ethereumNetworkRepository,
                new FindDefaultWalletInteract(walletRepository),
                fetchTokensInteract
                );
    }
}
