package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseRestAssured {

  public static RequestSpecification given() {
    return RestAssured
        .given().log().all();
  }
}
