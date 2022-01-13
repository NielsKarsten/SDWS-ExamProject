package dk.dtu.sdws.group3.services;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.UUID;

import dk.dtu.sdws.group3.connector.AccountServiceConnector;
import dk.dtu.sdws.group3.connector.TokenServiceConnector;
import dk.dtu.sdws.group3.models.Transaction;
import dk.dtu.sdws.group3.models.TransactionRequest;
import dk.dtu.sdws.group3.models.TransactionRequestResponse;
import dk.dtu.sdws.group3.models.User;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;

public class TransactionService {

    BankService bank;
    MessageQueue queue;
    TokenServiceConnector tokenServiceConnector;
    AccountServiceConnector accountServiceConnector;
    private final HashMap<Integer, Transaction> transactions = new HashMap<>();

    public TransactionService(MessageQueue queue, BankService bank) {
        this(queue, bank, new TokenServiceConnector(queue), new AccountServiceConnector(queue));
    }

    public TransactionService(MessageQueue queue, BankService bank, TokenServiceConnector tokenServiceConnector, AccountServiceConnector accountServiceConnector) {
        this.queue = queue;
        this.bank = bank;
        this.tokenServiceConnector = tokenServiceConnector;
        this.accountServiceConnector = accountServiceConnector;

        this.queue.addHandler("TransactionRequest", this::handleTransactionRequestEvent);
    }

    public boolean pay(User merchant, User customer, BigDecimal amount) {
        String description = "Payment of " + amount + " to merchant " + merchant.getAccount().getId();
        try {
            bank.transferMoneyFromTo(merchant.getAccount().getId(), customer.getAccount().getId(), amount, description);
        } catch (BankServiceException_Exception e) {
            System.out.println(e.getMessage());
            return false;
        }

        Transaction t = new Transaction(merchant, customer, amount, description);
        transactions.put(t.hashCode(), t);
        return true;
    }

    public void handleTransactionRequestEvent(Event event) {
        TransactionRequest request = event.getArgument(0, TransactionRequest.class);
        String customerId = tokenServiceConnector.getUserIdFromToken(UUID.fromString(request.getUserToken()));
        User customer = accountServiceConnector.getUserFromId(UUID.fromString(customerId));
        User merchant = accountServiceConnector.getUserFromId(UUID.fromString(request.getMerchantId()));

        TransactionRequestResponse trxReqResp = new TransactionRequestResponse();
        trxReqResp.setSuccessful(this.pay(merchant, customer, request.getAmount()));
        Event outgoingEvent = new Event("TransactionRequestResponse", new Object[]{trxReqResp});
        this.queue.publish(outgoingEvent);
    }

    public HashMap<Integer, Transaction> getTransactions () {
        return transactions;
    }
}
