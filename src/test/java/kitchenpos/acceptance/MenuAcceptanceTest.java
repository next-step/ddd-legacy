package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.acceptance.MenuGroupSteps.메뉴그룹이_등록됨;
import static kitchenpos.acceptance.MenuSteps.*;
import static kitchenpos.acceptance.ProductSteps.제품이_등록됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 관련 기능")
public class MenuAcceptanceTest extends AcceptanceTest {

    private UUID 후라이드치킨;
    private UUID 양념치킨;
    private UUID 콜라;
    private UUID 세트메뉴;
    private List<MenuProduct> 후라이드치킨_콜라;
    private List<MenuProduct> 양념치킨_콜라;

    @BeforeEach
    void setUp() {
        super.setup();

        후라이드치킨 = 제품이_등록됨(given(), "후라이드 치킨", 15_000L);
        양념치킨 = 제품이_등록됨(given(), "양념 치킨", 17_000L);
        콜라 = 제품이_등록됨(given(), "콜라", 2_000L);
        세트메뉴 = 메뉴그룹이_등록됨(given(), "세트메뉴");

        MenuProduct 콜라_2개 = 메뉴상품을_구성함(콜라, 2);
        MenuProduct 후라이드치킨_1개 = 메뉴상품을_구성함(후라이드치킨, 1);
        후라이드치킨_콜라 = List.of(후라이드치킨_1개, 콜라_2개);

        MenuProduct 앙념치킨_1개 = 메뉴상품을_구성함(양념치킨, 1);
        양념치킨_콜라 = List.of(앙념치킨_1개, 콜라_2개);
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void addMenu() {
        // when
        메뉴_등록_요청함("후라이드 치킨 세트", 19_000L, 세트메뉴, true, 후라이드치킨_콜라);

        // then
        var 메뉴_목록 = 메뉴_목록_조회_요청함();
        메뉴가_조회됨(메뉴_목록, "후라이드 치킨 세트");
    }

    @DisplayName("메뉴의 가격을 수정한다.")
    @Test
    void updatePrice() {
        // given
        UUID 후라이드_치킨_세트 = 메뉴가_등록됨("후라이드 치킨 세트", 19_000L, 세트메뉴, true, 후라이드치킨_콜라);

        // when
        메뉴의_가격_수정_요청함(후라이드_치킨_세트, 15_000L);

        // then
        var 메뉴_목록 = 메뉴_목록_조회_요청함();
        메뉴의_가격이_변경됨(메뉴_목록, 후라이드_치킨_세트, 15_000L);
    }

    @DisplayName("메뉴를 표시한다.")
    @Test
    void displayMenu() {
        // given
        UUID 후라이드_치킨_세트 = 메뉴가_등록됨("후라이드 치킨 세트", 19_000L, 세트메뉴, false, 후라이드치킨_콜라);

        // when
        메뉴_표시를_요청함(후라이드_치킨_세트);

        // then
        var 메뉴_목록 = 메뉴_목록_조회_요청함();
        메뉴가_표시됨(메뉴_목록, 후라이드_치킨_세트);
    }

    @DisplayName("메뉴를 숨긴다.")
    @Test
    void hideMenu() {
        // given
        UUID 후라이드_치킨_세트 = 메뉴가_등록됨("후라이드 치킨 세트", 19_000L, 세트메뉴, true, 후라이드치킨_콜라);

        // when
        메뉴_숨김을_요청함(후라이드_치킨_세트);

        // then
        var 메뉴_목록 = 메뉴_목록_조회_요청함();
        메뉴가_숨겨짐(메뉴_목록, 후라이드_치킨_세트);
    }

    @DisplayName("메뉴목록을 조회한다.")
    @Test
    void showMenus() {
        // given
        메뉴가_등록됨("후라이드 치킨 세트", 19_000L, 세트메뉴, true, 후라이드치킨_콜라);
        메뉴가_등록됨("양념 치킨 세트", 20_000L, 세트메뉴, true, 양념치킨_콜라);

        // when
        var 메뉴_목록 = 메뉴_목록_조회_요청함();

        // then
        메뉴가_조회됨(메뉴_목록, "후라이드 치킨 세트", "양념 치킨 세트");
    }

    private ExtractableResponse<Response> 메뉴_등록_요청함(final String name, final Long price, final UUID menuGroupId, final boolean displayed, final List<MenuProduct> menuProducts) {
        final Menu menu = new Menu();
        menu.setName(name);
        menu.setPrice(BigDecimal.valueOf(price));
        menu.setMenuGroupId(menuGroupId);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(menuProducts);

        return 메뉴_등록_요청(given(), menu);
    }

    private ExtractableResponse<Response> 메뉴_목록_조회_요청함() {
        return 메뉴_목록_조회_요청(given());
    }

    private ExtractableResponse<Response> 메뉴의_가격_수정_요청함(final UUID id, final Long price) {
        Menu menu = new Menu();
        menu.setPrice(BigDecimal.valueOf(price));

        return 메뉴_가격_수정_요청(given(), id, menu);
    }

    private ExtractableResponse<Response> 메뉴_표시를_요청함(final UUID id) {
        return 메뉴_표시를_요청(given(), id);
    }

    private ExtractableResponse<Response> 메뉴_숨김을_요청함(final UUID id) {
        return 메뉴_숨김을_요청(given(), id);
    }

    private UUID 메뉴가_등록됨(final String name, final Long price, final UUID menuGroupId, final boolean displayed, final List<MenuProduct> menuProducts) {
        return 메뉴_등록_요청함(name, price, menuGroupId, displayed, menuProducts).jsonPath().getUUID("id");
    }

    private void 메뉴의_가격이_변경됨(final ExtractableResponse<Response> response, final UUID id, final Long price) {
        List<Menu> menus = response.jsonPath().getList("", Menu.class);
        menus.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> assertThat(it.getPrice().longValue()).isEqualTo(price));
    }

    private void 메뉴가_표시됨(final ExtractableResponse<Response> response, final UUID id) {
        List<Menu> menus = response.jsonPath().getList("", Menu.class);
        menus.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> assertThat(it.isDisplayed()).isTrue());
    }

    private void 메뉴가_숨겨짐(final ExtractableResponse<Response> response, final UUID id) {
        List<Menu> menus = response.jsonPath().getList("", Menu.class);
        menus.stream()
                .filter(it -> id.equals(it.getId()))
                .forEach(it -> assertThat(it.isDisplayed()).isFalse());
    }

    private void 메뉴가_조회됨(final ExtractableResponse<Response> response, final String... names) {
        assertThat(response.jsonPath().getList("name", String.class)).contains(names);
    }
}
