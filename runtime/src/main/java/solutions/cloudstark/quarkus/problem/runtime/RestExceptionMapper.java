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

package solutions.cloudstark.quarkus.problem.runtime;

import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.net.URI;

@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {

    @Context
    private UriInfo uriInfo;

    public final static MediaType PROBLEM_JSON = MediaType.valueOf("application/problem+json");

    @Override
    public Response toResponse(Exception ex) {
        final ThrowableProblem throwableProblem;
        ProblemBuilder builder = Problem.builder();
        if (ex instanceof ThrowableProblem) {
            throwableProblem = (ThrowableProblem)ex;
        } else {
            throwableProblem = Problem.builder()
                    .withStatus(Status.INTERNAL_SERVER_ERROR)
                    .withTitle(ex.getMessage())
                    .withDetail(ex.toString())
                    .withInstance(URI.create(uriInfo.getPath()))
                    .build();
        }
        return Response
                .status(throwableProblem.getStatus().getStatusCode())
                .type(PROBLEM_JSON)
                .entity(throwableProblem)
                .build();
    }

}