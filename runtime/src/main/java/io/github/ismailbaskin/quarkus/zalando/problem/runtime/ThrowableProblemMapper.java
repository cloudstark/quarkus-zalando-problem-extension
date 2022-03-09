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

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.ThrowableProblem;

/**
 * A {@link ExceptionMapper} for {@link Throwable}s.
 */
@Provider
public class ThrowableProblemMapper implements ExceptionMapper<ThrowableProblem> {

  private static final Logger LOGGER = Logger.getLogger(ThrowableProblemMapper.class);

  @Override
  public Response toResponse(final ThrowableProblem throwableProblem) {
    LOGGER.debug("Mapping " + throwableProblem, throwableProblem);

    StatusType status = throwableProblem.getStatus();
    int statusCode = Status.INTERNAL_SERVER_ERROR.getStatusCode();
    if (status != null) {
      statusCode = status.getStatusCode();
    }

    return Response.status(statusCode)
        .type(MediaType.APPLICATION_PROBLEM_JSON)
        .entity(throwableProblem)
        .build();
  }
}
