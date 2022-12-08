package pro.upchain.wallet.interact;


import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.repository.TransactionRepositoryType;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.upchain.wallet.utils.LogUtils;

public class FetchTransactionsInteract {

    private final TransactionRepositoryType transactionRepository;

    public FetchTransactionsInteract(TransactionRepositoryType transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Observable<Transaction[]> fetch(String walletAddr, String token) {
        LogUtils.d("fetch");

        return transactionRepository
                .fetchTransaction(walletAddr, token)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
