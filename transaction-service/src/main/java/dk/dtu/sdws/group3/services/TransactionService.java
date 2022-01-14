package dk.dtu.sdws.group3.services;

import java.math.BigDecimal;
import java.util.*;

import dk.dtu.sdws.group3.connector.AccountServiceConnector;
import dk.dtu.sdws.group3.connector.TokenServiceConnector;
import dk.dtu.sdws.group3.models.*;
import dk.dtu.sdws.group3.persistance.TransactionStore;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import messaging.Event;
import messaging.MessageQueue;

public class TransactionService {

    private BankService bank;
    private MessageQueue queue;
    private TokenServiceConnector tokenServiceConnector;
    private AccountServiceConnector accountServiceConnector;

    private String errorMessage;

    public TransactionService(MessageQueue queue, BankService bank) {
        this(queue, bank, new TokenServiceConnector(queue), new AccountServiceConnector(queue));
    }

    public TransactionService(MessageQueue queue, BankService bank, TokenServiceConnector tokenServiceConnector, AccountServiceConnector accountServiceConnector) {
        this.queue = queue;
        this.bank = bank;
        this.tokenServiceConnector = tokenServiceConnector;
        this.accountServiceConnector = accountServiceConnector;

        this.queue.addHandler("TransactionRequest", this::handleTransactionRequestEvent);
        this.queue.addHandler("TransactionsByUserIdRequest", this::handleTransactionsByUserIdRequest);
    }

    public boolean pay(User merchant, User customer, BigDecimal amount) {
        String description = "Payment of " + amount + " to merchant " + merchant.getAccount().getId();
        try {
            bank.transferMoneyFromTo(merchant.getAccount().getId(), customer.getAccount().getId(), amount, description);
        } catch (BankServiceException_Exception e) {
            errorMessage = e.getMessage();
            System.out.println(e.getMessage());
            return false;
        }

        Transaction t = new Transaction(merchant, customer, amount, description);
        TransactionStore.getInstance().addTransaction(t);
        return true;
    }

    public void handleTransactionRequestEvent(Event event) {
        TransactionRequest request = event.getArgument(0, TransactionRequest.class);
        String customerId = tokenServiceConnector.getUserIdFromToken(UUID.fromString(request.getUserToken()));
        User customer = accountServiceConnector.getUserFromId(UUID.fromString(customerId));
        User merchant = accountServiceConnector.getUserFromId(UUID.fromString(request.getMerchantId()));

        TransactionRequestResponse trxReqResp = new TransactionRequestResponse();
        trxReqResp.setSuccessful(this.pay(merchant, customer, request.getAmount()));
        if (errorMessage != null) trxReqResp.setErrorMessage(errorMessage);
        Event outgoingEvent = new Event("TransactionRequestResponse", new Object[]{trxReqResp});
        this.queue.publish(outgoingEvent);
    }

    public void handleTransactionsByUserIdRequest(Event event) {
        UUID userId = event.getArgument(0, UUID.class);
        Map<UUID, Transaction> transactionMap = TransactionStore.getInstance().getTransactions();
        List<Transaction> transactionList = new ArrayList<>();

        for (Transaction t : transactionMap.values()) {
            if (t.getCustomer().getId() == userId || t.getMerchant().getId() == userId)
                transactionList.add(t);
        }

        Event outgoingEvent = new Event("TransactionsByUserIdResponse", new Object[]{userId, transactionList});
        this.queue.publish(outgoingEvent);
    }
}
