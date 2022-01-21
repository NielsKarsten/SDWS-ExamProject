package transaction.service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * Main: Gustav Lintrup Kirkholt
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestResponse {
    private boolean successful;
    private String errorMessage;

    public TransactionRequestResponse(boolean s) {
        this.successful = s;
    }
}
