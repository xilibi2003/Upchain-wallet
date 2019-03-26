package pro.upchain.wallet.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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
