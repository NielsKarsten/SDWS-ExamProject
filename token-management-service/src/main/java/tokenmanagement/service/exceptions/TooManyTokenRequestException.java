package tokenmanagement.service.exceptions;

public class TooManyTokenRequestException extends Exception {
    public TooManyTokenRequestException(String msg) {
        super(msg);
    }
}
