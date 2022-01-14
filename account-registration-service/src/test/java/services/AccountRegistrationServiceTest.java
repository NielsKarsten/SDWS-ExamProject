package services;

import java.util.UUID;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import accountregistration.service.AccountRegistrationService;
import accountregistration.service.User;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccountRegistrationServiceTest {
	private MessageQueue queue;
	private AccountRegistrationService accountRegistrationService;
	private CompletableFuture<UUID> registeredUser;
	private EventConstruction eventConstruction;
	private CompletableFuture<Event> publishedEvent = new CompletableFuture<>();
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

		accountRegistrationService = new AccountRegistrationService(queue);
		registeredUser = new CompletableFuture<>();
		eventConstruction = new EventConstruction(accountRegistrationService);
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

	@Then("the account is registered")
	public void theAccountIsRegistered() {
		assertNotNull(registeredUser.join());
	}

	@Then("the account information is returned")
	public void theAccountInformationIsReturned() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}
}
