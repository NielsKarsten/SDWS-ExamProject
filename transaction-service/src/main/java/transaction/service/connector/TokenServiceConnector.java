package transaction.service.connector;

import messaging.Event;
import messaging.MessageQueue;
import transaction.service.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Connector class to handle communication with the token service
 * Author: Gustav Utke Kauman (s195396), Gustav Lintrup Kirkholt (...)
 */
public class TokenServiceConnector {

    private MessageQueue queue;
    private HashMap<UUID, CompletableFuture<UUID>> correlations = new HashMap<>();

    public TokenServiceConnector(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("TokenToCustomerIdResponse", this::handleGetUserFromTokenResponse);
    }

    public UUID getUserIdFromToken(UUID token) {
    	System.out.println("Gettting userId from token: "+ token.toString());
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(correlationId, "TokenToCustomerIdRequested", new Object[] { token });
        queue.publish(event);
        return correlations.get(correlationId).join();
    }

    public void handleGetUserFromTokenResponse(Event e) {
    	System.out.println("Receiving user ID from token: ");
        UUID userId = e.getArgument(0, UUID.class);
        System.out.println("User id: " + userId);
        UUID correlationId = e.getCorrelationId();
        System.out.println("Correlation ID for this userId: " + correlationId.toString());
        correlations.get(correlationId).complete(userId);
        System.out.println("Has returned UUID to completableFuture");
    }
}
