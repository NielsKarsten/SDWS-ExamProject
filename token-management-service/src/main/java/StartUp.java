import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import tokenmanagement.service.TokenManagementService;

public class StartUp {
    public static void main(String[] args) {
        MessageQueue q = new RabbitMqQueue("rabbitMq");
        new TokenManagementService(q);
    }
}
