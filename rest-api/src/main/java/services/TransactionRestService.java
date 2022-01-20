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
    	return correlations.get(correlationId).join();
    }

    public String createTransactionRequest(TransactionRequest request) {
    	return (String) buildCompletableFutureEvent(request,"TransactionRequest");
    }
    
    public List<Transaction> getAdminTransactions() {
    	return (List<Transaction>) buildCompletableFutureEvent(null,"AdminReportRequested");
    }

    public List<Transaction> getMerchantTransactions(UUID merchantId) {
    	return (List<Transaction>) buildCompletableFutureEvent(merchantId,"MerchantReportRequested");
    }

    public List<Transaction> getCustomerTransactions(UUID userId) {
    	return (List<Transaction>) buildCompletableFutureEvent(userId,"CustomerReportRequested");
    }
    
    public void handleTransactionRequestResponseSuccess(Event event) {
    	UUID correlationId = event.getCorrelationId();
        String r = event.getArgument(0, String.class);
        correlations.get(correlationId).complete(r);
    }
    
    public void handleTransactionRequestResponseInvalid(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	Exception exception = event.getArgument(0, Exception.class);
        correlations.get(correlationId).completeExceptionally(exception);
    }

    public void handleReportResponse(Event event) {
    	UUID correlationId = event.getCorrelationId();
        List<Transaction> requestedTransactions = event.getArgument(0, List.class);
        correlations.get(correlationId).complete(requestedTransactions);
    }
    
    public void handleReportRequestInvalid(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	Exception exception = event.getArgument(0, Exception.class);
    	correlations.get(correlationId).completeExceptionally(exception);
    }


}