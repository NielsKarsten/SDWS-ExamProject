package endpoints;

import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.QueryParam;

import adapters.ServicesFactory;
import models.User;

public abstract class UserResourceImpl implements UserResource{
	private ServicesFactory factory = new ServicesFactory();
	
	public UUID registerUserAcount(User user) {
		return factory.getAccountService().registerAsyncUserAccount(user);
	}

	@DELETE
	public boolean deleteUserAccount(@QueryParam("id") UUID id) {
		return factory.getAccountService().requestAsyncUserAccountDeletion(id);
	}
}
