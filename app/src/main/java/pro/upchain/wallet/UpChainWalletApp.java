package pro.upchain.wallet;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import pro.upchain.wallet.repository.RepositoryFactory;
import pro.upchain.wallet.repository.SharedPreferenceRepository;
import pro.upchain.wallet.utils.AppFilePath;
import pro.upchain.wallet.utils.LogInterceptor;
import wallet.domain.DaoMaster;
import wallet.domain.DaoSession;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class UpChainWalletApp extends MultiDexApplication {

    private static UpChainWalletApp sInstance;

    private RefWatcher refWatcher;

    private DaoSession daoSession;
    private static OkHttpClient httpClient;
    public static RepositoryFactory repositoryFactory;
    public static SharedPreferenceRepository sp;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    public static RefWatcher getRefWatcher(Context context) {
        UpChainWalletApp application = (UpChainWalletApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("TrustWalletCore");
        // 主要是添加下面这句代码
        MultiDex.install(this);

        sInstance = this;
        init();

        Realm.init(this);

        refWatcher = LeakCanary.install(this);

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


        //创建数据库表
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(this, "wallet", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        daoSession = new DaoMaster(db).newSession();


    }


    public static OkHttpClient okHttpClient() {
        return httpClient;
    }

    public static RepositoryFactory repositoryFactory() {
        return repositoryFactory;
    }


}
