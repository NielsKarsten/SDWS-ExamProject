package transaction.service.services;

import java.math.BigDecimal;
import java.util.*;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankServiceService;
import handling.AccountEventType;
import handling.TokenEventType;
import handling.TransactionEventType;
import messaging.Event;
import messaging.MessageQueue;
import transaction.service.connector.AccountServiceConnector;
import transaction.service.connector.TokenServiceConnector;
import transaction.service.models.*;
import transaction.service.persistance.TransactionStore;

public class TransactionService implements  AccountEventType, TokenEventType, TransactionEventType{

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

        this.queue.addHandler(TRANSACTION_REQUESTED, this::handleTransactionRequestEvent);
        this.queue.addHandler(CUSTOMER_REPORT_REQUESTED, this::handleCustomerReportRequest);
        this.queue.addHandler(MERCHANT_REPORT_REQUESTED, this::handleMerchantReportRequest);
        this.queue.addHandler(ADMIN_REPORT_REQUESTED, this::handleAdminReportRequest);
    }
    
    public void publishEvent(UUID correlationId, String eventName, Object eventData) {
        Event event = new Event(correlationId, eventName, new Object[]{eventData});
        this.queue.publish(event);
    }
    
    public void handleTransactionRequestEvent(Event event) {    	
        TransactionRequest request = event.getArgument(0, TransactionRequest.class);
        UUID correlationId = event.getCorrelationId();
        UUID merchantId = request.getMerchantId();
        UUID userToken = request.getUserToken();
        BigDecimal amount = request.getAmount();
        try 
        {
        	UUID customerId = tokenServiceConnector.getUserIdFromToken(userToken);
        	if(!accountServiceConnector.userExists(customerId))
        		throw new NullPointerException("Customer does not exists");
        	if(!accountServiceConnector.userExists(merchantId))
        		throw new NullPointerException("Merchant does not exists");
        	if (amount == null || amount.compareTo(BigDecimal.valueOf(0)) <= 0)
        		throw new NullPointerException("Amount incorrectly specified");
        	this.tryPayment(customerId, merchantId, amount, userToken);
        	this.publishEvent(correlationId, TRANSACTION_REQUEST_SUCCESFULL, "Transaction was completed succesfully");
        }
        catch(Exception e)
        {
        	System.out.println("Error occoured during a transaction request: " + e.getMessage());
        	this.publishEvent(correlationId, TRANSACTION_REQUEST_INVALID, e);
        }
    }

    public void tryPayment(UUID customer, UUID merchant, BigDecimal amount, UUID token) throws BankServiceException_Exception, NullPointerException {
    	String customerBankAccount = accountServiceConnector.getUserBankAccountFromId(customer);
        String merchantBankAccount = accountServiceConnector.getUserBankAccountFromId(merchant);
        String description = "Payment of " + amount + " to merchant " + merchantBankAccount;

        this.bankTransfer(customerBankAccount, merchantBankAccount, amount, description);
        Transaction t = new Transaction(merchant, customer, amount, description, token);
        TransactionStore.getInstance().addTransaction(t);
    }

	private void bankTransfer(String userBank, String merchantBank, BigDecimal amount, String description) throws BankServiceException_Exception {
		bank.transferMoneyFromTo(userBank, merchantBank, amount, description);
    }
    
    public void handleAdminReportRequest(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	List<Transaction> transactions = TransactionStore.getInstance().getAllTransactions();
    	this.publishEvent(correlationId, REPORT_RESPONSE, transactions);
    }
    
    public void handleCustomerReportRequest(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	UUID userId = event.getArgument(0, UUID.class);
    	try 
    	{
        	if (accountServiceConnector.userExists(userId)) {
            	List<Transaction> transactions = TransactionStore.getInstance().getCustomerTransactions(userId);
            	this.publishEvent(correlationId, REPORT_RESPONSE, transactions);        		
        	}
        	else
        	{
        		this.publishEvent(correlationId, REPORT_REQUEST_INVALID, new NullPointerException("No customer with that ID exists"));
        	}
    	}
    	catch(NullPointerException e)
    	{
    		this.publishEvent(correlationId, REPORT_REQUEST_INVALID, e);
    	}
    }
    
    public void handleMerchantReportRequest(Event event) {
    	UUID correlationId = event.getCorrelationId();
    	UUID merchantId = event.getArgument(0, UUID.class);
    	try 
    	{
        	if (accountServiceConnector.userExists(merchantId)) {
            	List<Transaction> transactions = TransactionStore.getInstance().getMerchantTransactions(merchantId);
            	this.publishEvent(correlationId, REPORT_RESPONSE, transactions);        		
        	}
        	else
        	{
        		this.publishEvent(correlationId, REPORT_REQUEST_INVALID, new NullPointerException("No customer with that ID exists"));
        	}
    	}
    	catch(NullPointerException e)
    	{
    		this.publishEvent(correlationId, REPORT_REQUEST_INVALID, e);
    	}
    }
}
