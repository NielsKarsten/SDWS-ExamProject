// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

package token;

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
import services.TokenRestService;
import messaging.Event;
import messaging.MessageQueue;


public class TokenRestServiceSteps {

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
    private TokenRestService service = new TokenRestService(q);
    private CompletableFuture<List<UUID>> issuedTokens = new CompletableFuture<>();
    private UUID customerId;
    private int NTokens;
    private UUID correlationId;

    public TokenRestServiceSteps() {
    }
    @Given ("there is a customer with id {string}")
    public void givenCustomer(String id){
        customerId = UUID.fromString(id);
    }
    @When ("the customer requests {int} tokens")
    public void customerRequestsTokens(int tokenAmount){
        NTokens=tokenAmount;
        new Thread(() -> {
            var result = service.issueTokens(customerId,tokenAmount);
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
        var tokens = new ArrayList<UUID>();
        for(int i = 0; i<3; i++){
            tokens.add(UUID.randomUUID());
        }
        String tokenString = gson.toJson(tokens);
        service.handleTokensIssued(new Event(correlationId,issueEvent,new Object[] {tokenString}));
    }
    @Then ("the customer has received {int} tokens")
    public void customerReceivedtokens(int tokenAmount){
        assertEquals(tokenAmount,issuedTokens.join().size());
    }
}
