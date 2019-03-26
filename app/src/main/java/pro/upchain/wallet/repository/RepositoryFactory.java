package pro.upchain.wallet.repository;

import pro.upchain.wallet.service.BlockExplorerClient;
import pro.upchain.wallet.service.EthplorerTokenService;
import pro.upchain.wallet.service.TokenExplorerClientType;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * 微信: xlbxiong
 */

public class RepositoryFactory {

    public TokenRepository tokenRepository;
    public TransactionRepository transactionRepository;
    public EthereumNetworkRepository ethereumNetworkRepository;

    public static RepositoryFactory sSelf;

    private RepositoryFactory(SharedPreferenceRepository sp, OkHttpClient httpClient, Gson gson) {
        ethereumNetworkRepository = EthereumNetworkRepository.init(sp);

        TokenLocalSource tokenLocalSource = new RealmTokenSource();

        TokenExplorerClientType tokenExplorerClientType =  new EthplorerTokenService(httpClient, gson);
        BlockExplorerClient blockExplorerClient = new BlockExplorerClient(httpClient, gson, ethereumNetworkRepository);

        tokenRepository = new TokenRepository(httpClient, ethereumNetworkRepository, tokenExplorerClientType, tokenLocalSource);

        TransactionLocalSource inMemoryCache = new TransactionInMemorySource();

        transactionRepository = new TransactionRepository(ethereumNetworkRepository, inMemoryCache, null, blockExplorerClient);
    }

    public static RepositoryFactory init (SharedPreferenceRepository sp, OkHttpClient httpClient, Gson gson) {
        if (sSelf == null) {
            sSelf = new RepositoryFactory(sp, httpClient, gson);
        }
        return sSelf;
    }

}
