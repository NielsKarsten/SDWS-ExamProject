package models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class TransactionRequest {
    private UUID merchantId;
    private UUID userToken;
    private BigDecimal amount;
}