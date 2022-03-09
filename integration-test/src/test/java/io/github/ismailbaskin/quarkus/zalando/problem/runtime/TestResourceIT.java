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

package io.github.ismailbaskin.quarkus.zalando.problem.runtime;

import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.METHOD_NOT_ALLOWED;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;

import io.quarkus.test.junit.DisabledOnNativeImage;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

@QuarkusTest
@DisabledOnNativeImage
class TestResourceIT {

  @Test
  void noPath() {
    final String path = "/some/thing/not/present/2094u294uasdf9824";
    given()
        .when()
        .get(path)
        .then()
        .statusCode(NOT_FOUND.getStatusCode())
        .body("title", startsWith("RESTEASY003210"))
        .body("status", is(NOT_FOUND.getStatusCode()))
        .body("http_method", is("GET"))
        .body("instance", is(path))
        .body("detail", startsWith("javax.ws.rs.NotFoundException"));
  }

  @Test
  void divideOk() {
    given().when().get("/test/divide/4/2").then().statusCode(OK.getStatusCode()).body(is("2"));
  }

  @Test
  void divideError() {
    final String path = "/test/divide/4/0";
    given()
        .when()
        .get(path)
        .then()
        .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
        .body("title", is("/ by zero"))
        .body("status", is(INTERNAL_SERVER_ERROR.getStatusCode()))
        .body("http_method", is("GET"))
        .body("instance", is(path))
        .body("detail", is("java.lang.ArithmeticException: / by zero"));
  }

  @Test
  void divideConstraintViolation() {
    final String path = "/test/divide/" + (TestResource.DIVIDEND_MAX_VALUE + 1) + "/0";
    given()
        .when()
        .get(path)
        .then()
        .statusCode(BAD_REQUEST.getStatusCode())
        .body("title", is("Constraint Violation"))
        .body("status", is(BAD_REQUEST.getStatusCode()))
        .body("http_method", is(nullValue()))
        .body("instance", is(nullValue()))
        .body("detail", is(nullValue()))
        .body("violations[0].size()", is(2))
        .body("violations[0].field", is("divide.arg0"))
        .body("violations[0].message", containsString("" + TestResource.DIVIDEND_MAX_VALUE));
  }

  @Test
  void exception() {
    final String path = "/test/exception";
    given()
        .when()
        .get(path)
        .then()
        .statusCode(INTERNAL_SERVER_ERROR.getStatusCode())
        .body("title", Matchers.is(TestResource.RUNTIME_EXCEPTION_MESSAGE))
        .body("status", is(INTERNAL_SERVER_ERROR.getStatusCode()))
        .body("http_method", is("GET"))
        .body("instance", is(path))
        .body("detail", endsWith(TestResource.RUNTIME_EXCEPTION_MESSAGE))
        .body("cause.title", Matchers.is(TestResource.IO_EXCEPTION1_MESSAGE))
        .body("cause.detail", endsWith(TestResource.IO_EXCEPTION1_MESSAGE))
        .body("cause.cause.title", Matchers.is(TestResource.IO_EXCEPTION2_MESSAGE))
        .body("cause.cause.detail", endsWith(TestResource.IO_EXCEPTION2_MESSAGE));
  }

  @Test
  void problem() {
    given()
        .when()
        .get("/test/problem")
        .then()
        .statusCode(BAD_REQUEST.getStatusCode())
        .body("title", Matchers.is(TestResource.STRANGE_PROBLEM_TITLE))
        .body("status", is(BAD_REQUEST.getStatusCode()))
        .body("http_method", is(nullValue()))
        .body("instance", is(nullValue()))
        .body("detail", is(nullValue()))
        .body("foo", is("bar"));
  }

  @Test
  void restrictedUnauthorized() {
    final String path = "/test/restricted";
    given()
        .when()
        .get(path)
        .then()
        .statusCode(UNAUTHORIZED.getStatusCode())
        .body("title", is(nullValue()))
        .body("status", is(UNAUTHORIZED.getStatusCode()))
        .body("http_method", is("GET"))
        .body("instance", is(path))
        .body("detail", is("io.quarkus.security.UnauthorizedException"));
  }

  @Test
  void restrictedForbidden() {
    final String path = "/test/restricted";
    given()
        .auth()
        .preemptive()
        .basic("jdoe", "p4ssw0rd")
        .when()
        .get(path)
        .then()
        .statusCode(FORBIDDEN.getStatusCode())
        .body("title", is(nullValue()))
        .body("status", is(FORBIDDEN.getStatusCode()))
        .body("http_method", is("GET"))
        .body("instance", is(path))
        .body("detail", is("io.quarkus.security.ForbiddenException"));
  }

  @Test
  void restrictedOk() {
    given()
        .auth()
        .preemptive()
        .basic("scott", "jb0ss")
        .when()
        .get("/test/restricted")
        .then()
        .statusCode(OK.getStatusCode())
        .body(is("true"));
  }

  @Test
  void methodNotAllowed() {
    given()
        .when()
        .post("/test/divide/4/2")
        .then()
        .statusCode(METHOD_NOT_ALLOWED.getStatusCode())
        .body("http_allowed_methods", hasItems("HEAD", "GET", "OPTIONS"));
  }

  @Test
  void causalChain() {
    given()
        .when()
        .get("/test/chain")
        .then()
        .statusCode(BAD_REQUEST.getStatusCode())
        .body("cause.title", is("Out of Stock"))
        .body("cause.cause.title", is("Extreme out of Stock"));
  }
}
