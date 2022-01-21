// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

// Handle events fra transaction. Sende response efter consume.

package tokenmanagement.service;

import handling.AccountEventType;
import handling.GenericHandler;
import handling.TokenEventType;
import messaging.Event;
import messaging.MessageQueue;
import token.service.manager.ActiveTokenManager;
import token.service.manager.ArchivedTokenManager;
import java.util.*;

public class TokenService extends GenericHandler implements AccountEventType, TokenEventType{
    private ArchivedTokenManager archivedTokens;
    private ActiveTokenManager activeTokens;

    public TokenService(MessageQueue q) {
    	super(q);
        this.activeTokens = new ActiveTokenManager();
        this.archivedTokens = new ArchivedTokenManager();

        addHandler(TOKENS_REQUESTED, this::handleTokensRequested);
        addHandler(TOKEN_TO_CUSTOMER_ID_REQUESTED, this::handleTokenToCustomerIdRequested);
        addHandler(RETIRE_USER_ACCOUNT_TOKENS_REQUEST, this::handleCustomerAccountClosed);
    }

    public void handleTokensRequested(Event e) {
        TokenRequest request = e.getArgument(0, TokenRequest.class);
        tryRequestTokens(e, request);
    }

    public List<UUID> requestTokens(TokenRequest tokenRequest) {
        UUID userId = tokenRequest.getUserId();
        int tokenAmount = tokenRequest.getAmount();
        return activeTokens.generateTokens(userId, tokenAmount);
    }

    private void tryRequestTokens(Event e, TokenRequest request) {
        System.out.println("Invoking tryRequestTokens");

        UUID userId = request.getUserId();
        int tokenAmount = request.getAmount();
        List<UUID> customerTokens;
        try {
            customerTokens = activeTokens.getUserTokens(userId);
        } catch (Exception ex) {
            customerTokens = null;
        }

        if (tokenAmount > 5 || tokenAmount < 1) {
            publishNewEvent(e, TOKEN_REQUEST_INVALID, new IllegalArgumentException(
                    "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time"));
        } else if (customerTokens != null && customerTokens.size() > 1) {
            publishNewEvent(e, TOKEN_REQUEST_INVALID, new IllegalArgumentException(
                    "Error: You can only request tokens when you have less than 2 active tokens"));
        } else {
            List<UUID> tokenList = requestTokens(request);
            publishNewEvent(e, TOKENS_ISSUED, tokenList);
        }
    }

    public void handleTokenToCustomerIdRequested(Event e) {
        UUID token = e.getArgument(0, UUID.class);
        UUID customerId = null;

        try {
            customerId = activeTokens.getTokenOwner(token);
            activeTokens.removeToken(customerId, token);
            archivedTokens.addToken(customerId, token);
            publishNewEvent(e, TOKEN_TO_CUSTOMER_ID_RESPONSE, customerId);
        } catch (NullPointerException tokenException) {
            publishNewEvent(e, TOKEN_TO_CUSTOMER_ID_RESPONSE_INVALID, "Invalid token");
        }
    }

    public void handleCustomerAccountClosed(Event e) {
        UUID userId = e.getArgument(0, UUID.class);
        try {
            List<UUID> userActiveTokens = activeTokens.getUserTokens(userId);
            activeTokens.removeTokens(userId, userActiveTokens);
            archivedTokens.addTokens(userId, userActiveTokens);
            this.publishNewEvent(e, CLOSED_USER_ACCOUNT_TOKENS_RETIRED, true);
        } catch (Exception ex) {
            this.publishNewEvent(e, ACCOUNT_CLOSED_RETIRE_TOKEN_REQUEST_INVALID, ex);
        }

    }
}
