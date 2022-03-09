package io.github.ismailbaskin.quarkus.zalando.problem.runtime;

import javax.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

class ThrowableProblemMapperTest {

  @Test
  void toResponse() {
    final Status status = Status.ALREADY_REPORTED;
    final ThrowableProblem throwableProblem = Problem.builder().withStatus(status).build();

    final Response response = new ThrowableProblemMapper().toResponse(throwableProblem);

    Assertions.assertEquals(status.getStatusCode(), response.getStatus());
  }
}
