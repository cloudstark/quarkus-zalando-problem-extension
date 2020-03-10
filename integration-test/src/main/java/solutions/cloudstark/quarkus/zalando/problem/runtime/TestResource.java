package solutions.cloudstark.quarkus.zalando.problem.runtime;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.net.URI;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.Max;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

@Path("/test")
@Produces(APPLICATION_JSON)
public class TestResource {

  static final int DIVIDEND_MAX_VALUE = 100;

  static final String STRANGE_PROBLEM_TITLE = "Strange problem!";

  static final String RUNTIME_EXCEPTION_MESSAGE = "This is a runtime exception!";

  @GET
  @Path("/divide/{a}/{b}")
  public int divide(
      @PathParam("a") @Max(DIVIDEND_MAX_VALUE) final int a, @PathParam("b") final int b) {
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
    throw Problem.builder()
        .withStatus(Status.BAD_REQUEST)
        .withTitle(STRANGE_PROBLEM_TITLE)
        .with("foo", "bar")
        .build();
  }

  @GET
  @Path("/restricted")
  @RolesAllowed("Tester")
  public boolean restricted() {
    return true;
  }

  @GET
  @Path("/chain")
  public void causalChain() {
    throw Problem.builder()
        .withStatus(Status.BAD_REQUEST)
        .withTitle("Order failed")
        .withType(URI.create("https://example.org/order-failed"))
        .withCause(
            Problem.builder()
                .withType(URI.create("https://example.org/out-of-stock"))
                .withTitle("Out of Stock")
                .withStatus(Status.BAD_REQUEST)
                .withDetail("Item B00027Y5QG is no longer available")
                .build())
        .build();
  }
}
