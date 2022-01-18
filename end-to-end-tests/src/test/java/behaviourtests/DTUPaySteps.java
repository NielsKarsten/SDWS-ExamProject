package behaviourtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import accountregistration.service.AccountRegistrationService;
import accountregistration.service.User;
import dtu.ws.fastmoney.test.BankService;
import dtu.ws.fastmoney.test.BankServiceException_Exception;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import issuetoken.service.IssueTokenService;
import messaging.Event;
import messaging.MessageQueue;
import transaction.service.services.TransactionService;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import static org.mockito.Mockito.mock;

public class DTUPaySteps {
	private MessageQueue queue;
	private AccountRegistrationService accountRegistrationService;
	private IssueTokenService issueTokenService;
	private BankService bankService;
	TransactionService transactionService = new TransactionService(queue, bank);

	private CompletableFuture<UUID> registeredUser;
	private CompletableFuture<List<UUID>> issuedTokens;
	//private CompletableFuture<String> userAccountId;
	//private CompletableFuture<Boolean> userAccountDeleted;
	//private EventConstruction eventConstruction;
	private CompletableFuture<Event> publishedEvent;
	private UUID correlationID;
	private User customer;
	private User merchant;
	boolean success;
	private List<UUID> tokens;

	Client client = ClientBuilder.newClient();
	WebTarget target = client.target("http://localhost:8080/");

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

		accountRegistrationService = new AccountRegistrationService(queue);
		issueTokenService = new IssueTokenService(queue);
		bankService = mock(BankService.class);

		registeredUser = new CompletableFuture<>();
		issuedTokens = new CompletableFuture<>();
		//userAccountId = new CompletableFuture<>();
		//userAccountDeleted = new CompletableFuture<>();
		//eventConstruction = new EventConstruction(accountRegistrationService);
		//publishedEvent = new CompletableFuture<>();
	}

	@Given("a customer {string} {string} with bank account {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName, String accountId) {
		customer = new User(firstName, lastName, accountId);
		customer.assignUserId();
	}

	@Given("a merchant {string} {string} with bank account {string}")
	public void aMerchantWithBankAccount(String firstName, String lastName, String accountId) {
		merchant = new User(firstName, lastName, accountId);
		merchant.assignUserId();
	}

	@When("customer is being registered")
	public void theCustomerIsBeingRegistered() {
		//new Thread(() -> {
		//	UUID result = accountRegistrationService.registerAsyncUserAccount(customer);
		//	registeredUser.complete(result);
		//}).start();
		target.path("/users").request().post(Entity.json(customer), User.class);
	}

	@When("merchant is being registered")
	public void theMerchantIsBeingRegistered() {
		//new Thread(() -> {
		//	UUID result = accountRegistrationService.registerAsyncUserAccount(merchant);
		//	registeredUser.complete(result);
		//}).start();
		target.path("/users").request().post(Entity.json(merchant), User.class);
	}

	@When ("customer requests {int} tokens")
	public void customerRequestsTokens(int tokenAmount){
		//new Thread(() -> {
		//	var result = issueTokenService.issue(customer.getUserId(),tokenAmount);
		//	issuedTokens.complete(result);
		//}).start();
		TokenRequest tokenRequest = new TokenRequest(customer.getUserId(), tokenAmount);
		tokens = target.path("/requesttokens").request().post(Entity.json(tokenRequest), TokenRequest.class);
	}

	@When("merchant initiates a transaction for {int}")
	public void theTransactionsIsInitiated(int amount) {
		//success = transactionService.pay(customer.getUserId(), merchant.getUserId(), BigDecimal.valueOf(amount)).isSuccessful();
		TransactionRequest transactionRequest = new TransactionRequest();
		target.path("/transactions").request().post()
	}

	@Then("customer has balance {int}")
	public void theCustomerHasBalance(int amount) throws BankServiceException_Exception {
		BigDecimal actual = bankService.getAccount(customer.getAccountId()).getBalance();
		assertEquals(actual, amount);
	}

	@Then("merchant has balance {int}")
	public void theMerchantHasBalance(int amount) throws BankServiceException_Exception {
		BigDecimal actual = bankService.getAccount(customer.getAccountId()).getBalance();
		assertEquals(actual, amount);
	}

	//@Given("an unregistered student with empty id")
	//public void anUnregisteredStudentWithEmptyId() {
	//    student = new Student();
	//    student.setName("James");
	//    assertNull(student.getId());
	//}
//
	//@When("the student is being registered")
	//public void theStudentIsBeingRegistered() {
	//	result = service.register(student);
	//}
//
	//@Then("the student is registered")
	//public void thenTheStudentIsRegistered() {
	//	// test needs to go here
	//}
//
	//@Then("has a non empty id")
	//public void hasANonEmptyId() {
	//	assertNotNull(result.getId());
	//}

}

