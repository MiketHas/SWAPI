import apiTesting.BasicSetup;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class HealthCheckTests extends BasicSetup {

    @Test
    public void healthCheckTest() {
        given().
                spec(spec).
                when().
                get("").
                then().
                assertThat().
                statusCode(200);
    }
}
