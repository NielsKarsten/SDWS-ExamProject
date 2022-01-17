import io.cucumber.java.Before;
import io.cucumber.java.PendingException;
import transaction.service.models.Transaction;
import transaction.service.models.TransactionRequest;
import transaction.service.models.TransactionRequestResponse;
import transaction.service.persistance.TransactionStore;
import transaction.service.services.TransactionService;
import dtu.ws.fastmoney.BankService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TransactionServiceSteps {
    BankService bank = mock(BankService.class);
    MessageQueue queue = mock(MessageQueue.class);
    MockAccountServiceConnector accountServiceConnector;
    MockTokenServiceConnector tokenServiceConnector;
    TransactionService transactionService;
    CompletableFuture<Event> publishedEvent;

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
    List<Transaction> transactions = new ArrayList<Transaction>();
    Object expected;
    
    
    //Event construction
	public Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case "TransactionRequest":
				obj = new TransactionRequest(merchantId, UUID.randomUUID(), BigDecimal.valueOf(amount));
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
				obj = merchantId;
				break;
			case "AdminReportResponse":
				obj = TransactionStore.getInstance().getAllTransactions();
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
        Event event = new Event(eventName, new Object[]{eventObject});
        switch (eventName) {
            case "TransactionRequest":
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
                System.out.println("No event handler found for " + eventName);
                break;
        }
    }
		
	@Before
	public void setUp() {		
	    accountServiceConnector = new MockAccountServiceConnector(queue);
	    tokenServiceConnector = new MockTokenServiceConnector(queue);
	    transactionService = new TransactionService(queue, bank, tokenServiceConnector, accountServiceConnector);
	    publishedEvent = new CompletableFuture<Event>();
	    
	    //Generate Customer data
		customerId = UUID.randomUUID();
		customerBankId = UUID.randomUUID();
		customerToken = UUID.randomUUID();

		//Generate Customer data
		merchantId = UUID.randomUUID();		
		merchantBankId = UUID.randomUUID();
	}
    
    @Given("a merchant with an account with a balance of {int}")
    public void a_merchant_with_merchant_id_and_a_account_with_balance_of(int balance) {
        accountServiceConnector.addUser(merchantId, merchantBankId.toString());
    }
    
    @And("a customer with an account with a balance of {int}")
    public void aCustomerWithAnAccountWithABalanceOf(int balance) {
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
        	String description = "Payment of "+transactionAmount.toString()+" to merchant "+merchantBankId.toString();
        	Transaction transaction = new Transaction(merchantId, customerId, transactionAmount,description, customerToken);
        	transactions.add(transaction);
        	transactionService.pay(customerId, merchantId, transactionAmount, customerToken);
		}
    }

    @When("the transaction is initiated")
    public void the_transactions_is_initiated() {
        description = "Payment of " + amount + " to merchant " + merchantBankId;
        success = transactionService.pay(customerId, merchantId, BigDecimal.valueOf(amount), customerToken).isSuccessful();
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
    	handleEventReceived(eventName);
    }

    @Then("a {string} event is sent")
    public void aEventIsSent(String eventName) {
        expected = getEventObject(eventName);
        verify(queue).publish(new Event(eventName, new Object[]{expected}));
    }

    @And("the transaction response has status successful")
    public void theTransactionResponseHasStatusSuccessful() {
        assertTrue(((TransactionRequestResponse) expected).isSuccessful());
    }
}
