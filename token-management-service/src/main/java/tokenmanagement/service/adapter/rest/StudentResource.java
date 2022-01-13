package tokenmanagement.service.adapter.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import tokenmanagement.service.Student;
import tokenmanagement.service.StudentRegistrationService;

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
