package dk.dtu.sdws.group3.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionRequest {
    private String merchantId;
    private String userToken;
    private BigDecimal amount;
}
