package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("주문 테이블")
public class OrderTableAcceptanceTest extends AcceptanceTest {

  @DisplayName("주문 테이블 등록")
  @Test
  void createOrderTable() {
    ExtractableResponse<Response> response = OrderTableSteps.createOrderTable("1번");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> result = OrderTableSteps.getOrderTables();

    assertThat(result.jsonPath().getList("name")).containsExactly("1번");
  }

  @DisplayName("주문 테이블 이름 null 등록 에러")
  @Test
  void createOrderTableNameNull() {
    ExtractableResponse<Response> response = OrderTableSteps.createOrderTable(null);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("주문 테이블 이름 빈값 등록 에러")
  @Test
  void createOrderTableNameEmpty() {
    ExtractableResponse<Response> response = OrderTableSteps.createOrderTable("");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("주문 테이블 착석으로 변경")
  @Test
  void chageOrderTableSit() {
    ExtractableResponse<Response> response = OrderTableSteps.createOrderTable("1번");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    assertThat(response.jsonPath().getBoolean("occupied")).isEqualTo(false);

    OrderTableSteps.chageOrderTableSit(response.jsonPath().getUUID("id"));

    ExtractableResponse<Response> result = OrderTableSteps.getOrderTables();

    assertThat(result.jsonPath().getList("name")).containsExactly("1번");
    assertThat(result.jsonPath().getList("occupied")).containsExactly(true);
  }

  @DisplayName("주문 테이블 손님수 변경")
  @Test
  void chageOrderTableNumberOfGuests() {
    ExtractableResponse<Response> response = OrderTableSteps.createOrderTable("1번");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    OrderTableSteps.chageOrderTableSit(response.jsonPath().getUUID("id"));
    OrderTableSteps.chageOrderTableNumberOfGuests(response.jsonPath().getUUID("id"), 2);

    ExtractableResponse<Response> result = OrderTableSteps.getOrderTables();

    assertThat(result.jsonPath().getList("name")).containsExactly("1번");
    assertThat(result.jsonPath().getList("numberOfGuests")).containsExactly(2);
  }

  @DisplayName("주문 테이블 손님수가 음수이면 에러")
  @Test
  void chageOrderTableNumberOfGuestsNegative() {
    ExtractableResponse<Response> response = OrderTableSteps.createOrderTable("1번");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> result = OrderTableSteps.chageOrderTableNumberOfGuests(response.jsonPath().getUUID("id"), -1);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("주문 테이블 빈 테이블에서 손님수 변경시 에러")
  @Test
  void chageOrderTableNumberOfGuestsNotSit() {
    ExtractableResponse<Response> response = OrderTableSteps.createOrderTable("1번");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> result = OrderTableSteps.chageOrderTableNumberOfGuests(response.jsonPath().getUUID("id"), 2);

    assertThat(result.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}
