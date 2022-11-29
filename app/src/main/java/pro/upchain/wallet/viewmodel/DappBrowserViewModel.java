package pro.upchain.wallet.viewmodel;

import static pro.upchain.wallet.C.DAPP_DEFAULT_URL;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pro.upchain.wallet.C;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.ConfirmationType;
import pro.upchain.wallet.entity.DAppFunction;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Ticker;
import pro.upchain.wallet.interact.CreateTransactionInteract;
import pro.upchain.wallet.interact.FetchTokensInteract;
import pro.upchain.wallet.interact.FetchWalletInteract;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.ui.activity.ConfirmationActivity;
import pro.upchain.wallet.web3.entity.Message;
import pro.upchain.wallet.web3.entity.Web3Transaction;


public class DappBrowserViewModel extends BaseViewModel  {
    private final MutableLiveData<NetworkInfo> defaultNetwork = new MutableLiveData<>();
    private final MutableLiveData<ETHWallet> defaultWallet = new MutableLiveData<>();
    private final MutableLiveData<GasSettings> gasSettings = new MutableLiveData<>();

    private final EthereumNetworkRepository ethereumNetworkRepository;

    private final FetchWalletInteract findDefaultWalletInteract;

//    private final AssetDefinitionService assetDefinitionService;
    private final CreateTransactionInteract createTransactionInteract;
    private final FetchTokensInteract fetchTokensInteract;

    private double ethToUsd = 0;
    private ArrayList<String> bookmarks;

    DappBrowserViewModel(
            EthereumNetworkRepository ethereumNetworkRepository,
            FetchWalletInteract findDefaultWalletInteract,
            CreateTransactionInteract createTransactionInteract,
            FetchTokensInteract fetchTokensInteract) {
        this.ethereumNetworkRepository = ethereumNetworkRepository;
        this.findDefaultWalletInteract = findDefaultWalletInteract;
//        this.assetDefinitionService = assetDefinitionService;
        this.createTransactionInteract = createTransactionInteract;
        this.fetchTokensInteract = fetchTokensInteract;
    }

//    public AssetDefinitionService getAssetDefinitionService() {
//        return assetDefinitionService;
//    }

    public LiveData<NetworkInfo> defaultNetwork() {
        return defaultNetwork;
    }

    public LiveData<ETHWallet> defaultWallet() {
        return defaultWallet;
    }

    public MutableLiveData<GasSettings> gasSettings() {
        return gasSettings;
    }

    public String getUSDValue(double eth)
    {
        if (defaultNetwork.getValue().chainId == 1)
        {
            return "$" + getUsdString(ethToUsd * eth);
        }
        else
        {
            return "$0.00";
        }
    }

    public static String getUsdString(double usdPrice)
    {
        DecimalFormat df = new DecimalFormat("0.00");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(usdPrice);
    }

    public String getNetworkName()
    {
        if (defaultNetwork.getValue().chainId == 1)
        {
            return "";
        }
        else
        {
            return defaultNetwork.getValue().name;
        }
    }

    public void prepare(Context context) {
        progress.postValue(true);
        loadBookmarks(context);

        defaultNetwork.postValue(ethereumNetworkRepository.getDefaultNetwork());

        disposable = findDefaultWalletInteract
                .findDefault()
                .subscribe(this::onDefaultWallet, this::onError);
    }

    private void loadBookmarks(Context context)
    {
        bookmarks = getBrowserBookmarksFromPrefs(context);
    }


    private void onDefaultWallet(ETHWallet wallet) {
        defaultWallet.setValue(wallet);
    }

    private void onTicker(Ticker ticker)
    {
        if (ticker != null && ticker.price_usd != null)
        {
            ethToUsd = Double.valueOf(ticker.price_usd);
        }
    }

    public Observable<ETHWallet> getWallet() {
            return Observable.fromCallable(() -> defaultWallet().getValue());
    }

