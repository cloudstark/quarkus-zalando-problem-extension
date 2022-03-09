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

import java.net.URI;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.StatusType;

/**
 * Serializes {@link AbstractThrowableProblem} instances to JSON.
 */
public class DefaultProblemSerializer implements JsonbSerializer<AbstractThrowableProblem> {

  private static final URI DEFAULT_URI = URI.create("about:blank");

  @Override
  public void serialize(
      final AbstractThrowableProblem problem,
      final JsonGenerator generator,
      final SerializationContext ctx) {
    generator.writeStartObject();
    if (problem.getType() != null && !problem.getType().equals(DEFAULT_URI)) {
      generator.write("type", problem.getType().toASCIIString());
    }
    StatusType status = problem.getStatus();
    if (status != null) {
      generator.write("status", status.getStatusCode());
    }
    if (problem.getTitle() != null) {
      generator.write("title", problem.getTitle());
    }
    if (problem.getDetail() != null) {
      generator.write("detail", problem.getDetail());
    }
    URI instance = problem.getInstance();
    if (instance != null) {
      generator.write("instance", instance.toASCIIString());
    }
    if (problem.getCause() != null) {
      ctx.serialize("cause", problem.getCause(), generator);
    }
    if (!problem.getParameters().isEmpty()) {
      problem.getParameters().forEach((key, value) -> ctx.serialize(key, value, generator));
    }
    generator.writeEnd();
  }
}
