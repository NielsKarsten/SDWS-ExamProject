package transaction.service.connector;

import messaging.MessageQueue;

import java.util.UUID;
import handling.AccountEventType;
import handling.GenericHandler;
import handling.TokenEventType;
import handling.TransactionEventType;

public class AccountServiceConnector extends GenericHandler implements  AccountEventType, TokenEventType, TransactionEventType{

    public AccountServiceConnector(MessageQueue q) {
    	super(q);
        addHandler("UserAccountInfoResponse", this::genericHandler);
        addHandler("VerifyUserAccountExistsResponse", this::genericHandler);
        addHandler("UserAccountInvalid", this::genericHandler);
    }

    public boolean userExists(UUID userId) throws NullPointerException {
        if (userId == null)
            throw new NullPointerException("User Id was not specified");
        return (boolean) buildCompletableFutureEvent(userId, VERIFY_USER_ACCOUNT_EXISTS_REQUESTS);
    }

    public String getUserBankAccountFromId(UUID userId) throws NullPointerException {
        if (userId == null)
            throw new NullPointerException("User Id was not specified");
        return (String) buildCompletableFutureEvent(userId, USER_ACCOUNT_INFO_REQUESTED);
    }
}
