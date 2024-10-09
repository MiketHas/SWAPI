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

public class GetStarshipsTests extends BasicSetup {

    private int numberOfShips = 36;
    private String chosenStarship = "Executor";
    private List<String> starships = new ArrayList<>();

    @BeforeClass
    public void setupClass() {
        starships = iteratePages("starships", "name");
    }

    @BeforeMethod
    public void setup() {
        response = RestAssured.given(spec).get("/starships");
    }

    @Test
    public void getStarshipsEndpointTest() {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not!");
    }

    @Test
    public void getInvalidEndpointTest() {
        response = RestAssured.given(spec).get("/spacerockets");
        Assert.assertEquals(response.getStatusCode(), 404, "Invalid endpoint should return 404!");
    }

    @Test
    public void getAllStarshipsTest() {
        SoftAssert softAssert = new SoftAssert();

        // getting the list of all starships
        starships.forEach(System.out::println); // printing the list of all starships

        // checking if the entire list is empty
        softAssert.assertTrue(!starships.isEmpty(), "The list should not be empty!");

        // Checking if each starship on the list has a name
        softAssert.assertTrue(starships.stream().allMatch(Objects::nonNull), "Some starships do not have a name!");
        softAssert.assertAll();
    }

    @Test
    public void countAllStarshipsTest() {
        Assert.assertEquals(starships.size(), numberOfShips, "There should be " + numberOfShips + " starships!");
    }

    @Test
    public void checkSpecificStarshipExistsTest() {
        Assert.assertTrue(starships.contains(chosenStarship), chosenStarship + " should be on the list!");
    }

    @Test (dependsOnMethods = {"checkSpecificStarshipExistsTest"})
    public void checkStarshipMatchUrlTest() {
        List<String> starshipsUrls = iteratePages("starships", "url");

        for (int i = 0; i<starships.size(); i++) {
            if (starships.get(i).contains(chosenStarship)) {
                Response selectedStarshipsUrl = RestAssured.given(spec).get(starshipsUrls.get(i));
                Assert.assertEquals(selectedStarshipsUrl.jsonPath().get("name"), chosenStarship, starshipsUrls.get(i) + " do not match with " + chosenStarship);
                selectedStarshipsUrl.prettyPrint();
            }
        }
    }

    @Test
    public void getInvalidStarshipIdTest() {
        response = RestAssured.given(spec).get("/starships/" + (numberOfShips+1));
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 404, "Starship with such ID doesn't exist!");
    }

    @Test
    public void checkEachStarshipHasAManufacturerTest() {
        List<String> manufacturers = iteratePages("starships", "manufacturer");
        Assert.assertTrue(manufacturers.stream().allMatch(Objects::nonNull), "Some starships don't have a manufacturer!");
    }

}
