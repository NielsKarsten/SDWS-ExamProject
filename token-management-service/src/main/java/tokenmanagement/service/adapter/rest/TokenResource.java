// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

package tokenmanagement.service.adapter.rest;

import tokenmanagement.service.Token;
import tokenmanagement.service.TokenManagementService;
import tokenmanagement.service.exceptions.TokenException;

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
	public List<Token> requestTokens(String customerId, int tokenAmount) throws TokenException {
		TokenManagementService service = new TokenManagementFactory().getService();
		return service.requestTokens(customerId, tokenAmount);
	}
}
