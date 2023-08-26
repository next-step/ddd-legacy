package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuGroupAcceptanceTest.*;
import static kitchenpos.acceptance.ProductAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@DisplayName("메뉴관리")
class MenuAcceptanceTest extends AcceptanceTest {

    private static final String MENU_PATH = "/api/menus";

    private static Map<String, Object> menuGroup;
    private static Map<String, Object> 돈가스;
    private static Map<String, Object> 김밥;

    @BeforeEach
    void setBaseData() {
        menuGroup = 메뉴그룹을_등록_한다("분식").jsonPath().getMap(".");
        돈가스 = 상품_등록_한다("돈가스", 9000).jsonPath().getMap(".");
        김밥 = 상품_등록_한다("김밥", 3000).jsonPath().getMap(".");
    }

    @Nested
    @DisplayName("메뉴 등록 인수테스트")
    class create {
        /**
         * Given : 메뉴그룹을 등록하고
         * Given : 상품을 등록하고
         * When  : 등록된 메뉴그룹과 상품으로 메뉴를 등록하면
         * Then  : 등록한 메뉴가 조회 된다.
         */
        @DisplayName("메뉴를 등록한다.")
        @Test
        void createMenu() {
            //Given
            Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 12000, false);
            메뉴그룹_추가(menu);
            기등록_상품의_메뉴상품_추가(menu);

            //When
            Map<String, Object> 등록_메뉴 = 메뉴_등록_한다(menu).jsonPath().getMap(".");

            //Then
            List<String> 조회메뉴ids = 전체_메뉴를_조회_한다().jsonPath().getList("id");
            assertThat(조회메뉴ids).contains((String)등록_메뉴.get("id"));
        }

        /**
         * Given : 메뉴그룹을 등록 하지 않고
         * Given : 상품을 등록하고
         * When  : 등록된  상품으로만 메뉴를 등록하면 ( 메뉴 그룹 X )
         * Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("메뉴그룹 없이 등록 요청")
        @Test
        void createMenu2() {
            //Given
            Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 12000, false);
            기등록_상품의_메뉴상품_추가(menu);

            //When
            //Then
            assertThat(메뉴_등록_한다(menu).response().statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * Given : 상품을 등록하고
         * Given : 메뉴그룹을 등록하고
         * When  : 등록된 메뉴그룹과 등록되지 않은 상품으로 메뉴를 등록하면
         * Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("등록되지 않은 상품으로 메뉴등록 요청")
        @Test
        void createMenu3() {
            //Given
            Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 12000, false);
            메뉴그룹_추가(menu);
            등록하지_않은_상품의_메뉴상품_추가(menu);

            //When
            //Then
            assertThat(메뉴_등록_한다(menu).response().statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * Given : 상품을 등록하고
         * Given : 메뉴그룹을 등록하고
         * When  : 등록된 메뉴그룹과 상품과
         *         상품가격*갯수 < 메뉴가격의  메뉴를 등록하면
         * Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("등록되지 않은 상품으로 메뉴등록 요청")
        @Test
        void createMenu4() {
            //Given
            Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 12001, false);
            메뉴그룹_추가(menu);
            기등록_상품의_메뉴상품_추가(menu);

            //When
            //Then
            assertThat(메뉴_등록_한다(menu).response().statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Nested
    @DisplayName("메뉴 가격변경 인수테스트")
    class changePrice {
        /**
         * Given : 상품을 등록하고
         * Given : 메뉴그룹을 등록하고
         * Given : 등록된 메뉴그룹과 상품으로 메뉴를 등록하고
         * When  : 가격변경을 하면
         * Then  : 변경된 가격의 메뉴가 조회 된다.
         */
        @DisplayName("메뉴 가격변경")
        @Test
        void changePrice1() {
            //Given
            Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 12000, false);
            메뉴그룹_추가(menu);
            기등록_상품의_메뉴상품_추가(menu);
            String menuId = 메뉴_등록_한다(menu).jsonPath().getString("id");

            //When
            메뉴_가격을_변경한다(menuId, 2999);

