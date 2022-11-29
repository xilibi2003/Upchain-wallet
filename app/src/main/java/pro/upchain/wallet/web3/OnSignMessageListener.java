package pro.upchain.wallet.web3;

import pro.upchain.wallet.web3.entity.Message;

public interface OnSignMessageListener {
    void onSignMessage(Message<String> message);
}
