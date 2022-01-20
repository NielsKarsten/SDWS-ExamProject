package services;

import messaging.Event;
import messaging.MessageQueue;
import models.Transaction;
import models.TransactionRequest;
import models.TransactionRequestResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRestService {

    private MessageQueue queue;
    private Map<UUID, CompletableFuture<Object>> correlations = new ConcurrentHashMap<>();
    private CompletableFuture<TransactionRequestResponse> response;

    public TransactionRestService(MessageQueue q) {
        queue = q;
        this.queue.addHandler("TransactionRequestSuccesfull", this::handleTransactionRequestResponseSuccess);
        this.queue.addHandler("TransactionRequestInvalid", this::handleTransactionRequestResponseInvalid);
        this.queue.addHandler("ReportResponse", this::handleReportResponse);
        this.queue.addHandler("ReportRequestInvalid", this::handleReportRequestInvalid);

    }

    private Object buildCompletableFutureEvent(Object eventObject, String eventTopic) {
        CompletableFuture<Object> completableFuture = new CompletableFuture<Object>();

        UUID correlationId = UUID.randomUUID();
        Event event = new Event(correlationId, eventTopic, new Object[] { eventObject });

        correlations.put(correlationId, completableFuture);
        queue.publish(event);
        return completableFuture.join();
    }

    public String createTransactionRequest(TransactionRequest request) {
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        var event = new Event(correlationId, "TransactionRequest", new Object[] { request });
        this.queue.publish(event);
        return (String) correlations.get(correlationId).join();
    }

    public void handleTransactionRequestResponseSuccess(Event event) {
        String r = event.getArgument(0, String.class);
        UUID correlationId = event.getCorrelationId();
        correlations.get(correlationId).complete(r);
    }
    
    public void handleTransactionRequestResponseInvalid(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	Exception exception = event.getArgument(0, Exception.class);
        correlations.get(correlationId).completeExceptionally(exception);
    }

    private List<Transaction> getTransactions(String eventName, UUID id) {
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        var event = new Event(correlationId, eventName, new Object[] { id });
        this.queue.publish(event);
        return (List<Transaction>) correlations.get(correlationId).join();
    }

    public List<Transaction> getCustomerTransactions(UUID userId) {
        return getTransactions("CustomerReportRequested", userId);
    }

    public void handleReportResponse(Event event) {
        List<Transaction> requestedTransactions = event.getArgument(0, List.class);
        UUID correlationId = event.getCorrelationId();
        correlations.get(correlationId).complete(requestedTransactions);
    }
    
    public void handleReportRequestInvalid(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	Exception exception = event.getArgument(0, Exception.class);
    	correlations.get(correlationId).completeExceptionally(exception);
    }

    public List<Transaction> getMerchantTransactions(UUID merchantId) {
        return getTransactions("MerchantReportRequested", merchantId);
    }

    public List<Transaction> getAdminTransactions() {
        UUID correlationId = UUID.randomUUID();
        correlations.put(correlationId, new CompletableFuture<>());
        var event = new Event(correlationId, "AdminReportRequested", new Object[] {});
        this.queue.publish(event);
        return (List<Transaction>) correlations.get(correlationId).join();
    }
}