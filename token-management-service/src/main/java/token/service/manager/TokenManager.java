package token.service.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Map.Entry;

public class TokenManager {
    protected HashMap<UUID, List<UUID>> tokens;

    public TokenManager() {
        this.tokens = new HashMap<UUID, List<UUID>>();
    }

    public void addToken(UUID userId, UUID token) {
        boolean customerExists = tokens.containsKey(userId);
        if (!customerExists) {
            tokens.put(userId, new ArrayList<UUID>());
        }
        tokens.get(userId).add(token);
    }

    public void addTokens(UUID userId, List<UUID> generatedTokens) {
        boolean customerExists = tokens.containsKey(userId);
        if (!customerExists) {
            tokens.put(userId, new ArrayList<UUID>());
        }
        tokens.get(userId).addAll(generatedTokens);
    }

    public List<UUID> getUserTokens(UUID userId) throws NullPointerException {
        return tokens.get(userId);
    }

    public UUID getTokenOwner(UUID token) throws NullPointerException {
        for (Entry<UUID, List<UUID>> customer : tokens.entrySet()) {
            boolean isCustomerOwnerOfToken = customer.getValue().contains(token);
            if (isCustomerOwnerOfToken) {
                return customer.getKey();
            }
        }
        throw new NullPointerException("ERROR: Token has no owner");
    }

    public void removeToken(UUID customerId, UUID tokenId) throws NullPointerException {
        try {
            tokens.get(customerId).remove(tokenId);
        } catch (NullPointerException e) {
            throw new NullPointerException("ERROR: Customer does not have any tokens");
        }
    }

}
