package studentregistration.service.adapter.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import studentregistration.service.Student;
import studentregistration.service.StudentRegistrationService;

@Path("/students")
public class StudentResource {

	@POST
	@Consumes("application/json")
	@Produces("application/json")
	public Student registerStudent(Student student) {
		StudentRegistrationService service = new StudentRegistrationFactory().getService();
		return service.register(student);
	}
}
