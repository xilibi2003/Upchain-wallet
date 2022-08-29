package pro.upchain.wallet.entity;

public class ServiceException extends Exception {
    public final ErrorEnvelope error;

    public ServiceException(String message) {
        super(message);

        error = new ErrorEnvelope(message);
    }
}