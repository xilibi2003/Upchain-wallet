package pro.upchain.wallet.entity;


import pro.upchain.wallet.web3.entity.Message;

public interface DAppFunction
{
    void DAppError(Throwable error, Message<String> message);
    void DAppReturn(byte[] data, Message<String> message);
}
