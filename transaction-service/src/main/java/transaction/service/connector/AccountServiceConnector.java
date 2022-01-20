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

    private Map<UUID, CompletableFuture<Object>> correlations = new ConcurrentHashMap<>();

    public AccountServiceConnector(MessageQueue q) {
        this.queue = q;
        this.queue.addHandler("UserAccountInfoResponse", this::handleGetAccountFromIdResponse);
    }

    public boolean userExists(UUID userId) throws NullPointerException {
    	if (userId == null)
    		throw new NullPointerException("User Id was not specified");
    	
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event e = new Event(correlationId, "VerifyUserAccountExistsRequest", new Object[]{userId});
        this.queue.publish(e);
        return (boolean) correlations.get(correlationId).join();    	
    }
    
    public String getUserBankAccountFromId(UUID userId) throws NullPointerException {
    	if (userId == null)
    		throw new NullPointerException("User Id was not specified");
    	
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        Event e = new Event(correlationId, "UserAccountInfoRequested", new Object[]{userId});
        this.queue.publish(e);
        return (String) correlations.get(correlationId).join();
    }

    public void handleGetAccountFromIdResponse(Event event) {
        String userBankAccountId = event.getArgument(0, String.class);
        UUID correlationId = event.getCorrelationId();
        correlations.get(correlationId).complete(userBankAccountId);
    }
}
