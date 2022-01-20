package transaction.service.services;

import java.math.BigDecimal;
import java.util.*;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
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

    public TransactionService(MessageQueue queue){
        this(queue, new BankServiceService().getBankServicePort());
    }

    public TransactionService(MessageQueue queue, BankService bank) {
        this(queue, bank, new TokenServiceConnector(queue), new AccountServiceConnector(queue));
    }

    public TransactionService(MessageQueue queue, BankService bank, TokenServiceConnector tokenServiceConnector, AccountServiceConnector accountServiceConnector) {
        this.queue = queue;
        this.bank = bank;
        this.tokenServiceConnector = tokenServiceConnector;
        this.accountServiceConnector = accountServiceConnector;

        TransactionStore.reset();

        this.queue.addHandler("TransactionRequested", this::handleTransactionRequestEvent);
        this.queue.addHandler("CustomerReportRequested", this::handleCustomerReportRequest);
        this.queue.addHandler("MerchantReportRequested", this::handleMerchantReportRequest);
        this.queue.addHandler("AdminReportRequested", this::handleAdminReportRequest);
    }
    
    public void publishEvent(UUID correlationId, String eventName, Object eventData) {
        Event event = new Event(correlationId, eventName, new Object[]{eventData});
        this.queue.publish(event);
    }
    
    public void handleTransactionRequestEvent(Event event) {
    	System.out.println("handleTransactionRequestEvent");
    	System.out.println("handleTransactionRequestEvent invoked");
    	
        TransactionRequest request = event.getArgument(0, TransactionRequest.class);
        UUID correlationId = event.getCorrelationId();
        UUID merchantId = request.getMerchantId();
        UUID userToken = request.getUserToken();
        BigDecimal amount = request.getAmount();
        try 
        {
        	System.out.println("Get uset account from token");
        	UUID customerId = tokenServiceConnector.getUserIdFromToken(userToken);
        	System.out.println("Verify customer exists");
        	if(!accountServiceConnector.userExists(customerId))
        		throw new NullPointerException("Customer does not exists");
        	System.out.println("Verify Merchant exists");
        	if(!accountServiceConnector.userExists(merchantId))
        		throw new NullPointerException("Merchant does not exists");
        	System.out.println("Verify Correct amount");
        	if (amount == null || amount.compareTo(BigDecimal.valueOf(0)) <= 0)
        		throw new NullPointerException("Amount incorrectly specified");
        	this.tryPayment(customerId, merchantId, amount, userToken);
        	this.publishEvent(correlationId, "TransactionRequestSuccesfull", "Transaction was completed succesfully");
        }
        catch(Exception e)
        {
        	this.publishEvent(correlationId, "TransactionRequestInvalid", e);
        }
    }

    public void tryPayment(UUID customer, UUID merchant, BigDecimal amount, UUID token) throws BankServiceException_Exception, NullPointerException {
    	System.out.println("tryPayment");
    	String customerBankAccount = accountServiceConnector.getUserBankAccountFromId(customer);
        String merchantBankAccount = accountServiceConnector.getUserBankAccountFromId(merchant);
        String description = "Payment of " + amount + " to merchant " + merchantBankAccount;

        this.bankTransfer(customerBankAccount, merchantBankAccount, amount, description);
        Transaction t = new Transaction(merchant, customer, amount, description, token);
        TransactionStore.getInstance().addTransaction(t);
    }

	private void bankTransfer(String userBank, String merchantBank, BigDecimal amount, String description) throws BankServiceException_Exception {
        System.out.println("bankTransfer");
		bank.transferMoneyFromTo(userBank, merchantBank, amount, description);
    }
    
    public void handleAdminReportRequest(Event event) {
    	System.out.println("handleAdminReportRequest");
    	UUID correlationId = event.getCorrelationId();
    	List<Transaction> transactions = TransactionStore.getInstance().getAllTransactions();
    	this.publishEvent(correlationId, "ReportResponse", transactions);
    }
    
    public void handleCustomerReportRequest(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	UUID userId = event.getArgument(0, UUID.class);
    	try 
    	{
        	if (accountServiceConnector.userExists(userId)) {
            	List<Transaction> transactions = TransactionStore.getInstance().getCustomerTransactions(userId);
            	this.publishEvent(correlationId, "ReportResponse", transactions);        		
        	}
        	else
        	{
        		this.publishEvent(correlationId, "ReportRequestInvalid", new NullPointerException("No customer with that ID exists"));
        	}
    	}
    	catch(NullPointerException e)
    	{
    		this.publishEvent(correlationId, "ReportRequestInvalid", e);
    	}
    }
    
    public void handleMerchantReportRequest(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	UUID merchantId = event.getArgument(0, UUID.class);
    	try 
    	{
        	if (accountServiceConnector.userExists(merchantId)) {
            	List<Transaction> transactions = TransactionStore.getInstance().getMerchantTransactions(merchantId);
            	this.publishEvent(correlationId, "ReportResponse", transactions);        		
        	}
        	else
        	{
        		this.publishEvent(correlationId, "ReportRequestInvalid", new NullPointerException("No customer with that ID exists"));
        	}
    	}
    	catch(NullPointerException e)
    	{
    		this.publishEvent(correlationId, "ReportRequestInvalid", e);
    	}
    }
}
