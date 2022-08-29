package pro.upchain.wallet.repository;


import io.reactivex.Maybe;
import io.reactivex.Observable;
import pro.upchain.wallet.entity.Transaction;

public interface TransactionRepositoryType {
    Observable<Transaction[]> fetchTransaction(String walletAddr, String token);

    Maybe<Transaction> findTransaction(String walletAddr, String transactionHash);
}