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

public class GetVehiclesTests extends BasicSetup {
    private int numberOfVehicles = 39;
    private String chosenVehicle = "Sith speeder";
    private List<String> vehicles = new ArrayList<>();

    @BeforeClass
    public void setupClass() {
        vehicles = iteratePages("vehicles", "name");
    }

    @BeforeMethod
    public void setup() {
        response = RestAssured.given(spec).get("/vehicles");
    }

    @Test
    public void getVehiclesEndpointTest() {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not!");
    }

    @Test
    public void getInvalidEndpointTest() {
        response = RestAssured.given(spec).get("/cars");
        Assert.assertEquals(response.getStatusCode(), 404, "Invalid endpoint should return 404!");
    }

    @Test
    public void getAllVehiclesTest() {
        SoftAssert softAssert = new SoftAssert();

        // getting the list of all vehicles
        vehicles.forEach(System.out::println); // printing the list of all vehicles

        // checking if the entire list is empty
        softAssert.assertTrue(!vehicles.isEmpty(), "The list should not be empty!");

        // Checking if each vehicle on the list has a name
        softAssert.assertTrue(vehicles.stream().allMatch(Objects::nonNull), "Some vehicles do not have a name!");
        softAssert.assertAll();
    }

    @Test
    public void countAllVehiclesTest() {
        Assert.assertEquals(vehicles.size(), numberOfVehicles, "There should be " + numberOfVehicles + " vehicles!");
    }

    @Test
    public void checkSpecificVehicleExistsTest() {
        Assert.assertTrue(vehicles.contains(chosenVehicle), chosenVehicle + " should be on the list!");
    }

    @Test (dependsOnMethods = {"checkSpecificVehicleExistsTest"})
    public void checkVehicleMatchUrlTest() {
        List<String> vehiclesUrls = iteratePages("vehicles", "url");

        for (int i = 0; i< vehicles.size(); i++) {
            if (vehicles.get(i).contains(chosenVehicle)) {
                Response selectedVehiclesUrl = RestAssured.given(spec).get(vehiclesUrls.get(i));
                Assert.assertEquals(selectedVehiclesUrl.jsonPath().get("name"), chosenVehicle, vehiclesUrls.get(i) + " do not match with " + chosenVehicle);
                selectedVehiclesUrl.prettyPrint();
            }
        }
    }

    @Test
    public void getInvalidVehicleIdTest() {
        response = RestAssured.given(spec).get("/vehicles/" + (numberOfVehicles +1));
        response.prettyPrint();
        Assert.assertEquals(response.getStatusCode(), 404, "Vehicle with such ID doesn't exist!");
    }

    @Test
    public void checkEachVehicleHasAManufacturerTest() {
        List<String> manufacturers = iteratePages("vehicles", "manufacturer");
        Assert.assertTrue(manufacturers.stream().allMatch(Objects::nonNull), "Some vehicles don't have a manufacturer!");
    }
}
