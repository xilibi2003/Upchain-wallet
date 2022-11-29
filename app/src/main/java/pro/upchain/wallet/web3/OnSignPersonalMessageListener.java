package pro.upchain.wallet.web3;


import pro.upchain.wallet.web3.entity.Message;

public interface OnSignPersonalMessageListener {
    void onSignPersonalMessage(Message<String> message);
}
