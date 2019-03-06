package pro.upchain.wallet.domain;


public class VerifyMnemonicWordTag {
    private String mnemonicWord;
    private boolean isSelected;

    public String getMnemonicWord() {
        return mnemonicWord;
    }

    public void setMnemonicWord(String mnemonicWord) {
        this.mnemonicWord = mnemonicWord;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}
