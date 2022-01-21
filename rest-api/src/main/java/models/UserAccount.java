package models;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Main: Thomas Rathsach Strange
 */
@Data
@AllArgsConstructor
public class UserAccount {
	private UUID userId;
	private Account account;

}

