package endpoints;

import java.math.BigDecimal;
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

@Path("/merchant")
public class MerchantResource {
    private ServicesFactory factory = new ServicesFactory();
    TransactionRestService transactionService = factory.getTransactionService();

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public UUID registerUserAcount(User user) {
		return factory.getAccountService().registerAsyncUserAccount(user);
	}

	@DELETE
	public boolean deleteUserAccount(@QueryParam("merchantId") UUID merchantId) {
		return factory.getAccountService().requestAsyncUserAccountDeletion(merchantId);
	}
	
	@Path("/transaction")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(@QueryParam("merchantId") UUID merchantId) {
		try {
			var obj = transactionService.getMerchantTransactions(merchantId);
			return Response.status(200).entity(obj).build();
		}
		catch (Exception e) {
			return Response.serverError().build();
		}
    }

	@Path("/transaction")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(TransactionRequest request) {
		try {
			var obj = transactionService.createTransactionRequest(request);
			return Response.status(200).entity(obj).build();
		}
		catch (Exception e) {
			return Response.serverError().build();
		}
    }
	
}
