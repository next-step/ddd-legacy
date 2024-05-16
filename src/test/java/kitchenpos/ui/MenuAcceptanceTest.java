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

import static kitchenpos.acceptacne.steps.MenuGroupSteps.메뉴그룹을_등록한다;
import static kitchenpos.acceptacne.steps.MenuSteps.메뉴_등록한다;
import static kitchenpos.acceptacne.steps.MenuSteps.메뉴를_노출한다;
import static kitchenpos.acceptacne.steps.MenuSteps.메뉴를_숨긴다;
import static kitchenpos.acceptacne.steps.MenuSteps.메뉴의_가격을_수정한다;
import static kitchenpos.acceptacne.steps.MenuSteps.메뉴의_목록을_조회한다;
import static kitchenpos.acceptacne.steps.ProductSteps.상품을_등록한다;
import static kitchenpos.fixture.MenuFixture.menuChangePriceRequest;
import static kitchenpos.fixture.MenuFixture.menuCreateRequest;
import static kitchenpos.fixture.MenuFixture.가격_19000;
import static kitchenpos.fixture.MenuFixture.가격_34000;
import static kitchenpos.fixture.MenuFixture.가격_38000;
import static kitchenpos.fixture.MenuFixture.이름_반반치킨;
import static kitchenpos.fixture.MenuFixture.이름_순살치킨;
import static kitchenpos.fixture.MenuGroupFixture.menuGroupCreateRequest;
import static kitchenpos.fixture.MenuGroupFixture.이름_추천메뉴;
import static kitchenpos.fixture.MenuProductFixture.menuProductResponse;
import static kitchenpos.fixture.ProductFixture.productCreateRequest;
import static kitchenpos.fixture.ProductFixture.가격_18000;
import static kitchenpos.fixture.ProductFixture.가격_20000;
import static kitchenpos.fixture.ProductFixture.이름_양념치킨;
import static kitchenpos.fixture.ProductFixture.이름_후라이드치킨;
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
        MenuGroup 메뉴그룹_추천메뉴 = 메뉴그룹을_등록한다(menuGroupCreateRequest(이름_추천메뉴)).as(MenuGroup.class);
        상품_양념치킨 = 상품을_등록한다(productCreateRequest(이름_양념치킨, 가격_20000)).as(Product.class);
        상품_후라이드치킨 = 상품을_등록한다(productCreateRequest(이름_후라이드치킨, 가격_18000)).as(Product.class);
        양념치킨_1개 = menuProductResponse(상품_양념치킨, 1);
        후라이드치킨_1개 = menuProductResponse(상품_후라이드치킨, 1);
        ID_추천메뉴 = 메뉴그룹_추천메뉴.getId();
    }

    @DisplayName("[성공] 메뉴를 등록 한다.")
    @Test
    void createMenu() {
        // given
        Menu 순살치킨_메뉴_요청 = 메뉴_등록_요청(이름_순살치킨, 가격_38000, ID_추천메뉴, true, 양념치킨_1개, 후라이드치킨_1개);

        // when
        var 순살치킨_메뉴_응답 = 메뉴_등록한다(순살치킨_메뉴_요청);

        // then
        메뉴가_등록되었는지_검증한다(순살치킨_메뉴_응답);
    }

    @DisplayName("[성공] 메뉴의 가격은 수정된다.")
    @Test
    void changeMenuPrice() {
        // given
        UUID 메뉴Id = 메뉴를_등록하고_해당_ID를_반환한다(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);
        Menu 메뉴_가격_수정_요청 = 메뉴의_가격_수정을_요청(가격_34000);

        // when
        var 메뉴_가격_수정_응답 = 메뉴의_가격을_수정한다(메뉴Id, 메뉴_가격_수정_요청);

        // then
        메뉴의_가격이_수정되었는지_검증한다(메뉴_가격_수정_응답);
    }

    @DisplayName("[성공] 메뉴가 노출된다.")
    @Test
    void displayMenu() {
        // given
        UUID 메뉴Id = 메뉴를_등록하고_해당_ID를_반환한다(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);
        메뉴를_숨긴다(메뉴Id);

        // when
        var 노출된_메뉴_응답 = 메뉴를_노출한다(메뉴Id);

        // then
        메뉴가_노출되었는지_검증한다(노출된_메뉴_응답);
    }

    @DisplayName("[성공] 메뉴를 보이지 않게 한다.")
    @Test
    void hideMenu() {
        // given
        UUID menuId = 메뉴를_등록하고_해당_ID를_반환한다(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);

        // when
        var 숨긴_메뉴_응답 = 메뉴를_숨긴다(menuId);

        // then
        메뉴가_숨겨졌는지_검증한다(숨긴_메뉴_응답);
    }

    @DisplayName("[성공] 메뉴의 목록을 볼 수 있다.")
    @Test
    void getMenus() {
        // given
        UUID 순살치킨_MENU_ID = 메뉴를_등록하고_해당_ID를_반환한다(이름_순살치킨, 가격_38000, 양념치킨_1개, 후라이드치킨_1개);
        UUID 반반치킨_MENU_ID = 메뉴를_등록하고_해당_ID를_반환한다(이름_반반치킨, 가격_19000, 양념치킨_1개, 후라이드치킨_1개);

        // when
        var 메뉴_목록_응답 = 메뉴의_목록을_조회한다();

        // then
        메뉴의_목록을_검증한다(순살치킨_MENU_ID, 반반치킨_MENU_ID, 메뉴_목록_응답);
    }

    private UUID 메뉴를_등록하고_해당_ID를_반환한다(String name, BigDecimal price, MenuProduct... menuProducts) {
        Menu request = 메뉴_등록_요청(name, price, ID_추천메뉴, true, menuProducts);
        return 메뉴_등록한다(request).as(Menu.class).getId();
    }

    private Menu 메뉴_등록_요청(String name, BigDecimal price, UUID menuGroupId, boolean displayed, MenuProduct... menuProducts) {
        return menuCreateRequest(name, price, menuGroupId, displayed, menuProducts);
    }

    private static Menu 메뉴의_가격_수정을_요청(BigDecimal price) {
        return menuChangePriceRequest(price);
    }

    private void 메뉴가_등록되었는지_검증한다(ExtractableResponse<Response> 순살치킨_메뉴_응답) {
        assertAll(
                () -> assertThat(순살치킨_메뉴_응답.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(순살치킨_메뉴_응답.jsonPath().getObject("id", UUID.class)).isNotNull(),
                () -> assertThat(순살치킨_메뉴_응답.jsonPath().getString("name")).isEqualTo(이름_순살치킨),
                () -> assertThat(순살치킨_메뉴_응답.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_38000),
                () -> assertThat(순살치킨_메뉴_응답.jsonPath().getObject("menuGroup.id", UUID.class)).isEqualTo(ID_추천메뉴),
                () -> assertThat(순살치킨_메뉴_응답.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(순살치킨_메뉴_응답.jsonPath().getList("menuProducts.product.id", UUID.class))
                        .containsExactly(상품_양념치킨.getId(), 상품_후라이드치킨.getId()),
                () -> assertThat(순살치킨_메뉴_응답.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    private static void 메뉴의_가격이_수정되었는지_검증한다(ExtractableResponse<Response> 메뉴_가격_수정_응답) {
        assertAll(
                () -> assertThat(메뉴_가격_수정_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴_가격_수정_응답.jsonPath().getObject("price", BigDecimal.class)).isEqualTo(가격_34000),
                () -> assertThat(메뉴_가격_수정_응답.jsonPath().getBoolean("displayed")).isTrue(),
                () -> assertThat(메뉴_가격_수정_응답.jsonPath().getList("menuProducts.product.name"))
                        .containsExactly(이름_양념치킨, 이름_후라이드치킨),
                () -> assertThat(메뉴_가격_수정_응답.jsonPath().getList("menuProducts.quantity")).hasSize(2).contains(1)
        );
    }

    private static void 메뉴가_노출되었는지_검증한다(ExtractableResponse<Response> 노출된_메뉴_응답) {
        assertAll(
                () -> assertThat(노출된_메뉴_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(노출된_메뉴_응답.jsonPath().getBoolean("displayed")).isTrue()
        );
    }

    private static void 메뉴가_숨겨졌는지_검증한다(ExtractableResponse<Response> 숨긴_메뉴_응답) {
        assertAll(
                () -> assertThat(숨긴_메뉴_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(숨긴_메뉴_응답.jsonPath().getBoolean("displayed")).isFalse()
        );
    }

    private static void 메뉴의_목록을_검증한다(UUID 순살치킨_MENU_ID, UUID 반반치킨_MENU_ID, ExtractableResponse<Response> 메뉴_목록_응답) {
        assertAll(
                () -> assertThat(메뉴_목록_응답.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(메뉴_목록_응답.jsonPath().getList("id", UUID.class))
                        .containsExactly(순살치킨_MENU_ID, 반반치킨_MENU_ID)
        );
    }
}
