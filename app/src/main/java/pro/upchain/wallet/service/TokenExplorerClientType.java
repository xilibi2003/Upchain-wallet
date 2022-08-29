package pro.upchain.wallet.service;


import io.reactivex.Observable;
import pro.upchain.wallet.entity.TokenInfo;

public interface TokenExplorerClientType {
    Observable<TokenInfo[]> fetch(String walletAddress);
}