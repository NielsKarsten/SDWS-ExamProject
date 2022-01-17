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

    public boolean pay(String userBank, String merchantBank, BigDecimal amount, String description) {
        try {
            bank.transferMoneyFromTo(merchantBank, userBank, amount, description);
        } catch (BankServiceException_Exception e) {
            errorMessage = e.getMessage();
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    public TransactionRequestResponse pay(UUID customer, UUID merchant, BigDecimal amount) {
        String customerBankAccount = accountServiceConnector.getUserBankAccountFromId(customer);
        String merchantBankAccount = accountServiceConnector.getUserBankAccountFromId(merchant);

        TransactionRequestResponse trxReqResp = new TransactionRequestResponse();
        String description = "Payment of " + amount + " to merchant " + merchantBankAccount;
        if (this.pay(customerBankAccount, merchantBankAccount, amount, description)) {
            trxReqResp.setSuccessful(true);
            Transaction t = new Transaction(merchant, customer, amount, description);
            TransactionStore.getInstance().addTransaction(t);
        } else {
            trxReqResp.setSuccessful(false);
            trxReqResp.setErrorMessage(errorMessage);
        }

        return trxReqResp;
    }

    public void handleTransactionRequestEvent(Event event) {
        TransactionRequest request = event.getArgument(0, TransactionRequest.class);
        UUID correlationId = event.getCorrelationId();

        String customerId = tokenServiceConnector.getUserIdFromToken(UUID.fromString(request.getUserToken()));
        TransactionRequestResponse trxReqResp = this.pay(UUID.fromString(customerId), UUID.fromString(request.getMerchantId()), request.getAmount());
        Event e = new Event(correlationId, "TransactionRequestResponse", new Object[]{trxReqResp});
        this.queue.publish(e);
    }

    public void handleTransactionsByUserIdRequest(Event event) {
        UUID userId = event.getArgument(0, UUID.class);
        UUID correlationId = event.getCorrelationId();
        Map<UUID, Transaction> transactionMap = TransactionStore.getInstance().getTransactions();
        List<Transaction> transactionList = new ArrayList<>();

        for (Transaction t : transactionMap.values()) {
            if (t.getCustomer() == userId || t.getMerchant() == userId)
                transactionList.add(t);
        }

        Event outgoingEvent = new Event(correlationId,"TransactionsByUserIdResponse", new Object[]{userId, transactionList});
        this.queue.publish(outgoingEvent);
    }
}
