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

import models.*;
import services.*;

@Path("/customer")
public class CustomerResource {
    private ServicesFactory factory = new ServicesFactory();

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public UUID registerUserAcount(User user) {
		return factory.getAccountService().registerAsyncUserAccount(user);
	}

	@DELETE
	public boolean deleteUserAccount(@QueryParam("customerId") UUID customerId) {
		return factory.getAccountService().requestAsyncUserAccountDeletion(customerId);
	}

    @Path("/transaction")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTransactions(@QueryParam("customerId") UUID customerId) {
    	System.out.println("Customer requested a list of their transactions. ID: " + customerId.toString());
    	try {
    		var customerTransactions = factory.getTransactionService().getCustomerTransactions(customerId);
    		return Response.status(200).entity(customerTransactions).build();
    	}
    	catch(Exception e) {
    		return Response.serverError().build();
    	}
    }

    @Path("/token")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestTokens(TokenRequest tokenRequest) {
    	try {
    		List<UUID> tokens = factory.getTokenService().issueTokens(tokenRequest);
    		return Response.status(200).entity(tokens).build();
    	}
    	catch(Exception e) {
    		return Response.serverError().build();
    	}
    }


}