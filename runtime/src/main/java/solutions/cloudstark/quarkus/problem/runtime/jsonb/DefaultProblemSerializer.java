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

package solutions.cloudstark.quarkus.problem.runtime.jsonb;

import org.zalando.problem.AbstractThrowableProblem;

import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;
import java.net.URI;

public class DefaultProblemSerializer implements JsonbSerializer<AbstractThrowableProblem> {

    private static final URI DEFAUL_URI = URI.create("about:blank");

    public DefaultProblemSerializer() {
    }

    @Override
    public void serialize(AbstractThrowableProblem problem, JsonGenerator generator, SerializationContext ctx) {
        generator.writeStartObject();
        if (problem.getType() != null && !problem.getType().equals(DEFAUL_URI)) {
            generator.write("type", problem.getType().toASCIIString());
        }
        if (problem.getStatus() != null) {
            generator.write("status", problem.getStatus().getStatusCode());
        }
        if (problem.getTitle() != null) {
            generator.write("title", problem.getTitle());
        }
        if (problem.getDetail() != null) {
            generator.write("detail", problem.getDetail());
        }
        if (problem.getInstance() != null) {
            generator.write("instance", problem.getInstance().toASCIIString());
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
