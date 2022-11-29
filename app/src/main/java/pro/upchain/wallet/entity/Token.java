package pro.upchain.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;

public class Token implements Parcelable {
    public final TokenInfo tokenInfo;
    public final String balance;
    public String value;

    public Token(TokenInfo tokenInfo, String balance) {
        this.tokenInfo = tokenInfo;
        this.balance = balance;
    }

    private Token(Parcel in) {
        tokenInfo = in.readParcelable(TokenInfo.class.getClassLoader());
        balance = in.readString();
        value = in.readString();
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(tokenInfo, flags);
        dest.writeString(balance);
        dest.writeString(value);
    }
}
