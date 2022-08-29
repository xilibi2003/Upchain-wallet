package pro.upchain.wallet;


import androidx.multidex.MultiDexApplication;

import com.google.gson.Gson;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.domain.MyObjectBox;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.repository.SharedPreferenceRepository;
import pro.upchain.wallet.utils.AppFilePath;
import pro.upchain.wallet.utils.LogInterceptor;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class UpChainWalletApp extends MultiDexApplication {

    private static UpChainWalletApp sInstance;

    private static OkHttpClient httpClient;
    public static RepositoryFactory repositoryFactory;
    public static SharedPreferenceRepository sp;

    public Box<ETHWallet> getDaoSession() {
        BoxStore boxStore = MyObjectBox.builder()
                .name("wallet")
                .androidContext(getApplicationContext())
                .build();
        return boxStore.boxFor(ETHWallet.class);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        init();

        Realm.init(this);

        AppFilePath.init(this);

    }


    public static UpChainWalletApp getsInstance() {
        return sInstance;
    }

    protected void init() {

        sp = SharedPreferenceRepository.init(getApplicationContext());

        httpClient = new OkHttpClient.Builder()
                .addInterceptor(new LogInterceptor())
                .build();

        Gson gson = new Gson();

        repositoryFactory = RepositoryFactory.init(sp, httpClient, gson);

//        //创建数据库表
//        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "wallet", null);
//        SQLiteDatabase db = mHelper.getWritableDatabase();
//        daoSession = new DaoMaster(db).newSession();


    }


    public static OkHttpClient okHttpClient() {
        return httpClient;
    }

    public static RepositoryFactory repositoryFactory() {
        return repositoryFactory;
    }


}