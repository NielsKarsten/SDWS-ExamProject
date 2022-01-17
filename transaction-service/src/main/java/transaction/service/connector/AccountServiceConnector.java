package transaction.service.connector;

import messaging.Event;
import messaging.MessageQueue;
import transaction.service.models.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class AccountServiceConnector {

    MessageQueue queue;

    private Map<UUID, CompletableFuture<User>> correlations = new ConcurrentHashMap<>();

    public AccountServiceConnector(MessageQueue q) {
        this.queue = q;

        this.queue.addHandler("GetAccountFromIdResponse", this::handleGetAccountFromIdResponse);
    }

    public User getUserFromId(UUID id) {
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event e = new Event(correlationId, "GetAccountFromIdRequest", new Object[]{id});
        this.queue.publish(e);
        return correlations.get(correlationId).join();
    }

    public void handleGetAccountFromIdResponse(Event event) {
        User u = event.getArgument(0, User.class);
        UUID correlationId = UUID.randomUUID();
        correlations.get(correlationId).complete(u);
    }
}
