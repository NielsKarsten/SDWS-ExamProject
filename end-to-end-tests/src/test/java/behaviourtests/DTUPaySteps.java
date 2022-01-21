// Authors:
// Theodor Guttesen s185121
// Christian Gernsøe s163552
// Gustav Lintrup Kirkholt s164765

package behaviourtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import com.google.gson.Gson;
import dtu.ws.fastmoney.test.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import models.DTUPayUser;
import models.TokenRequest;
import models.Transaction;
import models.TransactionRequest;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Krikholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Christian Gernsøe
 */
public class DTUPaySteps {	
	private BankService bankService = new BankServiceService().getBankServicePort();
	
	//Rest api properties
	Client client = ClientBuilder.newClient();
	WebTarget merchantTarget = client.target("http://localhost:8080/").path("merchants");
	WebTarget customerTarget = client.target("http://localhost:8080/").path("customers");
	WebTarget adminTarget = client.target("http://localhost:8080/").path("admins");
	Response response;
	String serverError;
	
	//Customer properties
	private User johnnyUser;
	private DTUPayUser customer;
	private UUID customerId;
	private String customerAccountId;
	private BigDecimal customerBankBalance = BigDecimal.valueOf(1000.0);
	private List<UUID> tokens;
	
	//Merchant properties
	private User bravoUser;
	private DTUPayUser merchant;
	private UUID merchantId;
	private String merchantAccountId;
	private BigDecimal merchantBankBalance = BigDecimal.valueOf(1000.0);
	
	//transaction properties
	private UUID transactionToken;
	private BigDecimal transactionAmount;
	private List<Transaction> transactionList = new ArrayList<>();
	
	//Admin properties
	private DTUPayUser admin;
	private UUID adminId;

	//Account deletion properties
	private boolean deleteAccountResponse;
	
	
	@Before
	public void createBankAccount() {
		System.out.println("createBankAccount");
		johnnyUser = new User();
		johnnyUser.setCprNumber("12341234");
		johnnyUser.setFirstName("Johnny");
		johnnyUser.setLastName("Bravo");

		bravoUser = new User();
		bravoUser.setCprNumber("12353253426");
		bravoUser.setFirstName("Bravo");
		bravoUser.setLastName("Johnny");

		for (AccountInfo accountInfo : bankService.getAccounts()) {
			boolean removeCustomerBankAccount = accountInfo.getUser().getCprNumber().equals(johnnyUser.getCprNumber());
			boolean removeMerchantBankAccount = accountInfo.getUser().getCprNumber().equals(bravoUser.getCprNumber());
			if (removeCustomerBankAccount || removeMerchantBankAccount) 
			{
				try 
				{
					bankService.retireAccount(accountInfo.getAccountId());
				}
				catch(BankServiceException_Exception bse) {
					serverError = bse.getMessage();
				}
			}
		}

		try 
		{
			customerAccountId = bankService.createAccountWithBalance(johnnyUser, customerBankBalance);			
		}
		catch(BankServiceException_Exception bse) {
			serverError = bse.getMessage();
		}
		try 
		{
			merchantAccountId = bankService.createAccountWithBalance(bravoUser, merchantBankBalance);	
		}
		catch(BankServiceException_Exception bse)
		{
			serverError = bse.getMessage();
		}
	}

	@Given("a customer {string} {string}")
	public void aCustomerWithBankAccount(String firstName, String lastName) {
		System.out.println("A customer");
		customer = new DTUPayUser(firstName, lastName, customerAccountId);
		customer.assignUserId();
	}

	@Given("a merchant {string} {string}")
	public void aMerchantWithBankAccount(String firstName, String lastName) {
		System.out.println("A merchant");
		merchant = new DTUPayUser(firstName, lastName, merchantAccountId);
		merchant.assignUserId();
	}

	@Given("an admin {string} {string}")
	public void anAdminWithBankAccount(String firstName, String lastName) {
		System.out.println("an Admin");
		admin = new DTUPayUser(firstName, lastName, customerAccountId);
		admin.assignUserId();
	}

	@When("customer is being registered")
	public void theCustomerIsBeingRegistered() {
		System.out.println("Customer is being registered");
		customerId = customerTarget.request().post(Entity.json(customer), UUID.class);
	}

	@When("merchant is being registered")
	public void theMerchantIsBeingRegistered() {
		System.out.println("merchant is being registered");
		merchantId = merchantTarget.request().post(Entity.json(merchant), UUID.class);
	}

	@When("admin is being registered")
	public void theAdminIsBeingRegistered() {
		System.out.println("admin is being registered");
		adminId = adminTarget.request().post(Entity.json(admin), UUID.class);
	}

	@When ("customer requests {int} tokens")
	public void customerRequestsTokens(int tokenAmount){
		System.out.println("customer requests tokens");
		TokenRequest tokenRequest = new TokenRequest(customerId, tokenAmount);
		String json = new Gson().toJson(tokenRequest);
		try {
			tokens = customerTarget.path("/tokens").request().post(Entity.json(json), new GenericType<List<UUID>>(){});
			transactionToken = tokens.get(0);			
		}
		catch(BadRequestException e)
		{
			serverError = e.getResponse().readEntity(String.class);
		}
	}

	@Then("customer has {int} tokens")
	public void theCustomerHasTokens(int amount)  {
		System.out.println("customer has tokens");
		assertEquals(tokens.size(), amount);
	}

	@When("merchant initiates a transaction for {float}")
	public void theTransactionsIsInitiated(float amount) {
		System.out.println("merchant initiates a transaction for");
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, transactionToken, transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transactions").request().post(Entity.json(json));
		int status = response.getStatus();
		if (status == 400) 
		{
			serverError = response.readEntity(String.class);
		}
		else {
			customerBankBalance = customerBankBalance.subtract(transactionAmount);
			merchantBankBalance = merchantBankBalance.add(transactionAmount);			
		}
		response.close();
	}
	
