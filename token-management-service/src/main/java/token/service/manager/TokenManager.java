package token.service.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map.Entry;

/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Kirkholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Niels Bisgaard-Bohr
 */
public class TokenManager {
    protected Map<UUID, List<UUID>> tokens;

    public TokenManager() {
        this.tokens = new ConcurrentHashMap<UUID, List<UUID>>();
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

    public List<UUID> getUserTokens(UUID userId) {
        if (tokens.get(userId) != null)
            return tokens.get(userId);
        return new ArrayList<>();
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
    public void removeTokens(UUID customerId, List<UUID> tokensToBeRemoved) throws NullPointerException {
        try {
        	if(tokens.containsKey(customerId))
        		tokens.get(customerId).removeAll(tokensToBeRemoved);
        } catch (NullPointerException e) {
            throw new NullPointerException("ERROR: Customer does not have any tokens");
        }
    }

}
