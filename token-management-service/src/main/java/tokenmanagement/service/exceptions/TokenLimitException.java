package tokenmanagement.service.exceptions;

public class TokenLimitException extends Exception {
    public TokenLimitException(String msg) {
        super(msg);
    }
}
