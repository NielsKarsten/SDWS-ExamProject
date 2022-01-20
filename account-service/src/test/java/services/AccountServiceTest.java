package services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import account.service.models.User;
import account.service.services.AccountService;
import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;

public class AccountServiceTest {

	MessageQueue queue = mock(MessageQueue.class);
	AccountService accountService = new AccountService(queue);
	UUID correlationId = UUID.randomUUID();
	UUID userId;
	User user;

	@Given("a user {string} {string} with bank account {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName, String accountId) {
		user = new User(firstName, lastName, accountId);
	}

	private Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case "AccountRegistrationRequested":
				obj = user;
				break;
			case "UserAccountRegistered":
				userId = accountService.getUsers().keySet().iterator().next();
				obj = userId;
				break;
			case "UserAccountInfoRequested":
				obj = userId;
				break;
			case "UserAccountInfoResponse":
				if (userId == null) {
					obj = null;
				} else {
					obj = user.getAccountId();
				}
				break;
			case "AccountClosedRequested":
				obj = userId;
				break;
			case "AccountClosedResponse":
				if (userId == null) {
					obj = false;
				} else {
					obj = true;
				}
				break;
			case "UserAccountInvalid":
				obj = false;
				break;
			case "VerifyUserAccountExistsRequest":
				obj = userId;
				break;
			case "VerifyUserAccountExistsResponse":
				obj = true;
				break;
			case "RetireUserAccountTokensRequest":
				obj = userId;
				break;
			case "ClosedUserAccountTokensRetired":
				obj = true;
				break;
			case "AccountClosedRetireTokenRequestInvalid":
				obj = new Exception();
				break;
			default:
				System.out.println("No event object found for " + eventName);
				obj = null;
				break;
		}
		return obj;
	}

	private void handleEventReceived(String eventName) {
		Object eventObject = getEventObject(eventName);
		Event event = new Event(eventName, new Object[] { eventObject });
		switch (eventName) {
			case "AccountRegistrationRequested":
				accountService.handleUserAccountRegistration(event);
				break;
			case "UserAccountInfoRequested":
				accountService.handleUserAccountInfoRequested(event);
				break;
			case "AccountClosedRequested":
				accountService.handleUserAccountClosedRequested(event);
				break;
			case "VerifyUserAccountExistsRequest":
				accountService.handleVerifyUserAccountExistsRequest(event);
				break;
			case "ClosedUserAccountTokensRetired":
				accountService.handleRetireUserAccountTokensResponse(event);
				break;
			case "AccountClosedRetireTokenRequestInvalid":
				accountService.handleRetireUserAccountTokensError(event);
				break;
			default:
				break;
		}
	}

	@When("the {string} event is received")
	public void theEventIsReceived(String eventName) {
		if (eventName.equals("AccountClosedRequested")) {
			new Thread(() -> handleEventReceived(eventName)).start();
		} else {
			handleEventReceived(eventName);
		}
	}

	@When("the {string} event is received with no user")
	public void theEventIsReceivedWithNoUser(String eventName) {
		userId = UUID.randomUUID();
		handleEventReceived(eventName);
	}

	@Then("the {string} event is sent")
	public void theEventIsSent(String eventName) throws InterruptedException {
		// We sleep here to ensure that other events have been posted to the queue
		// basically simulating the delay that interacting with another service has
		System.out.println("Sleeping here to avoid deadlocks...");
		Thread.sleep(1000);

		Object eventObject = getEventObject(eventName);
		var event = new Event(correlationId, eventName, new Object[] { eventObject });
		verify(queue).publish(event);
	}

	@Then("the account is registered")
	public void theAccountIsRegistered() {
		assertFalse(accountService.getUsers().isEmpty());
	}
}