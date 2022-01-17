import dtu.ws.fastmoney.Account;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import models.TransactionRequest;
import models.TransactionRequestResponse;
import models.User;
import services.TransactionRestService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionRestServiceSteps {

    MessageQueue queue = mock(MessageQueue.class);
    TransactionRestService transactionRestService = new TransactionRestService(queue);

    UUID correlationId;
    User merchant;
    User customer;
    int amount;
    String description;
    boolean success;
    TransactionRequest trxReq;
    TransactionRequestResponse expected;

    @Then("a {string} event is sent")
    public void a_event_is_sent(String eventName) {
        Event e = new Event(eventName, new Object[]{trxReq});
        verify(queue).publish(e);
    }

    @And("a {string} event is received")
    public void a_event_is_received(String eventName) {
        expected = new TransactionRequestResponse(true);
        transactionRestService.handleTransactionRequestResponse(new Event(correlationId, eventName, new Object[]{expected}));
    }

    @And("the transaction response has status successful")
    public void the_transaction_response_has_status_successful() {
        assertTrue(expected.isSuccessful());
    }

    @Given("a transaction request")
    public void aTransactionRequest() {
        correlationId = UUID.randomUUID();
        merchant = new User(UUID.randomUUID(), new Account());
        Account customerAccount = new Account();
        customerAccount.setBalance(BigDecimal.valueOf(1000));
        customer = new User(UUID.randomUUID(), customerAccount);
        amount = 100;

        trxReq = new TransactionRequest(merchant.getId().toString(), customer.getId().toString(), BigDecimal.valueOf(amount));
    }

    @When("the transaction request is being registered")
    public void theTransactionRequestIsBeingRegistered() {
        new Thread(() -> transactionRestService.createTransactionRequest(correlationId, trxReq)).start();
    }
}
