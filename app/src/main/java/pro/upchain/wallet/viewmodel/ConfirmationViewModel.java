package pro.upchain.wallet.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.interact.FetchGasSettingsInteract;
import pro.upchain.wallet.interact.FindDefaultWalletInteract;

import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.TokenRepository;

import pro.upchain.wallet.utils.LogUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class ConfirmationViewModel extends BaseViewModel {
    private final MutableLiveData<String> newTransaction = new MutableLiveData<>();
    private final MutableLiveData<ETHWallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<GasSettings> gasSettings = new MutableLiveData<>();

    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();

    private final EthereumNetworkRepository ethereumNetworkRepository;
    private final FindDefaultWalletInteract findDefaultWalletInteract;
    private final FetchGasSettingsInteract fetchGasSettingsInteract;



    boolean confirmationForTokenTransfer = false;

    public ConfirmationViewModel(
            EthereumNetworkRepository ethereumNetworkRepository,
            FindDefaultWalletInteract findDefaultWalletInteract,
            FetchGasSettingsInteract fetchGasSettingsInteract) {
        this.ethereumNetworkRepository = ethereumNetworkRepository;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
        this.fetchGasSettingsInteract = fetchGasSettingsInteract;
    }

    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public void createTransaction(String password, String to, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit) {
        progress.postValue(true);

        final Web3j web3j = Web3j.build(new HttpService(defaultNetwork.getValue().rpcServerUrl));

        disposable =  Single.fromCallable(() -> {

                    EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                            defaultWallet.getValue().getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

                    return ethGetTransactionCount.getTransactionCount();

                }).flatMap(nonce -> Single.fromCallable( () -> {

                    Credentials credentials = WalletUtils.loadCredentials(password,  defaultWallet.getValue().getKeystorePath());
                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, amount);
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

                    String hexValue = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

                    return ethSendTransaction.getTransactionHash();

                } )).subscribeOn(Schedulers.io())
                        .subscribe(this::onCreateTransaction, this::onError);

    }

    public void createTokenTransfer(String password, String to, String contractAddress,
                                    BigInteger amount, BigInteger gasPrice, BigInteger gasLimit) {
        progress.postValue(true);

        final Web3j web3j = Web3j.build(new HttpService(defaultNetwork.getValue().rpcServerUrl));

        String callFuncData = TokenRepository.createTokenTransferData(to, amount);


        disposable =  Single.fromCallable(() -> {

            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                    defaultWallet.getValue().getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

            return ethGetTransactionCount.getTransactionCount();

        }).flatMap(nonce -> Single.fromCallable( () -> {

            Credentials credentials = WalletUtils.loadCredentials(password,  defaultWallet.getValue().getKeystorePath());
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce, gasPrice, gasLimit, contractAddress, callFuncData);

            LogUtils.d("rawTransaction:" + rawTransaction);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

            return ethSendTransaction.getTransactionHash();

        } )).subscribeOn(Schedulers.io())
                .subscribe(this::onCreateTransaction, this::onError);
    }

    public LiveData<ETHWallet> defaultWallet() {
        return defaultWallet;
    }

    public MutableLiveData<GasSettings> gasSettings() {
        return gasSettings;
    }

    public LiveData<String> sendTransaction() { return newTransaction; }

    public void prepare(boolean confirmationForTokenTransfer) {
        this.confirmationForTokenTransfer = confirmationForTokenTransfer;
        disposable = ethereumNetworkRepository
                .find()
                .subscribe(this::onDefaultNetwork, this::onError);
    }

    private void onDefaultNetwork(NetworkInfo networkInfo) {
        defaultNetwork.postValue(networkInfo);
        disposable = findDefaultWalletInteract
                .find()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void onCreateTransaction(String transaction) {
        progress.postValue(false);
        newTransaction.postValue(transaction);
    }

    private void onDefaultWallet(ETHWallet wallet) {
        defaultWallet.setValue(wallet);
        if (gasSettings.getValue() == null) {
            onGasSettings(fetchGasSettingsInteract.fetch(confirmationForTokenTransfer));
        }
    }

    private void onGasSettings(GasSettings gasSettings) {
        this.gasSettings.setValue(gasSettings);
    }

}