    public void signMessage(byte[] signRequest, DAppFunction dAppFunction, Message<String> message, String pwd) {
        disposable = createTransactionInteract.sign(defaultWallet.getValue(), signRequest, pwd)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sig -> dAppFunction.DAppReturn(sig, message),
                           error -> dAppFunction.DAppError(error, message));
    }


    public void signTransaction(Web3Transaction transaction, DAppFunction dAppFunction, String url, String pwd)
    {
        Message errorMsg = new Message<>("Error executing transaction", url, 0);

        BigInteger addr = Numeric.toBigInt(transaction.recipient.toString());

        if (addr.equals(BigInteger.ZERO)) //constructor
        {
            disposable = createTransactionInteract
                    .createContract(defaultWallet.getValue(), transaction.gasPrice, transaction.gasLimit, transaction.payload, pwd)
                    .subscribe(hash -> onCreateTransaction(hash, dAppFunction, url),
                               error -> dAppFunction.DAppError(error, errorMsg));

        }
        else
        {
            byte[] data = Numeric.hexStringToByteArray(transaction.payload);
            disposable = createTransactionInteract
                    .create(defaultWallet.getValue(), transaction.recipient.toString(), transaction.value, transaction.gasPrice, transaction.gasLimit, transaction.payload, pwd)
                    .subscribe(hash -> onCreateTransaction(hash, dAppFunction, url),
                               error -> dAppFunction.DAppError(error, errorMsg));
        }
    }

    private void onCreateTransaction(String s, DAppFunction dAppFunction, String url)
    {
        //pushed transaction
        Message<String> msg = new Message<>(s, url, 0);
        dAppFunction.DAppReturn(s.getBytes(), msg);
    }

    public void openConfirmation(Context context, Web3Transaction transaction, String requesterURL)
    {
        String networkName = defaultNetwork.getValue().name;
        boolean mainNet = defaultNetwork.getValue().isMainNetwork;

        Intent intent = new Intent(context, ConfirmationActivity.class);
        intent.putExtra(C.EXTRA_WEB3TRANSACTION, transaction);
        intent.putExtra(C.EXTRA_AMOUNT, Convert.fromWei(transaction.value.toString(10), Convert.Unit.WEI).toString());
        intent.putExtra(C.TOKEN_TYPE, ConfirmationType.WEB3TRANSACTION.ordinal());
        intent.putExtra(C.EXTRA_NETWORK_NAME, networkName);
        intent.putExtra(C.EXTRA_NETWORK_MAINNET, mainNet);
        intent.putExtra(C.EXTRA_CONTRACT_NAME, requesterURL);
        context.startActivity(intent);

    }

    private ArrayList<String> getBrowserBookmarksFromPrefs(Context context) {
        ArrayList<String> storedBookmarks;
        String historyJson = PreferenceManager.getDefaultSharedPreferences(context).getString(C.DAPP_BROWSER_BOOKMARKS, "");
        if (!historyJson.isEmpty()) {
            storedBookmarks = new Gson().fromJson(historyJson, new TypeToken<ArrayList<String>>(){}.getType());
        } else {
            storedBookmarks = new ArrayList<>();
        }
        return storedBookmarks;
    }

    private void writeBookmarks(Context context, ArrayList<String> bookmarks)
    {
        String historyJson = new Gson().toJson(bookmarks);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(C.DAPP_BROWSER_BOOKMARKS, historyJson).apply();
    }

    public String getLastUrl(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(C.DAPP_LASTURL_KEY, DAPP_DEFAULT_URL);
    }

    public void setLastUrl(Context context, String url) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit().putString(C.DAPP_LASTURL_KEY, url).apply();
    }

    public ArrayList<String> getBookmarks()
    {
        return bookmarks;
    }

    public void addBookmark(Context context, String url)
    {
        //add to list
        bookmarks.add(url);
        //store
        writeBookmarks(context, bookmarks);
    }

    public void removeBookmark(Context context, String url)
    {
        if (bookmarks.contains(url)) bookmarks.remove(url);
        writeBookmarks(context, bookmarks);
    }
}
