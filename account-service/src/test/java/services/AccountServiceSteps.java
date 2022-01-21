package services;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import account.service.models.User;
import account.service.services.AccountService;
import handling.AccountEventType;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import messaging.Event;
import messaging.MessageQueue;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Krikholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Thomas Rathsach Strange
 */
public class AccountServiceSteps {
	MessageQueue queue;
	private CompletableFuture<Event> publishedEvent;
	AccountService accountService;
	UUID correlationId = UUID.randomUUID();
	UUID tokenCorrelationId;
	UUID userId;
	User user;

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
		accountService = new AccountService(queue);
		publishedEvent = new CompletableFuture<>();
		userId = null;
		
	}
	@Given("a user {string} {string} with bank account {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName, String accountId) {
		user = new User(firstName, lastName, accountId);
	}

	private Object getEventObject(String eventName) {
		Object obj = null;
		switch (eventName) {
			case AccountEventType.ACCOUNT_REGISTRATION_REQUESTED:
				obj = user;
				break;
			case AccountEventType.USER_ACCOUNT_REGISTERED:
				userId = accountService.getUsers().keySet().iterator().next();
				obj = userId;
				break;
			case AccountEventType.USER_ACCOUNT_INFO_REQUESTED:
				obj = userId;
				break;
			case AccountEventType.USER_ACCOUNT_INFO_RESPONSE:
				if (userId == null) {
					obj = null;
				} else {
					obj = user.getAccountId();
				}
				break;
			case AccountEventType.ACCOUNT_CLOSED_REQUESTED:
				obj = userId;
				break;
			case AccountEventType.ACCOUNT_CLOSED_RESPONSE:
				if (userId == null) {
					obj = false;
				} else {
					obj = true;
				}
				break;
			case AccountEventType.USER_ACCOUNT_INVALID:
				obj = false;
				break;
			case AccountEventType.VERIFY_USER_ACCOUNT_EXISTS_REQUESTS:
				obj = userId;
				break;
			case AccountEventType.VERIFY_USER_ACCOUNT_EXISTS_RESPONSE:
				obj = true;
				break;
			case AccountEventType.RETIRE_USER_ACCOUNT_TOKENS_REQUEST:
				obj = userId;
				break;
			case AccountEventType.CLOSED_USER_ACCOUNT_TOKENS_RETIRED:
				obj = true;
				break;
			case AccountEventType.ACCOUNT_CLOSED_RETIRE_TOKEN_REQUEST_INVALID:
				obj = new NullPointerException("No tokens to retire");
				break;
			default:
				obj = null;
				break;
		}
		return obj;
	}

	private void handleEventReceived(String eventName) {
		Object eventObject = getEventObject(eventName);
		Event event = new Event(correlationId, eventName, new Object[] { eventObject });
		switch (eventName) {
			case AccountEventType.ACCOUNT_REGISTRATION_REQUESTED:
				accountService.handleUserAccountRegistrationRequested(event);
				break;
			case AccountEventType.USER_ACCOUNT_INFO_REQUESTED:
				accountService.handleUserAccountInfoRequested(event);
				break;
			case AccountEventType.ACCOUNT_CLOSED_REQUESTED:
				accountService.handleUserAccountClosedRequested(event);
				break;
			case AccountEventType.VERIFY_USER_ACCOUNT_EXISTS_REQUESTS:
				accountService.handleVerifyUserAccountExistsRequest(event);
				break;
			case AccountEventType.CLOSED_USER_ACCOUNT_TOKENS_RETIRED:
				event = new Event(tokenCorrelationId, eventName, new Object[] { eventObject });
				accountService.genericHandler(event);
				break;
			case AccountEventType.ACCOUNT_CLOSED_RETIRE_TOKEN_REQUEST_INVALID:
				event = new Event(tokenCorrelationId, eventName, new Object[] { eventObject });
				accountService.genericErrorHandler(event);
				break;
			default:
				break;
		}
	}


	@When("the {string} event is received")
	public void theEventIsReceived(String eventName) {
		if (eventName.equals(AccountEventType.ACCOUNT_CLOSED_REQUESTED)) {
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
		Event pEvent = publishedEvent.join();
		if (eventName.equals(AccountEventType.USER_ACCOUNT_REGISTERED))
			userId = pEvent.getArgument(0, UUID.class);
		else if(eventName.equals(AccountEventType.RETIRE_USER_ACCOUNT_TOKENS_REQUEST))
			tokenCorrelationId = pEvent.getCorrelationId();
		assertEquals(eventName, pEvent.getType());
		publishedEvent = new CompletableFuture<>();
	}

	@Then("the account is registered")
	public void theAccountIsRegistered() {
		assertTrue(accountService.getUsers().containsKey(userId));
	}
}