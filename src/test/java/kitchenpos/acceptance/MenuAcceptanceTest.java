package kitchenpos.acceptance;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static kitchenpos.acceptance.MenuSteps.*;
import static kitchenpos.acceptance.ProductSteps.제품이_등록됨;
import static kitchenpos.acceptance.MenuGroupSteps.메뉴그룹이_등록됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("메뉴 관련 기능")
public class MenuAcceptanceTest extends AcceptanceTest {

    private UUID 후라이드치킨;
    private UUID 양념치킨;
    private UUID 콜라;
    private UUID 세트메뉴;
    private List<Map> 후라이드치킨_콜라;
    private List<Map> 양념치킨_콜라;

    @BeforeEach
    void setUp() {
        super.setup();

        후라이드치킨 = 제품이_등록됨(given(), "후라이드 치킨", 15_000);
        양념치킨 = 제품이_등록됨(given(), "양념 치킨", 17_000);
        콜라 = 제품이_등록됨(given(), "콜라", 2_000);
        세트메뉴 = 메뉴그룹이_등록됨(given(), "세트메뉴");

        Map<String, Object> 콜라_2개 = 메뉴상품을_구성함(콜라, 2);
        Map<String, Object> 후라이드치킨_1개 = 메뉴상품을_구성함(후라이드치킨, 1);
        후라이드치킨_콜라 = List.of(후라이드치킨_1개, 콜라_2개);

        Map<String, Object> 앙념치킨_1개 = 메뉴상품을_구성함(양념치킨, 1);
        양념치킨_콜라 = List.of(앙념치킨_1개, 콜라_2개);
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void addMenu() {
        // when
        메뉴_등록_요청함("후라이드 치킨 세트", 19_000, 세트메뉴, true, 후라이드치킨_콜라);

        // then
        var 메뉴_목록 = 메뉴_목록_조회_요청함();
        메뉴가_조회됨(메뉴_목록, "후라이드 치킨 세트");
    }

    @DisplayName("메뉴의 가격을 수정한다.")
    @Test
    void updatePrice() {
        // given
        UUID 후라이드_치킨_세트 = 메뉴가_등록됨("후라이드 치킨 세트", 19_000, 세트메뉴, true, 후라이드치킨_콜라);

        // when
        메뉴의_가격_수정_요청함(후라이드_치킨_세트, 15_000);

        // then
        var 메뉴_목록 = 메뉴_목록_조회_요청함();
        메뉴의_가격이_변경됨(메뉴_목록, 후라이드_치킨_세트, 15_000);
    }

    @DisplayName("메뉴를 표시한다.")
    @Test
    void displayMenu() {
        // given
        UUID 후라이드_치킨_세트 = 메뉴가_등록됨("후라이드 치킨 세트", 19_000, 세트메뉴, false, 후라이드치킨_콜라);

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
        UUID 후라이드_치킨_세트 = 메뉴가_등록됨("후라이드 치킨 세트", 19_000, 세트메뉴, true, 후라이드치킨_콜라);

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
        메뉴가_등록됨("후라이드 치킨 세트", 19_000, 세트메뉴, true, 후라이드치킨_콜라);
        메뉴가_등록됨("양념 치킨 세트", 20_000, 세트메뉴, true, 양념치킨_콜라);

        // when
        var 메뉴_목록 = 메뉴_목록_조회_요청함();

        // then
        메뉴가_조회됨(메뉴_목록, "후라이드 치킨 세트", "양념 치킨 세트");
    }

    private ExtractableResponse<Response> 메뉴_등록_요청함(final String name, final int price, final UUID menuGroupId, final boolean displayed, final List<Map> menuProducts) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("price", price);
        params.put("menuGroupId", menuGroupId);
        params.put("displayed", displayed);
        params.put("menuProducts", menuProducts);

        return 메뉴_등록_요청(given(), params);
    }

    private ExtractableResponse<Response> 메뉴_목록_조회_요청함() {
        return 메뉴_목록_조회_요청(given());
    }

    private ExtractableResponse<Response> 메뉴의_가격_수정_요청함(final UUID id, final int price) {
        Map<String, Object> params = new HashMap<>();
        params.put("price", String.valueOf(price));

        return 메뉴_가격_수정_요청(given(), id, params);
    }

    private ExtractableResponse<Response> 메뉴_표시를_요청함(final UUID id) {
        return 메뉴_표시를_요청(given(), id);
    }

    private ExtractableResponse<Response> 메뉴_숨김을_요청함(final UUID id) {
        return 메뉴_숨김을_요청(given(), id);
    }

    private UUID 메뉴가_등록됨(final String name, final int price, final UUID menuGroupId, final boolean displayed, final List<Map> menuProducts) {
        return 메뉴_등록_요청함(name, price, menuGroupId, displayed, menuProducts).jsonPath().getUUID("id");
    }

    private void 메뉴의_가격이_변경됨(final ExtractableResponse<Response> response, final UUID id, final int price) {
        List<Map> list = response.jsonPath().get();
        for (Map map : list) {
            comparePrice(map, id, price);
        }
    }

    private void comparePrice(final Map map, final UUID id, final int price) {
        if (id.toString().equals(map.get("id"))) {
            assertThat(Math.round((Float) map.get("price"))).isEqualTo(price);
        }
    }

    private void 메뉴가_표시됨(final ExtractableResponse<Response> response, final UUID id) {
        List<Map> list = response.jsonPath().get();
        for (Map map : list) {
            compareDisplayed(map, id, true);
        }
    }

    private void 메뉴가_숨겨짐(final ExtractableResponse<Response> response, final UUID id) {
        List<Map> list = response.jsonPath().get();
        for (Map map : list) {
            compareDisplayed(map, id, false);
        }
    }

    private void compareDisplayed(final Map map, final UUID id, final boolean displayed) {
        if (id.toString().equals(map.get("id"))) {
            assertThat((boolean) map.get("displayed")).isEqualTo(displayed);
        }
    }

    private void 메뉴가_조회됨(final ExtractableResponse<Response> response, final String... names) {
        assertThat(response.jsonPath().getList("name", String.class)).contains(names);
    }
}
