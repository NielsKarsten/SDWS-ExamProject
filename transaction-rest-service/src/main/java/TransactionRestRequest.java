import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionRestRequest {
    private String merchantId;
    private String userToken;
    private BigDecimal amount;
}
