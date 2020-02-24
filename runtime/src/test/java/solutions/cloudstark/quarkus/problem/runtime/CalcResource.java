package solutions.cloudstark.quarkus.problem.runtime;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import java.util.Collections;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

@Path("/calc")
@Produces(APPLICATION_JSON)
public class CalcResource {

  static final String STRANGE_PROBLEM_TITLE = "Strange problem!";

  static final String RUNTIME_EXCEPTION_MESSAGE = "This is a runtime exception!";

  @Context UriInfo uriInfo;

  @GET
  @Path("/divide/{a}/{b}")
  public int divide(@PathParam("a") final int a, @PathParam("b") final int b) {
    return a / b;
  }

  @GET
  @Path("/exception")
  public void exception() {
    throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE);
  }

  @GET
  @Path("/problem")
  public void problem() {
    throw Problem.builder().withStatus(Status.BAD_REQUEST).withTitle(STRANGE_PROBLEM_TITLE).build();
  }

  @GET
  @Path("/violation")
  public void constraintViolation() {
    throw new ConstraintViolationProblem(
        Status.BAD_REQUEST, Collections.singletonList(new Violation("name", "must not be null")));
  }

  @GET
  @Path("/auth")
  public void authenticate() {
    throw new UnauthorizedException();
  }

  @GET
  @Path("/forbidden")
  public void forbidden() {
    throw new ForbiddenException();
  }
}
