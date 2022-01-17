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
			default:
				break;
		}
	}

	@When("the {string} event is received")
	public void theEventIsReceived(String eventName) {
		handleEventReceived(eventName);
	}

	@Then("the {string} event is sent")
	public void theEventIsSent(String eventName) {
		Object eventObject = getEventObject(eventName);
		var event = new Event(eventName, new Object[] { eventObject });
		verify(queue).publish(event);
	}

	@Then("the account is registered")
	public void theAccountIsRegistered() {
		assertFalse(accountService.getUsers().isEmpty());
	}
}