package dk.dtu.sdws.group3;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import dtu.ws.fastmoney.*;

public class TransactionService {

    private BankService bank;
    public void pay(UUID merchantId, UUID token, BigDecimal amount) {


        String merchantAccount = AccountService.getAccount(merchantId);
        token = TokenService.validateToken(token);
        String customerAccount = AccountService.getAccount(token);
        if (checkBalance(amount, merchantAccount) == true) {
            bank.transferMoneyFromTo
        } return;

        // Transaction transaction = new Transaction(merchantAccount, customerAccount, amount);
        // saveTransaction(transaction);




        // 1. Contact account service to get bank account associated with merchant id
        // 2. Validate token with token service and get user account associated with token
        // 3. Get bank account from account service associated with user account
        // 4. Make payment in bank
        // 5. Return result to caller
    }

    public boolean checkBalance(BigDecimal amount, String customerAccount) {
        BigDecimal accountBalance = AccountService.getBalance(customerAccount);
        if (amount.intValue() < accountBalance.intValue()) {
            return true;
        } return false;
    }

    public void saveTransaction(Transaction transaction){

    }

   // public Account getBankAccount(UUID merchantId) throws BankServiceException_Exception {
    //  Account account = null;
    //  String accountId = AccountService.getAccount(merchantId);
    //  bank.getAccount(accountId);
    //   return account;
    // }

    public List<Transaction> getTransactions () {

    }

}
