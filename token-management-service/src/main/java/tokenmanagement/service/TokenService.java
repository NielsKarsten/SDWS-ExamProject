// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

// Handle events fra transaction. Sende response efter consume.

package tokenmanagement.service;

import messaging.Event;
import messaging.MessageQueue;
import token.service.manager.ActiveTokenManager;
import token.service.manager.ArchivedTokenManager;
import token.service.manager.TokenManager;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

public class TokenService {
    private MessageQueue queue;
    private ArchivedTokenManager archivedTokens;
    private ActiveTokenManager activeTokens;

    public TokenService(MessageQueue q) {
        queue = q;
        this.activeTokens = new ActiveTokenManager();
        this.archivedTokens = new ArchivedTokenManager();

        queue.addHandler("TokensRequested", this::handleTokensRequested);
        queue.addHandler("TokenToCustomerIdRequested", this::handleTokenToCustomerIdRequested);
    }

    private void publishNewEvent(Event e, String topic, Object object) {
        UUID correlationId = e.getCorrelationId();
        Event event = new Event(correlationId, topic, new Object[] { object });
        queue.publish(event);
    }

    public void handleTokensRequested(Event e) {
        TokenRequest request = e.getArgument(0, TokenRequest.class);

        List<UUID> tokenList = null;
        try {
            tokenList = requestTokens(request);
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        }
        publishNewEvent(e, "TokensIssued", tokenList);
    }

    public List<UUID> requestTokens(TokenRequest tokenRequest) throws IllegalArgumentException {
        UUID userId = tokenRequest.getUserId();
        int tokenAmount = tokenRequest.getAmount();
        verifyRequestTokenInput(userId, tokenAmount);
        return activeTokens.generateTokens(userId, tokenAmount);
    }

    private void verifyRequestTokenInput(UUID userId, int tokenAmount) throws IllegalArgumentException {
        if (tokenAmount > 5 || tokenAmount < 1) {
            throw new IllegalArgumentException(
                    "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time");
        }

        List<UUID> customerTokens = activeTokens.getUserTokens(userId);
        if (customerTokens != null && customerTokens.size() > 1) {
            throw new IllegalArgumentException(
                    "Error: You can only request tokens when you have less than 2 active tokens");
        }
    }

    public void handleTokenToCustomerIdRequested(Event e) {
        UUID correlationID = e.getCorrelationId();
        UUID token = e.getArgument(0, UUID.class);

        UUID customerId = null;
        try {
            customerId = activeTokens.getTokenOwner(token);
            activeTokens.removeToken(customerId, token);
            archivedTokens.addToken(customerId, token);
        } catch (NullPointerException tokenException) {
            tokenException.printStackTrace();
        }
        Event event = new Event(correlationID, "TokenToCustomerIdResponse", new Object[] { customerId });
        queue.publish(event);
    }
}
