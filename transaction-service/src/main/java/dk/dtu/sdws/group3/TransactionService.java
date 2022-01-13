package dk.dtu.sdws.group3;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionService {

    public void pay(UUID merchantId, UUID token, BigDecimal amount) {
        // 1. Contact account service to get bank account associated with merchant id
        // 2. Validate token with token service and get user account associated with token
        // 3. Get bank account from account service associated with user account
        // 4. Make payment in bank
        // 5. Return result to caller
    }

}
