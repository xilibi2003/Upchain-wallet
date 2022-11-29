package pro.upchain.wallet.interact;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Bytes;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pro.upchain.wallet.domain.ETHWallet;
import pro.upchain.wallet.entity.TransactionData;
import pro.upchain.wallet.repository.EthereumNetworkRepository;
import pro.upchain.wallet.repository.TokenRepository;
import pro.upchain.wallet.utils.LogUtils;

/**
 * Created by Tiny 熊 @ Upchain.pro
 * WeiXin: xlbxiong
 */
public class CreateTransactionInteract {


    private final EthereumNetworkRepository networkRepository;


    public CreateTransactionInteract(
            EthereumNetworkRepository networkRepository) {
        this.networkRepository = networkRepository;

    }

    public Single<byte[]> sign(ETHWallet wallet, byte[] message, String pwd) {
        return getSignature(wallet, message, pwd);
    }

    // transfer ether
    public Single<String>  createEthTransaction(ETHWallet from,  String to, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, String password) {
        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> Single.fromCallable( () -> {

            Credentials credentials = WalletUtils.loadCredentials(password,  from.getKeystorePath());
            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, amount);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

            return ethSendTransaction.getTransactionHash();

        } ).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    // transfer ERC20
    public Single<String>  createERC20Transfer(ETHWallet from,  String to, String contractAddress, BigInteger amount, BigInteger gasPrice, BigInteger gasLimit, String password) {
        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        String callFuncData = TokenRepository.createTokenTransferData(to, amount);


        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> Single.fromCallable( () -> {

            Credentials credentials = WalletUtils.loadCredentials(password,  from.getKeystorePath());
            RawTransaction rawTransaction = RawTransaction.createTransaction(
                    nonce, gasPrice, gasLimit, contractAddress, callFuncData);

            LogUtils.d("rawTransaction:" + rawTransaction);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).send();

            return ethSendTransaction.getTransactionHash();

        } ).subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread()));
    }

    public Single<String> create(ETHWallet from, String to, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit,  String data, String pwd)
    {
        return createTransaction(from, to, subunitAmount, gasPrice, gasLimit, data, pwd)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<String> createContract(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String pwd) {
        return createTransaction(from, gasPrice, gasLimit, data, pwd)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<TransactionData> createWithSig(ETHWallet from, String to, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, String data, String pwd)
    {
        return createTransactionWithSig(from, to, subunitAmount, gasPrice, gasLimit, data, pwd)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<TransactionData> createWithSig(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String pwd)
    {
        return createTransactionWithSig(from, gasPrice, gasLimit, data, pwd)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<TransactionData> createTransactionWithSig(ETHWallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {

        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        TransactionData txData = new TransactionData();

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit,toAddress, subunitAmount,  data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    txData.signature = Numeric.toHexString(signedMessage);
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    txData.txHash = raw.getTransactionHash();
                    return txData;
                })).subscribeOn(Schedulers.io());
    }


    public Single<TransactionData> createTransactionWithSig(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {

        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        TransactionData txData = new TransactionData();

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit, BigInteger.ZERO, data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    txData.signature = Numeric.toHexString(signedMessage);
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    txData.txHash = raw.getTransactionHash();
                    return txData;
                })).subscribeOn(Schedulers.io());
    }

    // https://github.com/web3j/web3j/issues/208
    // https://ethereum.stackexchange.com/questions/17708/solidity-ecrecover-and-web3j-sign-signmessage-are-not-compatible-is-it

    // message : TransactionEncoder.encode(rtx)   // may wrong

    public Single<byte[]> getSignature(ETHWallet wallet, byte[] message, String password) {
        return  Single.fromCallable(() -> {
            Credentials credentials = WalletUtils.loadCredentials(password, wallet.getKeystorePath());
            Sign.SignatureData signatureData = Sign.signMessage(
                    message, credentials.getEcKeyPair());

            List<RlpType> result = new ArrayList<>();
            result.add(RlpString.create(message));

            if (signatureData != null) {
                result.add(RlpString.create(signatureData.getV()));
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getR())));
                result.add(RlpString.create(Bytes.trimLeadingZeroes(signatureData.getS())));
            }

            RlpList rlpList = new RlpList(result);
            return RlpEncoder.encode(rlpList);
        });
    }

    public Single<String> createTransaction(ETHWallet from, String toAddress, BigInteger subunitAmount, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {
        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit,toAddress, subunitAmount,  data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    return raw.getTransactionHash();
                })).subscribeOn(Schedulers.io());
    }


    // for DApp create contract transaction
    public Single<String> createTransaction(ETHWallet from, BigInteger gasPrice, BigInteger gasLimit, String data, String password) {

        final Web3j web3j = Web3j.build(new HttpService(networkRepository.getDefaultNetwork().rpcServerUrl));

        return networkRepository.getLastTransactionNonce(web3j, from.address)
                .flatMap(nonce -> getRawTransaction(nonce, gasPrice, gasLimit, BigInteger.ZERO, data))
                .flatMap(rawTx -> signEncodeRawTransaction(rawTx, password, from, networkRepository.getDefaultNetwork().chainId))
                .flatMap(signedMessage -> Single.fromCallable( () -> {
                    EthSendTransaction raw = web3j
                            .ethSendRawTransaction(Numeric.toHexString(signedMessage))
                            .send();
                    if (raw.hasError()) {
                        throw new Exception(raw.getError().getMessage());
                    }
                    return raw.getTransactionHash();
                })).subscribeOn(Schedulers.io());

    };


    // for DApp  create contract  transaction
    private Single<RawTransaction> getRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, BigInteger value, String data)
    {
        return Single.fromCallable(() ->
                RawTransaction.createContractTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        value,
                        data));
    }

    private Single<RawTransaction> getRawTransaction(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to , BigInteger value, String data)
    {
        return Single.fromCallable(() ->
                RawTransaction.createTransaction(
                        nonce,
                        gasPrice,
                        gasLimit,
                        to,
                        value,
                        data));
    }

    private  Single<byte[]> signEncodeRawTransaction(RawTransaction rtx, String password, ETHWallet wallet, int chainId) {

        return Single.fromCallable(() -> {
            Credentials credentials = WalletUtils.loadCredentials(password, wallet.getKeystorePath());
            byte[] signedMessage = TransactionEncoder.signMessage(rtx, credentials);
            return signedMessage;
        });
    }



}
