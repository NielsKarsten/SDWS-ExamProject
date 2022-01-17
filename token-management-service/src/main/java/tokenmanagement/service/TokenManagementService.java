// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

// Handle events fra transaction. Sende response efter consume.

package tokenmanagement.service;

import messaging.Event;
import messaging.MessageQueue;
import tokenmanagement.service.exceptions.TokenException;

import java.util.*;

public class TokenManagementService {
    private MessageQueue queue;
    private HashMap<UUID, List<Token>> archiveTokens;
    private HashMap<UUID, List<Token>> tokens;

    private final String NoTokenExceptionMsg = "Customer does not have any tokens";
    private final String HaveTooManyTokensExceptionMsg = "Request denied - you can only request tokens when you have 1 token";
    private final String RequestTooManyTokensExceptionMsg = "Request denied - you can only request between one and 5 tokens at a time";
    private final String InvalidToken ="Request denied - invalid token";


    public TokenManagementService(MessageQueue q){
        queue = q;
        queue.addHandler("TokensRequested", this::handleTokensRequested);
        //queue.addHandler("TokenToCustomerIdRequested", this::handleTokenToCustomerIdRequested);
        this.archiveTokens = new HashMap<>();
        this.tokens = new HashMap<>();
    }

    public List<Token> requestTokens(UUID customerId, int tokenAmount) throws TokenException {
        verifyRequestTokenInput(customerId, tokenAmount);

        if (findCustomersTokens(customerId) == null) {
            List<Token> tokenList = new ArrayList<>();
            generateTokens(customerId, tokenAmount, tokenList);
        } else {
            List<Token> localList = tokens.get(customerId);
            generateTokens(customerId, tokenAmount, localList);
        }
        return findCustomersTokens(customerId);
    }

    public List<Token> findCustomersTokens(UUID customerId) {
        return tokens.get(customerId);
    }

    public UUID findCustomerId(Token token) throws TokenException {
        UUID customerIdResult = null;
        for (Map.Entry<UUID,List<Token>> entry : tokens.entrySet()) {
            if (entry.getValue().contains(token)) {
                customerIdResult = entry.getKey();
                consumeCustomerToken(customerIdResult);
                break;
            }
        }
        if(customerIdResult == null){
            throw new TokenException(InvalidToken);
        }
        return customerIdResult;
    }

    public UUID findCustomerId(UUID tokenId) throws TokenException {
        UUID customerIdResult = null;
        for (Map.Entry<UUID,List<Token>> entry : tokens.entrySet()) {
            for (Token token : entry.getValue()) {
                if (token.getToken() == tokenId) {
                    customerIdResult = entry.getKey();
                    consumeCustomerToken(customerIdResult);
                    break;
                }
            }
        }
        if(customerIdResult == null){
            throw new TokenException(InvalidToken);
        }
        return customerIdResult;
    }

    public void consumeCustomerToken(UUID customerId) throws TokenException {
        List<Token> tokenList = findCustomersTokens(customerId);
        if (tokenList.size() == 0) {
            throw new TokenException(NoTokenExceptionMsg);
        }
        Token token = tokenList.remove(0);
        tokens.put(customerId, tokenList);
        addToArchive(customerId, token);
    }

    public void handleTokensRequested(Event e){
        var correlationID = e.getCorrelationId();
        var customerId = e.getArgument(1, UUID.class);
        var tokenAmount = e.getArgument(2, int.class);
        List<Token> tokenList = null;
        try {
            tokenList = requestTokens(customerId, tokenAmount);
        } catch (TokenException tokenException) {
            tokenException.printStackTrace();
        }
        Event event = new Event(correlationID, "TokensIssued", new Object[] { tokenList });
        queue.publish(event);
    }

    //public void handleTokenToCustomerIdRequested(Event e) {
    //    var correlationID = e.getCorrelationId();
    //    var tokenId = e.getArgument(0, UUID.class);
    //    Token token = findToken(tokenId);
    //    UUID customerId = null;
    //    try {
    //        customerId = findCustomerId(token);
    //    } catch (TokenException tokenException) {
    //        tokenException.printStackTrace();
    //    }
    //    Event event = new Event(correlationID, "TokenToCustomerIdResponse", new Object[] { customerId });
    //    queue.publish(event);
    //}

    private void addToArchive(UUID customerId, Token token) {
        List<Token> archive = new ArrayList<>();
        archive.add(token);
        archiveTokens.put(customerId, archive);
    }

    private void generateTokens(UUID customerId, int tokenAmount, List<Token> tokenList) {
        for (int i = 0; i < tokenAmount; i++) {
            tokenList.add(new Token());
        }
        tokens.put(customerId, tokenList);
    }

    private void verifyRequestTokenInput(UUID customerId, int tokenAmount) throws TokenException {
        if (tokenAmount > 5 || tokenAmount < 1) {
            throw new TokenException(RequestTooManyTokensExceptionMsg);
        }
        if (findCustomersTokens(customerId) != null && findCustomersTokens(customerId).size() > 1) {
            throw new TokenException(HaveTooManyTokensExceptionMsg);
        }
    }
}
