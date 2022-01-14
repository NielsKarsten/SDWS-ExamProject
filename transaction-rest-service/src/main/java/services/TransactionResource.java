package services;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import messaging.Event;
import messaging.MessageQueue;
import models.TransactionRestRequestResponse;
import models.TransactionRestRequest;

import java.util.concurrent.CompletableFuture;

@Path("/transactions")
public class TransactionResource {

    private MessageQueue queue;
    private CompletableFuture<TransactionRestRequestResponse> response;

    public TransactionResource () {
        this.queue.addHandler("TransactionRestRequestResponse", this::handleTransactionRequestResponse);

    }

    private void handleTransactionRequestResponse(Event event) {
        TransactionRestRequestResponse r = event.getArgument(0, TransactionRestRequestResponse.class);
        response.complete(r);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionRestRequestResponse createTransaction(TransactionRestRequest request) {
        var event = new Event("TransactionRestRequest", new Object[] {request});
        this.queue.publish(event);
        response = new CompletableFuture<>();
        return response.join();

    }


}
