package dk.dtu.sdws.group3.connector;

import dk.dtu.sdws.group3.models.User;
import messaging.Event;
import messaging.MessageQueue;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Connector class to handle communication with the token service
 * Author: Gustav Utke Kauman (s195396), Gustav Lintrup Kirkholt (...)
 */
public class TokenServiceConnector {

    private MessageQueue queue;
    private CompletableFuture<String> userId;

    public TokenServiceConnector(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("GetUserFromTokenResponse", this::handleGetUserFromTokenResponse);
    }

    public String getUserIdFromToken(UUID token) {
        userId = new CompletableFuture<>();
        Event event = new Event("GetUserFromTokenRequest", new Object[]{token});
        queue.publish(event);
        return userId.join();
    }

    public void handleGetUserFromTokenResponse(Event e) {
        String s = e.getArgument(0, String.class);
        userId.complete(s);
    }
}
