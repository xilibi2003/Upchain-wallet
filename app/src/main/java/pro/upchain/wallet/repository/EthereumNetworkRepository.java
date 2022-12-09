package pro.upchain.wallet.repository;

import android.text.TextUtils;
import android.util.Log;


import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import pro.upchain.wallet.entity.NetworkInfo;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static pro.upchain.wallet.C.CLASSIC_NETWORK_NAME;
import static pro.upchain.wallet.C.ETC_SYMBOL;
import static pro.upchain.wallet.C.ETHEREUM_MAIN_NETWORK_NAME;
import static pro.upchain.wallet.C.ETH_SYMBOL;
import static pro.upchain.wallet.C.KOVAN_NETWORK_NAME;
import static pro.upchain.wallet.C.LOCAL_DEV_NETWORK_NAME;
import static pro.upchain.wallet.C.POA_NETWORK_NAME;
import static pro.upchain.wallet.C.POA_SYMBOL;
import static pro.upchain.wallet.C.ROPSTEN_NETWORK_NAME;
import static pro.upchain.wallet.C.GOERLI_NETWORK_NAME;
import static pro.upchain.wallet.BuildConfig.InfuraKey;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class EthereumNetworkRepository {

    public static EthereumNetworkRepository sSelf;

    private final NetworkInfo[] NETWORKS = new NetworkInfo[]{

            new NetworkInfo("Binance Smart Chain testnet", "Binance test Chain",
                    "https://bsc-testnet.public.blastapi.io",
                    "https://api-testnet.bscscan.com/",
                    "https://testnet.bscscan.com", 0x61,  false),

            new NetworkInfo("Binance Smart Chain", "Binance Smart Chain",
                    "https://bsc-dataseed1.binance.org/",
                    "https://api.bscscan.com/",
                    "https://bscscan.com", 0x38, true),

            new NetworkInfo(ETHEREUM_MAIN_NETWORK_NAME, ETH_SYMBOL,
                    "https://mainnet.infura.io/v3/" + InfuraKey,
                    "https://api.etherscan.io/",
                    "https://etherscan.io/", 1, true),

            new NetworkInfo(GOERLI_NETWORK_NAME, ETH_SYMBOL,
                    "https://goerli.infura.io/v3/" + InfuraKey,
                    "https://api-goerli.etherscan.io/",
                    "https://goerli.etherscan.io/", 5, true),

            new NetworkInfo(CLASSIC_NETWORK_NAME, ETC_SYMBOL,
                    "https://mewapi.epool.io/",
                    "https://classic.trustwalletapp.com",
                    "https://gastracker.io", 61, false),
            new NetworkInfo(POA_NETWORK_NAME, POA_SYMBOL,
                    "https://core.poa.network",
                    "https://poa.trustwalletapp.com", "poa", 99, false),
            new NetworkInfo(KOVAN_NETWORK_NAME, ETH_SYMBOL,
                    "https://kovan.infura.io/llyrtzQ3YhkdESt2Fzrk",
                    "http://192.168.8.103:8001/",
                    "https://kovan.etherscan.io", 42, false),

            new NetworkInfo(ROPSTEN_NETWORK_NAME, ETH_SYMBOL,
                    "https://ropsten.infura.io/llyrtzQ3YhkdESt2Fzrk",
                    "http://192.168.8.103:8000/",
                    "https://ropsten.etherscan.io", 3, false),

            new NetworkInfo(LOCAL_DEV_NETWORK_NAME, ETH_SYMBOL,
                    "http://192.168.1.10:8545",
                    "http://192.168.1.10:8000/",
                    "", 31337, false),
    };

    private final SharedPreferenceRepository preferences;
    private NetworkInfo defaultNetwork;
    private final Set<OnNetworkChangeListener> onNetworkChangedListeners = new HashSet<>();


    public static EthereumNetworkRepository init(SharedPreferenceRepository sp) {
        if (sSelf == null) {
            sSelf = new EthereumNetworkRepository(sp);
        }
        return sSelf;
    }

    private EthereumNetworkRepository(SharedPreferenceRepository preferenceRepository) {
        this.preferences = preferenceRepository;
        defaultNetwork = getByName(preferences.getDefaultNetwork());
        if (defaultNetwork == null) {
            defaultNetwork = NETWORKS[0];
        }
    }

    private NetworkInfo getByName(String name) {
        if (!TextUtils.isEmpty(name)) {
            for (NetworkInfo NETWORK : NETWORKS) {
                if (name.equals(NETWORK.name)) {
                    return NETWORK;
                }
            }
        }
        return null;
    }

    public String getCurrency() {
        int currencyUnit = preferences.getCurrencyUnit();
        if (currencyUnit == 0) {
            return "CNY";
        } else {
            return "USD";
        }
    }

    public NetworkInfo getDefaultNetwork() {
        return defaultNetwork;
    }

    public void setDefaultNetworkInfo(NetworkInfo networkInfo) {
        defaultNetwork = networkInfo;
        preferences.setDefaultNetwork(defaultNetwork.name);

        for (OnNetworkChangeListener listener : onNetworkChangedListeners) {
            listener.onNetworkChanged(networkInfo);
        }
    }

    public NetworkInfo[] getAvailableNetworkList() {
        return NETWORKS;
    }

    public void addOnChangeDefaultNetwork(OnNetworkChangeListener onNetworkChanged) {
        onNetworkChangedListeners.add(onNetworkChanged);
    }

    public Single<NetworkInfo> find() {
        return Single.just(getDefaultNetwork())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<BigInteger> getLastTransactionNonce(Web3j web3j, String walletAddress) {
        Log.i("EthereumNetworkRepository", "getLastTransactionNonce，walletAddress: " + walletAddress);

        return Single.fromCallable(() -> {
            EthGetTransactionCount ethGetTransactionCount = web3j
                    .ethGetTransactionCount(walletAddress, DefaultBlockParameterName.LATEST)   // or DefaultBlockParameterName.LATEST
                    .send();
            return ethGetTransactionCount.getTransactionCount();
        });
    }

}
