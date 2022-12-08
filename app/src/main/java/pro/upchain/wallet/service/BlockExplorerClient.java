package pro.upchain.wallet.service;

import android.text.TextUtils;

import pro.upchain.wallet.entity.NetworkInfo;
import pro.upchain.wallet.entity.Transaction;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import pro.upchain.wallet.utils.LogUtils;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class BlockExplorerClient implements BlockExplorerClientType {

	private final OkHttpClient httpClient;
	private final Gson gson;
	private final EthereumNetworkRepository networkRepository;

	private TransactionsApiClient transactionsApiClient;

	public BlockExplorerClient(
			OkHttpClient httpClient,
			Gson gson,
			EthereumNetworkRepository networkRepository) {
		this.httpClient = httpClient;
		this.gson = gson;
		this.networkRepository = networkRepository;
		this.networkRepository.addOnChangeDefaultNetwork(this::onNetworkChanged);
		NetworkInfo networkInfo = networkRepository.getDefaultNetwork();
		onNetworkChanged(networkInfo);
	}

	private void buildApiClient(String baseUrl) {
		transactionsApiClient = new Retrofit.Builder()
				.baseUrl(baseUrl)
				.client(httpClient)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.build()
				.create(TransactionsApiClient.class);
	}

	@Override
	public Observable<Transaction[]> fetchTransactions(String address, String tokenAddr) {
		LogUtils.d("fetchTransactions", address);
	    if (TextUtils.isEmpty(tokenAddr)) {
            return transactionsApiClient
                    .fetchTransactions(address, "account",  "txlist", "desc")
                    .lift(apiError(gson))
                    .map(r -> r.result)
                    .subscribeOn(Schedulers.io());
        } else {
            return transactionsApiClient
                    .fetchTransactions(address, tokenAddr, "account", "tokentx", "desc"  )
                    .lift(apiError(gson))
                    .map(r -> r.result)
                    .subscribeOn(Schedulers.io());
        }

	}

	private void onNetworkChanged(NetworkInfo networkInfo) {
		buildApiClient(networkInfo.backendUrl);
	}

	private static @NonNull
    <T> ApiErrorOperator<T> apiError(Gson gson) {
		return new ApiErrorOperator<>(gson);
	}


// api.etherscan.io
	// TODO: need add apiKey
	private interface TransactionsApiClient {
		@GET("/api")
        Observable<Response<EtherScanResponse>> fetchTransactions(
                @Query("address") String address,
				@Query("module") String module,
				@Query("action") String action,
				@Query("sort") String sort);

    @GET("/api")
    Observable<Response<EtherScanResponse>> fetchTransactions(
            @Query("address") String address,
			@Query("contractaddress") String contract,
			@Query("module") String module,
			@Query("action") String action,
			@Query("sort") String sort);
	}

	private final static class EtherScanResponse {
		Transaction[] result;
	}

	private final static class ApiErrorOperator <T> implements ObservableOperator<T, Response<T>> {

		private final Gson gson;

		public ApiErrorOperator(Gson gson) {
			this.gson = gson;
		}

		@Override
		public Observer<? super Response<T>> apply(Observer<? super T> observer) throws Exception {
            return new DisposableObserver<Response<T>>() {
                @Override
                public void onNext(Response<T> response) {
                    observer.onNext(response.body());
                    observer.onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            };
		}
	}
}
