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

package solutions.cloudstark.quarkus.problem.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.jsonb.spi.JsonbSerializerBuildItem;
import io.quarkus.resteasy.common.spi.ResteasyJaxrsProviderBuildItem;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.ext.ExceptionMapper;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;
import solutions.cloudstark.quarkus.problem.runtime.ConstraintViolationExceptionMapper;
import solutions.cloudstark.quarkus.problem.runtime.ForbiddenExceptionMapper;
import solutions.cloudstark.quarkus.problem.runtime.NotFoundExceptionMapper;
import solutions.cloudstark.quarkus.problem.runtime.RestExceptionMapper;
import solutions.cloudstark.quarkus.problem.runtime.ThrowableProblemMapper;
import solutions.cloudstark.quarkus.problem.runtime.UnauthorizedExceptionMapper;
import solutions.cloudstark.quarkus.problem.runtime.jsonb.ConstraintViolationProblemSerializer;
import solutions.cloudstark.quarkus.problem.runtime.jsonb.DefaultProblemSerializer;
import solutions.cloudstark.quarkus.problem.runtime.jsonb.ViolationSerializer;

public class ProblemProcessor {

  private static final String FEATURE_NAME = "problem";

  private static final List<Class<? extends ExceptionMapper<?>>> EXCEPTION_MAPPER_CLASSES =
      Arrays.asList(
          ConstraintViolationExceptionMapper.class,
          ForbiddenExceptionMapper.class,
          NotFoundExceptionMapper.class,
          UnauthorizedExceptionMapper.class,
          ThrowableProblemMapper.class,
          RestExceptionMapper.class);

  @BuildStep
  FeatureBuildItem createFeatureItem() {
    return new FeatureBuildItem(FEATURE_NAME);
  }

  @BuildStep
  void registerReflectiveClasses(final BuildProducer<ReflectiveClassBuildItem> reflectives) {
    reflectives.produce(
        ReflectiveClassBuildItem.builder(
                DefaultProblem.class, ConstraintViolationProblem.class, Violation.class)
            .build());
  }

  @BuildStep
  void registerJsonbSerializers(final BuildProducer<JsonbSerializerBuildItem> serializers) {
    serializers.produce(
        new JsonbSerializerBuildItem(
            Arrays.asList(
                DefaultProblemSerializer.class.getName(),
                ViolationSerializer.class.getName(),
                ConstraintViolationProblemSerializer.class.getName())));
  }

  @BuildStep
  void registerJaxRsProviders(final BuildProducer<ResteasyJaxrsProviderBuildItem> providers) {
    EXCEPTION_MAPPER_CLASSES.forEach(
        exceptionMapper ->
            providers.produce(new ResteasyJaxrsProviderBuildItem(exceptionMapper.getName())));
  }
}
