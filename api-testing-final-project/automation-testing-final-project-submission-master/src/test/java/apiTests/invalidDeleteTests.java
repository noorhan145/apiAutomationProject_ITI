package apiTests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class invalidDeleteTests {

    String loginToken = null;
    String invalidToken = "aaa111";

    String bookingId = null;
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

    @Test(priority = 0)
    public void createBookingTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking";
        String body = """
                {
                    "firstname" : "Noor",
                    "lastname" : "Mostafa",
                    "totalprice" : 200,
                    "depositpaid" : true,
                    "bookingdates" : {
                        "checkin" : "2022-04-11",
                        "checkout" : "2022-04-28"
                    },
                    "additionalneeds" : "Breakfast"
                }""";
        ValidatableResponse validatableResponse =
                given().body(body).header("Content-Type","application/json")
                        .header("Accept","application/json")
                        .when().post(endPoint).then();

        //Assertions
        validatableResponse.statusCode(200);
        validatableResponse.body("booking.firstname",equalTo("Noor"),
                "booking.lastname", equalTo("Mostafa"),
                "booking.totalprice", equalTo(200),"booking.depositpaid", equalTo(true),
                "booking.bookingdates.checkin", equalTo("2022-04-11"),
                "booking.bookingdates.checkout", equalTo("2022-04-28"));
        validatableResponse.header("Content-Type",equalTo("application/json; charset=utf-8"));

        //Extract booking Id
        Response response = validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        bookingId = jsonPath.getString("bookingid") ;
        System.out.println(bookingId);
    }

    @Test
    public void deleteBookingWithInvalidTokenTest(){
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
    public void deleteBookingWithInvalidBookingIDTest(){
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
