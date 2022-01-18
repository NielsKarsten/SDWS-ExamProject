// Authors:
// Theodor Guttesen s185121
// Christian Gernsøe s163552
// Gustav Lintrup Kirkholt s164765

package behaviourtests;

import static org.junit.Assert.assertEquals;

import com.google.gson.Gson;
import dtu.ws.fastmoney.test.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class DTUPaySteps {
	private BankService bankService = new BankServiceService().getBankServicePort();
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

	private UUID customerId;
	private UUID merchantId;

	Client client = ClientBuilder.newClient();
	WebTarget merchantTarget = client.target("http://localhost:8080/").path("merchant");
	WebTarget customerTarget = client.target("http://localhost:8080/").path("customer");
	WebTarget adminTarget = client.target("http://localhost:8080/").path("admin");

	@Before
	public void createBankAccount() throws BankServiceException_Exception {
		System.out.println("Before is called");
		johnnyUser = new User();
		johnnyUser.setCprNumber("12341234");
		johnnyUser.setFirstName("Johnny");
		johnnyUser.setLastName("Bravo");

		bravoUser = new User();
		bravoUser.setCprNumber("12353253426");
		bravoUser.setFirstName("Bravo");
		bravoUser.setLastName("Johnny");

		for (AccountInfo accountInfo : bankService.getAccounts()) {
			if (accountInfo.getUser().getCprNumber().equals(johnnyUser.getCprNumber()))
				bankService.retireAccount(accountInfo.getAccountId());

			if (accountInfo.getUser().getCprNumber().equals(bravoUser.getCprNumber()))
				bankService.retireAccount(accountInfo.getAccountId());
		}

		customerAccountId = bankService.createAccountWithBalance(johnnyUser, BigDecimal.valueOf(0));
		merchantAccountId = bankService.createAccountWithBalance(bravoUser, BigDecimal.valueOf(0));
	}

	@Given("a customer {string} {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName) {
		System.out.println("Customer with bank account is called");
		customer = new DTUPayUser(firstName, lastName, customerAccountId);
		customer.assignUserId();
	}

	@Given("a merchant {string} {string}")
	public void aMerchantWithBankAccount(String firstName, String lastName) {
		System.out.println("Merchant with bank account is called");
		merchant = new DTUPayUser(firstName, lastName, merchantAccountId);
		merchant.assignUserId();
	}

	@Given("an admin {string} {string}")
	public void anAdminWithBankAccount(String firstName, String lastName) {
		System.out.println("Admin with bank account is called");
		admin = new DTUPayUser(firstName, lastName, customerAccountId);
		admin.assignUserId();
	}

	@When("customer is being registered")
	public void theCustomerIsBeingRegistered() {
		System.out.println("Customer is being registered account is called");
		customerId = customerTarget.request().post(Entity.json(customer), UUID.class);
	}

	@When("merchant is being registered")
	public void theMerchantIsBeingRegistered() {
		System.out.println("Merchant is being registered account is called");
		merchantId = merchantTarget.request().post(Entity.json(merchant), UUID.class);
	}

	@When("admin is being registered")
	public void theAdminIsBeingRegistered() {
		System.out.println("Admin is being registered account is called");
		adminTarget.request().post(Entity.json(admin), DTUPayUser.class);
	}

	@When ("customer requests {int} tokens")
	public void customerRequestsTokens(int tokenAmount){
		System.out.println("Customer requests tokens is called");
		TokenRequest tokenRequest = new TokenRequest(customerId, tokenAmount);
		String json = new Gson().toJson(tokenRequest);
		tokens = customerTarget.path("/token").request().post(Entity.json(json), new GenericType<List<UUID>>(){});
	}

	@Then("customer has {int} tokens")
	public void theCustomerHasTokens(int amount)  {
		System.out.println("Customer has tokens is called");
		assertEquals(tokens.size(), amount);
	}

	@When("merchant initiates a transaction for {int}")
	public void theTransactionsIsInitiated(int amount) {
		System.out.println("Merchant initiates transaction is called");
		transactionToken = tokens.get(0);
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, transactionToken, transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		var response = merchantTarget.path("/transaction").request().post(Entity.json(json));
		String test = "J";
	}

	@When("customer requests transactions")
	public void theCustomerRequestsTransactions() {
		System.out.println("Customer initiates transactions list is called");
		var response = customerTarget.path("/transaction").request().get();
		transactionList = response.readEntity(List.class);
	}

	@When("merchant requests transactions")
	public void theMerchantRequestsTransactions() {
		System.out.println("Merchant requests transactions list is called");
		var response = merchantTarget.path("/transaction").request().get();
		transactionList = response.readEntity(List.class);
	}

	@When("admin requests transactions")
	public void theAdminRequestsTransactions() {
		System.out.println("Admin initiates transactions list is called");
		var response = adminTarget.path("/transaction").request().get();
		transactionList = response.readEntity(List.class);
	}

	@Then("user gets transaction")
	public void theUserGetsTransactions() {
		System.out.println("User gets transaction verification is called");
		Transaction transaction = transactionList.get(0);
		assertEquals(transaction.getToken(), transactionToken);
		assertEquals(transaction.getMerchant(), merchant.getUserId());
		assertEquals(transaction.getAmount(), transactionAmount);
	}

	@Then("customer has balance {int}")
	public void theCustomerHasBalance(int amount) throws BankServiceException_Exception {
		System.out.println("Customer has balance verification is called");
		BigDecimal actual = bankService.getAccount(customer.getAccountId()).getBalance();
		assertEquals(BigDecimal.valueOf(amount), actual);
	}

	@Then("merchant has balance {int}")
	public void theMerchantHasBalance(int amount) throws BankServiceException_Exception {
		System.out.println("Merchant has balance verification is called");
		BigDecimal actual = bankService.getAccount(merchant.getAccountId()).getBalance();
		assertEquals(BigDecimal.valueOf(amount), actual);
	}

	@When("customer account is retired")
	public void theCustomerAccountIsDeleted() {
		System.out.println("Customer account retired is called");
		var response = customerTarget.queryParam("userId", customerId).request().delete();
		deleteAccountResponse = response.readEntity(Boolean.class);
	}

	@When("merchant account is retired")
	public void theMerchantAccountIsDeleted() {
		System.out.println("Merchant account retired is called");
		var response = merchantTarget.queryParam("userId", merchantId).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
	}

	@When("admin account is retired")
	public void theAdminAccountIsDeleted() {
		System.out.println("Admin account retired is called");
		var response = adminTarget.queryParam("userId", admin.getUserId()).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
	}

	@Then("account does not exist")
	public void theAccountDoesNotExist() {
		System.out.println("account retired verification is called");
		assertTrue(deleteAccountResponse);
	}

	@After
	public void retireBankAccount() throws BankServiceException_Exception {
		System.out.println("After is called");
		bankService.retireAccount(customerAccountId);
		bankService.retireAccount(merchantAccountId);
	}
}

