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
import static kitchenpos.fixture.MenuFixture.PRICE_34000;
import static kitchenpos.fixture.MenuFixture.PRICE_38000;
import static kitchenpos.fixture.MenuFixture.menuChangePriceRequest;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.NAME_양념치킨;
import static kitchenpos.fixture.ProductFixture.NAME_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.PRICE_20000;
import static kitchenpos.fixture.ProductFixture.PRICE_18000;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("메뉴 인수테스트")
class MenuAcceptanceTest {
    private Product PRODUCT_양념치킨;
    private Product PRODUCT_후라이드치킨;
    private MenuProduct 양념치킨_1개;
    private MenuProduct 후라이드치킨_1개;
    private UUID MENU_GROUP_ID;

    @BeforeEach
    void setUp() {
        MenuGroup menuGroup = createMenuGroupStep(menuGroupCreateRequest(이름_추천메뉴)).as(MenuGroup.class);
        PRODUCT_양념치킨 = createProduct(NAME_양념치킨, PRICE_20000);
        PRODUCT_후라이드치킨 = createProduct(NAME_후라이드치킨, PRICE_18000);
        양념치킨_1개 = menuProductResponse(PRODUCT_양념치킨, 1);
        후라이드치킨_1개 = menuProductResponse(PRODUCT_후라이드치킨, 1);
        MENU_GROUP_ID = menuGroup.getId();
    }

    @DisplayName("메뉴를 등록 한다.")
    @Test
    void createMenu() {
        // given
        Menu request = menuCreateRequest(NAME_순살치킨, PRICE_38000, MENU_GROUP_ID, true, 양념치킨_1개, 후라이드치킨_1개);

        // when
        ExtractableResponse<Response> response = createMenuStep(request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(NAME_순살치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(PRICE_38000),
                () -> assertThat(response.jsonPath().getObject("menuGroup.id", UUID.class)).isEqualTo(MENU_GROUP_ID),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(response.jsonPath().getList("menuProducts.product.id", UUID.class))
                        .containsExactly(PRODUCT_양념치킨.getId(), PRODUCT_후라이드치킨.getId()),
                () -> assertThat(response.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    @DisplayName("메뉴의 가격은 수정된다.")
    @Test
    void changeMenuPrice() {
        // given
        UUID menuId = createMenuId(NAME_순살치킨, PRICE_38000, 양념치킨_1개, 후라이드치킨_1개);
        Menu request = menuChangePriceRequest(PRICE_34000);

        // when
        ExtractableResponse<Response> response = changePriceMenuStep(menuId, request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(PRICE_34000),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(response.jsonPath().getList("menuProducts.product.name"))
                        .containsExactly(NAME_양념치킨, NAME_후라이드치킨),
                () -> assertThat(response.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    @DisplayName("메뉴가 노출된다.")
    @Test
    void displayMenu() {
        // given
        UUID menuId = createMenuId(NAME_순살치킨, PRICE_38000, 양념치킨_1개, 후라이드치킨_1개);
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
        UUID menuId = createMenuId(NAME_순살치킨, PRICE_38000, 양념치킨_1개, 후라이드치킨_1개);

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
        UUID 순살치킨_MENU_ID = createMenuId(NAME_순살치킨, PRICE_38000, 양념치킨_1개, 후라이드치킨_1개);
        UUID 반반치킨_MENU_ID = createMenuId(NAME_반반치킨, PRICE_19000, 양념치킨_1개, 후라이드치킨_1개);

        // when
        ExtractableResponse<Response> response = getMenusStep();

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("id", UUID.class))
                        .containsExactly(순살치킨_MENU_ID, 반반치킨_MENU_ID)
        );
    }

    private UUID createMenuId(String name, BigDecimal price, MenuProduct... menuProducts) {
        Menu request = menuCreateRequest(name, price, MENU_GROUP_ID, true, menuProducts);
        return createMenuStep(request).as(Menu.class).getId();
    }


    private static Product createProduct(String name, BigDecimal price) {
        return createProductStep(productCreateRequest(name, price)).as(Product.class);
    }
}
