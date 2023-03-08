package apiTests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class invalidCreateTests {

    @Test
    public void createBookingWithOneEmptyFieldTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking";
        String body = """
                {
                    "firstname" : ,
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }
                """;
        ValidatableResponse validatableResponse =
                given().body(body).header("Content-Type","application/json")
                        .when().post(endPoint).then();
        //Assertions
        validatableResponse.statusCode(400);
        Response response = validatableResponse.extract().response();
        String responseBody = response.asString();
        Assert.assertTrue(responseBody.contains("Bad Request"));
    }

    @Test
    public void createBookingWithAllEmptyFieldsTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking";
        String body = """
                {
                    "firstname" : ,
                    "lastname" : ,
                    "totalprice" : ,
                    "depositpaid" : ,
                    "bookingdates" : {
                        "checkin" : ,
                        "checkout" : 
                    },
                    "additionalneeds" : 
                }
                """;
        ValidatableResponse validatableResponse =
                given().body(body).header("Content-Type","application/json")
                        .when().post(endPoint).then();
        //Assertions
        validatableResponse.statusCode(400);
        Response response = validatableResponse.extract().response();
        String responseBody = response.asString();
        Assert.assertTrue(responseBody.contains("Bad Request"));
    }

    @Test
    public void createBookingWithInvalidFirstname(){
        String endPoint = "https://restful-booker.herokuapp.com/booking";
        String body = """
                {
                    "firstname" : 455,
                    "lastname" : "Brown",
                    "totalprice" : 111,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2018-01-01",
                        "checkout" : "2019-01-01"
                    },
                    "additionalneeds" : "Breakfast"
                }
                """;
        ValidatableResponse validatableResponse =
                given().body(body).header("Content-Type","application/json")
                        .when().post(endPoint).then();
        //Assertions
        validatableResponse.statusCode(500);
        Response response = validatableResponse.extract().response();
        String responseBody = response.asString();
        Assert.assertTrue(responseBody.contains("Error"));
    }


}
