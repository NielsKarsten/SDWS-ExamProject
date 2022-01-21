package endpoints;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import adapters.AdapterFactory;
import models.Transaction;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Gustav Lintrup Kirkholt
 */
@Path("/admins")
public class AdminResource extends UserResourceImpl {
    private AdapterFactory factory = new AdapterFactory();

	@Path("/transactions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction() {
		try {
			List<Transaction> transactions = factory.getTransactionRestAdapter().getAdminTransactions();
			return Response.status(200).entity(transactions).build();
		}
		catch (Exception e) 
		{
			return Response.status(400).entity(e.getMessage()).build();
		}
    }

}
