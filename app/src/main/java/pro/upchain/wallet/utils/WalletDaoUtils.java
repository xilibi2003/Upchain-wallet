package pro.upchain.wallet.utils;

import android.text.TextUtils;

import java.util.List;

import io.objectbox.Box;
import pro.upchain.wallet.UpChainWalletApp;
import pro.upchain.wallet.domain.ETHWallet;


/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */

public class WalletDaoUtils {
    public static Box<ETHWallet> ethWalletDao = UpChainWalletApp.getsInstance().getDaoSession();

    /**
     * 插入新创建钱包
     *
     * @param ethWallet 新创建钱包
     */
    public static void insertNewWallet(ETHWallet ethWallet) {
        updateCurrent(-1);
        ethWallet.setCurrent(true);
        ethWalletDao.put(ethWallet);
    }

    /**
     * 更新选中钱包
     *
     * @param id 钱包ID
     */
    public static ETHWallet updateCurrent(long id) {
        List<ETHWallet> ethWallets = ethWalletDao.getAll();
        ETHWallet currentWallet = null;
        for (ETHWallet ethwallet : ethWallets) {
            if (id != -1 && ethwallet.getId() == id) {
                ethwallet.setCurrent(true);
                currentWallet = ethwallet;
            } else {
                ethwallet.setCurrent(false);
            }
            ethWalletDao.put(ethwallet);
        }
        return currentWallet;
    }

    /**
     * 获取当前钱包
     *
     * @return 钱包对象
     */
    public static ETHWallet getCurrent() {
        List<ETHWallet> ethWallets = ethWalletDao.getAll();
        for (ETHWallet ethwallet : ethWallets) {
            if (ethwallet.isCurrent()) {
                ethwallet.setCurrent(true);
                return ethwallet;
            }
        }
        return null;
    }

    /**
     * 查询所有钱包
     */
    public static List<ETHWallet> loadAll() {
        return ethWalletDao.getAll();
    }

    /**
     * 检查钱包名称是否存在
     *
     * @param name
     * @return
     */
    public static boolean walletNameChecking(String name) {
        List<ETHWallet> ethWallets = loadAll();
        for (ETHWallet ethWallet : ethWallets
        ) {
            if (TextUtils.equals(ethWallet.getName(), name)) {
                return true;
            }
        }
        return false;
    }

    public static ETHWallet getWalletById(long walletId) {
        return ethWalletDao.get(walletId);

    }

    /**
     * 设置isBackup为已备份
     *
     * @param walletId 钱包Id
     */
    public static void setIsBackup(long walletId) {
        ETHWallet ethWallet = ethWalletDao.get(walletId);
        ethWallet.setIsBackup(true);
        ethWalletDao.put(ethWallet);
    }

    /**
     * 以助记词检查钱包是否存在
     *
     * @param mnemonic
     * @return true if repeat
     */
    public static boolean checkRepeatByMenmonic(String mnemonic) {
        List<ETHWallet> ethWallets = loadAll();
        for (ETHWallet ethWallet : ethWallets
        ) {
            if (TextUtils.isEmpty(ethWallet.getMnemonic())) {
                LogUtils.d("wallet mnemonic empty");
                continue;
            }
            if (TextUtils.equals(ethWallet.getMnemonic().trim(), mnemonic.trim())) {
                LogUtils.d("aleady");
                return true;
            }
        }
        return false;
    }


    public static boolean isValid(String mnemonic) {
        return mnemonic.split(" ").length >= 12;
    }

    public static boolean checkRepeatByKeystore(String keystore) {
        return false;
    }

    /**
     * 修改钱包名称
     *
     * @param walletId
     * @param name
     */
    public static void updateWalletName(long walletId, String name) {
        ETHWallet wallet = ethWalletDao.get(walletId);
        wallet.setName(name);
        ethWalletDao.put(wallet);
    }

    public static void setCurrentAfterDelete() {
        List<ETHWallet> ethWallets = ethWalletDao.getAll();
        if (ethWallets != null && ethWallets.size() > 0) {
            ETHWallet ethWallet = ethWallets.get(0);
            ethWallet.setCurrent(true);
            ethWalletDao.put(ethWallet);
        }
    }
}