package transaction.service.models;

import dtu.ws.fastmoney.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
/**
 *
 * Main: Gustav Lintrup Kirkholt
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private Account account;
}
