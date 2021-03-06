package tokenmanagement.service;

import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main: Niels Bisgaard-Bohr
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenRequest implements Serializable {
    private UUID userId;
    private int amount;
}
