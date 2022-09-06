package kitchenpos.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@DisplayName("메뉴 그룹")
public class MenuGroupAcceptanceTest extends AcceptanceTest {

  @DisplayName("메뉴 그룹 등록")
  @Test
  void createMenuGroup() {
    ExtractableResponse<Response> response = MenuGroupSteps.createMenuGroup("추천메뉴");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    ExtractableResponse<Response> result = MenuGroupSteps.getMenuGroups();

    assertThat(result.jsonPath().getList("name")).contains("추천메뉴");
  }

  @DisplayName("메뉴 그룹 이름 null 등록 에러")
  @Test
  void createMenuGroupNameNull() {
    ExtractableResponse<Response> response = MenuGroupSteps.createMenuGroup(null);

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }

  @DisplayName("메뉴 그룹 이름 빈값 등록 에러")
  @Test
  void createMenuGroupNameEnpty() {
    ExtractableResponse<Response> response = MenuGroupSteps.createMenuGroup("");

    assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
  }
}