	@When("merchant initiates a transaction for {float} again")
	public void theTransactionsIsInitiatedAgain(float amount) {
		System.out.println("merchant initiates a transaction");
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, transactionToken, transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transactions").request().post(Entity.json(json));
		serverError = response.readEntity(String.class);
		response.close();
	}
	
	@When("unregistered merchant initiates a transaction for {float}")
	public void UnregisteredMerchantInitiatesATransactionFor(float amount) {
		System.out.println("unregistered merchant initiates a transaction for");
		merchantId = UUID.randomUUID();
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, transactionToken, transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transactions").request().post(Entity.json(json));
		serverError = response.readEntity(String.class);
		response.close();
	}
	
	@When("merchant initiates a transaction for {float} with wrong token")
	public void theTransactionsIsInitiatedWrongToken(float amount) {
		System.out.println("merchant initiates a transaction for n with wrong token");
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, UUID.randomUUID(), transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transactions").request().post(Entity.json(json));
		serverError = response.readEntity(String.class);
		response.close();
	}

	@When("customer requests transactions")
	public void theCustomerRequestsTransactions() {
		System.out.println("customer requests transactions");
		Response response = customerTarget.queryParam("customerId", customerId).path("/transactions").request().get();
		List<Transaction> customerRecievedTransactions = response.readEntity(new GenericType<List<Transaction>>() {});
		transactionList.addAll(customerRecievedTransactions);
		response.close();
	}
	
	@When("another customer requests transactions")
	public void anotherTheCustomerRequestsTransactions() {
		System.out.println("another customer requests transactions");
		UUID anotherCustomerId = UUID.randomUUID();
		Response response = customerTarget.queryParam("customerId", UUID.randomUUID()).path("/transactions").request().get();
		if (response.getStatus() == 200)
		{
			List<Transaction> customerRecievedTransactions = response.readEntity(new GenericType<List<Transaction>>() {});
			transactionList.addAll(customerRecievedTransactions);			
		}
		else
		{
			serverError = response.readEntity(String.class);
		}
		response.close();
	}

	@When("merchant requests transactions")
	public void theMerchantRequestsTransactions() {
		System.out.println("merchant requests transaction");
		Response response = merchantTarget.queryParam("merchantId", merchantId).path("/transactions").request().get();
		List<Transaction> merchantReceivedTransactions = response.readEntity(new GenericType<List<Transaction>>() {});
		transactionList.addAll(merchantReceivedTransactions);
		response.close();
	}

	@When("admin requests transactions")
	public void theAdminRequestsTransactions() {
		System.out.println("admin requests transactions");
		Response response = adminTarget.path("/transactions").request().get();
		List<Transaction> adminReceivedTransactions = response.readEntity(new GenericType<List<Transaction>>() {});
		transactionList.addAll(adminReceivedTransactions);
		response.close();
	}

	@Then("user gets transaction")
	public void theUserGetsTransactions() {
		System.out.println("user gets transaction");
		Transaction transaction = transactionList.get(0);
		assertTrue(transactionList.contains(transaction));
	}
	
	@Then("user gets no transactions")
	public void theUserGetsNoTransactions() {
		System.out.println("user gets no transactions");
		assertTrue(transactionList.isEmpty());
	}
	
	@Then("merchant cannot identify customer identity")
	public void merchantCannotIdentifyCustomerIdentity() {
		System.out.println("merchant cannot identify customer identity");
		Transaction transaction = transactionList.get(0);
		assertTrue(transaction.getCustomer() == null);
	}

	@Then("customer has correct balance")
	public void theCustomerHasBalance() throws BankServiceException_Exception {
		System.out.println("customer has correct balance");
		BigDecimal actual = bankService.getAccount(customer.getAccountId()).getBalance();
		assertEquals(customerBankBalance, actual);
	}

	@Then("merchant has correct balance")
	public void theMerchantHasBalance() throws BankServiceException_Exception {
		System.out.println("merchant has correct balance");
		BigDecimal actual = bankService.getAccount(merchant.getAccountId()).getBalance();
		assertEquals(actual, merchantBankBalance);
	}

	@When("customer account is retired")
	public void theCustomerAccountIsDeleted() {
		System.out.println("customer account is retired");
		response = customerTarget.queryParam("id", customerId).request().delete();
		deleteAccountResponse = response.readEntity(Boolean.class);
		response.close();
	}

	@When("merchant account is retired")
	public void theMerchantAccountIsDeleted() {
		System.out.println("merchant account is retired");
		response = merchantTarget.queryParam("id", merchantId).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
		response.close();
	}

	@When("admin account is retired")
	public void theAdminAccountIsDeleted() {
		System.out.println("admin account is retired");
		response = adminTarget.queryParam("id", adminId).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
		response.close();
	}

	@Then("account does not exist")
	public void theAccountDoesNotExist() {
		System.out.println("account does not exist");
		assertTrue(deleteAccountResponse);
	}
	
	@Then("customer exists")
	public void customerExists() {
		System.out.println("customer exists");
	    assertNotNull(customerId);
	}

	@Then("merchant exists")
	public void merchantExists() {
		System.out.println("merchant exists");
		assertNotNull(merchantId);
	}
	
	@Then("they receive an error message {string}")
	public void verifyErrorMessage(String errorMessage) {
		System.out.println("they receive an error message");
		assertEquals(errorMessage, serverError);
		assertNotNull(serverError);
	}

	@After
	public void retireBankAccount() throws BankServiceException_Exception {
		System.out.println("After");
		System.out.println("");
		bankService.retireAccount(customerAccountId);
		bankService.retireAccount(merchantAccountId);
	}
}

