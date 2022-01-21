import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import transaction.service.models.Transaction;
import transaction.service.models.TransactionRequest;
import transaction.service.persistance.TransactionStore;
import transaction.service.services.TransactionService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Christian Gerns�e - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Gustav Utke Kauman
 */
public class TransactionServiceSteps {
    BankService bank = mock(BankService.class);
    MessageQueue queue = mock(MessageQueue.class);
    MockAccountServiceConnector accountServiceConnector;
    MockTokenServiceConnector tokenServiceConnector;
    TransactionService transactionService;
    CompletableFuture<Event> publishedEvent;
    String request;
    //Setup
    UUID customerId;
    UUID customerToken;
    UUID customerBankId;
    
    UUID merchantId;
    UUID merchantBankId;

    int amount;
    String description;
    
    //Results
    boolean success;
    Exception exception;
    List<Transaction> transactions = new ArrayList<>();
    Object expected;
    
    
    //Event construction
	public Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case "TransactionRequested":
                UUID token = UUID.randomUUID();
                tokenServiceConnector.addToken(customerId, token);
				obj = new TransactionRequest(merchantId, token, BigDecimal.valueOf(amount));
				break;
			case "TransactionRequestSuccesfull":
				obj = "Transaction was completed succesfully";
				break;
			case "CustomerReportRequested":
				obj = customerId;
				break;
			case "MerchantReportRequested":
				obj = merchantId;
				break;
			case "AdminReportRequested":
                break;
            case "TransactionRequestInvalid":
                obj = new NullPointerException();
                break;
			case "ReportResponse":
				if (request.equals("MerchantReportRequested")) {
					List<Transaction> merchantTransactions = new ArrayList<>();
					for (Transaction transaction : transactions) {
						transaction.setCustomer(null);
						merchantTransactions.add(transaction);
					}
					obj = merchantTransactions;					
				} else {
                    obj = transactions;
                }
				break;
            default:
				obj = null;
				break;
		}
		return obj;
	}

	public void handleEventReceived(String eventName) {
        Object eventObject = getEventObject(eventName);
        Event event = new Event(eventName, new Object[]{eventObject});
        switch (eventName) {
            case "TransactionRequested":
                transactionService.handleTransactionRequestEvent(event);
                break;
            case "CustomerReportRequested":
                transactionService.handleCustomerReportRequest(event);
                break;
           case "MerchantReportRequested":
        	   transactionService.handleMerchantReportRequest(event);
               break;
           case "AdminReportRequested":
        	   transactionService.handleAdminReportRequest(event);
        	   break;
            default:
                break;
        }
    }
		
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
	    accountServiceConnector = new MockAccountServiceConnector(queue);
	    tokenServiceConnector = new MockTokenServiceConnector(queue);
	    transactionService = new TransactionService(queue, bank, tokenServiceConnector, accountServiceConnector);
	    publishedEvent = new CompletableFuture<Event>();
        transactions = new ArrayList<>();
        exception = null;
	}
    
    @Given("a merchant with an account with a balance of {int}")
    public void a_merchant_with_merchant_id_and_a_account_with_balance_of(int balance) {
        merchantId = UUID.randomUUID();
        merchantBankId = UUID.randomUUID();
        accountServiceConnector.addUser(merchantId, merchantBankId.toString());
    }
    
    @And("a customer with an account with a balance of {int}")
    public void aCustomerWithAnAccountWithABalanceOf(int balance) {
        customerId = UUID.randomUUID();
        customerBankId = UUID.randomUUID();
        customerToken = UUID.randomUUID();
        accountServiceConnector.addUser(customerId, customerBankId.toString());
    }
    
    @And("an amount of {int}")
    public void an_amount_of(int amount) {
        this.amount = amount;
    }
    
    @And("a list of transactions")
    public void aListOfTransactions() {
        for (int i = 100; i < 500; i+=100) {
        	BigDecimal transactionAmount = BigDecimal.valueOf(i);
        	String description = "Payment of " + transactionAmount + " to merchant " + merchantBankId.toString();
        	Transaction transaction = new Transaction(merchantId, customerId, transactionAmount,description, customerToken);
        	try {
				transactionService.tryPayment(customerId, merchantId, transactionAmount, customerToken);
                transactions.add(transaction);
				success = true;
			} catch (NullPointerException | BankServiceException_Exception e) {
				exception = e;
				e.printStackTrace();
			}
		}
    }

    @When("the transaction is initiated")
    public void the_transactions_is_initiated() {
        description = "Payment of " + amount + " to merchant " + merchantBankId;
        try {
			transactionService.tryPayment(customerId, merchantId, BigDecimal.valueOf(amount), customerToken);
			success = true;
		} catch (NullPointerException | BankServiceException_Exception e) {
			exception = e;
		}
    }

    @Then("the transaction is successful")
    public void the_transaction_is_successful() {
        assertTrue(success);
    }

    @And("the transaction is saved")
    public void theTransactionIsSaved() {
        Transaction t = new Transaction(merchantId, customerId, BigDecimal.valueOf(amount), description, customerToken);
        assertTrue(TransactionStore.getInstance().getAllTransactions().contains(t));
    }

    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
    	request = eventName;
    	handleEventReceived(eventName);
    }

    @Then("a {string} event is sent")
    public void aEventIsSent(String eventName) {
    	Event pEvent = publishedEvent.join();
        expected = getEventObject(eventName);
        Event event = new Event(eventName, new Object[]{expected});
        assertEquals(pEvent, event);
    }

    @Then("a {string} event is sent with error message {string}")
    public void aEventIsSentWithErrorMessage(String eventName, String errorMsg) {
    	Event pEvent = publishedEvent.join();
    	exception = pEvent.getArgument(0, NullPointerException.class);
        assertEquals(pEvent.getType(), eventName);
        assertEquals(errorMsg, exception.getMessage());
    }
}
