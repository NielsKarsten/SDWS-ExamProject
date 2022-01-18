import account.service.services.AccountService;
import messaging.implementations.RabbitMqQueue;

public class StartUp {
	public static void main(String[] args) {
		RabbitMqQueue messageQueue = new RabbitMqQueue("rabbitMq");
		AccountService service = new AccountService(messageQueue);
	}
}
