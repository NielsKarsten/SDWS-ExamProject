import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import messaging.Event;
import messaging.MessageQueue;
import models.TransactionRestRequest;
import models.TransactionRestRequestResponse;
import org.junit.runner.RunWith;
import services.TransactionResource;

import java.math.BigDecimal;

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
    TransactionRestRequestResponse expected;
    // TransactionResource transactionResource = new TransactionResource()

    @When("a {string} event is sent")
    public void a_event_is_received(String eventName) {
        expected = new TransactionRestRequestResponse(true);
        verify(queue).publish(new Event(eventName, new Object[]{expected}));
    }

    @Then("a {string} event is received")
    public void a_event_is_sent(String eventName) {
      //  TransactionRestRequest trxReq = new TransactionRestRequest(merchant.getId().toString(), customer.getId().toString(), BigDecimal.valueOf(amount));
      //  transactionService.handleTransactionRequestEvent(new Event(eventName, new Object[] {trxReq}));
    }

    @And("the transaction response has status successful")
    public void the_transaction_response_has_status_successful() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
