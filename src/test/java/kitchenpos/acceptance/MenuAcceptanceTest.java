package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.MenuAcceptanceStep;
import kitchenpos.acceptance.step.MenuGroupAcceptanceStep;
import kitchenpos.acceptance.step.ProductAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuFixture.createMenuWithId;
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
        Response 추천메뉴 = MenuGroupAcceptanceStep.create(createMenuGroup(null, "추천메뉴"));

        Response 후라이드_치킨 = ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));

        UUID menuGroupId = 추천메뉴.getBody().jsonPath().getUUID("id");

        Response 후라이드_치킨_메뉴 = MenuAcceptanceStep.create(createMenu(createMenuGroup(menuGroupId), "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(UUID.fromString(후라이드_치킨.getBody().jsonPath().getString("id")), 1))));
        String 후라이드_치킨_메뉴Id = 후라이드_치킨_메뉴.getBody().jsonPath().getString("id");

        assertThat(후라이드_치킨_메뉴.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        Response findAll = MenuAcceptanceStep.findAll();
        assertThat(findAll.getBody().jsonPath().getList("name")).contains("후라이드 치킨");
        assertThat(findAll.getBody().jsonPath().getList("id")).contains(후라이드_치킨_메뉴Id);

        Response 변경된_후라이드_치킨_메뉴 = MenuAcceptanceStep.changePrice(UUID.fromString(후라이드_치킨_메뉴Id), createMenuWithId(createMenuGroup(menuGroupId), "후라이드 치킨", BigDecimal.valueOf(15000), true, List.of(createMenuProduct(UUID.fromString(후라이드_치킨.getBody().jsonPath().getString("id")), 1))));
        assertThat(변경된_후라이드_치킨_메뉴.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(변경된_후라이드_치킨_메뉴.getBody().jsonPath().getObject("price", BigDecimal.class)).isEqualTo(BigDecimal.valueOf(15000));

        findAll = MenuAcceptanceStep.findAll();
        assertThat(findAll.getBody().jsonPath().getList("name")).contains("후라이드 치킨");
        assertThat(findAll.getBody().jsonPath().getList("id")).contains(후라이드_치킨_메뉴Id);

        Response 숨김_후라이드_치킨_메뉴 = MenuAcceptanceStep.hide(UUID.fromString(후라이드_치킨_메뉴Id), createMenuWithId(createMenuGroup(menuGroupId), "후라이드 치킨", BigDecimal.valueOf(15000), false, List.of(createMenuProduct(UUID.fromString(후라이드_치킨.getBody().jsonPath().getString("id")), 1))));
        assertThat(숨김_후라이드_치킨_메뉴.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(숨김_후라이드_치킨_메뉴.getBody().jsonPath().getBoolean("displayed")).isFalse();

        findAll = MenuAcceptanceStep.findAll();
        assertThat(findAll.getBody().jsonPath().getList("name")).contains("후라이드 치킨");
        assertThat(findAll.getBody().jsonPath().getList("id")).contains(후라이드_치킨_메뉴Id);

        Response 노출_후라이드_치킨_메뉴 = MenuAcceptanceStep.display(UUID.fromString(후라이드_치킨_메뉴Id), createMenuWithId(createMenuGroup(menuGroupId), "후라이드 치킨", BigDecimal.valueOf(15000), true, List.of(createMenuProduct(UUID.fromString(후라이드_치킨.getBody().jsonPath().getString("id")), 1))));
        assertThat(노출_후라이드_치킨_메뉴.getBody().jsonPath().getBoolean("displayed")).isTrue();
    }
}