            //Then
            assertThat(전체_메뉴를_조회_한다().jsonPath().getList("", Map.class)
                .stream()
                .filter(val -> val.get("id").equals(menuId))
                .findFirst()
                .orElseThrow()
                .get("price")).isEqualTo(2999f);
        }

        /**
         * Given : 상품을 등록하고
         * Given : 메뉴그룹을 등록하고
         * Given : 등록된 메뉴그룹과 상품으로 메뉴를 등록하고
         * When  : 1개라도 (상품가격*갯수) < 메뉴 조건을 만족하는 메뉴 가격을 변경하면
         * Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("메뉴 가격변경중 메뉴가격 검증 오류")
        @Test
        void changePrice2() {
            //Given
            Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 12000, false);
            메뉴그룹_추가(menu);
            기등록_상품의_메뉴상품_추가(menu);
            String menuId = 메뉴_등록_한다(menu).jsonPath().getString("id");

            //When
            메뉴_가격을_변경한다(menuId, 3001);

            //Then
            assertThat(메뉴_가격을_변경한다(menuId, 3001).response().statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * Given : 상품을 등록하고
     * Given : 메뉴그룹을 등록하고
     * Given : 등록된 메뉴그룹과 상품으로 메뉴를 등록하고
     * When  : 메뉴의 상품표시(display) 처리하면
     * Then  : 해당 메뉴가 display 로 된것으로 조회된다.
     */
    @DisplayName("메뉴 display")
    @Test
    void display1() {
        //Given
        Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 2999, false);
        메뉴그룹_추가(menu);
        기등록_상품의_메뉴상품_추가(menu);
        String menuId = 메뉴_등록_한다(menu).jsonPath().getString("id");

        //When
        메뉴_DISPLAY_처리한다(menuId);

        //Then
        assertThat(전체_메뉴를_조회_한다().jsonPath().getList("", Map.class)
            .stream()
            .filter(val -> val.get("id").equals(menuId))
            .findFirst()
            .orElseThrow()
            .get("displayed")).isEqualTo(true);
    }

    /**
     * Given : 상품을 등록하고
     * Given : 메뉴그룹을 등록하고
     * Given : 등록된 메뉴그룹과 상품으로 메뉴를 등록하고
     * When  : 1개라도 (상품가격*갯수) < 메뉴 조건을 만족하는 메뉴를 display 처리하면
     * Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
     */
    @DisplayName("메뉴 display 처리 중 메뉴가격 검증 오류")
    @Test
    void display2() {
        //Given
        Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 12000, false);
        메뉴그룹_추가(menu);
        기등록_상품의_메뉴상품_추가(menu);
        String menuId = 메뉴_등록_한다(menu).jsonPath().getString("id");

        //When
        메뉴_DISPLAY_처리한다(menuId);

        //Then
        assertThat(메뉴_가격을_변경한다(menuId, 3001).response().statusCode())
            .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given : 상품을 등록하고
     * Given : 메뉴그룹을 등록하고
     * Given : 등록된 메뉴그룹과 상품으로 메뉴를 등록하고
     * When  : 메뉴를 숨김 처리하면
     * Then  : 해당 메뉴가 숨김으로 된것으로 조회된다.
     */
    @DisplayName("메뉴 hide")
    @Test
    void hide1() {
        //Given
        Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", 2999, true);
        메뉴그룹_추가(menu);
        기등록_상품의_메뉴상품_추가(menu);
        String menuId = 메뉴_등록_한다(menu).jsonPath().getString("id");

        //When
        메뉴_숨김_처리한다(menuId);

        //Then
        assertThat(전체_메뉴를_조회_한다().jsonPath().getList("", Map.class)
            .stream()
            .filter(val -> val.get("id").equals(menuId))
            .findFirst()
            .orElseThrow()
            .get("displayed")).isEqualTo(false);
    }

    public static Map<String, Object> 메뉴_기본_입력_스텝(String name, long price, boolean displayed) {
        Map<String, Object> menu = new HashMap<>();
        menu.put("name", name);
        menu.put("price", price);
        menu.put("displayed", displayed);
        return menu;
    }

    private Map<String, Object> 메뉴그룹_추가(Map<String, Object> menu) {
        menu.put("menuGroupId", menuGroup.get("id"));
        return menu;
    }

    public static Map<String, Object> 기등록_상품의_메뉴상품_추가(Map<String, Object> menu) {
        menu.put("menuProducts",
            List.of(Map.of(
                    "productId", 돈가스.get("id"),
                    "quantity", 1),
                Map.of(
                    "productId", 김밥.get("id"),
                    "quantity", 1)
            ));
        return menu;
    }

    private Map<String, Object> 등록하지_않은_상품의_메뉴상품_추가(Map<String, Object> menu) {
        menu.put("menuProducts",
            List.of(Map.of(
                "productId", UUID.randomUUID(),
                "quantity", 1)
            ));
        return menu;
    }

    public static Map<String, Object> getMenuInput(String productId, long price) {
        Map<String, Object> menuGroup = 메뉴그룹을_등록_한다("분식").jsonPath().getMap(".");
        Map<String, Object> menu = 메뉴_기본_입력_스텝("돈가스_세트", price, false);
        menu.put("menuGroupId", menuGroup.get("id"));
        menu.put("menuProducts",
            List.of(Map.of(
                "productId", productId,
                "quantity", 1)
            ));
        return menu;
    }

    public static ExtractableResponse<Response> 메뉴_등록_한다(Map<String, Object> menu) {
        return RestAssured.given().log().all()
            .body(menu)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(MENU_PATH)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 전체_메뉴를_조회_한다() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(MENU_PATH)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 메뉴_가격을_변경한다(String id, long price) {
        return RestAssured.given().log().all()
            .body(Map.of("price", price))
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(getPath(id, "price"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 메뉴_DISPLAY_처리한다(String id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(getPath(id, "display"))
            .then().log().all()
            .extract();
    }

    public static ExtractableResponse<Response> 메뉴_숨김_처리한다(String id) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(getPath(id, "hide"))
            .then().log().all()
            .extract();
    }

    private static String getPath(String id, String detailPath) {
        return String.format("%s/%s/%s", MENU_PATH, id, detailPath);
    }

}
