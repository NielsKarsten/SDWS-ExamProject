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

public class TransactionRestService extends GenericService{

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
    
    public String createTransactionRequest(TransactionRequest request) throws Exception {
    	return (String) buildCompletableFutureEvent(request,"TransactionRequested");
    }
    
    public List<Transaction> getAdminTransactions() throws Exception {
    	return (List<Transaction>) buildCompletableFutureEvent(null,"AdminReportRequested");
    }

    public List<Transaction> getMerchantTransactions(UUID merchantId) throws Exception {
    	return (List<Transaction>) buildCompletableFutureEvent(merchantId,"MerchantReportRequested");
    }

    public List<Transaction> getCustomerTransactions(UUID userId) throws Exception{
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