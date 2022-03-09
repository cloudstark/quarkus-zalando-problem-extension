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

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Priority;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.jboss.logging.Logger;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

/**
 * Maps {@link ConstraintViolationException} to {@link Response}.
 */
@Provider
@Priority(Priorities.USER)
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {

  private static final Logger LOGGER = Logger.getLogger(ConstraintViolationExceptionMapper.class);

  @Override
  public Response toResponse(final ConstraintViolationException exception) {
    LOGGER.debug("Mapping " + exception, exception);

    final List<Violation> violations =
        exception.getConstraintViolations().stream()
            .map(c -> new Violation(c.getPropertyPath().toString(), c.getMessage()))
            .collect(Collectors.toList());

    final Status httpStatus = Status.BAD_REQUEST;
    return Response.status(httpStatus.getStatusCode())
        .type(MediaType.APPLICATION_PROBLEM_JSON)
        .entity(new ConstraintViolationProblem(httpStatus, violations))
        .build();
  }
}
