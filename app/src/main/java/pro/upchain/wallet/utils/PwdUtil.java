package pro.upchain.wallet.utils;


public class PwdUtil {
    /**
     * 校验密码强度
     *
     * @param pwd 密码
     * @return 0为弱 1为一般 2为强 3 为很好
     */
    public static int checkPwd(String pwd) {

        String regexZ = "\\d*";
        String regexS = "[a-zA-Z]+";
        String regexT = "\\W+$";
        String regexZT = "\\D*";
        String regexST = "[\\d\\W]*";
        String regexZS = "\\w*";
        String regexZST = "[\\w\\W]*";

        if (pwd.matches(regexZ)) {
            if (pwd.length() > 6) {

            } else {
                return 0;
            }
        }
        if (pwd.matches(regexS)) {
            return 0;
        }
        if (pwd.matches(regexT)) {
            return 0;
        }
        if (pwd.matches(regexZT)) {
            return 1;
        }
        if (pwd.matches(regexST)) {
            return 1;
        }
        if (pwd.matches(regexZS)) {
            return 1;
        }
        if (pwd.matches(regexZST)) {
            return 3;
        }
        return 0;
    }
}
