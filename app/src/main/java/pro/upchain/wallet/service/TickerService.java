package pro.upchain.wallet.service;


import io.reactivex.Observable;
import pro.upchain.wallet.entity.Ticker;

public interface TickerService {

    Observable<Ticker> fetchTickerPrice(String symbols, String currency);
}