package services;

import messaging.Event;
import messaging.MessageQueue;
import transaction.service.models.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRestService {

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<TransactionRequestResponse>> correlations = new ConcurrentHashMap<>();
    private CompletableFuture<TransactionRequestResponse> response;

    public TransactionRestService(MessageQueue q) {
        queue = q;
        this.queue.addHandler("TransactionRequestResponse", this::handleTransactionRequestResponse);

    }

    public TransactionRequestResponse createTransactionRequest(TransactionRequest request) {
    	UUID correlationId = UUID.randomUUID();
    	correlations.put(correlationId, new CompletableFuture<>());
        var event = new Event(correlationId, "TransactionRequest", new Object[] {request});
        this.queue.publish(event);
        return correlations.get(correlationId).join();
    }

    public void handleTransactionRequestResponse(Event event) {
        TransactionRequestResponse r = event.getArgument(0, TransactionRequestResponse.class);
        UUID correlationId = event.getCorrelationId();
        correlations.get(correlationId).complete(r);
    }

}
