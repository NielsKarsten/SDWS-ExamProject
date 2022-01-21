package behaviourtests;
/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Christian Gernsøe
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import token.service.manager.TokenManager;

public class TokenManagerSteps {
	private TokenManager tokenManager;
	
	private UUID userId;
	private UUID token;	
	
	@Before
	public void setUp() {
		tokenManager = new TokenManager();
	}
	
	@Given("a user")
	public void aUser() {
		this.userId = UUID.randomUUID();
	}
	
	@When("{int} tokens are generated")
	public void TokensRequested(int n) {
		for (int i = 0; i < n; i++) {			
			tokenManager.addToken(userId, UUID.randomUUID());
		}
	}
	
	@When("token is removed")
	public void RemoveToken() {
		token = tokenManager.getUserTokens(userId).get(0);
		tokenManager.removeToken(userId, token);
	}
	
	@Then("user has {int} tokens")
	public void verifyUserTokensNumber(int n) {
		assertEquals(n, tokenManager.getUserTokens(userId).size());
	}
	
	@Then("is the owner of the tokens")
	public void verifyUserOwnerStatus() {
		for (UUID token : tokenManager.getUserTokens(userId)) {
			assertEquals(userId, tokenManager.getTokenOwner(token));
		}
	}
	
	@Then("token is succesfully removed")
	public void verifyTokenRemoved() {
		assertTrue(tokenManager.getUserTokens(userId).contains(token) == false);
	}
	
}
