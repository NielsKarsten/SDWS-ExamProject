// Authors:
// Code used from Hubert Baumeisters example,
// The code has been adapted by:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

package endpoints;

import services.TokenRestService;
import messaging.implementations.RabbitMqQueue;
import tokenmanagement.service.TokenRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.List;
import java.util.UUID;

@Path("/requesttokens")
public class TokenResource {
	RabbitMqQueue queue = new RabbitMqQueue("RabbitMQ");
	TokenRestService service = new TokenRestService(queue);
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public List<UUID> requestTokens(TokenRequest tokenRequest) {
		return service.issue(tokenRequest);
	}
}