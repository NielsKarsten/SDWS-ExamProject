package token.service.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ActiveTokenManager extends TokenManager {

    public ActiveTokenManager() {
        super();
    }

    public List<UUID> generateTokens(UUID userId, int tokenAmount) {
        List<UUID> generatedTokens = new ArrayList<>();
        while (generatedTokens.size() < tokenAmount) {
            UUID token = UUID.randomUUID();
            generatedTokens.add(token);
        }
        addTokens(userId, generatedTokens);
        return generatedTokens;
    }
}
