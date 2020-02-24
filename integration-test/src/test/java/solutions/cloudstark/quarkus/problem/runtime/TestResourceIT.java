package solutions.cloudstark.quarkus.problem.runtime;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TestResourceIT {

  @Test
  public void noPath() {
    final String path = "/some/thing/not/present/2094u294uasdf9824";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(NOT_FOUND.getStatusCode())
        .body("title", startsWith("RESTEASY003210"))
        .body("status", is(NOT_FOUND.getStatusCode()))
        .body("instance", is(path))
        .body("detail", startsWith("javax.ws.rs.NotFoundException"));
  }

  @Test
  public void divideOk() {
    RestAssured.given()
        .when()
        .get("/test/divide/4/2")
        .then()
        .statusCode(OK.getStatusCode())
        .body(is("2"));
  }

  @Test
  public void divideError() {
    final String path = "/test/divide/4/0";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
        .body("title", is("/ by zero"))
        .body("status", is(INTERNAL_SERVER_ERROR.getStatusCode()))
        .body("instance", is(path))
        .body("detail", is("java.lang.ArithmeticException: / by zero"));
  }

  @Test
  public void divideConstraintViolation() {
    final String path = "/test/divide/" + (TestResource.DIVIDEND_MAX_VALUE + 1) + "/0";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(BAD_REQUEST.getStatusCode())
        .body("title", is("Constraint Violation"))
        .body("status", is(BAD_REQUEST.getStatusCode()))
        .body("instance", is(nullValue()))
        .body("detail", is(nullValue()))
        .body("violations[0].size()", is(2))
        .body("violations[0].field", is("divide.arg0"))
        .body("violations[0].message", containsString("" + TestResource.DIVIDEND_MAX_VALUE));
  }

  @Test
  public void exception() {
    final String path = "/test/exception";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
        .body("title", is(TestResource.RUNTIME_EXCEPTION_MESSAGE))
        .body("status", is(INTERNAL_SERVER_ERROR.getStatusCode()))
        .body("instance", is(path))
        .body("detail", endsWith(TestResource.RUNTIME_EXCEPTION_MESSAGE));
  }

  @Test
  void problem() {
    RestAssured.given()
        .when()
        .get("/test/problem")
        .then()
        .statusCode(BAD_REQUEST.getStatusCode())
        .body("title", is(TestResource.STRANGE_PROBLEM_TITLE))
        .body("status", is(BAD_REQUEST.getStatusCode()))
        .body("instance", is(nullValue()))
        .body("detail", is(nullValue()));
  }

  @Test
  void restrictedUnauthorized() {
    final String path = "/test/restricted";
    RestAssured.given()
        .when()
        .get(path)
        .then()
        .statusCode(UNAUTHORIZED.getStatusCode())
        .body("title", is(nullValue()))
        .body("status", is(UNAUTHORIZED.getStatusCode()))
        .body("instance", is(path))
        .body("detail", is("io.quarkus.security.UnauthorizedException"));
  }

  @Test
  void restrictedForbidden() {
    final String path = "/test/restricted";
    RestAssured.given()
        .auth()
        .preemptive()
        .basic("jdoe", "p4ssw0rd")
        .when()
        .get(path)
        .then()
        .statusCode(FORBIDDEN.getStatusCode())
        .body("title", is(nullValue()))
        .body("status", is(FORBIDDEN.getStatusCode()))
        .body("instance", is(path))
        .body("detail", is("io.quarkus.security.ForbiddenException"));
  }

  @Test
  void restrictedOk() {
    RestAssured.given()
        .auth()
        .preemptive()
        .basic("scott", "jb0ss")
        .when()
        .get("/test/restricted")
        .then()
        .statusCode(OK.getStatusCode())
        .body(is("true"));
  }
}
