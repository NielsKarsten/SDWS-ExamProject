package accountregistration.service.adapter.rest;

import java.util.UUID;

import javax.ws.rs.*;

import accountregistration.service.AccountRegistrationService;
import accountregistration.service.User;

/**
* @authors Thomas Rathsach Strange (s153390), 
* 		   Simon Pontoppidan (s144213), 
* 		   Niels Karsten Bisgaard-Bohr (s202745)
*/

@Path("/users")
public class AccountResource {
	AccountRegistrationFactory factory = new AccountRegistrationFactory();

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public UUID registerUserAcount(User user) {
		AccountRegistrationService service = factory.getService();
		return service.registerAsyncUserAccount(user);
	}

	@DELETE
	public boolean deleteUserAccount(@QueryParam("userId") UUID userId) {
		AccountRegistrationService service = factory.getService();
		return service.requestAsyncUserAccountDeletion(userId);
	}
}
