package behaviourtests;

// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import com.google.gson.Gson;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import org.junit.After;
import tokenmanagement.service.Token;
import tokenmanagement.service.exceptions.TokenException;
import tokenmanagement.service.TokenManagementService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenServiceSteps {
	private MessageQueue q = mock(MessageQueue.class);
	private TokenManagementService tokenService = new TokenManagementService(q);

	private UUID _customerId;
	private Exception exception;
	private UUID result;
	private Token tokenResult;
	private Token expectedToken;
	private List<Token> tokenList;
	private UUID correlationId;

	public TokenServiceSteps() {
	}

	@Given("customer id {string}")
	public void costumerWithID(String customerId) {
		_customerId = UUID.fromString(customerId);
	}

	@Given("has {int} tokens")
	public void hasTokens(int tokenAmount) throws TokenException {
		tokenList = tokenService.requestTokens(_customerId, tokenAmount);
		tokenResult = tokenList.get(0);
	}

	@When("{int} tokens are requested")
	public void costumerRequestsNewTokens(int tokenAmount) throws TokenException {
		tokenList = tokenService.requestTokens(_customerId, tokenAmount);
	}

	@When("{int} tokens are requested then customer has too many tokens")
	public void costumerHasTooManyTokensWhenRequesting(int tokenAmount) {
		exception = assertThrows(TokenException.class, () -> {
			tokenService.requestTokens(_customerId, tokenAmount);
		});
	}

	@When("{int} tokens are requested then token limit exceeds")
	public void requestingTokensExceedsLimit(int tokenAmount) {
		exception = assertThrows(TokenException.class, () -> {
			tokenService.requestTokens(_customerId, tokenAmount);
		});
	}

	@When("get customer id from token")
	public void getCustomerIdFromToken() throws TokenException {
		result = tokenService.findCustomerId(tokenResult);
	}

	@When("get customer id from token then invalid token")
	public void getCustomerIdFromTokenInvalid() throws TokenException {
		exception = assertThrows(TokenException.class, () -> {
			result = tokenService.findCustomerId(tokenResult);
		});
	}
	@When("customer consumes token with expected")
	public void customerConsumesTokenExpected() throws TokenException {
		tokenService.findCustomerId(tokenList.get(0));
	}

	@When("customer consumes token")
	public void customerConsumesToken() {
		exception = assertThrows(TokenException.class, () -> {
			tokenService.consumeCustomerToken(_customerId);
		});
	}

	//@When ("the {string} event is received")
	//public void customerIdFromTokenEvent(String issueEvent){
	//	Token token = tokenService.findCustomersTokens(_customerId).get(0);
	//	UUID tokenID = token.getToken();
	//	tokenService.handleTokenToCustomerIdRequested(new Event(correlationId,issueEvent,new Object[] {tokenID}));
	//}

	@Then("customer id {string} is returned")
	public void customerIdReturned(String testingId) {
		assertEquals(UUID.fromString(testingId), result);
	}

	@Then("customer has {int} tokens")
	public void tokensAreGenerated(int tokenAmount) {
		assertEquals(tokenAmount, tokenList.size());
	}

	@Then("exception {string} is returned")
	public void tooManyTokensAreGenerated(String errorMsg) {
		assertEquals(errorMsg, exception.getMessage());
	}

	@After
	public void deleteTokens() {
		tokenService = new TokenManagementService(q);
	}
	//@Given("there is a student with empty id")
	//public void thereIsAStudentWithEmptyId() {
	//	student = new Student();
	//	student.setName("James");
	//	assertNull(student.getId());
	//}
//
	//@When("the student is being registered")
	//public void theStudentIsBeingRegistered() {
	//	// We have to run the registration in a thread, because
	//	// the register method will only finish after the next @When
	//	// step is executed.
	//	new Thread(() -> {
	//		var result = service.register(student);
	//		registeredStudent.complete(result);
	//	}).start();
	//}
//
	//@Then("the {string} event is sent")
	//public void theEventIsSent(String string) {
	//	Event event = new Event(string, new Object[] { student });
	//	verify(q).publish(event);
	//}
//
	//@When("the {string} event is sent with non-empty id")
	//public void theEventIsSentWithNonEmptyId(String string) {
	//	// This step simulate the event created by a downstream service.
	//	var c = new Student();
	//	c.setName(student.getName());
	//	c.setId("123");
	//	service.handleStudentIdAssigned(new Event("..",new Object[] {c}));
	//}
//
	//@Then("the student is registered and his id is set")
	//public void theStudentIsRegisteredAndHisIdIsSet() {
	//	// Our logic is very simple at the moment; we don't
	//	// remember that the student is registered.
	//	assertNotNull(registeredStudent.join().getId());
	//}
}
