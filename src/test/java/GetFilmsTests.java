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

public class GetFilmsTests extends BasicSetup {

    private String chosenMovie = "The Phantom Menace";
    private int numberOfFilms = 6;
    private String phantomMenaceReleaseDate = "1999-05-19";
    private List<String> filmTitles = new ArrayList<>();

    @BeforeClass
    public void setupClass() {
        filmTitles = iteratePages("films", "title");
    }

    @BeforeMethod
    public void setup() {
        response = RestAssured.given(spec).get("/films");
    }

    @Test
    public void getFilmsEndpointTest() {
        Assert.assertEquals(response.getStatusCode(), 200, "Status code should be 200, but it's not!");
    }

    @Test
    public void getInvalidEndpointTest() {
        response = RestAssured.given(spec).get("/movies");
        Assert.assertEquals(response.getStatusCode(), 404, "Invalid endpoint should get a 404!");
    }

    @Test
    public void getAllFilmsTest() {
        SoftAssert softAssert = new SoftAssert();
        // response.prettyPrint();

        filmTitles.forEach(System.out::println); // printing the list

        // Checking if the entire list is empty
        softAssert.assertTrue(!filmTitles.isEmpty(), "The film titles list should not be empty!");

        // Checking if all films have titles
        softAssert.assertTrue(filmTitles.stream().allMatch(Objects::nonNull), "Some movies do not have a title!");
        softAssert.assertAll();
    }

    @Test
    public void countAllFilmsTest() {
        Assert.assertEquals(filmTitles.size(), numberOfFilms, "There should be 6 movies in total but test found " + filmTitles.size());
    }

    @Test
    public void checkSpecificFilmExistsTest() {
        Assert.assertTrue(filmTitles.contains(chosenMovie), chosenMovie +  " should be on the list!"); // checking if the selected movie is on the list
    }

    @Test (dependsOnMethods = {"checkSpecificFilmExistsTest"})
    public void getSpecificFilmDetailsTest() {
        List<String> filmUrls = response.jsonPath().getList("results.url"); // List of movies URLs

        for (int i = 0; i<filmTitles.size(); i++) {
            if (filmTitles.get(i).contains(chosenMovie)) {
                Response selectedMovie = RestAssured.given(spec).get(filmUrls.get(i));
                Assert.assertEquals(selectedMovie.jsonPath().get("title"), chosenMovie, filmUrls.get(i) + " do not match with " + chosenMovie);
                selectedMovie.prettyPrint();
            }
        }
    }

    @Test
    public void getInvalidFilmIdTest() {
        response = RestAssured.given(spec).get("/films/10");
        Assert.assertEquals(response.getStatusCode(), 404, "Movie with such ID doesn't exist!");
    }

    @Test
    public void checkEachMovieHasDirectorTest() {
        List<String> directors = response.jsonPath().getList("results.director");
        Assert.assertTrue(directors.stream().allMatch(Objects::nonNull), "Not every movie has a director assigned!");
    }

    @Test
    public void getFilmReleaseDateTest() {
        response = RestAssured.given(spec).get("/films/4");
        String releaseDate = response.jsonPath().get("release_date");
        System.out.println(response.jsonPath().get("title") + " was released in " + releaseDate);
        Assert.assertEquals(releaseDate, phantomMenaceReleaseDate, "Wrong release date!");
    }

}
