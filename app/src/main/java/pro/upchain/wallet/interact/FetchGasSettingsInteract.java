package pro.upchain.wallet.interact;


import static pro.upchain.wallet.C.GAS_LIMIT_MIN;
import static pro.upchain.wallet.C.GAS_PER_BYTE;

import androidx.lifecycle.MutableLiveData;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import pro.upchain.wallet.C;
import pro.upchain.wallet.entity.ConfirmationType;
import pro.upchain.wallet.entity.GasSettings;
import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.SharedPreferenceRepository;
import pro.upchain.wallet.utils.BalanceUtils;
import pro.upchain.wallet.utils.LogUtils;

public class FetchGasSettingsInteract {


    private final EthereumNetworkRepository networkRepository;
    private final SharedPreferenceRepository repository;

    private BigInteger cachedGasPrice;

    private final MutableLiveData<BigInteger> gasPrice = new MutableLiveData<>();

    private int currentChainId;

    private Disposable gasSettingsDisposable;

    private final static long FETCH_GAS_PRICE_INTERVAL = 60;

    public FetchGasSettingsInteract(SharedPreferenceRepository repository, EthereumNetworkRepository networkRepository) {
        this.repository = repository;
        this.networkRepository = networkRepository;

        this.currentChainId = networkRepository.getDefaultNetwork().chainId;

        cachedGasPrice = new BigInteger(C.DEFAULT_GAS_PRICE);

        gasSettingsDisposable = Observable.interval(0, FETCH_GAS_PRICE_INTERVAL, TimeUnit.SECONDS)
                .doOnNext(l ->
                        fetchGasPriceByWeb3()
                ).subscribe();

    }


    public MutableLiveData<BigInteger> gasPriceUpdate()
    {
        return gasPrice;
    }

    public Single<GasSettings> fetch(ConfirmationType type) {

        return Single.fromCallable( () -> {
            BigInteger gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT);
            if (type == ConfirmationType.ETH) {
                gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_ETH);
            } else if (type == ConfirmationType.ERC20) {
                gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_TOKENS);
            }
            return new GasSettings(cachedGasPrice, gasLimit);
        });

//        return repository.getGasSettings(forTokenTransfer);
    }

    public Single<GasSettings> fetch(byte[] transactionBytes, boolean isNonFungible) {
        return getGasSettings(transactionBytes, isNonFungible);
    }

    private void fetchGasPriceByWeb3() {

        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        try {
            EthGasPrice price = web3j
                    .ethGasPrice()
                    .send();
            if (price.getGasPrice().compareTo(BalanceUtils.gweiToWei(BigDecimal.ONE)) >= 0)
            {
                cachedGasPrice = price.getGasPrice();
                LogUtils.d("FetchGasSettingsInteract", "web3 price:" +  price.getGasPrice());
                gasPrice.postValue(cachedGasPrice);
            }
            else if (networkRepository.getDefaultNetwork().chainId != currentChainId)
            {
                //didn't update the current price correctly, switch to default:
                cachedGasPrice = new BigInteger(C.DEFAULT_GAS_PRICE);
                this.currentChainId = networkRepository.getDefaultNetwork().chainId;
            }
        } catch (Exception ex) {
            // silently
        }
    }

    public Single<GasSettings> getGasSettings(byte[] transactionBytes, boolean isNonFungible) {
        return Single.fromCallable( () -> {
            BigInteger gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT);
            if (transactionBytes != null) {
                if (isNonFungible)
                {
                    gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_NONFUNGIBLE_TOKENS);
                }
                else
                {
                    gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_TOKENS);
                }
                BigInteger estimate = estimateGasLimit(transactionBytes);
                if (estimate.compareTo(gasLimit) > 0) gasLimit = estimate;
            }
            return new GasSettings(cachedGasPrice, gasLimit);
        });
    }

    public Single<GasSettings> fetchDefault(boolean tokenTransfer, NetworkInfo networkInfo) {
        return Single.fromCallable(() -> {
            BigInteger gasPrice = new BigInteger(C.DEFAULT_GAS_PRICE);
            BigInteger gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT);
            if (tokenTransfer) {
                gasLimit = new BigInteger(C.DEFAULT_GAS_LIMIT_FOR_TOKENS);
            }
            return new GasSettings(gasPrice, gasLimit);
        });
    }

    private BigInteger estimateGasLimit(byte[] data)
    {
        BigInteger roundingFactor = BigInteger.valueOf(10000);
        BigInteger txMin = BigInteger.valueOf(GAS_LIMIT_MIN);
        BigInteger bytePrice = BigInteger.valueOf(GAS_PER_BYTE);
        BigInteger dataLength = BigInteger.valueOf(data.length);
        BigInteger estimate = bytePrice.multiply(dataLength).add(txMin);
        estimate = estimate.divide(roundingFactor).add(BigInteger.ONE).multiply(roundingFactor);
        return estimate;
    }

}
