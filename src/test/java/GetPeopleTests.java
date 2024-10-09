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

public class GetPeopleTests extends BasicSetup {

    private int numberOfCharacters = 82;
    private String chosenCharacter = "Yoda";
    private List<String> people = new ArrayList<>();

    @BeforeClass
    public void setupClass() {
        people = iteratePages("people", "name");
    }

    @BeforeMethod
    public void setup() {
        response = RestAssured.given(spec).get("/people");
    }

    @Test
    public void getPeopleEndpointTest() {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not!");
    }

    @Test
    public void getInvalidEndpointTest() {
        response = RestAssured.given(spec).get("/humans");
        Assert.assertEquals(response.getStatusCode(), 404, "Invalid endpoint should return 404!");
    }

    @Test
    public void getAllPeopleTest() {
        SoftAssert softAssert = new SoftAssert();

        // getting the list of all people
        people.forEach(System.out::println); // printing the list of all people

        // checking if the entire list is empty
        softAssert.assertTrue(!people.isEmpty(), "The list should not be empty!");

        // Checking if each character on the list has a name
        softAssert.assertTrue(people.stream().allMatch(Objects::nonNull), "Some characters do not have a name!");
        softAssert.assertAll();
    }

    @Test
    public void countAllPeopleTest() {
        Assert.assertEquals(people.size(), numberOfCharacters, "There should be " + numberOfCharacters + " characters!");
    }

    @Test
    public void checkSpecificCharacterExistsTest() {
        Assert.assertTrue(people.contains(chosenCharacter), chosenCharacter + " should be on the list!");
    }

    @Test (dependsOnMethods = {"checkSpecificCharacterExistsTest"})
    public void checkCharacterMatchUrlTest() {
        List<String> peopleUrls = iteratePages("people", "url");

        for (int i = 0; i<people.size(); i++) {
            if (people.get(i).contains(chosenCharacter)) {
                Response selectedCharacterUrl = RestAssured.given(spec).get(peopleUrls.get(i));
                Assert.assertEquals(selectedCharacterUrl.jsonPath().get("name"), chosenCharacter, peopleUrls.get(i) + " do not match with " + chosenCharacter);
                // selectedCharacterUrl.prettyPrint();
            }
        }
    }

    @Test
    public void getInvalidCharacterIdTest() { // Test will FAIL. There are 82 characters, thus the final URL should be "/82", however "/17" was omitted, so the final URL is "/83"
        response = RestAssured.given(spec).get("/people/" + (numberOfCharacters+1));
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 404, "Character with such ID doesn't exist!");
    }

    @Test
    public void checkEachPersonHasAHomeworldTest() {
        List<String> homeworlds = iteratePages("people", "homeworld");
        Assert.assertTrue(homeworlds.stream().allMatch(Objects::nonNull), "Some characters don't have a homeworld!");
    }
}
