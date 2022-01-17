import dtu.ws.fastmoney.Account;
import messaging.Event;
import messaging.MessageQueue;
import transaction.service.connector.AccountServiceConnector;
import transaction.service.models.User;

import java.util.UUID;

public class MockAccountServiceConnector extends AccountServiceConnector {
    public MockAccountServiceConnector(MessageQueue q) {
        super(q);
    }

    @Override
    public User getUserFromId(UUID id) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setAccount(new Account());
        return u;
    }
}
