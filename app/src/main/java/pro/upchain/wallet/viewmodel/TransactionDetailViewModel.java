package pro.upchain.wallet.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pro.upchain.wallet.R;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.EthereumNetworkRepository;

public class TransactionDetailViewModel extends BaseViewModel {

    private final EthereumNetworkRepository ethereumNetworkRepository;

    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<ETHWallet> defaultWallet = new MutableLiveData<>();

    TransactionDetailViewModel(
            EthereumNetworkRepository ethereumNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract) {
        this.ethereumNetworkRepository = ethereumNetworkRepository;

        ethereumNetworkRepository
                .find()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(defaultNetwork::postValue);

        disposable = findDefaultWalletInteract
                .findDefault()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(defaultWallet::postValue);
    }


    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public void showMoreDetails(Context context, Transaction transaction) {
        NetworkInfo networkInfo = defaultNetwork.getValue();
        if (networkInfo != null && !TextUtils.isEmpty(networkInfo.etherscanUrl)) {
            Uri uri = Uri.parse(networkInfo.etherscanUrl)
                    .buildUpon()
                    .appendEncodedPath("tx")
                    .appendEncodedPath(transaction.hash)
                    .build();


            Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(launchBrowser);
        }
    }

    public void shareTransactionDetail(Context context, Transaction transaction) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.subject_transaction_detail));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, buildEtherscanUri(transaction).toString());
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    private Uri buildEtherscanUri(Transaction transaction) {
        NetworkInfo networkInfo = defaultNetwork.getValue();
        if (networkInfo != null && !TextUtils.isEmpty(networkInfo.etherscanUrl)) {
            return Uri.parse(networkInfo.etherscanUrl)
                    .buildUpon()
                    .appendEncodedPath("tx")
                    .appendEncodedPath(transaction.hash)
                    .build();
        }
        return null;
    }

    public LiveData<ETHWallet> defaultWallet() {
        return defaultWallet;
    }
}
