import dtu.ws.fastmoney.Account;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import messaging.Event;
import messaging.MessageQueue;
import services.TransactionRestService;
import models.TransactionRequest;
import models.TransactionRequestResponse;
import org.junit.runner.RunWith;
import endpoints.TransactionResource;
import models.User;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Cucumber.class)
@CucumberOptions(plugin="summary"
        , publish= false
        , features = "features"  // directory of the feature files
        , snippets = SnippetType.CAMELCASE
)

public class TransactionRestServiceTest {

    MessageQueue queue = mock(MessageQueue.class);
    TransactionRestService transactionRestService = new TransactionRestService(queue);

    User merchant;
    User customer;
    int amount;
    String description;
    boolean success;
    TransactionRequestResponse expected;

    @When("a {string} event is sent")
    public void a_event_is_sent(String eventName) {

        merchant = new User(UUID.randomUUID(), new Account());
        Account customerAccount = new Account();
        customerAccount.setBalance(BigDecimal.valueOf(1000));
        customer = new User(UUID.randomUUID(), customerAccount);
        amount = 100;

        TransactionRequest trxReq = new TransactionRequest(merchant.getId().toString(), customer.getId().toString(), BigDecimal.valueOf(amount));
        transactionRestService.handleTransactionRequestResponse(new Event(eventName, new Object[] {trxReq}));

    }

    @Then("a {string} event is received")
    public void a_event_is_received(String eventName) {

        expected = new TransactionRequestResponse(true);
        verify(queue).publish(new Event(eventName, new Object[]{expected}));


    }

    @And("the transaction response has status successful")
    public void the_transaction_response_has_status_successful() {
    }

}
