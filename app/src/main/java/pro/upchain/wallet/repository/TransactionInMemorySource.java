package pro.upchain.wallet.repository;

import android.text.format.DateUtils;


import pro.upchain.wallet.entity.Transaction;

import java.util.Map;

import io.reactivex.Single;

// TODO: Add pagination.
public class TransactionInMemorySource implements TransactionLocalSource {

    private static final long MAX_TIME_OUT = DateUtils.MINUTE_IN_MILLIS;
    private final Map<String, CacheUnit> cache = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public Single<Transaction[]> fetchTransaction(String walletAddr) {
        return Single.fromCallable(() -> {
            CacheUnit unit = cache.get(walletAddr);
            Transaction[] transactions = new Transaction[0];
            if (unit != null) {
                if (System.currentTimeMillis() - unit.create > MAX_TIME_OUT) {
                    cache.remove(walletAddr);
                } else {
                    transactions = unit.transactions;
                }

            }
            return transactions;
        });
    }

    @Override
    public void putTransactions(String walletAddr, Transaction[] transactions) {
        cache.put(walletAddr, new CacheUnit(walletAddr, System.currentTimeMillis(), transactions));
    }

    @Override
    public
    Single<Transaction[]> fetchTransaction(String walletAddr, String tokenAddr) {
        return Single.fromCallable(() -> {
            CacheUnit unit = cache.get(walletAddr + tokenAddr);
            Transaction[] transactions = new Transaction[0];
            if (unit != null) {
                if (System.currentTimeMillis() - unit.create > MAX_TIME_OUT) {
                    cache.remove(walletAddr + tokenAddr);
                } else {
                    transactions = unit.transactions;
                }

            }
            return transactions;
        });
    }

    @Override
    public void putTransactions(String walletAddr, String tokenAddr, Transaction[] transactions) {
        cache.put(walletAddr + tokenAddr, new CacheUnit(walletAddr, System.currentTimeMillis(), transactions));
    }

    @Override
    public void clear() {
        cache.clear();
    }

    private static class CacheUnit {
        final String accountAddress;
        final long create;
        final Transaction[] transactions;

        private CacheUnit(String accountAddress, long create, Transaction[] transactions) {
            this.accountAddress = accountAddress;
            this.create = create;
            this.transactions = transactions;
        }
    }
}
