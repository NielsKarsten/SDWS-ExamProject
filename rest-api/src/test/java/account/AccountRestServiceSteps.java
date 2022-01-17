package account;

import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import services.AccountRestService;
import account.service.models.User;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;

import static org.junit.Assert.*;

/**
* @authors Thomas Rathsach Strange (s153390), 
* 		   Simon Pontoppidan (s144213), 
* 		   Niels Karsten Bisgaard-Bohr (s202745)
*/

public class AccountRestServiceSteps {
	private MessageQueue queue;
	private AccountRestService accountRegistrationService;
	private CompletableFuture<UUID> registeredUser;
	private CompletableFuture<String> userAccountId;
	private CompletableFuture<Boolean> userAccountDeleted;
	private EventConstruction eventConstruction;
	private CompletableFuture<Event> publishedEvent;
	private UUID correlationID;

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

		accountRegistrationService = new AccountRestService(queue);
		registeredUser = new CompletableFuture<>();
		userAccountId = new CompletableFuture<>();
		userAccountDeleted = new CompletableFuture<>();
		eventConstruction = new EventConstruction(accountRegistrationService);
		publishedEvent = new CompletableFuture<>();
	}

	@Given("a user {string} {string} with bank account {string}")
	public void aUserWithBankAccount(String firstName, String lastName, String accountId) {
		User user = new User(firstName, lastName, accountId);
		eventConstruction.setUser(user);
	}

	@When("the user is being registered")
	public void theUserIsBeingRegistered() {
		// We have to run the registration in a thread, because
		// the register method will only finish after the next @When
		// step is executed.
		new Thread(() -> {
			UUID result = accountRegistrationService.registerAsyncUserAccount(eventConstruction.getUser());
			registeredUser.complete(result);
		}).start();
	}

	@When("the user account id is requested")
	public void theUserAccountIdIsRequested() {
		new Thread(() -> {
			String accountId = accountRegistrationService
					.requestAsyncUserAccountInfo(eventConstruction.getUserId());
			userAccountId.complete(accountId);
		}).start();
	}

	@Then("the {string} event is sent")
	public void theEventIsSent(String eventName) throws InterruptedException {
		Event pEvent = publishedEvent.join();
		correlationID = pEvent.getCorrelationId();
		Event event = new Event(correlationID, eventName, new Object[] { eventConstruction.getEventObject(eventName) });
		assertEquals(event, pEvent);
	}

	@When("the {string} event is received")
	public void theEventIsReceived(String eventName) {
		eventConstruction.handleEventReceived(eventName, correlationID);
	}

	@When("the user account is closed")
	public void theUserAccountIsClosed() {
		new Thread(() -> {
			Boolean accountClosed = accountRegistrationService
					.requestAsyncUserAccountDeletion(eventConstruction.getUserId());
			userAccountDeleted.complete(accountClosed);
		}).start();
	}

	@Then("the account is registered")
	public void theAccountIsRegistered() {
		assertNotNull(registeredUser.join());
	}

	@Then("the account information is returned")
	public void theAccountInformationIsReturned() {
		assertNotNull(userAccountId.join());
	}

	@Then("the account is closed")
	public void theAccountIsClosed() {
		assertTrue(userAccountDeleted.join());
	}
}
