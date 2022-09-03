package kitchenpos;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class AcceptanceTestSteps {
    public static RequestSpecification given() {
        return RestAssured
                .given().log().all();
    }
}
