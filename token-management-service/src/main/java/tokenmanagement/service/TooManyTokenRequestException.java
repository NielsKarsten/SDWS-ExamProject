package tokenmanagement.service;

public class TooManyTokenRequestException extends Throwable {
    private final String errorMsg = "Request denied - you can only request between one and 5 tokens at a time";


    public TooManyTokenRequestException() {
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
