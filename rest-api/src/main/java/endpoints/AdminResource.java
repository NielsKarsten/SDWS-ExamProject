package endpoints;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Transaction;
import models.User;
import services.ServicesFactory;
@Path("/admin")
public class AdminResource {
    private ServicesFactory factory = new ServicesFactory();

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public UUID registerUserAcount(User user) {
		return factory.getAccountService().registerAsyncUserAccount(user);
	}

	@DELETE
	public boolean deleteUserAccount(@QueryParam("adminId") UUID adminId) {
		return factory.getAccountService().requestAsyncUserAccountDeletion(adminId);
	}
	
	@Path("/transaction")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction() {
		try {
			List<Transaction> transactions = factory.getTransactionService().getAdminTransactions();
			return Response.status(200).entity(transactions).build();
		}
		catch (Exception e) 
		{
			return Response.status(400).entity(e.getMessage()).build();
		}
    }

}
