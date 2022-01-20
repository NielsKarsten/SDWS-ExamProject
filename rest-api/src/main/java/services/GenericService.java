package services;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import messaging.Event;
import messaging.MessageQueue;

public class GenericService {
	protected MessageQueue queue;
	protected Map<UUID, CompletableFuture<Object>> completableFutures;
	
	public GenericService(MessageQueue q) {
		q = queue;
		completableFutures = new ConcurrentHashMap<UUID, CompletableFuture<Object>>();
	}
	
	protected <T> void genericHandler(Event e, Class<T> argType) {
		UUID correlationId = e.getCorrelationId();
		T arg = e.getArgument(0, argType);
		CompletableFuture<Object> completableFuture = completableFutures.get(correlationId);
		completableFuture.complete(arg);
	}
	
	protected <T> void genericErrorHandler(Event e, Class<T> argType, Exception ex) {
		UUID correlationId = e.getCorrelationId();
		T arg = e.getArgument(0, argType);
		CompletableFuture<Object> completableFuture = completableFutures.get(correlationId);
		completableFuture.completeExceptionally(ex);
	}

	protected Object buildCompletableFutureEvent(Object eventObject, String eventTopic) {
		CompletableFuture<Object> completableFuture = new CompletableFuture<Object>();

		UUID correlationId = UUID.randomUUID();
		Event event = new Event(correlationId, eventTopic, new Object[] { eventObject });

		completableFutures.put(correlationId, completableFuture);
		queue.publish(event);
		return completableFutures.get(correlationId).join();
	}
	
	protected boolean verifyUserExists(UUID userId) {
		return (boolean) buildCompletableFutureEvent(userId,"VerifyUserAccountExistsRequest");
	}
}
