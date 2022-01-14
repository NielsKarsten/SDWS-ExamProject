import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import messaging.Event;
import messaging.MessageQueue;

@Path("/transactions")
public class TransactionResource {

    MessageQueue queue;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void createTransaction(TransactionRestRequest request) {
        var event = new Event("TransactionRequest", new Object[] {request});
        this.queue.publish(event);

    }


}
