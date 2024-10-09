package apiTesting;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.ArrayList;
import java.util.List;

public class BasicSetup {

    protected static RequestSpecification spec;
    protected static String baseUri = "https://swapi.dev/api/";
    protected Response response;

    @BeforeClass
    public void setUp() {
        spec = new RequestSpecBuilder().setBaseUri(baseUri).build();
    }

    /* There's a limitation of 10 characters per page, so in order to get the complete list it's required
    to iterate through all pages with data to assamble the entire list of characters */
    public List<String> iteratePages(String category, String key) {
        String nextUrl = "/"+ category + "/?page=1";
        List<String> attr = new ArrayList<>();
        while (!(nextUrl == null)) {
            response = RestAssured.given(spec).get(nextUrl);
            Assert.assertEquals(response.getStatusCode(), 200, "Page " + nextUrl + " does not return 200");
            attr.addAll(response.jsonPath().getList("results." + key));
            nextUrl = response.jsonPath().getString("next");
        }
        return attr;
    }

    /*
    "films": "https://swapi.dev/api/films/",
    "people": "https://swapi.dev/api/people/",
    "planets": "https://swapi.dev/api/planets/",
    "species": "https://swapi.dev/api/species/",
    "starships": "https://swapi.dev/api/starships/",
    "vehicles": "https://swapi.dev/api/vehicles/"
    */
}
