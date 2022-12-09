package pro.upchain.wallet.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import pro.upchain.wallet.entity.Ticker;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.ObservableOperator;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import pro.upchain.wallet.utils.LogUtils;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static pro.upchain.wallet.C.TICKER_API_URL;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */


public class UpWalletTickerService implements TickerService {

    //
    private final OkHttpClient httpClient;
    private final Gson gson;
    private ApiClient apiClient;

    public UpWalletTickerService(
            OkHttpClient httpClient,
            Gson gson) {
        this.httpClient = httpClient;
        this.gson = gson;
        buildApiClient(TICKER_API_URL);
    }

    private void buildApiClient(String baseUrl) {
        apiClient = new Retrofit.Builder()
                .baseUrl(baseUrl + "/")
                .client(httpClient)
                .addConverterFactory(new BaseConverterFactory() {
                    @Override
                    public BaseResponseConverter responseConverter() {
                        return new TickerResponseConvert();
                    }
                })
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(ApiClient.class);
    }

    @Override
    public Observable<Ticker> fetchTickerPrice(String symbols, String currency) {
        LogUtils.d("symbols:" + symbols + ", currency:" + symbols);

        // TODO: https://docs.google.com/spreadsheets/d/1wTTuxXt8n9q7C4NDXqQpI3wpKu1_5bGVmP9Xz0XGSyU/edit#gid=0

        String ids = "ethereum";
        String vs_currencies = "usd,cny";

        return apiClient
                .fetchTickerPrice(ids, vs_currencies)
                .lift(apiError())
                .map(r -> r.response)
                .subscribeOn(Schedulers.io());
    }

    private static @NonNull
    <T> ApiErrorOperator<T> apiError() {
        return new ApiErrorOperator<>();
    }

    public interface ApiClient {
        @GET("api/v3/simple/price")
        Observable<Response<TickerResponse>> fetchTickerPrice(
                @Query("ids") String symbols, // ethereum
                @Query("vs_currencies") String currency);
    }

    private static class TickerResponse {
        Ticker response;

        public TickerResponse(Ticker ticker) {
            response = ticker;
        }
    }


    public abstract class BaseResponseConverter<T> implements Converter<ResponseBody,T> {

        @Override
        public T convert(ResponseBody value) throws IOException {
            return parserJson(value.string());
        }

        public abstract T parserJson(String json);
    }


    public abstract class BaseConverterFactory<T> extends Converter.Factory {
        @Override
        public Converter<ResponseBody, T> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return responseConverter();
        }

        public abstract BaseResponseConverter<T> responseConverter();

        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        }
    }

    public class TickerResponseConvert extends BaseResponseConverter<TickerResponse>{

        @Override
        public TickerResponse parserJson(String json) {
            LogUtils.d(json);
            Ticker ticker = new Ticker();
            try {
                JSONObject jsonRoot = new JSONObject(json);
                // TODO:
                JSONObject jsonPkg = jsonRoot.getJSONObject("ethereum");
                ticker.name ="ethereum";
                ticker.symbol = "ETH";
                ticker.price_usd = jsonPkg.getString("usd");
                ticker.price_cny = jsonPkg.getString("cny");


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return new TickerResponse(ticker);
        }
    }


    private final static class ApiErrorOperator <T> implements ObservableOperator<T, Response<T>> {

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
