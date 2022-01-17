import dtu.ws.fastmoney.Account;
import dk.dtu.sdws.group3.models.Transaction;
import dk.dtu.sdws.group3.models.TransactionRequest;
import dk.dtu.sdws.group3.models.TransactionRequestResponse;
import dk.dtu.sdws.group3.persistance.TransactionStore;
import dk.dtu.sdws.group3.services.TransactionService;
import dtu.ws.fastmoney.BankService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import messaging.Event;
import messaging.MessageQueue;
import transaction.service.connector.AccountServiceConnector;
import transaction.service.connector.TokenServiceConnector;
import transaction.service.models.Transaction;
import transaction.service.models.TransactionRequest;
import transaction.service.models.TransactionRequestResponse;
import transaction.service.models.User;
import transaction.service.persistance.TransactionStore;
import transaction.service.services.TransactionService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class TransactionServiceSteps {
    BankService bank = mock(BankService.class);
    MessageQueue queue = mock(MessageQueue.class);
    MockAccountServiceConnector accountServiceConnector = new MockAccountServiceConnector(queue);
    MockTokenServiceConnector tokenServiceConnector = new MockTokenServiceConnector(queue);
    TransactionService transactionService = new TransactionService(queue, bank, tokenServiceConnector, accountServiceConnector);

    int amount;
    String description;
    boolean success;
    UUID merchantId;
    UUID customerId;
    UUID merchantBankId;
    UUID customerBankId;
    TransactionRequestResponse expected;

    @Given("a merchant with an account with a balance of {int}")
    public void a_merchant_with_merchant_id_and_a_account_with_balance_of(int balance) {
        merchantId = UUID.randomUUID();
        merchantBankId = UUID.randomUUID();
        accountServiceConnector.addUser(merchantId, merchantBankId.toString());
    }

    @And("an amount of {int}")
    public void an_amount_of(int amount) {
        this.amount = amount;
    }

    @When("the transaction is initiated")
    public void the_transactions_is_initiated() {
        success = transactionService.pay(customerId, merchantId, BigDecimal.valueOf(amount)).isSuccessful();
    }

    @Then("the transaction is successful")
    public void the_transaction_is_successful() {
        assertTrue(success);
    }

    @And("a customer with an account with a balance of {int}")
    public void aCustomerWithAnAccountWithABalanceOf(int balance) {
        customerId = UUID.randomUUID();
        customerBankId = UUID.randomUUID();
        accountServiceConnector.addUser(customerId, customerBankId.toString());
    }

    @And("the transaction is saved")
    public void theTransactionIsSaved() {
        Transaction t = new Transaction(merchantId, customerId, BigDecimal.valueOf(amount), description);
        TransactionStore ts = TransactionStore.getInstance();
        assertTrue(TransactionStore.getInstance().getTransactions().containsKey(customerId));
    }

    @When("a {string} event is received")
    public void aEventIsReceived(String eventName) {
        merchantId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        TransactionRequest trxReq = new TransactionRequest(merchantId.toString(), customerId.toString(), BigDecimal.valueOf(amount));
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
