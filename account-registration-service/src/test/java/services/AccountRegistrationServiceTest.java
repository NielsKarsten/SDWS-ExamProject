package services;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import accountregistration.service.AccountRegistrationService;
import accountregistration.service.User;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AccountRegistrationServiceTest {
	private MessageQueue queue = mock(MessageQueue.class);
	private AccountRegistrationService accountRegistrationService = new AccountRegistrationService(queue);
	private CompletableFuture<UUID> registeredUser = new CompletableFuture<>();
	private EventConstruction eventConstruction = new EventConstruction(accountRegistrationService);

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
			var result = accountRegistrationService.registerAsyncUserAccount(eventConstruction.getUser());
			registeredUser.complete(result);
		}).start();
	}

	@Then("the {string} event is sent")
	public void theEventIsSent(String eventName) {
		Event event = new Event(eventName, new Object[] { eventConstruction.getEventObject(eventName) });
		verify(queue).publish(event);
	}

	@When("the {string} event is received")
	public void theEventIsReceived(String eventName) {
		eventConstruction.handleEventReceived(eventName);
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
