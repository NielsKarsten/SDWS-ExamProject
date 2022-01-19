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
import models.TokenRequest;


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
    private CompletableFuture<Object> issuedTokens = new CompletableFuture<>();
    private UUID customerId;
    private int NTokens;
    private UUID correlationId;
    private TokenRequest tokenRequest;

    public TokenRestServiceSteps() {
    }
    @Given ("there is a customer with id")
    public void givenCustomer(){
        customerId = UUID.randomUUID();
    }

    @When ("the customer requests {int} tokens")
    public void customerRequestsTokens(int tokenAmount){
        tokenRequest = new TokenRequest(customerId, tokenAmount);
        new Thread(() -> {
            var result = service.issueTokens(tokenRequest);
            issuedTokens.complete(result);
        }).start();
    }
    
    public Object createEventObject(String eventName) {
    	Object obj = null;
    	switch(eventName) {
	    	case "TokensRequested":
	    		obj = tokenRequest;
	    		break;
	    	case "TokensIssued":
		        List<UUID> tokens = new ArrayList<UUID>();
		        for(int i = 0; i<3; i++){
		            tokens.add(UUID.randomUUID());
		        }
		        obj = tokens;
		        break;
	    	case "invalidTokenAmountRequested":
	    		obj = "Error: Invalid token amount - you can only request between 1 and 5 tokens at a time";
	    		break;
	    	default:
	    		System.out.println("No object for event: " + eventName);
	    		break;
    	}
    	return obj;
    }
    
    private void eventHandler(String eventName) {
    	Object eventObject = createEventObject(eventName);
    	Event event = new Event(correlationId,eventName,new Object[] {eventObject});
    	switch(eventName) {
	    	case "TokensIssued":
	    		service.handleTokensIssued(event);
	    		break;
	    	case "invalidTokenAmountRequested":
	    		service.handleTokenRequestError(event);
	    		break;
	    	default:
	    		System.out.println("No event handler for event: " + eventName);
	    		break;
	    	}
    }
    
    @Then ("the {string} event is sent from issuetoken")
    public void requestEvent(String eventName){
        Event pEvent = publishedEvent.join();
        correlationId = pEvent.getCorrelationId();
        Event event = new Event(correlationId,eventName, new Object[] { createEventObject(eventName) });
        assertEquals(event,pEvent);
    }
    
    @When ("the {string} token event is sent")
    public void issueEvent(String eventName){
    	eventHandler(eventName);
    }
    
    @Then ("the customer has received {int} tokens")
    public void customerReceivedtokens(int tokenAmount){
        assertEquals(tokenAmount,((List<UUID>) issuedTokens.join()).size());
    }
    
    @Then ("the customer has received an error {string}")
    public void customerReceivedtokens(String errorMessage){
        assertEquals(errorMessage, issuedTokens.join());
    }
}
