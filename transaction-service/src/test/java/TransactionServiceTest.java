import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;


@RunWith(Cucumber.class)
@CucumberOptions(plugin="summary"
        , publish= false
        , features = "features"  // directory of the feature files
        , snippets = SnippetType.CAMELCASE
)

public class TransactionServiceTest {

    @Given("a merchant with merchant id {string} and a account with balance of {int}")
    public void a_merchant_with_merchant_id_and_a_account_with_balance_of(String string, Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Given("a merchant with no id")
    public void a_merchant_with_no_id() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("a customer with a valid token")
    public void a_customer_with_a_valid_token() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("a customer with a invalid token")
    public void a_customer_with_a_invalid_token() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @And("an amount of {int}")
    public void an_amount_of(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @When("the transactions is initiated")
    public void the_transactions_is_initiated() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


    @Then("the transaction is successful")
    public void the_transaction_is_successful() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("the transaction is unsuccessful")
    public void the_transaction_is_unsuccessful() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
