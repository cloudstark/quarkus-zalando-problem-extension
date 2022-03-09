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
import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

/**
 * Maps {@link NotFoundException} to {@link Response}.
 */
@Provider
@Priority(Priorities.USER)
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

  private static final Logger LOGGER = Logger.getLogger(NotFoundExceptionMapper.class);

  @Context HttpServerRequest request;

  @Context UriInfo uriInfo;

  @Override
  public Response toResponse(final NotFoundException exception) {
    LOGGER.debug("Mapping " + exception, exception);

    final Problem throwableProblem =
        Problem.builder()
            .withStatus(Status.NOT_FOUND)
            .withTitle(exception.getMessage())
            .withDetail(exception.toString())
            .with(RestExceptionMapper.HTTP_METHOD_KEY, request.method().toString())
            .withInstance(URI.create(uriInfo.getPath()))
            .build();

    return Response.status(Status.NOT_FOUND.getStatusCode())
        .type(MediaType.APPLICATION_PROBLEM_JSON)
        .entity(throwableProblem)
        .build();
  }
}
