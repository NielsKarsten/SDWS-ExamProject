package endpoints;

import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import models.User;

public interface UserResource {
	
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	UUID registerUserAcount(User user);
	
	@DELETE
	boolean deleteUserAccount(@QueryParam("id") UUID id);

}
