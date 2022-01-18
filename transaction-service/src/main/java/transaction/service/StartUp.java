package transaction.service;

import messaging.MessageQueue;
import messaging.implementations.RabbitMqQueue;
import transaction.service.services.TransactionService;

public class StartUp {
    public static void main(String[] args) {
        MessageQueue q = new RabbitMqQueue("rabbitMq");
        new TransactionService(q);
    }
}
