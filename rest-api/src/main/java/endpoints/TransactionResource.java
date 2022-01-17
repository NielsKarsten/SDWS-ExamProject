package endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import messaging.implementations.RabbitMqQueue;
import services.TransactionRestService;
import transaction.service.models.*;

@Path("/transactions")
public class TransactionResource {

    private TransactionRestService transactionRestService = new TransactionRestService(new RabbitMqQueue("RabbitMQ"));

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionRequestResponse createTransaction(TransactionRequest request) {
        return transactionRestService.createTransactionRequest(request);
    }


}
