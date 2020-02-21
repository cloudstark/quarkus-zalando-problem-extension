package solutions.cloudstark.quarkus.problem.runtime;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.zalando.problem.ThrowableProblem;

@Provider
public class ThrowableProblemMapper implements ExceptionMapper<ThrowableProblem> {

  @Override
  public Response toResponse(final ThrowableProblem throwableProblem) {
    return Response.status(throwableProblem.getStatus().getStatusCode())
        .type(MediaType.APPLICATION_PROBLEM_JSON)
        .entity(throwableProblem)
        .build();
  }
}
