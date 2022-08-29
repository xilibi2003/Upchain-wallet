package pro.upchain.wallet.service;


import io.reactivex.Observable;
import pro.upchain.wallet.entity.Transaction;

public interface BlockExplorerClientType {
    Observable<Transaction[]> fetchTransactions(String forAddress, String forToken);
}