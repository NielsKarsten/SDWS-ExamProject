// Authors:
// Theodor Guttesen s185121
// Christian Gernsøe s163552
// Gustav Lintrup Kirkholt s164765

package behaviourtests;

import static org.junit.Assert.assertEquals;

import dtu.ws.fastmoney.test.BankService;
import dtu.ws.fastmoney.test.BankServiceException_Exception;
import dtu.ws.fastmoney.test.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.After;
import org.junit.Before;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DTUPaySteps {
	private BankService bankService = mock(BankService.class);;
	private DTUPayUser customer;
	private DTUPayUser merchant;
	private DTUPayUser admin;
	private List<UUID> tokens;
	private UUID transactionToken;
	private BigDecimal transactionAmount;
	private List<Transaction> transactionList;
	private User johnnyUser;
	private User bravoUser;
	private String customerAccountId;
	private String merchantAccountId;
	private boolean deleteAccountResponse;

	Client client = ClientBuilder.newClient();
	WebTarget merchantTarget = client.target("http://localhost:8080/").path("merchant");
	WebTarget customerTarget = client.target("http://localhost:8080/").path("customer");
	WebTarget adminTarget = client.target("http://localhost:8080/").path("admin");

	@Before
	public void createBankAccount() throws BankServiceException_Exception {
		johnnyUser = new User();
		johnnyUser.setCprNumber("12341234");
		johnnyUser.setFirstName("Johnny");
		johnnyUser.setLastName("Bravo");

		bravoUser = new User();
		bravoUser.setCprNumber("12353253426");
		bravoUser.setFirstName("Bravo");
		bravoUser.setLastName("Johnny");

		customerAccountId = bankService.createAccountWithBalance(johnnyUser, BigDecimal.valueOf(1000));
		merchantAccountId = bankService.createAccountWithBalance(bravoUser, BigDecimal.valueOf(1000));
	}

	@Given("a customer {string} {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName) {
		customer = new DTUPayUser(firstName, lastName, customerAccountId);
		customer.assignUserId();
	}

	@Given("a merchant {string} {string}")
	public void aMerchantWithBankAccount(String firstName, String lastName) {
		merchant = new DTUPayUser(firstName, lastName, merchantAccountId);
		merchant.assignUserId();
	}

	@Given("an admin {string} {string}")
	public void anAdminWithBankAccount(String firstName, String lastName) {
		admin = new DTUPayUser(firstName, lastName, customerAccountId);
		admin.assignUserId();
	}

	@When("customer is being registered")
	public void theCustomerIsBeingRegistered() {
		customerTarget.request().post(Entity.json(customer), DTUPayUser.class);
	}

	@When("merchant is being registered")
	public void theMerchantIsBeingRegistered() {
		merchantTarget.request().post(Entity.json(merchant), DTUPayUser.class);
	}

	@When("admin is being registered")
	public void theAdminIsBeingRegistered() {
		adminTarget.request().post(Entity.json(admin), DTUPayUser.class);
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
		assertEquals(actual, BigDecimal.valueOf(amount));
	}

	@Then("merchant has balance {int}")
	public void theMerchantHasBalance(int amount) throws BankServiceException_Exception {
		BigDecimal actual = bankService.getAccount(merchant.getAccountId()).getBalance();
		assertEquals(actual, BigDecimal.valueOf(amount));
	}

	@When("customer account is retired")
	public void theCustomerAccountIsDeleted() {
		var response = customerTarget.queryParam("userId", customer.getUserId()).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
	}

	@When("merchant account is retired")
	public void theMerchantAccountIsDeleted() {
		var response = merchantTarget.queryParam("userId", merchant.getUserId()).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
	}

	@When("admin account is retired")
	public void theAdminAccountIsDeleted() {
		var response = adminTarget.queryParam("userId", admin.getUserId()).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
	}

	@Then("account does not exist")
	public void theAccountDoesNotExist() {
		assertTrue(deleteAccountResponse);
	}

	@After
	public void retireBankAccount() throws BankServiceException_Exception {
		bankService.retireAccount(customerAccountId);
		bankService.retireAccount(merchantAccountId);
	}
}

