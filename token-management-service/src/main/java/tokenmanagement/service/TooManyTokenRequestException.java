package tokenmanagement.service;

public class TooManyTokenRequestException extends Exception {
    public TooManyTokenRequestException(String msg) {
        super(msg);
    }
}
