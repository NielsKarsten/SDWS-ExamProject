package services;

import messaging.Event;
import messaging.MessageQueue;
import models.Transaction;
import models.TransactionRequest;
import models.TransactionRequestResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionRestService extends UserHandling{

    private CompletableFuture<TransactionRequestResponse> response;

    public TransactionRestService(MessageQueue q) {
    	super(q);
        this.queue.addHandler("TokenValidityResponse", this::handleTokenValidityResponse);
    	this.queue.addHandler("TransactionRequestSuccesfull", this::handleTransactionRequestResponseSuccess);
        this.queue.addHandler("TransactionRequestInvalid", this::handleTransactionRequestResponseInvalid);
        this.queue.addHandler("ReportResponse", this::handleReportResponse);
        this.queue.addHandler("ReportRequestInvalid", this::handleReportRequestInvalid);
    }
    
	protected boolean verifyTokenValidity(UUID token) {
		return (boolean) buildCompletableFutureEvent(token, "VerifyTokenRequest");
	}
	
	protected void handleTokenValidityResponse(Event e) {
		genericHandler(e, Boolean.class);
	}
    
    public String createTransactionRequest(TransactionRequest request) {
    	if (!verifyUserExists(request.getMerchantId()))
    		throw new NullPointerException("No merchent with ID " + request.getMerchantId() + " Exists");
    	if (!verifyTokenValidity(request.getUserToken()))
    		throw new NullPointerException("Invalid token");
    	if (request.getAmount().compareTo(BigDecimal.valueOf(0)) < 0)
    		throw new IllegalArgumentException("Transaction amounts ");
    	return (String) buildCompletableFutureEvent(request,"TransactionRequest");
    }
    
    public List<Transaction> getAdminTransactions() {
    	return (List<Transaction>) buildCompletableFutureEvent(null,"AdminReportRequested");
    }

    public List<Transaction> getMerchantTransactions(UUID merchantId) {
    	if (!verifyUserExists(merchantId))
    		throw new NullPointerException("No merchent with ID " + merchantId + " Exists");
    	return (List<Transaction>) buildCompletableFutureEvent(merchantId,"MerchantReportRequested");
    }

    public List<Transaction> getCustomerTransactions(UUID userId) {
    	if (!verifyUserExists(userId))
    		throw new NullPointerException("No customer with ID " + userId + " Exists");
    	return (List<Transaction>) buildCompletableFutureEvent(userId,"CustomerReportRequested");
    }
    
    public void handleTransactionRequestResponseSuccess(Event event) {
        genericHandler(event,String.class);
    }
    
    public void handleTransactionRequestResponseInvalid(Event event) {
        genericHandler(event,Exception.class);
    }

    public void handleReportResponse(Event event) {
        genericHandler(event,List.class);
    }
    
    public void handleReportRequestInvalid(Event event) {
    	genericErrorHandler(event);
    }

}