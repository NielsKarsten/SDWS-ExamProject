import dk.dtu.sdws.group3.connector.AccountServiceConnector;
import dk.dtu.sdws.group3.models.User;
import dtu.ws.fastmoney.Account;
import messaging.Event;
import messaging.MessageQueue;

import java.util.HashMap;
import java.util.UUID;

public class MockAccountServiceConnector extends AccountServiceConnector {

    private HashMap<UUID, String> bankAccounts = new HashMap<>();

    public MockAccountServiceConnector(MessageQueue q) {
        super(q);
    }

    public void addUser(UUID userId, String bankId) {
        bankAccounts.put(userId, bankId);
    }

    @Override
    public String getUserBankAccountFromId(UUID id) {
        if (bankAccounts.get(id) != null)
            return bankAccounts.get(id);

        return UUID.randomUUID().toString();
    }
}
