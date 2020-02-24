package solutions.cloudstark.quarkus.problem.runtime;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import javax.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class CalcResourceTest {

  @Test
  public void noPath() {
    final String path = "/some/thing/not/present/2094u294uasdf9824";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(Status.NOT_FOUND.getStatusCode())
        .body("type", is("about:blank"))
        .body("title", startsWith("RESTEASY003210"))
        .body("status", is("NOT_FOUND"))
        .body("instance", is(path))
        .body("detail", startsWith("javax.ws.rs.NotFoundException"));
  }

  @Test
  public void divideOk() {
    RestAssured.given()
        .when()
        .get("/calc/divide/4/2")
        .then()
        .statusCode(Status.OK.getStatusCode())
        .body(is("2"));
  }

  @Test
  public void divideError() {
    final String path = "/calc/divide/4/0";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .body("type", is("about:blank"))
        .body("title", is("/ by zero"))
        .body("status", is("INTERNAL_SERVER_ERROR"))
        .body("instance", is(path))
        .body("detail", is("java.lang.ArithmeticException: / by zero"));
  }

  @Test
  public void exception() {
    final String path = "/calc/exception";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(Status.INTERNAL_SERVER_ERROR.getStatusCode())
        .body("type", is("about:blank"))
        .body("title", is(CalcResource.RUNTIME_EXCEPTION_MESSAGE))
        .body("status", is("INTERNAL_SERVER_ERROR"))
        .body("instance", is(path))
        .body("detail", endsWith(CalcResource.RUNTIME_EXCEPTION_MESSAGE));
  }

  @Test
  void problem() {
    RestAssured.given()
        .when()
        .get("/calc/problem")
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", is("about:blank"))
        .body("title", is(CalcResource.STRANGE_PROBLEM_TITLE))
        .body("status", is("BAD_REQUEST"))
        .body("instance", is(nullValue()))
        .body("detail", is(nullValue()));
  }

  @Test
  void constraintViolation() {
    RestAssured.given()
        .when()
        .get("/calc/violation")
        .then()
        .statusCode(Status.BAD_REQUEST.getStatusCode())
        .body("type", is("https://zalando.github.io/problem/constraint-violation"))
        .body("title", is("Constraint Violation"))
        .body("status", is("BAD_REQUEST"))
        .body("instance", is(nullValue()))
        .body("detail", is(nullValue()))
        .body("violations[0].field", is("name"));
  }

  @Test
  void authenticated() {
    final String path = "/calc/auth";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(Status.UNAUTHORIZED.getStatusCode())
        .body("type", is("about:blank"))
        .body("title", is(nullValue()))
        .body("status", is("UNAUTHORIZED"))
        .body("instance", is(path))
        .body("detail", is("io.quarkus.security.UnauthorizedException"));
  }

  @Test
  void forbidden() {
    final String path = "/calc/forbidden";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(Status.FORBIDDEN.getStatusCode())
        .body("type", is("about:blank"))
        .body("title", is(nullValue()))
        .body("status", is("FORBIDDEN"))
        .body("instance", is(path))
        .body("detail", is("io.quarkus.security.ForbiddenException"));
  }
}
