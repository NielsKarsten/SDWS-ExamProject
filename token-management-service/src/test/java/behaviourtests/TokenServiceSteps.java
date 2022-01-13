package behaviourtests;

// Authors:
// Main: Theodor Guttesen s185121
// Christian Gernsøe s163552

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.MessageQueue;
import studentregistration.service.TokenManagementService;

public class TokenServiceSteps {
	private MessageQueue q = mock(MessageQueue.class);
	private TokenManagementService tokenService = new TokenManagementService();

	private String _customerId;

	public TokenServiceSteps() {
	}

	@Given("customer id {string}")
	public void costumerWithID(String customerId) {
		_customerId = customerId;
	}

	@When("{int} tokens are requested")
	public void costumerRequestsNewTokens(int tokenAmount) {
		tokenService.generateTokens(_customerId, tokenAmount);
	}

	@Then("{int} tokens are generated")
	public void tokensAreGenerated(int tokenAmount) {
		assertEquals(tokenAmount, tokenService.findCustomersTokens(_customerId).size());
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
