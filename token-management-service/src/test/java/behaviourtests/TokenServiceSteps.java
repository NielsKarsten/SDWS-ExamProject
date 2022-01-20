package behaviourtests;

import static org.junit.Assert.assertEquals;

// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.junit.After;

import java.util.function.Consumer;

import tokenmanagement.service.EventType;
import tokenmanagement.service.TokenRequest;
import tokenmanagement.service.TokenService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TokenServiceSteps {
	private MessageQueue queue;
	private TokenService tokenService;

	//Customer
	private UUID customerId;	
	private UUID token;
	
	//Event
	private UUID correlationId;
	private CompletableFuture<Event> publishedEvent;
	private List<UUID> tokens;
	private TokenRequest tokenRequest;
	private IllegalArgumentException exception;
	private UUID TokenOwner;
	
	@Before
	public void setUp() {
		System.out.println("BEFORE!");
		queue = new MessageQueue() {

			@Override
			public void publish(Event message) {
				publishedEvent.complete(message);
			}

			@Override
			public void addHandler(String eventType, Consumer<Event> handler) {
			}
		};

		tokenService = new TokenService(queue);
		tokens = new ArrayList<>();
		publishedEvent = new CompletableFuture<>();
	}
	@Given("a customer")
	public void costumerWithID() {
		customerId = UUID.randomUUID();
	}

	@When("{int} tokens are requested")
	public void costumerRequestsNewTokens(int tokenAmount) {
		tokenRequest = new TokenRequest(customerId, tokenAmount);
	}
	
	public Object createEventObject(String eventName) {
		Object obj = null;
		switch(eventName) {
			case EventType.TOKENS_REQUESTED:
				obj = tokenRequest;
				break;
			case EventType.TOKENS_ISSUED:
				obj = new ArrayList<UUID>().add(token);
			case EventType.TOKEN_TO_CUSTOMER_ID_REQUESTED:
				obj = tokens.get(0);
				break;
			case EventType.TOKEN_TO_CUSTOMER_ID_RESPONSE:
				obj = customerId;
				break;
			default:
				System.out.println("No event object for event: " + eventName);
				break;
		}
		return obj;
	}
	
	public void handleEventRecieved(String eventName) {
		Object eventObject = createEventObject(eventName);
		Event event = new Event(correlationId, eventName ,new Object[] {eventObject});
		switch(eventName) {
			case EventType.TOKENS_REQUESTED:
				tokenService.handleTokensRequested(event);
				break;
			case EventType.TOKEN_TO_CUSTOMER_ID_REQUESTED:
				tokenService.handleTokenToCustomerIdRequested(event);
				break;
			default:
				System.out.println("No event recieved handler for event: " + eventName);
				break;
		}
	}

	@When ("the {string} event is received")
	public void customerIdFromTokenEvent(String eventName){
		handleEventRecieved(eventName);
	}

	@When ("the {string} event is sent")
	public void customerIdFromTokenEventResponse(String eventName){
		Event pEvent = publishedEvent.join();
		if (pEvent.getType().contains("Invalid"))
			exception = pEvent.getArgument(0, IllegalArgumentException.class);	
		else if (pEvent.getType().contains("Id"))
			TokenOwner = pEvent.getArgument(0, UUID.class);
		else
			tokens = (List<UUID>) pEvent.getArgument(0, Object.class);
		
		assertEquals(eventName, pEvent.getType());
		publishedEvent = new CompletableFuture<>();
	}

	@Then("customer recieved {int} tokens")
	public void tokensAreGenerated(int tokenAmount) {
		assertEquals(tokenAmount, tokens.size());
	}
	
	@Then("An exception is thrown")
	public void anExceptionIsThrown() {
	    assertNotNull(exception);
	}
	
	@Then("the id matches the customer")
	public void theIdMatchesTheCustomer() {
	    assertEquals(customerId, TokenOwner);
	}
}
