package pro.upchain.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import pro.upchain.wallet.UpChainWalletApp;
import pro.upchain.wallet.interact.AddTokenInteract;
import pro.upchain.wallet.interact.FindDefaultWalletInteract;
import pro.upchain.wallet.repository.RepositoryFactory;

public class AddTokenViewModelFactory implements ViewModelProvider.Factory {

    private final AddTokenInteract addTokenInteract;
    private final FindDefaultWalletInteract findDefaultWalletInteract;

    public AddTokenViewModelFactory() {
        RepositoryFactory rf = UpChainWalletApp.repositoryFactory();

        this.addTokenInteract = new AddTokenInteract(rf.walletRepository, rf.tokenRepository);;
        this.findDefaultWalletInteract = new FindDefaultWalletInteract(rf.walletRepository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTokenViewModel(addTokenInteract, findDefaultWalletInteract);
    }
}
