import dtu.ws.fastmoney.Account;
import messaging.Event;
import transaction.service.connector.*;
import messaging.MessageQueue;
import transaction.service.connector.AccountServiceConnector;
import transaction.service.models.User;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MockAccountServiceConnector extends AccountServiceConnector {

    private HashMap<UUID, String> bankAccounts = new HashMap<>();

    public MockAccountServiceConnector(MessageQueue q) {
        super(q);
    }

    public void addUser(UUID userId, String bankId) {
        bankAccounts.put(userId, bankId);
    }
    
    @Override
    public boolean userExists(UUID userId) {
        return bankAccounts.containsKey(userId);
    }
    
    @Override
    public String getUserBankAccountFromId(UUID id) {
        if (bankAccounts.get(id) != null)
            return bankAccounts.get(id);

        return UUID.randomUUID().toString();
    }
}
