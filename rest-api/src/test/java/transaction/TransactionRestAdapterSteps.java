package transaction;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import models.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import adapters.AccountRestAdapter;
import adapters.TransactionRestAdapter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;
/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Simon Pontoppidan
 */
public class TransactionRestAdapterSteps {
    MessageQueue queue;;
    CompletableFuture<Event> publishedEvent;
    TransactionRestAdapter transactionRestService;

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
		
		transactionRestService = new TransactionRestAdapter(queue);
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
			case "TransactionRequested":
				obj = trxReq;
				break;
			case "TransactionRequestSuccesfull":
				obj = "Transaction was completed succesfully";
				break;
			case "CustomerReportRequested":
				obj = customerId;
				break;
			case "MerchantReportRequested":
				obj = merchantId;
				for (Transaction transaction : transactions) {
					transaction.setCustomer(null);
				}
				break;
			case "AdminReportRequested":
				obj = '1';
				break;
			case "ReportResponse":
				obj = transactions;
				break;
			default:
				obj = null;
				break;
		}
		return obj;
	}

	public void handleEventReceived(String eventName) {
        Object eventObject = getEventObject(eventName);
        Event event = new Event(correlationId, eventName, new Object[]{eventObject});
        switch (eventName) {
            case "TransactionRequestSuccesfull":
                transactionRestService.genericHandler(event);
                break;
            case "ReportResponse":
        	   transactionRestService.genericHandler(event);
        	   break;
            default:
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
			try {
				String result = transactionRestService.createTransactionRequest(trxReq);
	        	actual.complete(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }).start();
    }
    
    @When("the Customer requests a list of their transactions")
    public void theCustomerRequestsAListOfTheirTransactions() {
        new Thread(() -> {
			try {
				List<Transaction> result = transactionRestService.getCustomerTransactions(customerId);
				actual.complete(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }).start();
    }

    @When("the Merchant requests a list of their transactions")
    public void theMerchantRequestsAListOfTheirTransactions() {
        new Thread(() -> {
			try {
				List<Transaction> result = transactionRestService.getMerchantTransactions(merchantId);
	        	actual.complete(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }).start();
    }

    @When("the Admin requests a list of all transactions")
    public void theAdminRequestsAListOfAllTransactions() {
        new Thread(() -> {
			try {
				List<Transaction> result = transactionRestService.getAdminTransactions();
	        	actual.complete(result);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }).start();
    }
    
    @Then("a {string} event is sent")
    public void a_event_is_sent(String eventName) {
    	Event pEvent = publishedEvent.join();
    	correlationId = pEvent.getCorrelationId();
    	Event event = new Event(correlationId, eventName, new Object[]{getEventObject(eventName)});
    	assertEquals(event.getType(), pEvent.getType());
    }
    
    @And("a {string} event is received")
    public void a_event_is_received(String eventName) {
        handleEventReceived(eventName);
    }

    @And("the transaction response has status successful")
    public void the_transaction_response_has_status_successful() {
    	String receivedResponse =  (String) actual.join();
    	assertNotNull(receivedResponse);
    }
    
    @And("the ReportRequest was successful")
    public void theReportRequestWasSuccesfull() {
    	List<Transaction> receivedResponse = (List<Transaction>) actual.join();
    	assertNotNull(receivedResponse);
    }


}
