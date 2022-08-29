package pro.upchain.wallet.repository;


import io.reactivex.Completable;
import io.reactivex.Single;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.TokenInfo;

public interface TokenLocalSource {
    Completable put(NetworkInfo networkInfo, String walletAddress, TokenInfo tokenInfo);

    Single<TokenInfo[]> fetch(NetworkInfo networkInfo, String walletAddress);
}