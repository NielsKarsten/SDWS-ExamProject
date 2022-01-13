package accountregistration.service.adapter.rest;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import accountregistration.service.AccountRegistrationService;
import accountregistration.service.User;

@Path("/users")
public class AccountResource {
	// TODO make as response
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public UUID registerStudent(User user) {
		AccountRegistrationService service = new AccountRegistrationFactory().getService();
		return service.registerAsyncUserAccount(user);
	}
}
