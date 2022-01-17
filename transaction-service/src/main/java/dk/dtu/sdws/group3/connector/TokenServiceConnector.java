package dk.dtu.sdws.group3.connector;

import messaging.Event;
import messaging.MessageQueue;

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
    private Map<UUID, CompletableFuture<String>> correlations = new ConcurrentHashMap<>();

    public TokenServiceConnector(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("TokenToCustomerIdResponse", this::handleGetUserFromTokenResponse);
    }

    public String getUserIdFromToken(UUID token) {
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event event = new Event("TokenToCustomerIdRequested", new Object[]{token});
        queue.publish(event);
        return correlations.get(correlationId).join();
    }

    public void handleGetUserFromTokenResponse(Event e) {
        String s = e.getArgument(0, String.class);
        UUID correlationId = e.getCorrelationId();
        correlations.get(correlationId).complete(s);
    }
}
