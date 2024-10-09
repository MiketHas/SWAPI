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

public class GetSpeciesTests extends BasicSetup {

    private int numberOfSpecies = 37;
    private String chosenSpecies = "Togruta";
    private List<String> species = new ArrayList<>();

    @BeforeClass
    public void setupClass() {
        species = iteratePages("species", "name");
    }

    @BeforeMethod
    public void setup() {
        response = RestAssured.given(spec).get("/species");
    }

    @Test
    public void getSpeciesEndpointTest() {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not!");
    }

    @Test
    public void getInvalidEndpointTest() {
        response = RestAssured.given(spec).get("/strain");
        Assert.assertEquals(response.getStatusCode(), 404, "Invalid endpoint should return 404!");
    }

    @Test
    public void getAllSpeciesTest() {
        SoftAssert softAssert = new SoftAssert();

        // getting the list of all species
        species.forEach(System.out::println); // printing the list of all species

        // checking if the entire list is empty
        softAssert.assertTrue(!species.isEmpty(), "The list should not be empty!");

        // Checking if each species on the list has a name
        softAssert.assertTrue(species.stream().allMatch(Objects::nonNull), "Some species do not have a name!");
        softAssert.assertAll();
    }

    @Test
    public void countAllSpeciesTest() {
        Assert.assertEquals(species.size(), numberOfSpecies, "There should be " + numberOfSpecies + " species!");
    }

    @Test
    public void checkSpecificSpeciesExistsTest() {
        Assert.assertTrue(species.contains(chosenSpecies), chosenSpecies + " should be on the list!");
    }

    @Test (dependsOnMethods = {"checkSpecificSpeciesExistsTest"})
    public void checkSpeciesMatchUrlTest() {
        List<String> speciesUrls = iteratePages("species", "url");

        for (int i = 0; i<species.size(); i++) {
            if (species.get(i).contains(chosenSpecies)) {
                Response selectedSpeciesUrl = RestAssured.given(spec).get(speciesUrls.get(i));
                Assert.assertEquals(selectedSpeciesUrl.jsonPath().get("name"), chosenSpecies, speciesUrls.get(i) + " do not match with " + chosenSpecies);
                selectedSpeciesUrl.prettyPrint();
            }
        }
    }

    @Test
    public void getInvalidSpeciesIdTest() { // Test will FAIL. There are 82 characters, thus the final URL should be "/82", however "/17" was omitted, so the final URL is "/83"
        response = RestAssured.given(spec).get("/species/" + (numberOfSpecies+1));
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 404, "Species with such ID doesn't exist!");
    }

    @Test
    public void checkEachSpeciesHasALanguageTest() {
        List<String> language = iteratePages("species", "language");
        Assert.assertTrue(language.stream().allMatch(Objects::nonNull), "Some species don't have a language!");
    }

}
