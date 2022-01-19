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

    private Map<UUID, CompletableFuture<String>> correlations = new ConcurrentHashMap<>();

    public AccountServiceConnector(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("UserAccountInfoResponse", this::handleGetAccountFromIdResponse);
    }

    public String getUserBankAccountFromId(UUID userId) {
    	System.out.println("Getting user bank account from user Id: " + userId.toString());
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event e = new Event(correlationId, "UserAccountInfoRequested", new Object[]{userId});
        this.queue.publish(e);
        return correlations.get(correlationId).join();
    }

    public void handleGetAccountFromIdResponse(Event event) {
    	System.out.println("Receiving User bank account ID");
        String userBankAccountId = event.getArgument(0, String.class);
        System.out.println("User bank account id: " + userBankAccountId);
        UUID correlationId = event.getCorrelationId();
        correlations.get(correlationId).complete(userBankAccountId);
    }
}
