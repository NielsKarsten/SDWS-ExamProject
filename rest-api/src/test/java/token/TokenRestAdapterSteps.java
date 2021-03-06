package token;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import adapters.TokenRestAdapter;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import models.TokenRequest;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Gustav Lintrup Kirkholt
 */
public class TokenRestAdapterSteps {
	private TokenRestAdapter service;
    private CompletableFuture<Event> publishedEvent = new CompletableFuture<>();
    private CompletableFuture<Object> issuedTokens = new CompletableFuture<>();
    private CompletableFuture<Boolean> errorRecieved = new CompletableFuture<>();
    
    private UUID customerId;
    private UUID correlationId;
    private TokenRequest tokenRequest;
    public TokenRestAdapterSteps() {
    }

    @Before
    public void setUp() {
        MessageQueue q = new MessageQueue() {

            @Override
            public void publish(Event event) {
                publishedEvent.complete(event);
            }

            @Override
            public void addHandler(String eventType, Consumer<Event> handler) {
            }

        };
        service = new TokenRestAdapter(q);
    }
    @Given ("there is a customer with id")
    public void givenCustomer(){
        customerId = UUID.randomUUID();
    }

    @When ("the customer requests {int} tokens")
    public void customerRequestsTokens(int tokenAmount){
        tokenRequest = new TokenRequest(customerId, tokenAmount);
        new Thread(() -> {
        	try {
                var result = service.issueTokens(tokenRequest);        		
                issuedTokens.complete(result);
                errorRecieved.complete(false);
        	}
        	catch(Exception e){
        		errorRecieved.complete(true);
    		}
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
	    	case "TokenRequestInvalid":
	    		obj = new NullPointerException("");
	    		break;
	    	default:
	    		break;
    	}
    	return obj;
    }
    
    private void eventHandler(String eventName) {
    	Object eventObject = createEventObject(eventName);
    	Event event = new Event(correlationId,eventName,new Object[] {eventObject});
    	switch(eventName) {
	    	case "TokensIssued":
	    		service.genericHandler(event);
	    		break;
	    	case "TokenRequestInvalid":
	    		service.genericErrorHandler(event);
	    		break;
	    	default:
	    		break;
	    	}
    }
    
    @Then ("the {string} event is sent from TokenRestService")
    public void requestEvent(String eventName){
        Event pEvent = publishedEvent.join();
        correlationId = pEvent.getCorrelationId();
        Event event = new Event(correlationId,eventName, new Object[] { createEventObject(eventName) });
        assertEquals(event.getType(),pEvent.getType());
    }
    
    @When ("the {string} token event is received")
    public void issueEvent(String eventName){
    	eventHandler(eventName);
    }
    
    @Then ("the customer has received {int} tokens")
    public void customerReceivedtokens(int tokenAmount){
        assertEquals(tokenAmount,((List<UUID>) issuedTokens.join()).size());
    }
    
    @Then ("the customer has received an error")
    public void customerReceivedtokens(){
        assertTrue(errorRecieved.join());
    }
}
