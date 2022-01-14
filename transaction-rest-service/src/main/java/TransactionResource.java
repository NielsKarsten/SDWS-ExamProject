import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import messaging.Event;
import messaging.MessageQueue;

import java.util.concurrent.CompletableFuture;

@Path("/transactions")
public class TransactionResource {

    private MessageQueue queue;
    private CompletableFuture<TransactionRequestResponse> response;

    public TransactionResource () {
        this.queue.addHandler("TransactionRequestResponse", this::handleTransactionRequestResponse);

    }

    private void handleTransactionRequestResponse(Event event) {
        TransactionRequestResponse r = event.getArgument(0, TransactionRequestResponse.class);
        response.complete(r);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionRequestResponse createTransaction(TransactionRestRequest request) {
        var event = new Event("TransactionRequest", new Object[] {request});
        this.queue.publish(event);
        response = new CompletableFuture<>();
        return response.join();

    }


}
