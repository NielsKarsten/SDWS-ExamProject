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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TransactionServiceSteps {
    BankService bank = mock(BankService.class);
    MessageQueue queue = mock(MessageQueue.class);
    MockAccountServiceConnector accountServiceConnector = new MockAccountServiceConnector(queue);
    MockTokenServiceConnector tokenServiceConnector = new MockTokenServiceConnector(queue);
    TransactionService transactionService = new TransactionService(queue, bank, tokenServiceConnector, accountServiceConnector);

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
    TransactionRequestResponse expected;
    
    
    //Event construction
	public Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case "TransactionRequest":
				obj = new TransactionRequest(merchantId, customerId, UUID.randomUUID(); BigDecimal.valueOf(amount));
				break;
			case "TransactionRequestResponse":
				obj = new TransactionRequestResponse(true);
				break;
			case "CustomerReportRequested":
				obj = userId;
				break;
			case "CustomerReportResponse":
				obj = user.getAccountId();
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
		Event event = new Event(eventName, new Object[] { eventObject });
		switch (eventName) {
			case "TransactionRequest":
				transactionService.handleTransactionRequestEvent(event);
				break;
			case "TransactionRequestResponse":
				service.handleUserAccountInfoResponse(event);
				break;
			case "CustomerReportRequested":
				service.handleUserAccountClosedResponse(event);
				break;
			case "CustomerReportResponse":
				service.handleUserAccountClosedResponse(event);
				break;
			default:
				System.out.println("No event handler found for " + eventName);
				break;
		}
		
	@Before
	public void setUp() {
		customerId = UUID.randomUUID();
		customerToken = UUID.randomUUID();
		merchantId = UUID.randomUUID();		
	}
    
    @Given("a merchant with an account with a balance of {int}")
    public void a_merchant_with_merchant_id_and_a_account_with_balance_of(int balance) {
        merchantBankId = UUID.randomUUID();
        accountServiceConnector.addUser(merchantId, merchantBankId.toString());
    }
    
    @And("a customer with an account with a balance of {int}")
    public void aCustomerWithAnAccountWithABalanceOf(int balance) {
        customerBankId = UUID.randomUUID();
        accountServiceConnector.addUser(customerId, customerBankId.toString());
    }
    
    @And("an amount of {int}")
    public void an_amount_of(int amount) {
        this.amount = amount;
    }
    
    @And("a list of transactions")
    public void aListOfTransactions() {
        for (int i = 100; i < 500; i+=100) {
        	Transaction transaction = new Transaction(merchantId, customerId, i,"Payment!", customerToken);
        	transactions.add(transaction);
		}
    }

    @When("the transaction is initiated")
    public void the_transactions_is_initiated() {
        success = transactionService.pay(customerId, merchantId, BigDecimal.valueOf(amount), customerToken).isSuccessful();
    }

    @Then("the transaction is successful")
    public void the_transaction_is_successful() {
        assertTrue(success);
    }

    @And("the transaction is saved")
    public void theTransactionIsSaved() {
        Transaction t = new Transaction(merchantId, customerId, BigDecimal.valueOf(amount), description, customerToken);
        TransactionStore ts = TransactionStore.getInstance();
        assertTrue(TransactionStore.getInstance().getTransactions().containsKey(customerId));
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
        assertTrue(expected.isSuccessful());
    }
}
