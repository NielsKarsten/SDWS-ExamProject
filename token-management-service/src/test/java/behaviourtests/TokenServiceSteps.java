package behaviourtests;

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
import tokenmanagement.service.TokenRequest;
import tokenmanagement.service.TokenService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenServiceSteps {
	private MessageQueue q = mock(MessageQueue.class);
	private TokenService tokenService;

	private UUID customerId;
	
	private Exception exception;
	private UUID result;
	private UUID tokenResult;
	private UUID expectedToken;
	private List<UUID> tokens;
	private UUID correlationId;
	private TokenRequest tokenRequest;

	@Before
	public void setUp() {
		tokenService = new TokenService(q);
		tokens = new ArrayList<>();
	}
	@Given("a customer")
	public void costumerWithID() {
		customerId = UUID.randomUUID();
	}

	@Given("has {int} tokens")
	public void hasTokens(int tokenAmount) {
		tokenRequest = new TokenRequest(customerId,tokenAmount);
		tokens.addAll(tokenService.requestTokens(tokenRequest));
	}

	@When("{int} tokens are requested")
	public void costumerRequestsNewTokens(int tokenAmount) {
		tokenRequest = new TokenRequest(customerId,tokenAmount);
		tokens.addAll(tokenService.requestTokens(tokenRequest));
	}

	@When("{int} tokens are requested causing an exception")
	public void costumerHasTooManyTokensWhenRequesting(int tokenAmount) {
		tokenRequest = new TokenRequest(customerId,tokenAmount);
		exception = assertThrows(IllegalArgumentException.class, () -> {
			tokenService.requestTokens(tokenRequest);
		});
	}
	
	public Object createEventObject(String eventName) {
		Object obj = null;
		switch(eventName) {
			case "TokensRequested":
				obj = customerId;
				break;
			case "TokenToCustomerIdRequested":
				obj = tokens.get(0);
				break;
			case "TokenToCustomerIdResponse":
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
		Event event;
		if (eventName == "TokensRequested")
			event = new Event(correlationId, eventName ,new Object[] {customerId, 3});
		else
			event = new Event(correlationId, eventName ,new Object[] {eventObject});
		switch(eventName) {
			case "TokensRequested":
				tokenService.handleTokensRequested(event);
				break;
			case "TokenToCustomerIdRequested":
				tokenService.handleTokenToCustomerIdRequested(event);
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
		Event event = new Event(correlationId,eventName,new Object[] {createEventObject(eventName)});
		verify(q).publish(event);
	}

	@Then("customer recieved {int} tokens")
	public void tokensAreGenerated(int tokenAmount) {
		assertEquals(tokenAmount, tokens.size());
	}

	@Then("exception {string} is returned")
	public void tooManyTokensAreGenerated(String errorMsg) {
		assertEquals(errorMsg, exception.getMessage());
	}
}
