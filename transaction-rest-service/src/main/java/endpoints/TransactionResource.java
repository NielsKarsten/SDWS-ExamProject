package endpoints;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import messaging.Event;
import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import models.TransactionRequestResponse;
import models.TransactionRequest;
import services.TransactionRestService;

import java.util.concurrent.CompletableFuture;

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
