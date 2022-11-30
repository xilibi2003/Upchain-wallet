package pro.upchain.wallet.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pro.upchain.wallet.UpChainWalletApp;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.RepositoryFactory;


public class TransactionDetailViewModelFactory implements ViewModelProvider.Factory {

    private final EthereumNetworkRepository EthereumNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;

    public TransactionDetailViewModelFactory() {
        RepositoryFactory rf = UpChainWalletApp.repositoryFactory();

        this.EthereumNetworkRepository = rf.ethereumNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new TransactionDetailViewModel(
                EthereumNetworkRepository,
                findDefaultWalletInteract
                );
    }
}
