# Quarkus Problem (RFC7807) Extension ![master](https://github.com/cloudstark/quarkus-zalando-problem-extension/workflows/master/badge.svg) [![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=cloudstark/quarkus-zalando-problem-extension)](https://dependabot.com)

Extension to use [Zalando Problem](https://github.com/zalando/problem) in your [Quarkus](https://quarkus.io) JAX-RS
application. The extension will register an `ExceptionMapper` that is responsible to map catched Exceptions into
valid `application/problem+json` to be used as the payload of the HTTP Response.

## Getting Started

The Problem extension is not available in Maven Central. For now you have to clone the repository and install the
extension in your local maven repository. Then follow these steps to write and deploy a simple JAX-RS application:

### Setup Project

Create a new project using the Quarkus [archetype](https://quarkus.io/guides/getting-started-guide#bootstrapping-the-project):

```bash
mvn io.quarkus:quarkus-maven-plugin:1.2.1.Final:create \
    -DprojectGroupId=org.acme.rfc7807 \
    -DprojectArtifactId=rfc7807 \
    -DclassName="org.acme.rest.json.CalcResource" \
    -Dpath="/calc" \
    -Dextensions="resteasy-jsonb"
```

Add the following dependency to your `pom.xml`:

```xml
<dependency>
      <groupId>io.github.ismailbaskin.quarkus</groupId>
      <artifactId>quarkus-zalando-problem</artifactId>
      <version>0.0.2-SNAPSHOT</version>
</dependency>
```

### Define JAX-RS resource

Create a service definition in `src/main/java/org/acme/rest/json/CalcResource.java`

```java
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.Arrays;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Path("/calc")
public class CalcResource {

    @Context
    UriInfo uriInfo;

    @GET
    @Path("/{a: [0-9]+}:{b: [0-9]+}")
    @Produces(TEXT_PLAIN)
    public int divide(@PathParam("a") int a, @PathParam("b") int b) {
        return a / b;
    }

    @GET
    @Path("/exception")
    @Produces(TEXT_PLAIN)
    public void exception() {
        throw new RuntimeException("This is a runtime exception!");
    }

    @GET
    @Path("/problem")
    @Produces(TEXT_PLAIN)
    public void problem() {
        throw Problem.builder()
                .withStatus(Status.BAD_REQUEST)
                .withTitle("Strange problem!")
                .build();
    }

    @GET
    @Path("/violation")
    @Produces(TEXT_PLAIN)
    public void violation() {
        throw new ConstraintViolationProblem(Status.BAD_REQUEST,
                Arrays.asList(new Violation("name", "must not be null")));
    }
}
```

Note: You will have to adjust the generated test in `src/test/java` - otherwise the build will fail.

```
$ curl localhost:8080/calc/20:0
{
  "status": 500,
  "title": "/ by zero",
  "detail": "java.lang.ArithmeticException: / by zero",
  "instance": "/calc/20:0"
}

$ curl localhost:8080/calc/violation
{
  "type": "https://zalando.github.io/problem/constraint-violation",
  "status": 400,
  "title": "Constraint Violation",
  "violations": [
    {
      "field": "name",
      "message": "must not be null"
    }
  ]
}
```

### Run

#### JVM Mode

```bash
mvn package
java -jar target/rfc7807-1.0-SNAPSHOT-runner.jar
```

#### Native Mode

```bash
mvn package -P native
./target/rfc7807-1.0-SNAPSHOT-runner
```

## Todos

- Jackson support
- more unit tests
