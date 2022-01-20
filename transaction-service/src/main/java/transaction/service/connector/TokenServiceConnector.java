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
 * Author: Gustav Utke Kauman (s195396), Gustav Lintrup Kirkholt, Niels Bisgaard-Bohr (S202745)
 */
public class TokenServiceConnector {

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<UUID>> correlations = new ConcurrentHashMap<>();

    public TokenServiceConnector(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("TokenToCustomerIdResponse", this::handleGetUserFromTokenResponse);
        this.queue.addHandler("TokenToCustomerIdResponseInvalid", this::handleGetUserIdFromTokenError);
    }

    public UUID getUserIdFromToken(UUID token) throws Exception {
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event event = new Event(correlationId, "TokenToCustomerIdRequested", new Object[] { token });
        queue.publish(event);
        return correlations.get(correlationId).join();
    }
    
    public void handleGetUserIdFromTokenError(Event e) {
        UUID correlationId = e.getCorrelationId();
        String errorMessage = e.getArgument(0, String.class);
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
    	correlations.get(correlationId).completeExceptionally(exception);
    }

    public void handleGetUserFromTokenResponse(Event e) {
        UUID userId = e.getArgument(0, UUID.class);
        UUID correlationId = e.getCorrelationId();
        correlations.get(correlationId).complete(userId);
    }
}
