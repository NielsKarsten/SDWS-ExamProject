package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRestRequestResponse {
    private boolean successful;
    private String errorMessage;

    public TransactionRestRequestResponse(boolean s) {
        this.successful = s;
    }
}
