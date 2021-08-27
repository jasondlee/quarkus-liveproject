package io.chillplus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;

import java.util.Arrays;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class TvShowResourceTest {

    @Test
    public void getAllTvShows() {
        createTvShow("Show 1");
        createTvShow("Show 2");
        Response response = given()
                .when().get("/api/tv");
        TvShow[] shows = response.body().as(TvShow[].class);
        response
                .then()
                .statusCode(200);
        Arrays.stream(shows).forEach(show -> assertThat(show.getId(), is(notNullValue())));
    }

    @Test
    public void checkTvShowTitleIsNotBlank() {
        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"\"}")
                .post("/api/tv")
                .then()
                .statusCode(400);
    }

    @Test
    public void createTvShow() {
        createTvShow("Test");

        given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"id\":\"1\"}")
                .post("/api/tv")
                .then()
                .statusCode(400);
    }

    @Test
    public void getOneTvShow() {
        TvShow show = createTvShow("Get One");
        Response response = given()
                .when().get("/api/tv/" + show.getId());
        response
                .then()
                .statusCode(200);
        TvShow returned = response.body().as(TvShow.class);
        assertThat(returned, is(notNullValue()));
        assertThat(returned.getTitle(), is("Get One"));
    }

    @Test
    public void getNonExistingTvShow() {
        given()
                .when()
                .get("/api/tv/49152")
                .then()
                .statusCode(404);
    }

    @Test
    public void deleteAllTvShows() {
        given()
                .when().delete("/api/tv")
                .then()
                .statusCode(200);
        given()
                .when().get("/api/tv")
                .then()
                .statusCode(200)
                .body(is("[]"));
    }

    @Test
    public void deleteOneTvShow() {
        TvShow show = createTvShow("Delete One");
        given()
                .when().delete("/api/tv/" + show.getId())
                .then()
                .statusCode(200);
        given()
                .when().get("/api/tv/" + show.getId())
                .then()
                .statusCode(404);
    }

    private TvShow createTvShow(String title) {
        Response response = given()
                .when()
                .contentType(ContentType.JSON)
                .body("{\"title\":\"" + title + "\"}")
                .post("/api/tv");
        TvShow show = response.body().as(TvShow.class);
        response
                .then()
                .statusCode(201);

        return show;
    }
}
