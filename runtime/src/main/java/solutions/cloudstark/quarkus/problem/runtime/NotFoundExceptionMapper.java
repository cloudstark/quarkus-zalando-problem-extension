package solutions.cloudstark.quarkus.problem.runtime;

import java.net.URI;
import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

@Provider
@Priority(Priorities.USER)
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

  @Context
  private UriInfo uriInfo;

  @Override
  public Response toResponse(NotFoundException exception) {
    ThrowableProblem throwableProblem =
        Problem.builder()
            .withStatus(Status.NOT_FOUND)
            .withTitle(exception.getMessage())
            .withDetail(exception.toString())
            .withInstance(URI.create(uriInfo.getPath()))
            .build();
    return Response.status(throwableProblem.getStatus().getStatusCode())
        .type(MediaType.APPLICATION_PROBLEM_JSON)
        .entity(throwableProblem)
        .build();
  }
}
