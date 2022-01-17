package dk.dtu.sdws.group3.connector;

import messaging.Event;
import messaging.MessageQueue;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AccountServiceConnector {

    MessageQueue queue;

    private Map<UUID, CompletableFuture<String>> correlations = new ConcurrentHashMap<>();

    public AccountServiceConnector(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("UserAccountInfoResponse", this::handleGetAccountFromIdResponse);
    }

    public String getUserBankAccountFromId(UUID id) {
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event e = new Event(correlationId, "UserAccountInfoRequested", new Object[]{id});
        this.queue.publish(e);
        return correlations.get(correlationId).join();
    }

    public void handleGetAccountFromIdResponse(Event event) {
        String s = event.getArgument(0, String.class);
        UUID correlationId = UUID.randomUUID();
        correlations.get(correlationId).complete(s);
    }
}
