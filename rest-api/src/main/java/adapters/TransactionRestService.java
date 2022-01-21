package adapters;

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

import handling.GenericHandler;

public class TransactionRestService extends GenericHandler{

    public TransactionRestService(MessageQueue q) {
    	super(q);
        addHandler("TokenValidityResponse", this::genericHandler);
    	addHandler("TransactionRequestSuccesfull", this::genericHandler);
        addHandler("TransactionRequestInvalid", this::genericErrorHandler);
        addHandler("ReportResponse", this::genericHandler);
        addHandler("ReportRequestInvalid", this::genericErrorHandler);
    }
    
	protected boolean verifyTokenValidity(UUID token) {
		return (boolean) buildCompletableFutureEvent(token, "VerifyTokenRequest");
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
}