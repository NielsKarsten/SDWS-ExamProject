package tokenmanagement.service;

public class TokenLimitException extends Exception {
    public TokenLimitException(String msg) {
        super(msg);
    }
}
