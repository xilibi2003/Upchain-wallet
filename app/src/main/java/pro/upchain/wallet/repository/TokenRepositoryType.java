package pro.upchain.wallet.repository;


import io.reactivex.Completable;
import io.reactivex.Observable;
import pro.upchain.wallet.entity.Token;

public interface TokenRepositoryType {

    Observable<Token[]> fetch(String walletAddress);

    Completable addToken(String walletAddress, String address, String symbol, int decimals);
}