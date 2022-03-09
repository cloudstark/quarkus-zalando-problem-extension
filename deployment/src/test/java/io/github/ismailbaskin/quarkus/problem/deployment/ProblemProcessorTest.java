package io.github.ismailbaskin.quarkus.problem.deployment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProblemProcessorTest {

  private static final ProblemProcessor PROBLEM_PROCESSOR = new ProblemProcessor();

  @Test
  void createFeatureItem() {
    Assertions.assertNotNull(PROBLEM_PROCESSOR.createFeatureItem().getName());
  }
}
