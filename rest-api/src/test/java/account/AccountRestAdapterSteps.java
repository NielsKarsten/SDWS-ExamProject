package account;

import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import adapters.AccountRestService;
import models.*;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;

import static org.junit.Assert.*;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Niels Bisgaard-Bohr
 */
public class AccountRestAdapterSteps {
	private MessageQueue queue;
	private AccountRestService accountRestService;
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

		accountRestService = new AccountRestService(queue);
		registeredUser = new CompletableFuture<>();
		userAccountId = new CompletableFuture<>();
		userAccountDeleted = new CompletableFuture<>();
		eventConstruction = new EventConstruction(accountRestService);
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
			UUID result = accountRestService.registerAsyncUserAccount(eventConstruction.getUser());
			registeredUser.complete(result);
		}).start();
	}

	@When("the user account id is requested")
	public void theUserAccountIdIsRequested() {
		new Thread(() -> {
			String accountId = accountRestService
					.requestAsyncUserAccountInfo(eventConstruction.getUserId());
			userAccountId.complete(accountId);
		}).start();
	}

	@Then("the {string} event is sent")
	public void theEventIsSent(String eventName) throws InterruptedException {
		Event pEvent = publishedEvent.join();
		correlationID = pEvent.getCorrelationId();
		Event event = new Event(correlationID, eventName, new Object[] { eventConstruction.getEventObject(eventName) });
		assertEquals(event.getType(), pEvent.getType());
		publishedEvent = new CompletableFuture<>();
	}

	@When("the {string} event is received")
	public void theEventIsReceived(String eventName) {
		eventConstruction.handleEventReceived(eventName, correlationID);
	}

	@When("the user account is closed")
	public void theUserAccountIsClosed() {
		new Thread(() -> {
			Boolean accountClosed = accountRestService
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
