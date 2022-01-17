package endpoints;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import account.service.models.User;
import messaging.implementations.RabbitMqQueue;
import services.AccountRestService;

/**
* @authors Thomas Rathsach Strange (s153390), 
* 		   Simon Pontoppidan (s144213), 
* 		   Niels Karsten Bisgaard-Bohr (s202745)
*/

@Path("/users")
public class AccountResource {
	RabbitMqQueue queue = new RabbitMqQueue("RabbitMQ");
	AccountRestService service = new AccountRestService(queue);

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public UUID registerUserAcount(User user) {
		return service.registerAsyncUserAccount(user);
	}

	@DELETE
	public boolean deleteUserAccount(@QueryParam("userId") UUID userId) {
		return service.requestAsyncUserAccountDeletion(userId);
	}
}
