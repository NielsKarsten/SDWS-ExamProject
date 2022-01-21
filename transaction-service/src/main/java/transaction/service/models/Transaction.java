package transaction.service.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 *
 * Main: Gustav Utke Kauman
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private UUID merchant;
    private UUID customer;
    private BigDecimal amount;
    private String description;
    private UUID token;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        if (!merchant.equals(that.merchant)) return false;
        if (!(customer == null) && !customer.equals(that.customer)) return false;
        if (!amount.equals(that.amount)) return false;
        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        int result = merchant.hashCode();
        result = 31 * result + customer.hashCode();
        result = 31 * result + amount.hashCode();
        result = 31 * result + description.hashCode();
        return result;
    }
}
