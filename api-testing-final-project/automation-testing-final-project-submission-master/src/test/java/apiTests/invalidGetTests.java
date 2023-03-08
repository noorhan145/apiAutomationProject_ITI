package apiTests;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class invalidGetTests {
    String invalidBookingId = "11010101";
    String invalidCheckinDate = "14-03-2014";
    String invalidCheckoutDate = "14-00-2014";
    @Test
    public void getBookingUsingInvalidBookingIdTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking/" + invalidBookingId;
        ValidatableResponse validatableResponse =
                given().header("Accept","application/json")
                        .when().get(endPoint).then();
        //Assertions
        validatableResponse.statusCode(404);
        Response response = validatableResponse.extract().response();
        Assert.assertEquals(response.asString(),"Not Found");


    }

    @Test
    public void getBookingUsingInvalidCheckinDateTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking?checkin=" + invalidCheckinDate;
        ValidatableResponse validatableResponse =
                given().header("Accept","application/json")
                        .when().get(endPoint).then();
        //Assertions
        validatableResponse.statusCode(500);
        Response response = validatableResponse.extract().response();
        String responseBody = response.asString();
        Assert.assertTrue(responseBody.contains("Error"));


    }

    @Test
    public void getBookingUsingInvalidCheckoutDateTest(){
        String endPoint = "https://restful-booker.herokuapp.com/booking?checkout=" + invalidCheckoutDate;
        ValidatableResponse validatableResponse =
                given().header("Accept","application/json")
                        .when().get(endPoint).then();
        //Assertions
        validatableResponse.statusCode(500);
        Response response = validatableResponse.extract().response();
        String responseBody = response.asString();
        Assert.assertTrue(responseBody.contains("Error"));


    }
}
