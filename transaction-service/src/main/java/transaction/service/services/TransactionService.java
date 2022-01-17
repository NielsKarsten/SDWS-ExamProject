package transaction.service.services;

import java.math.BigDecimal;
import java.util.*;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;
import transaction.service.connector.AccountServiceConnector;
import transaction.service.connector.TokenServiceConnector;
import transaction.service.models.*;
import transaction.service.persistance.TransactionStore;

public class TransactionService {

    private BankService bank;
    private MessageQueue queue;
    private TokenServiceConnector tokenServiceConnector;
    private AccountServiceConnector accountServiceConnector;

    private String errorMessage;

    public TransactionService(MessageQueue queue, BankService bank) {
        this(queue, bank, new TokenServiceConnector(queue), new AccountServiceConnector(queue));
    }

    public TransactionService(MessageQueue queue, BankService bank, TokenServiceConnector tokenServiceConnector, AccountServiceConnector accountServiceConnector) {
        this.queue = queue;
        this.bank = bank;
        this.tokenServiceConnector = tokenServiceConnector;
        this.accountServiceConnector = accountServiceConnector;

        this.queue.addHandler("TransactionRequest", this::handleTransactionRequestEvent);
        this.queue.addHandler("TransactionsByUserIdRequest", this::handleTransactionsByUserIdRequest);
        this.queue.addHandler("CustomerReportRequested", this::handleCustomerReportRequest);
        this.queue.addHandler("MerchantReportRequested", this::handleMerchantReportRequest);
    }

    public boolean pay(String userBank, String merchantBank, BigDecimal amount, String description) {
        try {
            bank.transferMoneyFromTo(merchantBank, userBank, amount, description);
        } catch (BankServiceException_Exception e) {
            errorMessage = e.getMessage();
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public TransactionRequestResponse pay(UUID customer, UUID merchant, BigDecimal amount, UUID token) {
        String customerBankAccount = accountServiceConnector.getUserBankAccountFromId(customer);
        String merchantBankAccount = accountServiceConnector.getUserBankAccountFromId(merchant);

        TransactionRequestResponse trxReqResp = new TransactionRequestResponse();
        String description = "Payment of " + amount + " to merchant " + merchantBankAccount;
        
        boolean paymentSuccesfull = this.pay(customerBankAccount, merchantBankAccount, amount, description); 
        if (paymentSuccesfull) {
            trxReqResp.setSuccessful(paymentSuccesfull);
            Transaction t = new Transaction(merchant, customer, amount, description, token);
            TransactionStore.getInstance().addTransaction(t);
        } else {
            trxReqResp.setSuccessful(false);
            trxReqResp.setErrorMessage(errorMessage);
        }

        return trxReqResp;
    }

    public void handleTransactionRequestEvent(Event event) {
        TransactionRequest request = event.getArgument(0, TransactionRequest.class);
        UUID correlationId = event.getCorrelationId();

        UUID customerId = tokenServiceConnector.getUserIdFromToken(request.getUserToken());
        
        TransactionRequestResponse trxReqResp = this.pay(customerId, request.getMerchantId(), request.getAmount(), request.getUserToken());
        Event e = new Event(correlationId, "TransactionRequestResponse", new Object[]{trxReqResp});
        this.queue.publish(e);
    }

    public void handleTransactionsByUserIdRequest(Event event) {
        UUID userId = event.getArgument(0, UUID.class);
        UUID correlationId = event.getCorrelationId();
        List<Transaction> allTransactionsList = TransactionStore.getInstance().getAllTransactions();
        List<Transaction> transactionList = new ArrayList<>();

        for (Transaction t : allTransactionsList) {
            if (t.getCustomer() == userId || t.getMerchant() == userId)
                transactionList.add(t);
        }

        Event outgoingEvent = new Event(correlationId,"TransactionsByUserIdResponse", new Object[]{userId, transactionList});
        this.queue.publish(outgoingEvent);
    }
    
    public void handleCustomerReportRequest(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	UUID userId = event.getArgument(0, UUID.class);
    	List<Transaction> userTransactions = TransactionStore.getInstance().getCustomerTransactions(userId);
    	
    	Event outgoingEvent = new Event(correlationId, "CustomerReportResponse", new Object[] {userTransactions});
    	this.queue.publish(outgoingEvent);
    }
    
    public void handleMerchantReportRequest(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	UUID merchantId = event.getArgument(0, UUID.class);
    	List<Transaction> merchantTransactions = TransactionStore.getInstance().getMerchantTransactions(merchantId);
    	
    	Event outgoingEvent = new Event(correlationId, "MerchantReportResponse", new Object[] {merchantTransactions});
    	this.queue.publish(outgoingEvent);
    }
}
