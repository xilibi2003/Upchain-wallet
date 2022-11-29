package pro.upchain.wallet.web3;

import pro.upchain.wallet.web3.entity.Web3Transaction;

public interface OnSignTransactionListener {
    void onSignTransaction(Web3Transaction transaction , String url);
}
