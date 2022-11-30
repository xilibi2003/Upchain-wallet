package pro.upchain.wallet.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pro.upchain.wallet.interact.AddTokenInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;

public class AddTokenViewModel extends BaseViewModel {

    private final AddTokenInteract addTokenInteract;
    private final FetchWalletInteract findDefaultWalletInteract;

    private final MutableLiveData<Boolean> result = new MutableLiveData<>();

    AddTokenViewModel(
            AddTokenInteract addTokenInteract,
            FetchWalletInteract findDefaultWalletInteract
            ) {
        this.addTokenInteract = addTokenInteract;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
    }

    public void save(String address, String symbol, int decimals) {
        addTokenInteract
                .add(address, symbol, decimals)
                .subscribe(this::onSaved, this::onError);
    }

    private void onSaved() {
        progress.postValue(false);
        result.postValue(true);
    }

    public LiveData<Boolean> result() {
        return result;
    }

    public void showTokens(Context context) {


    }
}
