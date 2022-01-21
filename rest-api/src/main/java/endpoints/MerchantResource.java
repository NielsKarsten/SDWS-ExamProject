package endpoints;

import java.util.List;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import adapters.*;
import models.*;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Gustav Utke Kauman
 */
@Path("/merchant")
public class MerchantResource extends UserResourceImpl{
    private ServicesFactory factory = new ServicesFactory();
    TransactionRestAdapter transactionService = factory.getTransactionService();

	@Path("/transaction")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(@QueryParam("merchantId") UUID merchantId) {
		try {
			List<Transaction> obj = transactionService.getMerchantTransactions(merchantId);
			return Response.status(200).entity(obj).build();
		}
		catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
    }

	@Path("/transaction")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createTransaction(TransactionRequest request) {
		try {
			String transactionResponse = transactionService.createTransactionRequest(request);
			return Response.status(200).entity(transactionResponse).build();
		}
		catch (Exception e) {
			return Response.status(400).entity(e.getMessage()).build();
		}
    }

}
