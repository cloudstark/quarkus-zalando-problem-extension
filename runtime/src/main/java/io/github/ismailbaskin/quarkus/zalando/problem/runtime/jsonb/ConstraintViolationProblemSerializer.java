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

package io.github.ismailbaskin.quarkus.zalando.problem.runtime.jsonb;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import org.zalando.problem.Status;
import org.zalando.problem.StatusType;
import org.zalando.problem.violations.ConstraintViolationProblem;

/**
 * A {@link JsonbSerializer} for {@link ConstraintViolationProblem}.
 */
public class ConstraintViolationProblemSerializer
    implements JsonbSerializer<ConstraintViolationProblem> {

  @Override
  public void serialize(
      final ConstraintViolationProblem problem,
      final JsonGenerator generator,
      final SerializationContext ctx) {

    StatusType status = problem.getStatus();
    int statusCode = Status.INTERNAL_SERVER_ERROR.getStatusCode();
    if (status != null) {
      statusCode = status.getStatusCode();
    }

    generator.writeStartObject();
    generator.write("type", problem.getType().toASCIIString());
    generator.write("status", statusCode);
    generator.write("title", problem.getTitle());
    ctx.serialize("violations", problem.getViolations(), generator);
    generator.writeEnd();
  }
}
