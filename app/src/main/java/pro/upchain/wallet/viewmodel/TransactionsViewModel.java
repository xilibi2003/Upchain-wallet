package pro.upchain.wallet.viewmodel;

import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.interact.FetchTransactionsInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.utils.LogUtils;

public class TransactionsViewModel extends BaseViewModel {
    private static final long FETCH_TRANSACTIONS_INTERVAL = 1;
    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<ETHWallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> defaultWalletBalance = new MutableLiveData<>();

    private final EthereumNetworkRepository ethereumNetworkRepository;
    private final FetchWalletInteract findDefaultWalletInteract;
    private final FetchTransactionsInteract fetchTransactionsInteract;

    private Disposable transactionDisposable;

    private String tokenAddr;

    TransactionsViewModel(
            EthereumNetworkRepository ethereumNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract,
            FetchTransactionsInteract fetchTransactionsInteract) {
        this.ethereumNetworkRepository = ethereumNetworkRepository;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.fetchTransactionsInteract = fetchTransactionsInteract;
    }

    @Override
    protected void onCleared() {
        super.onCleared();

//        transactionDisposable.dispose();
//        balanceDisposable.dispose();
    }

    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public LiveData<ETHWallet> defaultWallet() {
        return defaultWallet;
    }

    public LiveData<List<Transaction>> transactions() {
        return transactions;
    }

    public LiveData<Map<String, String>> defaultWalletBalance() {
        return defaultWalletBalance;
    }

    public void prepare(String token) {
        this.tokenAddr = token;
        progress.postValue(true);
        disposable = ethereumNetworkRepository
                .find()
                .subscribe(this::onDefaultNetwork, this::onError);
    }

    public void fetchTransactions() {
        LogUtils.d("fetchTransactions");
        progress.postValue(true);
        transactionDisposable = Observable.interval(0, FETCH_TRANSACTIONS_INTERVAL, TimeUnit.MINUTES)
            .doOnNext(l -> {
                        LogUtils.d("fetchTransactionsInteract");
                        disposable = fetchTransactionsInteract
                                .fetch(defaultWallet.getValue().address,  this.tokenAddr )
                                .subscribe(this::onTransactions, this::onError);
                    }
                )
            .subscribe();
    }

    public void getBalance() {
//        balanceDisposable = Observable.interval(0, GET_BALANCE_INTERVAL, TimeUnit.SECONDS)
//                .doOnNext(l -> getDefaultWalletBalance
//                        .get(defaultWallet.getValue())
//                        .subscribe(defaultWalletBalance::postValue, t -> {}))
//                .subscribe();
    }

    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = findDefaultWalletInteract
                .findDefault()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void onDefaultWallet(ETHWallet wallet) {
        LogUtils.d("onDefaultWallet");
        defaultWallet.setValue(wallet);
//        getBalance();
        fetchTransactions();
    }

    private void onTransactions(Transaction[] transactions) {
        progress.postValue(false);

        // ETH transfer ingore the contract call
        if (TextUtils.isEmpty(tokenAddr)) {
            ArrayList<Transaction> transactionList = new ArrayList<>();
            LogUtils.d("size:" + transactionList.size());
            for (Transaction t: transactions) {
                if (t.operations == null || t.operations.length == 0) {
                    transactionList.add(t);
                }
            }
            this.transactions.postValue(transactionList);
        } else {
            this.transactions.postValue(Arrays.asList(transactions));
        }


    }

}
