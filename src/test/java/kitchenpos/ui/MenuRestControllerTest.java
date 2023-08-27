package kitchenpos.ui;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.objectmother.MenuGroupMaker;
import kitchenpos.objectmother.MenuMaker;
import kitchenpos.objectmother.MenuProductMaker;
import kitchenpos.objectmother.ProductMaker;
import kitchenpos.ui.utils.ControllerTest;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.ui.requestor.MenuGroupRequestor.메뉴그룹생성요청_메뉴그룹반환;
import static kitchenpos.ui.requestor.MenuRequestor.*;
import static kitchenpos.ui.requestor.ProductRequestor.상품생성요청_상품반환;
import static org.assertj.core.api.Assertions.assertThat;

class MenuRestControllerTest extends ControllerTest {

    private MenuGroup 메뉴그룹;
    private Product 상품_1;
    private Product 상품_2;
    private MenuProduct 메뉴상품_1;
    private MenuProduct 메뉴상품_2;
    private MenuProduct 수량음수상품;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        메뉴그룹 = 메뉴그룹생성요청_메뉴그룹반환(MenuGroupMaker.make("메뉴그룹"));
        상품_1 = 상품생성요청_상품반환(ProductMaker.make("상품1", 1500L));
        상품_2 = 상품생성요청_상품반환(ProductMaker.make("상품2", 3000L));
        메뉴상품_1 = MenuProductMaker.make(상품_1, 2);
        메뉴상품_2 = MenuProductMaker.make(상품_2, 5);
        수량음수상품 = MenuProductMaker.make(상품_2, -3);
    }

    @DisplayName("메뉴생성시 요청한 데이터로 메뉴가 생성되야 한다.")
    @Test
    void 메뉴생성() {
        // given
        Menu 메뉴 = MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2);

        // when
        ExtractableResponse<Response> response = 메뉴생성요청(메뉴);

        // then
        메뉴생성됨(메뉴, response);
    }

    @DisplayName("메뉴생성시 메뉴가격이 음수일경우 에러를 던진다.")
    @Test
    void 메뉴생성실패_가격_음수() {
        // given
        Menu 메뉴 = MenuMaker.make("메뉴", -15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2);

        // when
        ExtractableResponse<Response> response = 메뉴생성요청(메뉴);

        // then
        메뉴생성실패됨(response);
    }

    @DisplayName("메뉴생성시 메뉴상품에 수량이 0보다 작을경우 에러를 던진다.")
    @Test
    void 메뉴생성실패_메뉴상품수량_음수() {
        // given
        Menu 메뉴 = MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 수량음수상품);

        // when
        ExtractableResponse<Response> response = 메뉴생성요청(메뉴);

        // then
        메뉴생성실패됨(response);
    }

    @DisplayName("메뉴생성시 메뉴이름에 욕설이 포함된경우 에러를 던진다.")
    @Test
    void 메뉴생성실패_메뉴이름_욕설포함() {
        // given
        Menu 메뉴 = MenuMaker.make("Fuck메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2);

        // when
        ExtractableResponse<Response> response = 메뉴생성요청(메뉴);

        // then
        메뉴생성실패됨(response);
    }

    @DisplayName("메뉴가격 변경시 변경된 메뉴가격이 조회되야 한다.")
    @Test
    void 가격변경() {
        // given
        UUID 메뉴식별번호 = 메뉴생성요청_메뉴식별번호반환(MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // when
        ExtractableResponse<Response> response = 메뉴가격변경요청(메뉴식별번호, MenuMaker.make("메뉴", 3000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // then
        메뉴가격변경됨(response);
    }

    @DisplayName("메뉴가격 변경시 메뉴가격이 메뉴상품총가격보다 클 경우 에러를 던진다.")
    @Test
    void 가격변경실패_메뉴가격_메뉴상품총가격_초과() {
        // given
        UUID 메뉴식별번호 = 메뉴생성요청_메뉴식별번호반환(MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // when
        ExtractableResponse<Response> response = 메뉴가격변경요청(메뉴식별번호, MenuMaker.make("메뉴", 5000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // then
        가격변경실패됨(response);
    }

    @DisplayName("메뉴노출 시 메뉴에 노출여부 상태가 노출로 변경된다.")
    @Test
    void 메뉴노출() {
        // given
        UUID 메뉴식별번호 = 메뉴생성요청_메뉴식별번호반환(MenuMaker.make("메뉴", 3000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // when
        ExtractableResponse<Response> response = 메뉴노출요청(메뉴식별번호);

        // then
        메뉴노출됨(response);
    }

    @DisplayName("메뉴노출 시 메뉴가격이 메뉴상품총가격을 넘을경우 에러를 던진다.")
    @Test
    void 메뉴노출실패_메뉴가격_메뉴상품총가격_초과() {
        // given
        UUID 메뉴식별번호 = 메뉴생성요청_메뉴식별번호반환(MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // when
        ExtractableResponse<Response> response = 메뉴노출요청(메뉴식별번호);

        // then
        메뉴노출실패됨(response);
    }

    @DisplayName("메뉴비노출 시 메뉴에 노출여부 상태가 비노출로 변경된다.")
    @Test
    void 메뉴비노출() {
        // given
        UUID 메뉴식별번호 = 메뉴생성요청_메뉴식별번호반환(MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // when
        ExtractableResponse<Response> response = 메뉴비노출요청(메뉴식별번호);

        // then
        메뉴비노출됨(response);
    }

    @DisplayName("메뉴전체조회시 지금까지 등록된 메뉴가 전체조회되야 한다.")
    @Test
    void 메뉴전체조회() {
        // given
        메뉴생성요청(MenuMaker.make("메뉴", 15000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));
        메뉴생성요청(MenuMaker.make("메뉴2", 12000L, 메뉴그룹, 메뉴상품_1, 메뉴상품_2));

        // when
        ExtractableResponse<Response> response = 메뉴전체조회요청();

        // then
        메뉴전체조회됨(response);
    }

    private void 메뉴생성실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 메뉴생성됨(Menu 메뉴, ExtractableResponse<Response> response) {
        Menu menu = response.jsonPath().getObject("$", Menu.class);
        assertThat(menu.getName()).isEqualTo(메뉴.getName());
        assertThat(menu.getPrice()).isEqualTo(메뉴.getPrice());
        assertThat(menu.isDisplayed()).isEqualTo(메뉴.isDisplayed());
        assertThat(menu.getMenuGroup())
                .extracting(MenuGroup::getName)
                .isEqualTo(메뉴그룹.getName());
        assertThat(menu.getMenuProducts())
                .hasSize(2)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .usingRecursiveFieldByFieldElementComparator(
                        getRecursiveComparisonConfiguration()
                )
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

    private void 메뉴가격변경됨(ExtractableResponse<Response> response) {
        Menu menu = response.jsonPath().getObject("$", Menu.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(menu.getPrice()).isEqualTo(new BigDecimal(3000L));
    }

    private void 가격변경실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 메뉴노출됨(ExtractableResponse<Response> response) {
        Menu menu = response.jsonPath().getObject("$", Menu.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(menu.isDisplayed()).isTrue();
    }

    private void 메뉴노출실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 메뉴비노출됨(ExtractableResponse<Response> response) {
        Menu menu = response.jsonPath().getObject("$", Menu.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(menu.isDisplayed()).isFalse();
    }

    private void 메뉴전체조회됨(ExtractableResponse<Response> response) {
        List<Menu> menus = response.jsonPath().getList("$", Menu.class);
        assertThat(menus)
                .hasSize(2)
                .extracting(Menu::getName, Menu::getPrice, Menu::isDisplayed)
                .usingRecursiveFieldByFieldElementComparator(
                        getRecursiveComparisonConfiguration()
                )
                .containsExactlyInAnyOrder(
                        Tuple.tuple("메뉴", new BigDecimal(15000L), true),
                        Tuple.tuple("메뉴2", new BigDecimal(12000L), true)
                );

        assertThat(menus)
                .extracting(Menu::getMenuGroup)
                .extracting(MenuGroup::getName)
                .containsExactly(메뉴그룹.getName(), 메뉴그룹.getName());

        assertThat(menus)
                .flatExtracting(Menu::getMenuProducts)
                .extracting(MenuProduct::getProduct)
                .extracting(Product::getName, Product::getPrice)
                .usingRecursiveFieldByFieldElementComparator(
                        getRecursiveComparisonConfiguration()
                )
                .containsExactlyInAnyOrder(
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice()),
                        Tuple.tuple(상품_1.getName(), 상품_1.getPrice()),
                        Tuple.tuple(상품_2.getName(), 상품_2.getPrice())
                );
    }

    private RecursiveComparisonConfiguration getRecursiveComparisonConfiguration() {
        return RecursiveComparisonConfiguration
                .builder()
                .withComparatorForType(BigDecimal::compareTo, BigDecimal.class)
                .build();
    }

}