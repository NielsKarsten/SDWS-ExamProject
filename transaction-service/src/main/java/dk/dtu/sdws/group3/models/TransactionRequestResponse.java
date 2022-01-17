package dk.dtu.sdws.group3.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
