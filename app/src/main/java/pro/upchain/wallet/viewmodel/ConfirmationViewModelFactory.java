package pro.upchain.wallet.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pro.upchain.wallet.UpChainWalletApp;
import pro.upchain.wallet.interact.CreateTransactionInteract;
import pro.upchain.wallet.interact.FetchGasSettingsInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.RepositoryFactory;


public class ConfirmationViewModelFactory implements ViewModelProvider.Factory {

    private final EthereumNetworkRepository ethereumNetworkRepository;
    private FetchWalletInteract findDefaultWalletInteract;
    private FetchGasSettingsInteract fetchGasSettingsInteract;
    private CreateTransactionInteract createTransactionInteract;

    public ConfirmationViewModelFactory() {
        RepositoryFactory rf = UpChainWalletApp.repositoryFactory();

        this.ethereumNetworkRepository = rf.ethereumNetworkRepository;
        this.findDefaultWalletInteract = new FetchWalletInteract();
        this.fetchGasSettingsInteract = new FetchGasSettingsInteract(UpChainWalletApp.sp, ethereumNetworkRepository);
        this.createTransactionInteract = new CreateTransactionInteract(ethereumNetworkRepository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ConfirmationViewModel(ethereumNetworkRepository, findDefaultWalletInteract, fetchGasSettingsInteract , createTransactionInteract);
    }
}
