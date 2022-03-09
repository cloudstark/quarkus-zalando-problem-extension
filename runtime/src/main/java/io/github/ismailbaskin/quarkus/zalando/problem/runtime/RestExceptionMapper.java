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

package io.github.ismailbaskin.quarkus.zalando.problem.runtime;

import io.vertx.core.http.HttpServerRequest;
import java.net.URI;
import java.util.Optional;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

/**
 * A JAX-RS ExceptionMapper that maps {@link Throwable} to {@link Problem}.
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Throwable> {

  static final String HTTP_METHOD_KEY = "http_method";

  private static final Logger LOGGER = Logger.getLogger(RestExceptionMapper.class);

  @Context HttpServerRequest request;

  @Context UriInfo uriInfo;

  private static Optional<ThrowableProblem> createCausalChain(final Throwable throwable) {
    final Throwable cause = throwable.getCause();
    if (cause == null) {
      return Optional.empty();
    }

    final Optional<ThrowableProblem> optionalCausalChain = createCausalChain(cause);

    final ProblemBuilder problemBuilder =
        Problem.builder().withTitle(cause.getMessage()).withDetail(cause.toString());
    optionalCausalChain.ifPresent(problemBuilder::withCause);

    final ThrowableProblem problem = problemBuilder.build();
    return Optional.of(problem);
  }

  @Override
  public Response toResponse(final Throwable throwable) {
    LOGGER.debug("Mapping " + throwable, throwable);

    final Optional<ThrowableProblem> optionalCausalChain = createCausalChain(throwable);

    final ProblemBuilder problemBuilder =
        Problem.builder()
            .withStatus(Status.INTERNAL_SERVER_ERROR)
            .withTitle(throwable.getMessage())
            .withDetail(throwable.toString())
            .with(HTTP_METHOD_KEY, request.method().toString())
            .withInstance(URI.create(uriInfo.getPath()));
    optionalCausalChain.ifPresent(problemBuilder::withCause);

    final Problem problem = problemBuilder.build();

    return Response.status(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .type(MediaType.APPLICATION_PROBLEM_JSON)
        .entity(problem)
        .build();
  }
}
