package io.github.ismailbaskin.quarkus.zalando.problem.runtime;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.IOException;
import java.net.URI;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.Max;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

/**
 * Test resource.
 */
@Path("/test")
@Produces(APPLICATION_JSON)
public class TestResource {

  static final int DIVIDEND_MAX_VALUE = 100;

  static final String STRANGE_PROBLEM_TITLE = "Strange problem!";

  static final String RUNTIME_EXCEPTION_MESSAGE = "This is a runtime exception!";

  static final String IO_EXCEPTION1_MESSAGE = "This is the first io exception!";

  static final String IO_EXCEPTION2_MESSAGE = "This is the second io exception!";

  /**
   * The endpoint for division problems.
   */
  @GET
  @Path("/divide/{a}/{b}")
  public int divide(
      @PathParam("a") @Max(DIVIDEND_MAX_VALUE) final int a, @PathParam("b") final int b) {
    return a / b;
  }

  /**
   * The endpoint for a RuntimeException.
   */
  @GET
  @Path("/exception")
  public String exception() {
    final IOException causeOfCause = new IOException(IO_EXCEPTION2_MESSAGE);
    final IOException cause = new IOException(IO_EXCEPTION1_MESSAGE, causeOfCause);
    throw new RuntimeException(RUNTIME_EXCEPTION_MESSAGE, cause);
  }

  /**
   * The endpoint for a Problem instance.
   */
  @GET
  @Path("/problem")
  public String problem() {
    throw Problem.builder()
        .withStatus(Status.BAD_REQUEST)
        .withTitle(STRANGE_PROBLEM_TITLE)
        .with("foo", "bar")
        .build();
  }

  /**
   * The endpoint for auth problem.
   */
  @GET
  @Path("/restricted")
  @RolesAllowed("Tester")
  public boolean restricted() {
    return true;
  }

  /**
   * The endpoint for chained problem.
   */
  @GET
  @Path("/chain")
  public String causalChain() {
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
                .withCause(
                    Problem.builder()
                        .withType(URI.create("https://example.org/more-out-of-stock"))
                        .withTitle("Extreme out of Stock")
                        .withStatus(Status.BAD_REQUEST)
                        .withDetail("Item ABC0123456 is no longer available")
                        .build())
                .build())
        .build();
  }
}
