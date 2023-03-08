package apiTests;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class validBookingTests {
    String loginToken = null;
    String bookingId = null;
    String checkinDate = null;
    String checkoutDate = null;


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

    @Test(priority = 1, dependsOnMethods = "createBookingTest")
    public void updateBookingTest(){
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
                        .header("Cookie","token=" + loginToken)
                        .header("Authorisation","Basic")
                        .when().put(endPoint).then();

        //Assertions
        validatableResponse.statusCode(200);
        validatableResponse.body("firstname",equalTo("Noorhan"),
                "lastname", equalTo("Mostafa"),
                "totalprice", equalTo(100),"depositpaid", equalTo(true),
                "bookingdates.checkin", equalTo("2022-04-11"),
                "bookingdates.checkout", equalTo("2022-05-28"));
        validatableResponse.header("Content-Type",equalTo("application/json; charset=utf-8"));

        //Extract booking chechin and checkout dates
        Response response = validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        checkinDate = jsonPath.getString("bookingdates.checkin") ;
        checkoutDate = jsonPath.getString("bookingdates.checkout") ;
        System.out.println("Checkin Date is: " + checkinDate);
        System.out.println("Checkout Date is: " + checkoutDate);



    }

    @Test(priority = 2, dependsOnMethods = "updateBookingTest")
    public void getAllBookingIdsTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking/";
        ValidatableResponse validatableResponse =
                given().header("Accept","application/json")
                        .when().get(endPoint).then();
        //Assertions
        validatableResponse.statusCode(200);
        validatableResponse.body("bookingid",hasItem(Integer.parseInt(bookingId)));


    }
    @Test(priority = 2, dependsOnMethods = "updateBookingTest")
    public void getBookingUsingIdTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        ValidatableResponse validatableResponse =
                given().header("Accept","application/json")
                        .when().get(endPoint).then();
        //Assertions
        validatableResponse.statusCode(200);
        validatableResponse.body("firstname",equalTo("Noorhan"),
                "lastname", equalTo("Mostafa"),
                "totalprice", equalTo(100),"depositpaid", equalTo(true),
                "bookingdates.checkin", equalTo("2022-04-11"),
                "bookingdates.checkout", equalTo("2022-05-28"));


    }

    @Test(priority = 2, dependsOnMethods = "updateBookingTest")
    public void getBookingUsingCheckinDateTest(){

        String endPoint = "https://restful-booker.herokuapp.com/booking?checkin=" + checkinDate;
        ValidatableResponse validatableResponse =
                given().header("Accept","application/json")
                        .when().get(endPoint).then();
        //Assertions
        validatableResponse.statusCode(200);
        validatableResponse.body("bookingid", hasItem(Integer.parseInt(bookingId)));
        validatableResponse.header("Content-Type", equalTo("application/json; charset=utf-8"));

    }

    @Test(priority = 2, dependsOnMethods = "updateBookingTest")
    public void getBookingUsingCheckoutDateTest(){

        String endPoint = "https://restful-booker.herokuapp.com/booking?checkin=" + checkoutDate;
        ValidatableResponse validatableResponse =
                given().header("Accept","application/json")
                        .when().get(endPoint).then();
        //Assertions
        validatableResponse.statusCode(200);
        validatableResponse.body("bookingid", hasItem(Integer.parseInt(bookingId)));
        validatableResponse.header("Content-Type", equalTo("application/json; charset=utf-8"));

    }

    @Test(priority = 3, dependsOnMethods = "getBookingUsingIdTest")
    public void deleteBookingTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + bookingId;
        ValidatableResponse validatableResponse =
                given().header("Content-Type","application/json")
                        .header("Cookie","token=" + loginToken)
                        .header("Authorisation","Basic")
                        .when().delete(endPoint).then();
        //Assertions
        validatableResponse.statusCode(201);
        Response response = validatableResponse.extract().response();
        JsonPath jsonPath = response.jsonPath();
        Assert.assertEquals(response.asString(),"Created");
    }


}
