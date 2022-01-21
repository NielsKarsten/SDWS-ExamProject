package endpoints;

import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.QueryParam;

import adapters.AdapterFactory;
import models.User;
/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Niels Bisgaard-Bohr
 */
public abstract class UserResourceImpl implements UserResource{
	private AdapterFactory factory = new AdapterFactory();
	
	public UUID registerUserAcount(User user) {
		return factory.getAccountRestAdapter().registerAsyncUserAccount(user);
	}

	@DELETE
	public boolean deleteUserAccount(@QueryParam("id") UUID id) {
		return factory.getAccountRestAdapter().requestAsyncUserAccountDeletion(id);
	}
}
