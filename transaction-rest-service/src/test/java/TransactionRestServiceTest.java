import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import messaging.MessageQueue;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;

@RunWith(Cucumber.class)
@CucumberOptions(plugin="summary"
        , publish= false
        , features = "features"  // directory of the feature files
        , snippets = SnippetType.CAMELCASE
)

public class TransactionRestServiceTest {

    MessageQueue queue = mock(MessageQueue.class);


    @When("a {string} event is received")
    public void a_event_is_received(String string) {

    }

    @Then("a {string} event is sent")
    public void a_event_is_sent(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("the transaction response has status successful")
    public void the_transaction_response_has_status_successful() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
