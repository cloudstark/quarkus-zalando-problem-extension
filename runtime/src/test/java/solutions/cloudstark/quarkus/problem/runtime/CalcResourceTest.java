package solutions.cloudstark.quarkus.problem.runtime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusTest;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class QuarkusZalandoProblemExtensionTest {

  @Test
  public void calcOk() {
    given().when().get("/calc/4:2").then().statusCode(HttpStatus.SC_OK).body(is(2));
  }

  @Test
  public void calcError() {
    given().when().get("/calc/4:0").then().statusCode(HttpStatus.SC_BAD_REQUEST).body(is(2));
  }
}
