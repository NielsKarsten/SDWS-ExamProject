package handling;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import messaging.Event;
import messaging.MessageQueue;
/**
 * @author Christian Gernsøe - S163552
 * @author Gustav Utke Kauman - S195396
 * @author Gustav Lintrup Krikholt - s164765
 * @author Niels Bisgaard-Bohr - S202745
 * @author Simon Pontoppidan - S144213
 * @author Theodor Peter Guttesen - S185121
 * @author Thomas Rathsach Strange - S153390
 *
 * Main: Thomas Rathsach Strange
 */
public abstract class GenericHandler {
	private MessageQueue queue;
	private Map<UUID, CompletableFuture<Object>> completableFutures;

	public GenericHandler(MessageQueue q) {
		queue = q;
		completableFutures = new ConcurrentHashMap<UUID, CompletableFuture<Object>>();
	}

	public <T> void genericHandler(Event e) {
		UUID correlationId = e.getCorrelationId();
		Object arg = e.getArgument(0, Object.class);
		CompletableFuture<Object> completableFuture = completableFutures.get(correlationId);
		completableFuture.complete(arg);
	}

	public <T> void genericErrorHandler(Event e) {
		UUID correlationId = e.getCorrelationId();
		Exception ex = e.getArgument(0, Exception.class);
		CompletableFuture<Object> completableFuture = completableFutures.get(correlationId);
		completableFuture.completeExceptionally(ex);
	}

	protected Object buildCompletableFutureEvent(Object eventObject, String eventTopic) {
		UUID correlationId = UUID.randomUUID();
		return buildCompletableFutureEvent(correlationId,eventObject,eventTopic);
	}
	
	protected Object buildCompletableFutureEvent(UUID correlationId, Object eventObject, String eventTopic) {
		CompletableFuture<Object> completableFuture = new CompletableFuture<Object>();		
		completableFutures.put(correlationId, completableFuture);
		publishNewEvent(correlationId, eventTopic, eventObject);
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
