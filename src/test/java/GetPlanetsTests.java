import apiTesting.BasicSetup;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GetPlanetsTests extends BasicSetup {

    private int numberOfPlanets = 60;
    private String chosenPlanet = "Dagobah";
    private List<String> planets = new ArrayList<>();

    @BeforeClass
    public void setupClass() {
        planets = iteratePages("planets", "name");
    }

    @BeforeMethod
    public void setup() {
        response = RestAssured.given(spec).get("/planets");
    }

    @Test
    public void getPlanetsEndpointTest() {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200!");
    }

    @Test
    public void getInvalidEndpointTest() {
        response = RestAssured.given(spec).get("/moons");
        Assert.assertEquals(response.getStatusCode(), 404, "Invalid endpoint should return 404!");
    }

    @Test
    public void getAllPlanetsTest() {
        SoftAssert softAssert = new SoftAssert();

        // getting the list of all planets
        planets.forEach(System.out::println);

        // Checking if the entire list is empty
        softAssert.assertTrue(!planets.isEmpty(), "The Planets list should not be empty!");

        // Checking if all planets have a name
        softAssert.assertTrue(planets.stream().allMatch(Objects::nonNull), "Some planets don't have a name!");
        softAssert.assertAll();
    }

    @Test
    public void countAllPlanetsTest() {
        Assert.assertEquals(planets.size(), numberOfPlanets, "There should be " + numberOfPlanets + " planets on the list!");
    }

    @Test
    public void checkSpecificPlanetExistsTest() {
        Assert.assertTrue(planets.contains(chosenPlanet), chosenPlanet + " is not on the list!");
    }

    @Test (dependsOnMethods = {"checkSpecificPlanetExistsTest"})
    public void checkPlanetMatchUrlTest() {
        List<String> planetsUrls = iteratePages("planets", "url");

        for (int i = 0; i<planets.size(); i++) {
            if (planets.get(i).contains(chosenPlanet)) {
                Response selectedPlanetUrl = RestAssured.given(spec).get(planetsUrls.get(i));
                Assert.assertEquals(selectedPlanetUrl.jsonPath().get("name"), chosenPlanet, planetsUrls.get(i) + " do not match with " + chosenPlanet);
                selectedPlanetUrl.prettyPrint();
            }
        }
    }

    @Test
    public void getInvalidPlanetIdTest() {
        response = RestAssured.given(spec).get("/planets/" + (numberOfPlanets+1));
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 404, "Planet with such ID doesn't exist!");
    }

    @Test
    public void checkEachPlanetHasPopulationTest() {
        List<String> population = iteratePages("planets", "population");
        Assert.assertTrue(population.stream().allMatch(Objects::nonNull), "Not every planet has a population number!");
    }

}
