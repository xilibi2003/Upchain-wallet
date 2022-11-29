package pro.upchain.wallet.repository;

import android.util.Log;

import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Token;
import pro.upchain.wallet.entity.TokenInfo;
import pro.upchain.wallet.service.TokenExplorerClientType;
import pro.upchain.wallet.utils.LogUtils;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.OkHttpClient;

public class TokenRepository implements TokenRepositoryType {

    private final TokenExplorerClientType tokenNetworkService;
    private final TokenLocalSource tokenLocalSource;
    private final OkHttpClient httpClient;
    private final EthereumNetworkRepository ethereumNetworkRepository;
    private Web3j web3j;

    public TokenRepository(
            OkHttpClient okHttpClient,
            EthereumNetworkRepository ethereumNetworkRepository,
            TokenExplorerClientType tokenNetworkService,
            TokenLocalSource tokenLocalSource) {

        this.httpClient = okHttpClient;
        this.ethereumNetworkRepository = ethereumNetworkRepository;
        this.tokenNetworkService = tokenNetworkService;
        this.tokenLocalSource = tokenLocalSource;
        this.ethereumNetworkRepository.addOnChangeDefaultNetwork(this::buildWeb3jClient);

        buildWeb3jClient(ethereumNetworkRepository.getDefaultNetwork());
    }

    private void buildWeb3jClient(NetworkInfo networkInfo) {
        LogUtils.d(networkInfo.rpcServerUrl + " " + networkInfo.rpcServerUrl);
        web3j = Web3j.build(new HttpService(networkInfo.rpcServerUrl, httpClient, false));
    }

    @Override
    public Observable<Token[]> fetch(String walletAddress) {
        return Observable.create(e -> {
            NetworkInfo defaultNetwork = ethereumNetworkRepository.getDefaultNetwork();

            Token[] tokens = tokenLocalSource.fetch(defaultNetwork, walletAddress)
                    .map(items -> {
                        int len = items.length;
                        Token[] result = new Token[len];
                        for (int i = 0; i < len; i++) {
                            result[i] = new Token(items[i], null);
                        }
                        return result;
                    })
                    .blockingGet();
            e.onNext(tokens);

            updateTokenInfoCache(defaultNetwork, walletAddress);
            tokens = tokenLocalSource.fetch(defaultNetwork,  walletAddress)
                        .map(items -> {
                            int len = items.length;
                            Token[] result = new Token[len];
                            for (int i = 0; i < len; i++) {
                                BigDecimal balance = null;
                                try {
                                    if (items[i].address.isEmpty()) {
                                        balance = getEthBalance(walletAddress);
                                    } else {
                                        balance = getBalance(walletAddress, items[i]);
                                    }


                                } catch (Exception e1) {
                                    Log.d("TOKEN", "Err" +  e1.getMessage());
                                    /* Quietly */
                                }

                                LogUtils.d("balance:" + balance);
                                if (balance == null || balance.compareTo(BigDecimal.ZERO) == 0) {
                                    result[i] = new Token(items[i], "0");
                                } else {
                                    BigDecimal decimalDivisor = new BigDecimal(Math.pow(10, items[i].decimals));
                                    BigDecimal ethBalance = balance.divide(decimalDivisor);
                                    if (items[i].decimals > 4) {
                                        result[i] = new Token(items[i], ethBalance.setScale(4, RoundingMode.CEILING).toPlainString());
                                    } else {
                                        result[i] = new Token(items[i], ethBalance.setScale(items[i].decimals, RoundingMode.CEILING).toPlainString());
                                    }

                                }


                            }
                            return result;
                        }).blockingGet();



            e.onNext(tokens);




        });
    }

    @Override
    public Completable addToken(String walletAddress, String address, String symbol, int decimals) {

        LogUtils.d("addToken:" + walletAddress + ", address: " + address + ", source:" + tokenLocalSource + ", ethereumNetworkRepository:" + ethereumNetworkRepository );

        return tokenLocalSource.put(
                ethereumNetworkRepository.getDefaultNetwork(),
                walletAddress,
                new TokenInfo(address, "", symbol, decimals));
    }

    private void updateTokenInfoCache(NetworkInfo defaultNetwork, String walletAddress) {
        if (!defaultNetwork.isMainNetwork) {
            return;
        }
        tokenNetworkService
                .fetch(walletAddress)
                .flatMapCompletable(items -> Completable.fromAction(() -> {
                    for (TokenInfo tokenInfo : items) {
                        try {
                            tokenLocalSource.put(
                                    ethereumNetworkRepository.getDefaultNetwork(), walletAddress, tokenInfo)
                                    .blockingAwait();
                        } catch (Throwable t) {
                            Log.d("TOKEN_REM", "Err", t);
                        }
                    }
                }))
                .blockingAwait();
    }

    private BigDecimal getEthBalance(String walletAddress) throws Exception {


        return new BigDecimal(web3j
                .ethGetBalance(walletAddress, DefaultBlockParameterName.LATEST)
                .send()
                .getBalance());
    }


    private BigDecimal getBalance(String walletAddress, TokenInfo tokenInfo) throws Exception {
        Function function = balanceOf(walletAddress);
        String responseValue = callSmartContractFunction(function, tokenInfo.address, walletAddress);

        List<Type> response = FunctionReturnDecoder.decode(
                responseValue, function.getOutputParameters());
        if (response.size() == 1) {
            return new BigDecimal(((Uint256) response.get(0)).getValue());
        } else {
            return null;
        }
    }

    private static Function balanceOf(String owner) {
        return new Function(
                "balanceOf",
                Collections.singletonList(new Address(owner)),
                Collections.singletonList(new TypeReference<Uint256>() {}));
    }

    private String callSmartContractFunction(
            Function function, String contractAddress, String walletAddress) throws Exception {
        String encodedFunction = FunctionEncoder.encode(function);

        org.web3j.protocol.core.methods.response.EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(walletAddress, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST)
                .sendAsync().get();

        return response.getValue();
    }

    public static String createTokenTransferData(String to, BigInteger tokenAmount) {
        List<Type> params = Arrays.<Type>asList(new Address(to), new Uint256(tokenAmount));

        List<TypeReference<?>> returnTypes = Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {
        });

        Function function = new Function("transfer", params, returnTypes);
        return FunctionEncoder.encode(function);
//        return Numeric.hexStringToByteArray(Numeric.cleanHexPrefix(encodedFunction));
    }
}
