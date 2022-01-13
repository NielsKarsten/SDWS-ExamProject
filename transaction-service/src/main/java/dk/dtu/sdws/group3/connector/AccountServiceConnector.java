package dk.dtu.sdws.group3.connector;

import dk.dtu.sdws.group3.models.User;
import messaging.Event;
import messaging.MessageQueue;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AccountServiceConnector {

    MessageQueue queue;

    private CompletableFuture<User> user;

    public AccountServiceConnector(MessageQueue q) {
        this.queue = q;

        this.queue.addHandler("GetAccountFromIdResponse", this::handleGetAccountFromIdResponse);
    }

    public User getUserFromId(UUID id) {
        user = new CompletableFuture<>();
        Event e = new Event("GetAccountFromIdRequest", new Object[]{id});
        this.queue.publish(e);
        return user.join();
    }

    public void handleGetAccountFromIdResponse(Event event) {
        User u = event.getArgument(0, User.class);
        user.complete(u);
    }
}
