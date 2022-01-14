// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

// Lav IssueTokenService og flyt tokenmanagement service til (student id service)

package tokenmanagement.service;

import messaging.Event;
import messaging.MessageQueue;
import tokenmanagement.service.exceptions.TokenException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TokenManagementService {
    private MessageQueue queue;
    private HashMap<String, List<Token>> archiveTokens;
    private HashMap<String, List<Token>> tokens;

    private final String NoTokenExceptionMsg = "Customer does not have any tokens";
    private final String HaveTooManyTokensExceptionMsg = "Request denied - you can only request tokens when you have 1 token";
    private final String RequestTooManyTokensExceptionMsg = "Request denied - you can only request between one and 5 tokens at a time";


    public TokenManagementService(MessageQueue q){
        queue = q;
        queue.addHandler("TokensRequested", this::handleTokensRequested);
        this.archiveTokens = new HashMap<>();
        this.tokens = new HashMap<>();
    }

    public List<Token> requestTokens(String customerId, int tokenAmount) throws TokenException {
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

    public List<Token> findCustomersTokens(String customerId) {
        return tokens.get(customerId);
    }

    public String findCustomerId(Token token) throws TokenException {
        String customerIdResult = "";
        for (Map.Entry<String,List<Token>> entry : tokens.entrySet()) {
            if (entry.getValue().contains(token)) {
                customerIdResult = entry.getKey();
                consumeCustomerToken(customerIdResult);
                break;
            }
        }
        if(customerIdResult.equals("")){
            throw new TokenException("Request denied - invalid token");
        }
        return customerIdResult;
    }

    public void consumeCustomerToken(String customerId) throws TokenException {
        List<Token> tokenList = findCustomersTokens(customerId);
        if (tokenList.size() == 0) {
            throw new TokenException(NoTokenExceptionMsg);
        }
        Token token = tokenList.remove(0);
        tokens.put(customerId, tokenList);
        addToArchive(customerId, token);
    }

    public void handleTokensRequested(Event e){
        var customerId = e.getArgument(0, String.class);
        var tokenAmount = e.getArgument(1, int.class);
        List<Token> tokenList = null;
        try {
            tokenList = requestTokens(customerId, tokenAmount);
        } catch (TokenException tokenException) {
            tokenException.printStackTrace();
        }
        Event event = new Event("TokensIssued", new Object[] { tokenList });
        queue.publish(event);
    }

    private void addToArchive(String customerId, Token token) {
        List<Token> archive = new ArrayList<>();
        archive.add(token);
        archiveTokens.put(customerId, archive);
    }

    private void generateTokens(String customerId, int tokenAmount, List<Token> tokenList) {
        for (int i = 0; i < tokenAmount; i++) {
            tokenList.add(new Token());
        }
        tokens.put(customerId, tokenList);
    }

    private void verifyRequestTokenInput(String customerId, int tokenAmount) throws TokenException {
        if (tokenAmount > 5 || tokenAmount < 1) {
            throw new TokenException(RequestTooManyTokensExceptionMsg);
        }
        if (findCustomersTokens(customerId) != null && findCustomersTokens(customerId).size() > 1) {
            throw new TokenException(HaveTooManyTokensExceptionMsg);
        }
    }
}
