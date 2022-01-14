// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

package issuetoken.service.adapter.rest;

import issuetoken.service.IssueTokenService;
import issuetoken.service.Token;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;

@Path("/requesttokens")
public class TokenResource {

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public List<Token> requestTokens(String customerId, int tokenAmount) {
		IssueTokenService service = new TokenManagementFactory().getService();
		return service.issue(customerId, tokenAmount);
	}
}