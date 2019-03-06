package pro.upchain.wallet.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDKeyDerivation;
import org.bitcoinj.wallet.DeterministicSeed;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 以太坊助记词
 * 用到了比特币的jar包 org.bitcoinj
 */
public class ETHMnemonic {
    /**
     * 通用的以太坊基于bip44协议的助记词路径 （imtoken jaxx Metamask myetherwallet）
     */
    private static String ETH_TYPE = "m/44'/60'/0'/0/0";

    private static SecureRandom secureRandom = new SecureRandom();

    public static void main(String[] args) {
        //生成助记词
        generateMnemonic(ETH_TYPE, "11111111");


        //导入助记词
        //[team, bid, property, oval, hedgehog, observe, badge, cabin, color, cruel, casino, blame]
        List<String> list = new ArrayList<>();
        list.add("team");
        list.add("bid");
        list.add("property");
        list.add("oval");
        list.add("hedgehog");
        list.add("observe");
        list.add("badge");
        list.add("cabin");
        list.add("color");
        list.add("cruel");
        list.add("casino");
        list.add("blame");
//		importMnemonic(ETH_TYPE, list, "11111111");
    }

    public static EthHDWallet generateMnemonic(String path, String password) {
        if (!path.startsWith("m") && !path.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = path.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }

        if (password.length() < 8) {
            //密码过短
            return null;
        }

        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(secureRandom, 128, passphrase, creationTimeSeconds);
        return createEthWallet(ds, pathArray, password);
    }

    private static EthHDWallet importMnemonic(String path, List<String> list, String password) {
        if (!path.startsWith("m") && !path.startsWith("M")) {
            //参数非法
            return null;
        }
        String[] pathArray = path.split("/");
        if (pathArray.length <= 1) {
            //内容不对
            return null;
        }
        if (password.length() < 8) {
            //密码过短
            return null;
        }
        String passphrase = "";
        long creationTimeSeconds = System.currentTimeMillis() / 1000;
        DeterministicSeed ds = new DeterministicSeed(list, null, passphrase, creationTimeSeconds);

        return createEthWallet(ds, pathArray, password);
    }

    private static EthHDWallet createEthWallet(DeterministicSeed ds, String[] pathArray, String password) {
        //种子
        byte[] seedBytes = ds.getSeedBytes();
        System.out.println(Arrays.toString(seedBytes));
        //助记词
        List<String> mnemonic = ds.getMnemonicCode();
        System.out.println(Arrays.toString(mnemonic.toArray()));

        if (seedBytes == null)
            return null;
        DeterministicKey dkKey = HDKeyDerivation.createMasterPrivateKey(seedBytes);
        for (int i = 1; i < pathArray.length; i++) {
            ChildNumber childNumber;
            if (pathArray[i].endsWith("'")) {
                int number = Integer.parseInt(pathArray[i].substring(0,
                        pathArray[i].length() - 1));
                childNumber = new ChildNumber(number, true);
            } else {
                int number = Integer.parseInt(pathArray[i]);
                childNumber = new ChildNumber(number, false);
            }
            dkKey = HDKeyDerivation.deriveChildKey(dkKey, childNumber);
        }
        System.out.println("path " + dkKey.getPathAsString());

        ECKeyPair keyPair = ECKeyPair.create(dkKey.getPrivKeyBytes());
        System.out.println("privateKey " + keyPair.getPrivateKey().toString(16));
        System.out.println("publicKey " + keyPair.getPublicKey().toString(16));

        EthHDWallet ethHDWallet = null;
        try {
            WalletFile walletFile = Wallet.createLight(password, keyPair);
            System.out.println("address " + "0x" + walletFile.getAddress());
            ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
            String jsonStr = objectMapper.writeValueAsString(walletFile);
            System.out.println("keystore " + jsonStr);

            ethHDWallet = new EthHDWallet(keyPair.getPrivateKey().toString(16),
                    keyPair.getPublicKey().toString(16),
                    mnemonic, dkKey.getPathAsString(),
                    "0x" + walletFile.getAddress(), jsonStr);
        } catch (CipherException | JsonProcessingException e) {
            e.printStackTrace();
        }

        return ethHDWallet;
    }

    public static class EthHDWallet {
        String privateKey;
        String publicKey;
        List<String> mnemonic;
        String mnemonicPath;
        String Address;
        String keystore;

        public EthHDWallet(String privateKey, String publicKey, List<String> mnemonic, String mnemonicPath, String address, String keystore) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
            this.mnemonic = mnemonic;
            this.mnemonicPath = mnemonicPath;
            Address = address;
            this.keystore = keystore;
        }
    }

}
