// Authors:
// Theodor Guttesen s185121
// Christian Gernsøe s163552
// Gustav Lintrup Kirkholt s164765

package behaviourtests;

import static org.junit.Assert.assertEquals;

import dtu.ws.fastmoney.test.BankService;
import dtu.ws.fastmoney.test.BankServiceException_Exception;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.mock;

public class DTUPaySteps {
	private BankService bankService = mock(BankService.class);;
	private User customer;
	private User merchant;
	private User admin;
	private List<UUID> tokens;
	private UUID transactionToken;
	private BigDecimal transactionAmount;
	private List<Transaction> transactionList;

	Client client = ClientBuilder.newClient();
	WebTarget merchantTarget = client.target("http://localhost:8080/").path("merchant");
	WebTarget customerTarget = client.target("http://localhost:8080/").path("customer");
	WebTarget adminTarget = client.target("http://localhost:8080/").path("admin");

	@Given("a customer {string} {string} with bank account {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName, String accountId) {
		customer = new User(firstName, lastName, accountId);
		customer.assignUserId();
	}

	@Given("a merchant {string} {string} with bank account {string}")
	public void aMerchantWithBankAccount(String firstName, String lastName, String accountId) {
		merchant = new User(firstName, lastName, accountId);
		merchant.assignUserId();
	}

	@Given("an admin {string} {string} with bank account {string}")
	public void anAdminWithBankAccount(String firstName, String lastName, String accountId) {
		admin = new User(firstName, lastName, accountId);
		admin.assignUserId();
	}

	@When("customer is being registered")
	public void theCustomerIsBeingRegistered() {
		customerTarget.request().post(Entity.json(customer), User.class);
	}

	@When("merchant is being registered")
	public void theMerchantIsBeingRegistered() {
		merchantTarget.request().post(Entity.json(merchant), User.class);
	}

	@When("admin is being registered")
	public void theAdminIsBeingRegistered() {
		adminTarget.request().post(Entity.json(admin), User.class);
	}

	@When ("customer requests {int} tokens")
	public void customerRequestsTokens(int tokenAmount){
		TokenRequest tokenRequest = new TokenRequest(customer.getUserId(), tokenAmount);
		tokens = customerTarget.path("/token").request().post(Entity.json(tokenRequest), new GenericType<List<UUID>>(){});
	}

	@Then("customer has {int} tokens")
	public void theCustomerHasTokens(int amount)  {
		assertEquals(tokens.size(), amount);
	}

	@When("merchant initiates a transaction for {int}")
	public void theTransactionsIsInitiated(int amount) {
		transactionToken = tokens.get(0);
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchant.getUserId(), transactionToken, transactionAmount);
		merchantTarget.path("/transaction").request().post(Entity.json(transactionRequest));
	}

	@When("customer requests transactions")
	public void theCustomerRequestsTransactions() {
		var response = customerTarget.path("/transaction").request().get();
		transactionList = response.readEntity(List.class);
	}

	@When("merchant requests transactions")
	public void theMerchantRequestsTransactions() {
		var response = merchantTarget.path("/transaction").request().get();
		transactionList = response.readEntity(List.class);
	}

	@When("admin requests transactions")
	public void theAdminRequestsTransactions() {
		var response = adminTarget.path("/transaction").request().get();
		transactionList = response.readEntity(List.class);
	}

	@Then("user gets transaction")
	public void theUserGetsTransactions() {
		Transaction transaction = transactionList.get(0);
		assertEquals(transaction.getToken(), transactionToken);
		assertEquals(transaction.getMerchant(), merchant.getUserId());
		assertEquals(transaction.getAmount(), transactionAmount);
	}

	@Then("customer has balance {int}")
	public void theCustomerHasBalance(int amount) throws BankServiceException_Exception {
		BigDecimal actual = bankService.getAccount(customer.getAccountId()).getBalance();
		assertEquals(actual, amount);
	}

	@Then("merchant has balance {int}")
	public void theMerchantHasBalance(int amount) throws BankServiceException_Exception {
		BigDecimal actual = bankService.getAccount(merchant.getAccountId()).getBalance();
		assertEquals(actual, amount);
	}

	@When("account with id {string} is retired")
	public void theAccountIsRetired(String accountId) throws BankServiceException_Exception {
		bankService.retireAccount(accountId);
	}

	@Then("account with id {string} does not exist")
	public void theAccountDoesNotExist(String accountId) throws BankServiceException_Exception {
		assertEquals("null", bankService.getAccount(accountId));
	}
}

