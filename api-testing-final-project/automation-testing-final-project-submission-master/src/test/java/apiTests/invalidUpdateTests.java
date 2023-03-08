package apiTests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class invalidUpdateTests {

    String loginToken = null;
    String invalidToken = "aaa111";

    String bookingId = "1";
    String invalidBookingId = "11010101";



    @BeforeClass
    public void loginToApp(){
        String endPoint = "https://restful-booker.herokuapp.com/auth";
        String body = """
                {
                    "username" : "admin",
                    "password" : "password123"
                }""";
        ValidatableResponse validatableResponse =
                given().body(body).header("Content-Type","application/json")
                        .when().post(endPoint).then();
        //Assertions
        validatableResponse.statusCode(200);
        //Extract Token
        Response response = validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        loginToken = jsonPath.getString("token") ;
        System.out.println(loginToken);
    }


    @Test
    public void updateBookingWithInvalidTokenTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        String body = """
                {
                    "firstname" : "Noorhan",
                    "lastname" : "Mostafa",
                    "totalprice" : 100,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2022-04-11",
                        "checkout" : "2022-05-28"
                    },
                    "additionalneeds" : "Breakfast"
                }""";
        ValidatableResponse validatableResponse =
                given().body(body).header("Content-Type","application/json")
                        .header("Accept","application/json")
                        .header("Cookie","token=" + invalidToken)
                        .header("Authorisation","Basic")
                        .when().put(endPoint).then();

        //Assertions
        validatableResponse.statusCode(403);
        Response response = validatableResponse.extract().response();
        Assert.assertEquals(response.asString(),"Forbidden");

    }

    @Test
    public void updateBookingWithInvalidBookingIDTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + invalidBookingId;
        String body = """
                {
                    "firstname" : "Noorhan",
                    "lastname" : "Mostafa",
                    "totalprice" : 100,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2022-04-11",
                        "checkout" : "2022-05-28"
                    },
                    "additionalneeds" : "Breakfast"
                }""";
        ValidatableResponse validatableResponse =
                given().body(body).header("Content-Type","application/json")
                        .header("Accept","application/json")
                        .header("Cookie","token=" + loginToken)
                        .header("Authorisation","Basic")
                        .when().put(endPoint).then();

        //Assertions
        validatableResponse.statusCode(405);
        Response response = validatableResponse.extract().response();
        Assert.assertEquals(response.asString(),"Method Not Allowed");

    }
}

