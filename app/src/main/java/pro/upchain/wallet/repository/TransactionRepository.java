package pro.upchain.wallet.repository;


import android.text.TextUtils;

import java.math.BigInteger;

import io.reactivex.Single;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.service.BlockExplorerClientType;
import pro.upchain.wallet.utils.LogUtils;

import io.reactivex.Maybe;
import io.reactivex.Observable;

public class TransactionRepository implements TransactionRepositoryType {

    private final EthereumNetworkRepository networkRepository;
    private final TransactionLocalSource transactionLocalSource;
    private final BlockExplorerClientType blockExplorerClient;

    public TransactionRepository(
            EthereumNetworkRepository networkRepository,
            TransactionLocalSource inMemoryCache,
            TransactionLocalSource inDiskCache,
            BlockExplorerClientType blockExplorerClient) {
        this.networkRepository = networkRepository;
        this.blockExplorerClient = blockExplorerClient;
        this.transactionLocalSource = inMemoryCache;

        this.networkRepository.addOnChangeDefaultNetwork(this::onNetworkChanged);
    }

    /**
     * 查询交易记录
     * @param walletAddr
     * @param tokenAddr
     * @return
     */
    @Override
    public Observable<Transaction[]> fetchTransaction(String walletAddr, String tokenAddr) {
        return Observable.create(e -> {
            Transaction[] transactions;
            if (TextUtils.isEmpty(tokenAddr)) {
                transactions = transactionLocalSource.fetchTransaction(walletAddr).blockingGet();
            } else {
                transactions = transactionLocalSource.fetchTransaction(walletAddr, tokenAddr).blockingGet();
            }

            if (transactions != null && transactions.length > 0) {
                e.onNext(transactions);
            }
            transactions = blockExplorerClient.fetchTransactions(walletAddr, tokenAddr).blockingFirst();
            transactionLocalSource.clear();
            if (TextUtils.isEmpty(tokenAddr)) {
                transactionLocalSource.putTransactions(walletAddr, transactions);
            } else {
                transactionLocalSource.putTransactions(walletAddr, tokenAddr, transactions);
            }
            e.onNext(transactions);
            e.onComplete();
        });
    }

    @Override
    public Maybe<Transaction> findTransaction(String walletAddr, String transactionHash) {
//        return fetchTransaction(walletAddr, null)
//                .firstElement()
//                .flatMap(transactions -> {
//                    for (Transaction transaction : transactions) {
//                        if (transaction.hash.equals(transactionHash)) {
//                            return Maybe.just(transaction);
//                        }
//                    }
//                    return null;
//                });
        return null;
    }

    private void onNetworkChanged(NetworkInfo networkInfo) {
        transactionLocalSource.clear();
    }


    @Override
    public Single<String> createTransaction(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {
        return null;
    }

    @Override
    public Single<String> createTransaction(ETHWallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, byte[] data, String password) {
        return null;
    }
}
