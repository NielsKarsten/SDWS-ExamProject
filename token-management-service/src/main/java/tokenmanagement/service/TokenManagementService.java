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
        if (tokenAmount > 5 || tokenAmount < 1) {
            throw new TokenLimitException();
        }
        if (findCustomersTokens(customerId) != null && findCustomersTokens(customerId).size() > 1) {
            throw new TooManyTokenRequestException();
        }

        if (findCustomersTokens(customerId) == null) {
            List<Token> tokenList = new ArrayList<>();
            generateTokens(customerId, tokenAmount, tokenList);
        } else {
            List<Token> localList = tokens.get(customerId);
            generateTokens(customerId, tokenAmount, localList);
        }
    }

    private void generateTokens(String customerId, int tokenAmount, List<Token> tokenList) throws TokenLimitException {
        for (int i = 0; i < tokenAmount; i++) {
            tokenList.add(new Token());
        }
        tokens.put(customerId, tokenList);
    }

    public List<Token> findCustomersTokens(String customerId) {
        return tokens.get(customerId);
    }
}
