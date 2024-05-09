package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.acceptacne.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static kitchenpos.acceptacne.steps.MenuGroupSteps.createMenuGroupStep;
import static kitchenpos.acceptacne.steps.MenuSteps.changePriceMenuStep;
import static kitchenpos.acceptacne.steps.MenuSteps.createMenuStep;
import static kitchenpos.acceptacne.steps.MenuSteps.displayMenuStep;
import static kitchenpos.acceptacne.steps.MenuSteps.getMenusStep;
import static kitchenpos.acceptacne.steps.MenuSteps.hideMenuStep;
import static kitchenpos.acceptacne.steps.ProductSteps.createProductStep;
import static kitchenpos.fixture.MenuFixture.NAME_반반치킨;
import static kitchenpos.fixture.MenuFixture.NAME_순살치킨;
import static kitchenpos.fixture.MenuFixture.PRICE_19000;
import static kitchenpos.fixture.MenuFixture.PRICE_32000;
import static kitchenpos.fixture.MenuFixture.menuChangePriceRequest;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.NAME_강정치킨;
import static kitchenpos.fixture.ProductFixture.NAME_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_17000;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("메뉴 인수테스트")
class MenuAcceptanceTest {
    private MenuGroup MENU_GROUP_추천메뉴;
    private Product PRODUCT_강정치킨;
    private Product PRODUCT_후라이드치킨;
    private MenuProduct 강정치킨_1개;
    private MenuProduct 후라이드치킨_1개;
    private UUID ID_MENU_GOURP_추천메뉴;

    @BeforeEach
    void setUp() {
        MENU_GROUP_추천메뉴 = createMenuGroupStep(menuGroupCreateRequest("추천메뉴")).as(MenuGroup.class);
        PRODUCT_강정치킨 = createProductStep(productCreateRequest(NAME_강정치킨, PRICE_17000)).as(Product.class);
        PRODUCT_후라이드치킨 = createProductStep(productCreateRequest(NAME_후라이드치킨, PRICE_18000)).as(Product.class);
        강정치킨_1개 = menuProductResponse(1L, PRODUCT_강정치킨, 1);
        후라이드치킨_1개 = menuProductResponse(2L, PRODUCT_후라이드치킨, 1);
        ID_MENU_GOURP_추천메뉴 = MENU_GROUP_추천메뉴.getId();
    }

    @DisplayName("메뉴를 등록 한다.")
    @Test
    void createMenu() {
        // given
        Menu request = menuCreateRequest(NAME_순살치킨, PRICE_32000, ID_MENU_GOURP_추천메뉴, true, 강정치킨_1개, 후라이드치킨_1개);

        // when
        ExtractableResponse<Response> response = createMenuStep(request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_순살치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(PRICE_32000),
                () -> assertThat(response.jsonPath().getObject("menuGroup.id", UUID.class)).isEqualTo(MENU_GROUP_추천메뉴.getId()),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(response.jsonPath().getList("menuProducts.product.id", UUID.class)).hasSize(2)
                        .contains(PRODUCT_강정치킨.getId(), PRODUCT_후라이드치킨.getId()),
                () -> assertThat(response.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    @DisplayName("메뉴의 가격은 수정된다.")
    @Test
    void changeMenuPrice() {
        // given
        UUID menuId = getUuidByCreatedMenu();
        Menu request = menuChangePriceRequest(BigDecimal.valueOf(30_000));

        // when
        ExtractableResponse<Response> response = changePriceMenuStep(menuId, request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(BigDecimal.valueOf(30_000)),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(response.jsonPath().getList("menuProducts.product.id", UUID.class)).hasSize(2)
                        .contains(PRODUCT_강정치킨.getId(), PRODUCT_후라이드치킨.getId()),
                () -> assertThat(response.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    @DisplayName("메뉴가 노출된다.")
    @Test
    void displayMenu() {
        // given
        UUID menuId = getUuidByCreatedMenu();
        hideMenuStep(menuId);

        // when
        ExtractableResponse<Response> response = displayMenuStep(menuId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue()
        );
    }

    @DisplayName("메뉴를 보이지 않게 한다.")
    @Test
    void hideMenu() {
        // given
        UUID menuId = getUuidByCreatedMenu();

        // when
        ExtractableResponse<Response> response = hideMenuStep(menuId);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isFalse()
        );
    }

    @DisplayName("메뉴를 목록을 볼 수 있다.")
    @Test
    void getMenus() {
        // given
        createMenu_순살치킨();
        createMenu_반반치킨();

        // when
        ExtractableResponse<Response> response = getMenusStep();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id")).hasSize(2)
        );
    }

    private UUID getUuidByCreatedMenu() {
        return createMenu_순살치킨().getId();
    }

    private Menu createMenu_순살치킨() {
        Menu createRequest = menuCreateRequest(NAME_순살치킨, PRICE_32000, MENU_GROUP_추천메뉴.getId(), true,
                강정치킨_1개, 후라이드치킨_1개);
        return createMenuStep(createRequest).as(Menu.class);
    }

    private void createMenu_반반치킨() {
        Menu createRequest = menuCreateRequest(NAME_반반치킨, PRICE_19000, MENU_GROUP_추천메뉴.getId(), true,
                강정치킨_1개, 후라이드치킨_1개);
        createMenuStep(createRequest).as(Menu.class);
    }
}
