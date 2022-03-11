package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static kitchenpos.ui.fixture.MenuAcceptanceFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@DisplayName("메뉴 관련 기능")
public class MenuAcceptanceTest extends AcceptanceTest {
    private static final String ENDPOINT = "/api/menus";

    @DisplayName("메뉴를 관리한다.")
    @Test
    void menuManage() {
        // 메뉴를 생성 한다.
        ExtractableResponse<Response> createResponse = createMenu();
        assertThat(createResponse.statusCode()).isEqualTo(CREATED.value());

        // 메뉴들을 조회 한다.
        ExtractableResponse<Response> findAllResponse = findAllMenu();
        assertThat(findAllResponse.statusCode()).isEqualTo(OK.value());

        String menuId = (String) findAllResponse.body().jsonPath().getList("id").get(0);

        // 메뉴의 가격을 변경한다.
        ExtractableResponse<Response> changePriceResponse = changePrice(menuId);
        assertThat(changePriceResponse.statusCode()).isEqualTo(OK.value());

        // 메뉴를 숨긴다.
        ExtractableResponse<Response> hideResponse = hideMenu(menuId);
        assertThat(hideResponse.statusCode()).isEqualTo(OK.value());

        // 메뉴를 전시한다.
        ExtractableResponse<Response> displayResponse = displayMenu(menuId);
        assertThat(displayResponse.statusCode()).isEqualTo(OK.value());
    }
}
