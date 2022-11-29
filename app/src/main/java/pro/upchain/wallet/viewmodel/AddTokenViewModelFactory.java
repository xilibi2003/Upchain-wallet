package pro.upchain.wallet.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pro.upchain.wallet.UpChainWalletApp;
import pro.upchain.wallet.interact.AddTokenInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.RepositoryFactory;

public class AddTokenViewModelFactory implements ViewModelProvider.Factory {

    private final AddTokenInteract addTokenInteract;
    private final FetchWalletInteract findDefaultWalletInteract;

    public AddTokenViewModelFactory() {
        RepositoryFactory rf = UpChainWalletApp.repositoryFactory();

        this.findDefaultWalletInteract = new FetchWalletInteract();
        this.addTokenInteract = new AddTokenInteract(findDefaultWalletInteract, rf.tokenRepository);;

    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddTokenViewModel(addTokenInteract, findDefaultWalletInteract);
    }
}
