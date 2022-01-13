package tokenmanagement.service;

public class TokenLimitException extends Exception {
    private final String errorMsg = "Request denied - you can only request tokens when you have 1 token";

    public TokenLimitException() {
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
