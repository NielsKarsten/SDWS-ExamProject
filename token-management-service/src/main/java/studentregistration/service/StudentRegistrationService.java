package studentregistration.service;

import java.util.concurrent.CompletableFuture;

import messaging.Event;
import messaging.MessageQueue;

public class StudentRegistrationService {

	private MessageQueue queue;
	private CompletableFuture<Student> registeredStudent;

	public StudentRegistrationService(MessageQueue q) {
		queue = q;
		queue.addHandler("StudentIdAssigned", this::handleStudentIdAssigned);
	}

	public Student register(Student s) {
		registeredStudent = new CompletableFuture<>();
		Event event = new Event("StudentRegistrationRequested", new Object[] { s });
		queue.publish(event);
		return registeredStudent.join();
	}

	public void handleStudentIdAssigned(Event e) {
		var s = e.getArgument(0, Student.class);
		registeredStudent.complete(s);
	}
}
