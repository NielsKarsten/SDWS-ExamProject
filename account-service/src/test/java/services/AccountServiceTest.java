package services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import account.service.AccountService;
import account.service.User;
import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;

public class AccountServiceTest {
	
	MessageQueue queue = mock(MessageQueue.class);
	AccountService accountService = new AccountService(queue);
	User user;

	@Given("a customer {string} {string} with bank account {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName, String accountId) {
		user = new User(firstName,lastName,accountId);
	}

	@When("the {string} event is received")
	public void theEventIsReceived(String eventName) {
		Event event = new Event(eventName, new Object[] {user});
		accountService.handleUserAccountRegistration(event);
	}

	@Then("the {string} event is sent")
	public void theEventIsSent(String eventName) {
		UUID userId = accountService.getUsers().keySet().iterator().next();
		var event = new Event(eventName, new Object[] {userId});
		verify(queue).publish(event);
	}
	
	@Then("the account is registered")
	public void theAccountIsRegistered() {
		assertFalse(accountService.getUsers().isEmpty());
	}

}
