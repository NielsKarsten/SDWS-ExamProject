package handling;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import messaging.Event;
import messaging.MessageQueue;

public abstract class GenericHandler {
	private MessageQueue queue;
	private Map<UUID, CompletableFuture<Object>> completableFutures;

	public GenericHandler(MessageQueue q) {
		queue = q;
		completableFutures = new ConcurrentHashMap<UUID, CompletableFuture<Object>>();
	}

	public <T> void genericHandler(Event e) {
		System.out.println("genericErrorHandler Invoked");
		UUID correlationId = e.getCorrelationId();
		Object arg = e.getArgument(0, Object.class);
		CompletableFuture<Object> completableFuture = completableFutures.get(correlationId);
		completableFuture.complete(arg);
	}

	public <T> void genericErrorHandler(Event e) {
		System.out.println("genericErrorHandler Invoked");
		UUID correlationId = e.getCorrelationId();
		Exception ex = e.getArgument(0, Exception.class);
		CompletableFuture<Object> completableFuture = completableFutures.get(correlationId);
		completableFuture.completeExceptionally(ex);
	}

	protected Object buildCompletableFutureEvent(Object eventObject, String eventTopic) {
		System.out.println("buildCompletableFutureEvent NO CORRELATION ID Invoked");
		UUID correlationId = UUID.randomUUID();
		return buildCompletableFutureEvent(correlationId,eventObject,eventTopic);
	}
	
	protected Object buildCompletableFutureEvent(UUID correlationId, Object eventObject, String eventTopic) {
		System.out.println("buildCompletableFutureEvent Invoked");
		CompletableFuture<Object> completableFuture = new CompletableFuture<Object>();		
		System.out.println("buildCompletableFutureEvent 2");
		completableFutures.put(correlationId, completableFuture);
		System.out.println("buildCompletableFutureEvent 3");
		publishNewEvent(correlationId, eventTopic, eventObject);
		System.out.println("buildCompletableFutureEvent 4");
		return completableFutures.get(correlationId).join();
	}
	
	protected void publishNewEvent(UUID correlationId, String topic, Object object) {
		Event event = new Event(correlationId, topic, new Object[] { object });
		queue.publish(event);
	}
	
	protected void publishNewEvent(Event e, String topic, Object object) {
		UUID correlationId = e.getCorrelationId();
		Event event = new Event(correlationId, topic, new Object[] { object });
		queue.publish(event);
	}
	
	protected void addHandler(String eventName, Consumer<Event> handler) {
		this.queue.addHandler(eventName, handler);
	}

}
