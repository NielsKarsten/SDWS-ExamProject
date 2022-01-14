import dk.dtu.sdws.group3.connector.AccountServiceConnector;
import dk.dtu.sdws.group3.connector.TokenServiceConnector;
import dk.dtu.sdws.group3.models.TransactionRequest;
import dk.dtu.sdws.group3.models.TransactionRequestResponse;
import dk.dtu.sdws.group3.persistance.TransactionStore;
import dk.dtu.sdws.group3.services.TransactionService;
import dk.dtu.sdws.group3.models.Transaction;
import dk.dtu.sdws.group3.models.User;
import dtu.ws.fastmoney.Account;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;
import messaging.Event;
import messaging.MessageQueue;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@RunWith(Cucumber.class)
@CucumberOptions(plugin="summary"
        , publish= false
        , features = "features"  // directory of the feature files
        , snippets = SnippetType.CAMELCASE
)

public class TransactionServiceTest {

    BankService bank = mock(BankService.class);
    MessageQueue queue = mock(MessageQueue.class);
    AccountServiceConnector accountServiceConnector = new MockAccountServiceConnector(queue);
    TokenServiceConnector tokenServiceConnector = new MockTokenServiceConnector(queue);
    TransactionService transactionService = new TransactionService(queue, bank, tokenServiceConnector, accountServiceConnector);

    User merchant;
    User customer;
    int amount;
    String description;
    boolean success;
    TransactionRequestResponse expected;

    @Given("a merchant with an account with a balance of {int}")
    public void a_merchant_with_merchant_id_and_a_account_with_balance_of(int balance) {
        merchant = new User();
        merchant.setId(UUID.randomUUID());
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(balance));
        merchant.setAccount(account);
    }

    @And("an amount of {int}")
    public void an_amount_of(int amount) {
        this.amount = amount;
    }

    @When("the transaction is initiated")
    public void the_transactions_is_initiated() {
        success = transactionService.pay(merchant, customer, BigDecimal.valueOf(amount));
    }

    @Then("the transaction is successful")
    public void the_transaction_is_successful() throws BankServiceException_Exception {
        assertTrue(success);
        description = "Payment of " + amount + " to merchant " + merchant.getAccount().getId();
        verify(bank).transferMoneyFromTo(merchant.getAccount().getId(), customer.getAccount().getId(), BigDecimal.valueOf(amount), description);
    }

    @And("a customer with an account with a balance of {int}")
    public void aCustomerWithAnAccountWithABalanceOf(int balance) {
        customer = new User();
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(balance));
        customer.setAccount(account);
    }

    @And("the transaction is saved")
    public void theTransactionIsSaved() {
        Transaction t = new Transaction(merchant, customer, BigDecimal.valueOf(amount), description);
        assertTrue(TransactionStore.getInstance().getTransactions().containsKey(customer.getId()));
    }

    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
        merchant = new User(UUID.randomUUID(), new Account());
        Account customerAccount = new Account();
        customerAccount.setBalance(BigDecimal.valueOf(1000));
        customer = new User(UUID.randomUUID(), customerAccount);
        amount = 100;

        TransactionRequest trxReq = new TransactionRequest(merchant.getId().toString(), customer.getId().toString(), BigDecimal.valueOf(amount));
        transactionService.handleTransactionRequestEvent(new Event(eventName, new Object[] {trxReq}));
    }

    @Then("a {string} event is sent")
    public void aEventIsSent(String eventName) {
        expected = new TransactionRequestResponse(true);
        verify(queue).publish(new Event(eventName, new Object[]{expected}));
    }

    @And("the transaction response has status successful")
    public void theTransactionResponseHasStatusSuccessful() {
        assertTrue(expected.isSuccessful());
    }
}
