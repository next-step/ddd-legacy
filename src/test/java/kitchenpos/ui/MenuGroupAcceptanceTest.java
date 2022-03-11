package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.ui.fixture.MenuGroupAcceptanceFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("메뉴 그룹 관련 기능")
public class MenuGroupAcceptanceTest extends AcceptanceTest {

    @DisplayName("메뉴 그룹을 추가 한다.")
    @Test
    void createMenuGroup() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "best 메뉴");

        ExtractableResponse<Response> response = MenuGroupAcceptanceFixture.createMenu();
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
    }

    @DisplayName("모든 메뉴 그룹을 조회 한다.")
    @Test
    void findAll() {
        ExtractableResponse<Response> response = MenuGroupAcceptanceFixture.findAll();

        assertThat(response.statusCode()).isEqualTo(OK.value());
    }
}
