package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.MenuAcceptanceStep;
import kitchenpos.acceptance.step.MenuGroupAcceptanceStep;
import kitchenpos.acceptance.step.ProductAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;

@AcceptanceTest
class MenuAcceptanceTest {
    @MockBean
    PurgomalumClient purgomalumClient;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("메뉴를 생성하고 관리하고 조회할 수 있다.")
    @Test
    void menuCreateAndManageAndFindAll() {
        // 메뉴 그룹 생성
        final Response recommendedMenuGroupResponse = MenuGroupAcceptanceStep.create(createMenuGroup(null, "추천메뉴"));
        final UUID menuGroupId = recommendedMenuGroupResponse.getBody().jsonPath().getUUID("id");

        // 제품 생성
        final Response friedChickenProduct = ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));
        final UUID friedChickenProductId = friedChickenProduct.getBody().jsonPath().getUUID("id");

        // 메뉴 생성
        final Menu menu = createMenu(createMenuGroup(menuGroupId), "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(friedChickenProductId, 1)));
        final Response friedChickenMenu = MenuAcceptanceStep.create(menu);
        final UUID friedChickenMenuId = friedChickenMenu.getBody().jsonPath().getUUID("id");

        // 전제 메뉴 조회
        Response findAllResponse = MenuAcceptanceStep.findAll();
        assertThat(findAllResponse.getBody().jsonPath().getList("id")).contains(friedChickenMenuId.toString());
        assertThat(findAllResponse.getBody().jsonPath().getList("name")).contains("후라이드 치킨");

        // 가격 변경
        menu.setPrice(BigDecimal.valueOf(15000));
        MenuAcceptanceStep.changePrice(friedChickenMenuId, menu);

        // 메뉴 조회
        findAllResponse = MenuAcceptanceStep.findAll();
        assertThat(findAllResponse.getBody().jsonPath().getList("id")).contains(friedChickenMenuId.toString());
        assertThat(findAllResponse.getBody().jsonPath().getList("price")).contains(15000.0f);

        // 메뉴 숨기기
        MenuAcceptanceStep.hide(friedChickenMenuId);

        // 메뉴 조회
        findAllResponse = MenuAcceptanceStep.findAll();
        assertThat(findAllResponse.getBody().jsonPath().getList("name")).contains("후라이드 치킨");
        assertThat(findAllResponse.getBody().jsonPath().getList("id")).contains(friedChickenMenuId.toString());
        assertThat(findAllResponse.getBody().jsonPath().getList("displayed")).contains(false);

        // 메뉴 노출
        MenuAcceptanceStep.display(friedChickenMenuId);

        // 메뉴 조회
        findAllResponse = MenuAcceptanceStep.findAll();
        assertThat(findAllResponse.getBody().jsonPath().getList("name")).contains("후라이드 치킨");
        assertThat(findAllResponse.getBody().jsonPath().getList("id")).contains(friedChickenMenuId.toString());
        assertThat(findAllResponse.getBody().jsonPath().getList("displayed")).contains(true);
    }
}
