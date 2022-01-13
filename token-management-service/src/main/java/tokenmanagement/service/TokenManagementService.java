// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

package tokenmanagement.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TokenManagementService {
    private HashMap<String, List<Token>> tokens;

    public TokenManagementService() {
        this.tokens = new HashMap<>();
    }

    public void requestTokens(String customerId, int tokenAmount) throws TokenLimitException, TooManyTokenRequestException {
        verifyRequestTokenInput(customerId, tokenAmount);

        if (findCustomersTokens(customerId) == null) {
            List<Token> tokenList = new ArrayList<>();
            generateTokens(customerId, tokenAmount, tokenList);
        } else {
            List<Token> localList = tokens.get(customerId);
            generateTokens(customerId, tokenAmount, localList);
        }
    }

    public List<Token> findCustomersTokens(String customerId) {
        return tokens.get(customerId);
    }

    public String findCustomerId(Token token) {
        return "implement";
    }

    private void generateTokens(String customerId, int tokenAmount, List<Token> tokenList) throws TokenLimitException {
        for (int i = 0; i < tokenAmount; i++) {
            tokenList.add(new Token());
        }
        tokens.put(customerId, tokenList);
    }

    private void verifyRequestTokenInput(String customerId, int tokenAmount) throws TooManyTokenRequestException, TokenLimitException {
        if (tokenAmount > 5 || tokenAmount < 1) {
            throw new TokenLimitException("Request denied - you can only request between one and 5 tokens at a time");
        }
        if (findCustomersTokens(customerId) != null && findCustomersTokens(customerId).size() > 1) {
            throw new TooManyTokenRequestException("Request denied - you can only request tokens when you have 1 token");
        }
    }
}
