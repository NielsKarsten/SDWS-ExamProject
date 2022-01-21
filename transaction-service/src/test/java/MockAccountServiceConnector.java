import dtu.ws.fastmoney.Account;
import messaging.Event;
import transaction.service.connector.*;
import messaging.MessageQueue;
import transaction.service.connector.AccountServiceConnector;
import transaction.service.models.User;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Gustav Lintrup Kirkholt
 */
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
