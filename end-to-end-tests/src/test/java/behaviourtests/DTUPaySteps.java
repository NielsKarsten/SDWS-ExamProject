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
	WebTarget merchantTarget = client.target("http://localhost:8080/").path("merchant");
	WebTarget customerTarget = client.target("http://localhost:8080/").path("customer");
	WebTarget adminTarget = client.target("http://localhost:8080/").path("admin");
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
			boolean removeCustomerBankAccount = accountInfo.getUser().getCprNumber().equals(johnnyUser.getCprNumber());
			boolean removeMerchantBankAccount = accountInfo.getUser().getCprNumber().equals(bravoUser.getCprNumber());
			if (removeCustomerBankAccount || removeMerchantBankAccount) 
			{
				try 
				{
					bankService.retireAccount(accountInfo.getAccountId());
				}
				catch(BankServiceException_Exception bse) {
					System.out.println("Bank account Retire ERROR");
					System.out.println(bse.getMessage());
				}
			}
		}

		try 
		{
			customerAccountId = bankService.createAccountWithBalance(johnnyUser, customerBankBalance);			
		}
		catch(BankServiceException_Exception bse) {
			System.out.println("Customer bank account creation ERROR");
			System.out.println(bse.getMessage());
		}
		try 
		{
			merchantAccountId = bankService.createAccountWithBalance(bravoUser, merchantBankBalance);	
		}
		catch(BankServiceException_Exception bse)
		{
			System.out.println("Merchant bank account creation ERROR");
			System.out.println(bse.getMessage());
		}
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
		adminId = adminTarget.request().post(Entity.json(admin), UUID.class);
	}

	@When ("customer requests {int} tokens")
	public void customerRequestsTokens(int tokenAmount){
		System.out.println("Customer requests tokens is called");
		TokenRequest tokenRequest = new TokenRequest(customerId, tokenAmount);
		String json = new Gson().toJson(tokenRequest);
		try {
			tokens = customerTarget.path("/token").request().post(Entity.json(json), new GenericType<List<UUID>>(){});
			transactionToken = tokens.get(0);			
		}
		catch(BadRequestException e)
		{
			serverError = e.getResponse().readEntity(String.class);
		}
	}

	@Then("customer has {int} tokens")
	public void theCustomerHasTokens(int amount)  {
		System.out.println("Customer has tokens is called");
		assertEquals(tokens.size(), amount);
	}

	@When("merchant initiates a transaction for {float}")
	public void theTransactionsIsInitiated(float amount) {
		System.out.println("Merchant initiates transaction is called");
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, transactionToken, transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transaction").request().post(Entity.json(json));
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
		System.out.println("Merchant initiates transaction is called");
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, transactionToken, transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transaction").request().post(Entity.json(json));
		serverError = response.readEntity(String.class);
		response.close();
	}
	
	@When("unregistered merchant initiates a transaction for {float}")
	public void UnregisteredMerchantInitiatesATransactionFor(float amount) {
		merchantId = UUID.randomUUID();
		System.out.println("Merchant initiates transaction is called");
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, transactionToken, transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transaction").request().post(Entity.json(json));
		serverError = response.readEntity(String.class);
		response.close();
	}
	
	@When("merchant initiates a transaction for {float} with wrong token")
	public void theTransactionsIsInitiatedWrongToken(float amount) {
		System.out.println("Merchant initiates transaction with wrong token is called");
		transactionAmount = BigDecimal.valueOf(amount);
		TransactionRequest transactionRequest = new TransactionRequest(merchantId, UUID.randomUUID(), transactionAmount);
		String json = new Gson().toJson(transactionRequest);
		Response response = merchantTarget.path("/transaction").request().post(Entity.json(json));
		serverError = response.readEntity(String.class);
		response.close();
	}

	@When("customer requests transactions")
	public void theCustomerRequestsTransactions() {
		System.out.println("Customer initiates transactions list is called");
		Response response = customerTarget.queryParam("customerId", customerId).path("/transaction").request().get();
		System.out.println("Response = ");
		System.out.println(response);
		List<Transaction> customerRecievedTransactions = response.readEntity(new GenericType<List<Transaction>>() {});
		transactionList.addAll(customerRecievedTransactions);
		response.close();
	}
	
	@When("another customer requests transactions")
	public void anotherTheCustomerRequestsTransactions() {
		System.out.println("Another customer initiates transactions list is called");
		UUID anotherCustomerId = UUID.randomUUID();
		Response response = customerTarget.queryParam("customerId", UUID.randomUUID()).path("/transaction").request().get();
		System.out.println("Response = ");
		System.out.println(response);
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
		System.out.println("Merchant requests transactions list is called");
		Response response = merchantTarget.queryParam("merchantId", merchantId).path("/transaction").request().get();
		List<Transaction> merchantReceivedTransactions = response.readEntity(new GenericType<List<Transaction>>() {});
		transactionList.addAll(merchantReceivedTransactions);
		response.close();
	}

	@When("admin requests transactions")
	public void theAdminRequestsTransactions() {
		System.out.println("Admin initiates transactions list is called");
		Response response = adminTarget.path("/transaction").request().get();
		List<Transaction> adminReceivedTransactions = response.readEntity(new GenericType<List<Transaction>>() {});
		transactionList.addAll(adminReceivedTransactions);
		response.close();
	}

	@Then("user gets transaction")
	public void theUserGetsTransactions() {
		System.out.println("User gets transaction verification is called");
		Transaction transaction = transactionList.get(0);
		assertTrue(transactionList.contains(transaction));
	}
	
	@Then("user gets no transactions")
	public void theUserGetsNoTransactions() {
		System.out.println("User gets no transactions verification is called");
		assertTrue(transactionList.isEmpty());
	}
	
	@Then("merchant cannot identify customer identity")
	public void merchantCannotIdentifyCustomerIdentity() {
		Transaction transaction = transactionList.get(0);
		assertTrue(transaction.getCustomer() == null);
	}

	@Then("customer has correct balance")
	public void theCustomerHasBalance() throws BankServiceException_Exception {
		System.out.println("Customer has balance verification is called");
		BigDecimal actual = bankService.getAccount(customer.getAccountId()).getBalance();
		assertEquals(customerBankBalance, actual);
	}

	@Then("merchant has correct balance")
	public void theMerchantHasBalance() throws BankServiceException_Exception {
		System.out.println("Merchant has balance verification is called");
		BigDecimal actual = bankService.getAccount(merchant.getAccountId()).getBalance();
		assertEquals(actual, merchantBankBalance);
	}

	@When("customer account is retired")
	public void theCustomerAccountIsDeleted() {
		System.out.println("Customer account retired is called");
		response = customerTarget.queryParam("customerId", customerId).request().delete();
		deleteAccountResponse = response.readEntity(Boolean.class);
		response.close();
	}

	@When("merchant account is retired")
	public void theMerchantAccountIsDeleted() {
		System.out.println("Merchant account retired is called");
		response = merchantTarget.queryParam("merchantId", merchantId).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
		response.close();
	}

	@When("admin account is retired")
	public void theAdminAccountIsDeleted() {
		System.out.println("Admin account retired is called");
		response = adminTarget.queryParam("adminId", adminId).request().delete();
		deleteAccountResponse = response.readEntity(boolean.class);
		response.close();
	}

	@Then("account does not exist")
	public void theAccountDoesNotExist() {
		System.out.println("account retired verification is called");
		assertTrue(deleteAccountResponse);
	}
	
	@Then("customer exists")
	public void customerExists() {
	    assertNotNull(customerId);
	}

	@Then("merchant exists")
	public void merchantExists() {
		assertNotNull(merchantId);
	}
	
	@Then("they receive an error message {string}")
	public void verifyErrorMessage(String errorMessage) {
		assertEquals(errorMessage, serverError);
		assertNotNull(serverError);
	}

	@After
	public void retireBankAccount() throws BankServiceException_Exception {
		System.out.println("After is called");
		bankService.retireAccount(customerAccountId);
		bankService.retireAccount(merchantAccountId);
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}
}

