package com.shatnenko.suits;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import org.junit.Before;
import org.junit.Test;


import java.text.MessageFormat;

import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.CoreMatchers.*;


public class GistCommentsTestSuite {

    public static String baseUrl = "https://api.github.com";
    public static String targetGistID = "ec26dc0486d802296e1ffff57f9fadb8";
    public static String oauthAutorization = "Bearer fae43705aff9782e73ef2af7bb97662c9c239125";



    @Before
    public void setUp() {
        RestAssured.config = new RestAssuredConfig().encoderConfig(encoderConfig().defaultContentCharset("UTF-8"));
        RestAssured.baseURI = baseUrl;
    }


    @Test
    public void newCommentIsCreatedSuccessfullyViaAuthorisedAccess() {
        given().
                accept(ContentType.JSON).
                header("Authorization", oauthAutorization).
                pathParam(":gist_id",targetGistID).
                body("{\"body\":\"First comment\"}").
        when().
                post("/gists/{:gist_id}/comments").
        then().
                assertThat().statusCode(201).
                assertThat().body("body", equalTo("First comment"));
    }


    @Test
    public void gettingCommentsIsSuccessul() {
        given().
                accept(ContentType.JSON).
                pathParam(":gist_id",targetGistID).
        when().
               get("/gists/{:gist_id}/comments").
        then().
                assertThat().statusCode(200).
                assertThat().body("body", notNullValue());

    }

    @Test
    public void gettingSingleCommentIsSuccessul() {
        given().
                accept(ContentType.JSON).
                pathParam(":gist_id",targetGistID).
                pathParam(":comment_id",getTargetCommentID()).
        when().
                get("/gists/{:gist_id}/comments/{:comment_id}").
        then().
                assertThat().statusCode(200).
                assertThat().body("body", equalTo("First comment"));

    }

    @Test
    public void existingCommentIsUpdatedSuccessfullyViaAuthorisedAccess() {
        given().
                header("Authorization", oauthAutorization).
                pathParam(":gist_id",targetGistID).
                pathParam(":comment_id",getTargetCommentID()).
                body("{\"body\":\"First comment update\"}").
        when().
                patch("/gists/{:gist_id}/comments/{:comment_id}").
        then().
                assertThat().statusCode(200).
                assertThat().body("body",equalTo("First comment update"));
    }

    @Test
    public void existingCommentIsDeletedSuccessfullyViaAuthorisedAccess() {
        given().
                header("Authorization", oauthAutorization).
                pathParam(":gist_id", targetGistID).
                pathParam(":comment_id",getTargetCommentID()).
        when().
                delete("/gists/{:gist_id}/comments/{:comment_id}").
        then().
                assertThat().statusCode(204);
    }

    @Test
    public void newCommentIsNotCreatedSuccessfullyViaUnAuthorisedAccess() {
        given().
                contentType(ContentType.JSON).
                header("Authentication", oauthAutorization).
                pathParam(":gist_id",targetGistID).
                body("{\"body\":\"Second comment\"}").
        when().
                post("/gists/{:gist_id}/comments").
        then().
                assertThat().statusCode(404);
    }

    public static String getTargetCommentID() {

        String targetID = get(MessageFormat.format("{0}/gists/{1}/comments", baseUrl, targetGistID)).jsonPath().getString("id");
        if (targetID.startsWith("[") && targetID.endsWith("]")) {
            targetID = targetID.substring(1, targetID.length() - 1);
        }
        return targetID;
    }


}
