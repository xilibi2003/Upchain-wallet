package pro.upchain.wallet.entity;

import android.util.Base64;

import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Arrays;


public class CryptoFunctions
{
    public byte[] Base64Decode(String message)
    {
        return Base64.decode(message, Base64.URL_SAFE);
    }

    public byte[] Base64Encode(byte[] data)
    {
        return Base64.encode(data, Base64.URL_SAFE | Base64.NO_WRAP);
    }

    public BigInteger signedMessageToKey(byte[] data, byte[] signature) throws SignatureException
    {
        Sign.SignatureData sigData = sigFromByteArray(signature);
        return Sign.signedMessageToKey(data, sigData);
    }

    public String getAddressFromKey(BigInteger recoveredKey)
    {
        return Keys.getAddress(recoveredKey);
    }

    public static Sign.SignatureData sigFromByteArray(byte[] sig)
    {
        byte   subv = sig[64];
        if (subv < 27) subv += 27;

        byte[] subrRev = Arrays.copyOfRange(sig, 0, 32);
        byte[] subsRev = Arrays.copyOfRange(sig, 32, 64);

        BigInteger r = new BigInteger(1, subrRev);
        BigInteger s = new BigInteger(1, subsRev);

        return new Sign.SignatureData(subv, subrRev, subsRev);
    }
}

