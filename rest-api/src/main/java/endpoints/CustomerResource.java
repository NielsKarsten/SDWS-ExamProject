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

import adapters.*;
import models.*;

@Path("/customer")
public class CustomerResource extends UserResourceImpl{
    private ServicesFactory factory = new ServicesFactory();

    @Path("/transaction")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTransactions(@QueryParam("customerId") UUID customerId) {
    	try {	
    		List<Transaction> customerTransactions = factory.getTransactionService().getCustomerTransactions(customerId);
    		return Response.status(200).entity(customerTransactions).build();
    	}
    	catch(Exception e) {
    		return Response.status(400).entity(e.getMessage()).build();
    	}
    }

    @Path("/token")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response requestTokens(TokenRequest tokenRequest) {
    	try 
    	{
    		List<UUID> tokens = factory.getTokenService().issueTokens(tokenRequest);
    		return Response.status(200).entity(tokens).build();
    	}
    	catch(Exception e) 
    	{
    		return Response.status(400).entity(e.getMessage()).build();
    	}
    }


}