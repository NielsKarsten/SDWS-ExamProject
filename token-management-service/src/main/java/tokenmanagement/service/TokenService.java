// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

// Handle events fra transaction. Sende response efter consume.

package tokenmanagement.service;

import com.google.gson.Gson;
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
    	List<UUID> customerTokens = activeTokens.getUserTokens(userId);
    	
        if (tokenAmount > 5 || tokenAmount < 1) 
        {
        	publishNewEvent(e, "TokenRequestInvalid", "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time");
        }
        else if (customerTokens != null && customerTokens.size() > 1) 
        {
        	publishNewEvent(e, "TokenRequestInvalid", "Error: You can only request tokens when you have less than 2 active tokens");
        }
        else 
        {
        	List<UUID> tokenList = requestTokens(request);
            publishNewEvent(e, "TokensIssued", tokenList);    
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
