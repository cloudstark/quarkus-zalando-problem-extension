/*
 *    Copyright 2020 SMB GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package solutions.cloudstark.quarkus.zalando.problem.runtime;

import io.quarkus.runtime.LaunchMode;
import io.vertx.core.http.HttpServerRequest;
import java.net.URI;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

  public static final String STACKTRACE_KEY = "stacktrace";

  static final String HTTP_METHOD_KEY = "http_method";

  @Context HttpServerRequest request;

  @Context UriInfo uriInfo;

  @Override
  public Response toResponse(final Throwable throwable) {
    final ProblemBuilder problemBuilder =
        Problem.builder()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .withTitle(throwable.getMessage())
            .withDetail(throwable.toString())
            .with(HTTP_METHOD_KEY, request.rawMethod())
            .withInstance(URI.create(uriInfo.getPath()));
    if (LaunchMode.current().isDevOrTest()) {
      problemBuilder.with(STACKTRACE_KEY, throwable.getStackTrace());
    }

    final ThrowableProblem throwableProblem = problemBuilder.build();

    return Response.status(throwableProblem.getStatus().getStatusCode())
        .type(MediaType.APPLICATION_PROBLEM_JSON)
        .entity(throwableProblem)
        .build();
  }
}
