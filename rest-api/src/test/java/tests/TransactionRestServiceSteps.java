package tests;

import dtu.ws.fastmoney.Account;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import transaction.service.models.*;
import services.AccountRestService;
import services.TransactionRestService;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRestServiceSteps {

    MessageQueue queue;;
    CompletableFuture<Event> publishedEvent;
    TransactionRestService transactionRestService;

    UUID correlationId;
    User merchant;
    User customer;
    int amount;
    String description;
    boolean success;
    TransactionRequest trxReq;
    TransactionRequestResponse expected;
    CompletableFuture<TransactionRequestResponse> actual;

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
		
		transactionRestService = new TransactionRestService(queue);
		publishedEvent = new CompletableFuture<Event>();
		actual = new CompletableFuture<>();
	}
	
    @Given("a transaction request")
    public void aTransactionRequest() {
        merchant = new User(UUID.randomUUID(), new Account());
        Account customerAccount = new Account();
        customerAccount.setBalance(BigDecimal.valueOf(1000));
        customer = new User(UUID.randomUUID(), customerAccount);
        amount = 100;

        trxReq = new TransactionRequest(merchant.getId().toString(), customer.getId().toString(), BigDecimal.valueOf(amount));
    }
    
    @When("the transaction request is being registered")
    public void theTransactionRequestIsBeingRegistered() {
        new Thread(() -> {
        	TransactionRequestResponse result = transactionRestService.createTransactionRequest(trxReq);
        	actual.complete(result);
        }).start();
    }
    
    @Then("a {string} event is sent")
    public void a_event_is_sent(String eventName) {
    	Event pEvent = publishedEvent.join();
    	correlationId = pEvent.getCorrelationId();
        Event event = new Event(correlationId, eventName, new Object[]{trxReq});
        assertEquals(event, pEvent);
    }
    
    @And("a {string} event is received")
    public void a_event_is_received(String eventName) {
        expected = new TransactionRequestResponse(true);
        transactionRestService.handleTransactionRequestResponse(new Event(correlationId, eventName, new Object[]{expected}));
    }

    @And("the transaction response has status successful")
    public void the_transaction_response_has_status_successful() {
    	TransactionRequestResponse receivedResponse = actual.join();
    	assertEquals(expected, receivedResponse);
        assertTrue(receivedResponse.isSuccessful());
    }


}
