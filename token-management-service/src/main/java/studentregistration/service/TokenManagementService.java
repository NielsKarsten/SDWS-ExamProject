// Authors:
// Theodor Guttesen s185121
// Main: Christian Gernsøe s163552

package studentregistration.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TokenManagementService {
    private HashMap<String, List<Token>> tokens;

    public TokenManagementService() {
        this.tokens = new HashMap<>();
    }

    public void generateTokens(String customerId, int tokenAmount) {
        List<Token> tokenList = new ArrayList<>();
        for (int i = 0; i < tokenAmount; i++) {
            tokenList.add(new Token());
        }
        tokens.put(customerId, tokenList);
    }

    public List<Token> findCustomersTokens(String customerId) {
        return tokens.get(customerId);
    }
}
