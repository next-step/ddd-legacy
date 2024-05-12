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
import static kitchenpos.fixture.MenuFixture.이름_반반치킨;
import static kitchenpos.fixture.MenuFixture.이름_순살치킨;
import static kitchenpos.fixture.MenuFixture.가격_19000;
import static kitchenpos.fixture.MenuFixture.가격_34000;
import static kitchenpos.fixture.MenuFixture.가격_38000;
import static kitchenpos.fixture.MenuFixture.menuChangePriceRequest;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@AcceptanceTest
@DisplayName("메뉴 인수테스트")
class MenuAcceptanceTest {
    private Product 상품_양념치킨;
    private Product 상품_후라이드치킨;
    private MenuProduct 양념치킨_1개;
    private MenuProduct 후라이드치킨_1개;
    private UUID ID_추천메뉴;

    @BeforeEach
    void setUp() {
        MenuGroup 메뉴그룹_추천메뉴 = createMenuGroupStep(menuGroupCreateRequest(이름_추천메뉴)).as(MenuGroup.class);
        상품_양념치킨 = createProduct(이름_양념치킨, 가격_20000);
        상품_후라이드치킨 = createProduct(이름_후라이드치킨, 가격_18000);
        양념치킨_1개 = menuProductResponse(상품_양념치킨, 1);
        후라이드치킨_1개 = menuProductResponse(상품_후라이드치킨, 1);
        ID_추천메뉴 = 메뉴그룹_추천메뉴.getId();
    }

    @DisplayName("메뉴를 등록 한다.")
    @Test
    void createMenu() {
        // given
        Menu request = menuCreateRequest(이름_순살치킨, 가격_38000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

        // when
        ExtractableResponse<Response> response = createMenuStep(request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo(이름_순살치킨),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_38000),
                () -> assertThat(response.jsonPath().getObject("menuGroup.id", UUID.class)).isEqualTo(ID_추천메뉴),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(response.jsonPath().getList("menuProducts.product.id", UUID.class))
                        .containsExactly(상품_양념치킨.getId(), 상품_후라이드치킨.getId()),
                () -> assertThat(response.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    @DisplayName("메뉴의 가격은 수정된다.")
    @Test
    void changeMenuPrice() {
        // given
        UUID menuId = createMenuId(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);
        Menu request = menuChangePriceRequest(가격_34000);

        // when
        ExtractableResponse<Response> response = changePriceMenuStep(menuId, request);

        // then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_34000),
                () -> assertThat(response.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(response.jsonPath().getList("menuProducts.product.name"))
                        .containsExactly(이름_양념치킨, 이름_후라이드치킨),
                () -> assertThat(response.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    @DisplayName("메뉴가 노출된다.")
    @Test
    void displayMenu() {
        // given
        UUID menuId = createMenuId(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);
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
        UUID menuId = createMenuId(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);

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
        UUID 순살치킨_MENU_ID = createMenuId(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);
        UUID 반반치킨_MENU_ID = createMenuId(이름_반반치킨, 가격_19000, 양념치킨_1개, 후라이드치킨_1개);

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
        Menu request = menuCreateRequest(name, price, ID_추천메뉴, true, menuProducts);
        return createMenuStep(request).as(Menu.class).getId();
    }


    private static Product createProduct(String name, BigDecimal price) {
        return createProductStep(productCreateRequest(name, price)).as(Product.class);
    }
}
