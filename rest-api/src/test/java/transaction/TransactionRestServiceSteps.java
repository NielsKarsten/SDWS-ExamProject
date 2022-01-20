package transaction;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import services.AccountRestService;
import services.TransactionRestService;
import models.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRestServiceSteps {
    MessageQueue queue;;
    CompletableFuture<Event> publishedEvent;
    TransactionRestService transactionRestService;

    UUID correlationId;
    CompletableFuture<Object> actual;

    //Customer
    UUID customerId;    
    UserAccount customer;
    UUID customerToken;
    
    //Merchant
    UUID merchantId;
    UserAccount merchant;
    
    //Transaction
    List<Transaction> transactions = new ArrayList<>();
    int amount;
    String description;
    boolean success;
    TransactionRequest trxReq;
    
	@Before
	public void setUp() {
		queue = new MessageQueue() {

			@Override
			public void publish(Event message) {
				publishedEvent.complete(message);
			}

			@Override
			public void addHandler(String eventType, Consumer<Event> handler) {
			}
		};
		
		transactionRestService = new TransactionRestService(queue);
		publishedEvent = new CompletableFuture<Event>();
		actual = new CompletableFuture<>();
		customerId = UUID.randomUUID();
		customerToken = UUID.randomUUID();
		merchantId = UUID.randomUUID();
	}
    
    //Event construction
	public Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case "TransactionRequest":
				obj = trxReq;
				break;
			case "TransactionRequestResponse":
				obj = new TransactionRequestResponse(true);
				break;
			case "CustomerReportRequested":
				obj = customerId;
				break;
			case "MerchantReportRequested":
				obj = merchantId;
				break;
			case "CustomerReportResponse":
				obj = transactions;
				break;
			case "MerchantReportResponse":
				List<Transaction> merchantTransactions = new ArrayList<Transaction>();
				for (Transaction transaction : transactions) {
					transaction.setCustomer(null);
					merchantTransactions.add(transaction);
				}
				obj = merchantTransactions;
				break;
			case "AdminReportRequested":
				obj = '1';
				break;
			case "AdminReportResponse":
				obj = transactions;
				break;
			default:
				System.out.println("No event object found for " + eventName);
				obj = null;
				break;
		}
		return obj;
	}

	public void handleEventReceived(String eventName) {
        Object eventObject = getEventObject(eventName);
        Event event = new Event(correlationId, eventName, new Object[]{eventObject});
        switch (eventName) {
            case "TransactionRequestResponse":
                transactionRestService.handleTransactionRequestResponseInvalid(event);
                break;
            case "AdminReportResponse":
            case "CustomerReportResponse":
            case "MerchantReportResponse":
        	   transactionRestService.handleReportResponse(event);
        	   break;
            default:
                System.out.println("No event handler found for " + eventName);
                break;
        }
    }
	
    @Given("a transaction request")
    public void aTransactionRequest() {
        merchant = new UserAccount(merchantId, new Account());
        Account customerAccount = new Account();
        customerAccount.setBalance(BigDecimal.valueOf(1000));
        customer = new UserAccount(customerId, customerAccount);
        amount = 100;

        trxReq = new TransactionRequest(merchant.getUserId(), UUID.randomUUID(), BigDecimal.valueOf(amount));
    }

    @And("a list of transactions")
    public void aListOfTransactions() {
        customerId = UUID.randomUUID();
        merchantId = UUID.randomUUID();	
        for (int i = 100; i < 500; i += 100) {        	
        	BigDecimal transactionAmount = BigDecimal.valueOf(i);
        	String description = "Payment of " + transactionAmount.toString() + " to merchant " + merchantId.toString();
            
            Transaction transaction = new Transaction(merchantId, customerId, transactionAmount, description, customerToken);
            transactions.add(transaction);
        }
    }
    
    @When("the transaction request is being registered")
    public void theTransactionRequestIsBeingRegistered() {
        new Thread(() -> {
        	TransactionRequestResponse result = transactionRestService.createTransactionRequest(trxReq);
        	actual.complete(result);
        }).start();
    }
    
    @When("the Customer requests a list of their transactions")
    public void theCustomerRequestsAListOfTheirTransactions() {
        new Thread(() -> {
        	List<Transaction> result = transactionRestService.getCustomerTransactions(customerId);
        	actual.complete(result);
        }).start();
    }

    @When("the Merchant requests a list of their transactions")
    public void theMerchantRequestsAListOfTheirTransactions() {
        new Thread(() -> {
        	List<Transaction> result = transactionRestService.getMerchantTransactions(merchantId);
        	actual.complete(result);
        }).start();
    }

    @When("the Admin requests a list of all transactions")
    public void theAdminRequestsAListOfAllTransactions() {
        new Thread(() -> {
        	List<Transaction> result = transactionRestService.getAdminTransactions();
        	actual.complete(result);
        }).start();
    }
    
    @Then("a {string} event is sent")
    public void a_event_is_sent(String eventName) {
    	Event pEvent = publishedEvent.join();
    	correlationId = pEvent.getCorrelationId();
    	Event event;
    	if (eventName.equals("AdminReportRequested")) {
    		event = new Event(correlationId, eventName, new Object[]{});
    	}
    	else {
    		event = new Event(correlationId, eventName, new Object[]{getEventObject(eventName)});
    	}
    	assertEquals(event, pEvent);
    }
    
    @And("a {string} event is received")
    public void a_event_is_received(String eventName) {
        handleEventReceived(eventName);
    }

    @And("the transaction response has status successful")
    public void the_transaction_response_has_status_successful() {
    	TransactionRequestResponse receivedResponse = (TransactionRequestResponse) actual.join();
    	assertNotNull(receivedResponse);
    }
    
    @And("the ReportRequest was successful")
    public void theReportRequestWasSuccesfull() {
    	List<Transaction> receivedResponse = (List<Transaction>) actual.join();
    	assertNotNull(receivedResponse);
    }


}
