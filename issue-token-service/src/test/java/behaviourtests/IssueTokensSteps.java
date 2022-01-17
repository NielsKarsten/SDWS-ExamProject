package behaviourtests;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.gson.Gson;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import issuetoken.service.IssueTokenService;
import issuetoken.service.Token;
import messaging.Event;
import messaging.MessageQueue;


public class IssueTokensSteps {

    private CompletableFuture<Event> publishedEvent = new CompletableFuture<>();

    private MessageQueue q = new MessageQueue() {

        @Override
        public void publish(Event event) {
            publishedEvent.complete(event);
        }

        @Override
        public void addHandler(String eventType, Consumer<Event> handler) {
        }

    };
    private IssueTokenService service = new IssueTokenService(q);
    private CompletableFuture<List<Token>> issuedTokens = new CompletableFuture<>();
    private String customerId;
    private int NTokens;
    private UUID correlationId;

    public IssueTokensSteps() {
    }
    @Given ("there is a customer with id {string}")
    public void givenCustomer(String id){
        customerId = id;
    }
    @When ("the customer requests {int} tokens")
    public void customerRequestsTokens(int tokenAmount){

        NTokens=tokenAmount;
        new Thread(() -> {
            var result = service.issue(customerId,tokenAmount);
            issuedTokens.complete(result);
        }).start();
    }
    @Then ("the {string} event is sent from issuetoken")
    public void requestEvent(String requestEvent){
        Event pEvent = publishedEvent.join();
        correlationId = pEvent.getCorrelationId();
        Event event = new Event(correlationId,requestEvent, new Object[] { customerId,NTokens });
        assertEquals(event,pEvent);
    }
    @When ("the {string} event is sent")
    public void issueEvent(String issueEvent){
        Gson gson= new Gson();
        var tokens = new ArrayList<Token>();
        for(int i = 0; i<3; i++){
            tokens.add(new Token());
        }
        String tokenString = gson.toJson(tokens);
        service.handleTokensIssued(new Event(correlationId,"..",new Object[] {tokenString}));
    }
    @Then ("the customer has received {int} tokens")
    public void customerReceivedtokens(int tokenAmount){
        assertEquals(tokenAmount,issuedTokens.join().size());
    }
  // @Given("there is a student with empty id")
  // public void thereIsAStudentWithEmptyId() {
  //     student = new Student();
  //     student.setName("James");
  //     assertNull(student.getId());
  // }

  // @When("the student is being registered")
  // public void theStudentIsBeingRegistered() {
  //     // We have to run the registration in a thread, because
  //     // the register method will only finish after the next @When
  //     // step is executed.
  //     new Thread(() -> {
  //         var result = service.register(student);
  //         registeredStudent.complete(result);
  //     }).start();
  // }

  // @Then("the {string} event is sent")
  // public void theEventIsSent(String string) {
  //     Event event = new Event(string, new Object[] { student });
  //     assertEquals(event,publishedEvent.join());
  // }

  // @When("the {string} event is sent with non-empty id")
  // public void theEventIsSentWithNonEmptyId(String string) {
  //     // This step simulate the event created by a downstream service.
  //     var c = new Student();
  //     c.setName(student.getName());
  //     c.setId("123");
  //     service.handleStudentIdAssigned(new Event("..",new Object[] {c}));
  // }

  // @Then("the student is registered and his id is set")
  // public void theStudentIsRegisteredAndHisIdIsSet() {
  //     // Our logic is very simple at the moment; we don't
  //     // remember that the student is registered.
  //     assertNotNull(registeredStudent.join().getId());
  // }
}
