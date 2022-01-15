package services;

import messaging.Event;
import messaging.MessageQueue;
import models.TransactionRequest;
import models.TransactionRequestResponse;

import java.util.concurrent.CompletableFuture;

public class TransactionRestService {

    private MessageQueue queue;
    private CompletableFuture<TransactionRequestResponse> response;

    public TransactionRestService(MessageQueue q) {
        queue = q;
        this.queue.addHandler("TransactionRequestResponse", this::handleTransactionRequestResponse);

    }

    public TransactionRequestResponse createTransactionRequest(TransactionRequest request) {
        var event = new Event("TransactionRequest", new Object[] {request});
        this.queue.publish(event);
        response = new CompletableFuture<>();
        return response.join();
    }

    public void handleTransactionRequestResponse(Event event) {
        TransactionRequestResponse r = event.getArgument(0, TransactionRequestResponse.class);
        response.complete(r);
    }

}
