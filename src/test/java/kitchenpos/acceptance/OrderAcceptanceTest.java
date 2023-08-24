package kitchenpos.acceptance;

import static kitchenpos.acceptance.MenuAcceptanceTest.*;
import static kitchenpos.acceptance.OrderTableAcceptanceTest.*;
import static kitchenpos.acceptance.ProductAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

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

@DisplayName("주문 관리")
class OrderAcceptanceTest extends AcceptanceTest {
    private static final String ORDER_PATH = "/api/orders";

    private String 돈가스Id;
    private String menuId;
    private String orderTableId;
    private long price = 4500;

    @BeforeEach
    void setBaseData() {
        돈가스Id = 상품_등록_한다("돈가스", price).jsonPath().getString("id");
        menuId = 메뉴_등록_한다(getMenuInput(돈가스Id, price)).jsonPath().getString("id");
        메뉴_DISPLAY_처리한다(menuId);
        orderTableId = 매장_테이블을_등록_한다("백합").jsonPath().getString("id");
    }

    @Nested
    @DisplayName("주문 등록 인수테스트")
    class create {
        /**
         * When : takeOut 주문을 생성하면
         * Then : 주문을 전체 조회 시 생성한 주문을 찾을 수 있다.
         */
        @DisplayName("TakeOut 주문을 생성한다.")
        @Test
        void createTakeoutOrder() {
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            assertThat(전체_주문을_조회한다().jsonPath().getList("id"))
                .containsOnly(orderId);
        }

        /**
         *  Given : 매장 테이블에 손님이 앉고
         *  When  : 매장식사 주문을 생성하면
         *  Then  : 주문을 전체 조회 시 생성한 주문을 찾을 수 있다.
         */
        @DisplayName("매장 식사 주문을 생성한다.")
        @Test
        void createEatInOrder() {
            매장_테이블에_손님이_앉는다(orderTableId);

            String orderId = 주문을_생성_한다(주문_입력("EAT_IN", "")).jsonPath().getString("id");

            assertThat(전체_주문을_조회한다().jsonPath().getList("id"))
                .containsOnly(orderId);
        }

        /**
         *  When  : 배달 주문을 생성하면
         *  Then  : 주문을 전체 조회 시 생성한 주문을 찾을 수 있다.
         */
        @DisplayName("매장 식사 주문을 생성한다.")
        @Test
        void createDeliveryOrder() {

            String orderId = 주문을_생성_한다(주문_입력("DELIVERY", "사무실주소")).jsonPath().getString("id");

            assertThat(전체_주문을_조회한다().jsonPath().getList("id"))
                .containsOnly(orderId);
        }

        /**
         * When : 등록 되지 않은 메뉴로 주문을 하면
         * Then : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("등록되지 않은 메뉴로 주문한다.")
        @Test
        void notExistsMenu() {
            menuId = UUID.randomUUID().toString();
            assertThat(주문을_생성_한다(주문_입력("EAT_IN", "")).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * When : 화면에 표시 하지 않은 메뉴로 주문을 하면
         * Then : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("등록되지 않은 메뉴로 주문한다.")
        @Test
        void hideMenu() {
            메뉴_숨김_처리한다(menuId);
            assertThat(주문을_생성_한다(주문_입력("EAT_IN", "")).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * When : 배달 주문일떄, 배송지 주소가 없으면
         * Then : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("등록되지 않은 메뉴로 주문한다.")
        @Test
        void withoutAddrInDelivery() {
            assertThat(주문을_생성_한다(주문_입력("DELIVERY", "")).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         * When : 매장식사지만 등록되지 않은 매장테이블을 선택해서 주문하면
         * Then : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("등록되지 않은 메뉴로 주문한다.")
        @Test
        void withoutOrderTable() {
            orderTableId = "";
            assertThat(주문을_생성_한다(주문_입력("EAT_IN", "")).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Nested
    @DisplayName("주문 수락 인수테스트")
    class accept {
        /**
         *  Given : 주문을 생성 하고
         *  When  : 주문을 수락 하면
         *  Then  : 주문을 전체 조회 했을떄, 수락한 주문을 찾을 수 있다.
         */
        @DisplayName("주문을 생성 후, 수락 한다.")
        @Test
        void normalAccept() {
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            주문을_수락_한다(orderId);

            assertThat(전체_주문을_조회한다().jsonPath().getList("", Map.class)
                .stream()
                .filter(val -> val.get("id").equals(orderId))
                .findFirst()
                .orElseThrow()
                .get("status")).isEqualTo("ACCEPTED");
        }

        /**
         *  Given : 주문을 생성 하고
         *  Given : 주문을 수락 하고
         *  Given : 주문을 서빙 하고
         *  When  : 주문을 수락 하면
         *  Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("주문을 생성 후, 수락 한다.")
        @Test
        void wrongStatusAccept() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);

            //When
            //Then
            assertThat(주문을_수락_한다(orderId).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Nested
    @DisplayName("주문 서빙 인수테스트")
    class serve {
        /**
         *  Given : 주문을 생성 하고
         *  Given : 주문을 수락 하고
         *  When  : 주문을 서빙 하면
         *  Then  : 주문을 전체 조회 했을떄, 서빙한 주문을 찾을 수 있다.
         */
        @DisplayName("주문을 생성 후, 수락 한다.")
        @Test
        void normalServe() {
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);

            assertThat(전체_주문을_조회한다().jsonPath().getList("", Map.class)
                .stream()
                .filter(val -> val.get("id").equals(orderId))
                .findFirst()
                .orElseThrow()
                .get("status")).isEqualTo("SERVED");
        }

        /**
         *  Given : 주문을 생성 하고
         *  When  : 주문을 서빙 하고
         *  Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("주문을 생성 후, 수락 한다.")
        @Test
        void wrongStatusServe() {
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            assertThat(주문을_서빙_한다(orderId).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Nested
    @DisplayName("배달 시작 인수테스트")
    class startDelivery {

        /**
         *  Given : 주문을 배달로 생성 하고
         *  Given : 주문을 수락 하고
         *  Given : 주문을 서빙 하고
         *  When  : 배달을 시작하면
         *  Then  : 주문을 전체 조회 했을떄, 배달중인 주문을 찾을 수 있다.
         */
        @DisplayName("배달을 시작한다.")
        @Test
        void normalStartDelivery() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("DELIVERY", "우리집")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);

            //When
            배달을_시작_한다(orderId);

            //Then
            assertThat(전체_주문을_조회한다().jsonPath().getList("", Map.class)
                .stream()
                .filter(val -> val.get("id").equals(orderId))
                .findFirst()
                .orElseThrow()
                .get("status")).isEqualTo("DELIVERING");
        }

        /**
         *  Given : 주문을 포장으로 생성 하고
         *  Given : 주문을 수락 하고
         *  Given : 주문을 서빙 하고
         *  When  : 배달을 시작하면
         *  Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("포장 주문의 배달을 시작한다.")
        @Test
        void startDeliveryByEatIn() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);

            //When
            assertThat(배달을_시작_한다(orderId).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         *  Given : 주문을 매장식사 생성 하고
         *  Given : 주문을 수락 하고
         *  When  : 배달을 시작하면
         *  Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("주문 수락 상태에서 배달을 시작한다.")
        @Test
        void startDeliveryWrongStatus() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("DELIVERY", "우리집")).jsonPath().getString("id");
            주문을_수락_한다(orderId);

            //When
            배달을_시작_한다(orderId);

            //Then
            assertThat(배달을_시작_한다(orderId).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @Nested
    @DisplayName("배달 완료 인수테스트")
    class completeDelivery {

        /**
         *  Given : 주문을 배달로 생성 하고
         *  Given : 주문을 수락 하고
         *  Given : 주문을 서빙 하고
         *  Given : 배달을 시작하고
         *  When  : 배달을 완료하면
         *  Then  : 주문을 전체 조회 했을떄, 배달완료의 주문을 찾을 수 있다.
         */
        @DisplayName("배달을 완료 한다.")
        @Test
        void normalComplteDelivery() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("DELIVERY", "우리집")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);
            배달을_시작_한다(orderId);

            //When
            배달을_완료_한다(orderId);

            //Then
            assertThat(전체_주문을_조회한다().jsonPath().getList("", Map.class)
                .stream()
                .filter(val -> val.get("id").equals(orderId))
                .findFirst()
                .orElseThrow()
                .get("status")).isEqualTo("DELIVERED");
        }

        /**
         *  Given : 주문을 배달로 생성 하고
         *  Given : 주문을 수락 하고
         *  Given : 주문을 서빙 하고
         *  When  : 배달을 완료하면
         *  Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("배달 시작 안하고 배달을 완료한다.")
        @Test
        void ComplteDeliveryWithWrongStatus() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("DELIVERY", "우리집")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);

            //When
            //Then
            assertThat(배달을_완료_한다(orderId).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @DisplayName("주문을 완료 한다.")
    @Nested
    class complete {

        /**
         *  Given : 포장 주문을 생성 하고
         *  Given : 주문을 수락 하고
         *  Given : 주문을 서빙 하고
         *  When  : 주문 완료 하면
         *  Then  : 주문을 전체 조회 했을떄, 완료된 주문을 찾을 수 있다.
         */
        @DisplayName("주문을 정상 완료 한다.")
        @Test
        void normalComplte() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);

            //When
            주문을_완료_한다(orderId);

            //Then
            assertThat(전체_주문을_조회한다().jsonPath().getList("", Map.class)
                .stream()
                .filter(val -> val.get("id").equals(orderId))
                .findFirst()
                .orElseThrow()
                .get("status")).isEqualTo("COMPLETED");
        }

        /**
         *  Given : 배달 주문을 생성 하고
         *  Given : 주문을 수락 하고
         *  Given : 주문을 서빙 하고
         *  When  : 주문 완료 하면
         *  Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("주문을 정상 완료 한다.")
        @Test
        void complteWrongStatus1() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("DELIVERY", "adrr")).jsonPath().getString("id");
            주문을_수락_한다(orderId);
            주문을_서빙_한다(orderId);

            //When
            //Then
            assertThat(주문을_완료_한다(orderId).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        /**
         *  Given : 포장 주문을 생성 하고
         *  Given : 주문을 수락 하고
         *  When  : 주문 완료 하면
         *  Then  : INTERNAL_SERVER_ERROR 오류가 발생한다.
         */
        @DisplayName("주문을 정상 완료 한다.")
        @Test
        void completeWrongStatus2() {
            //Given
            String orderId = 주문을_생성_한다(주문_입력("TAKEOUT", "")).jsonPath().getString("id");
            주문을_수락_한다(orderId);

            //When
            //Then
            assertThat(주문을_완료_한다(orderId).statusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private ExtractableResponse<Response> 주문을_완료_한다(String orderId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_PATH, orderId, "complete"))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 배달을_완료_한다(String orderId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_PATH, orderId, "complete-delivery"))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 배달을_시작_한다(String orderId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_PATH, orderId, "start-delivery"))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 주문을_서빙_한다(String orderId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_PATH, orderId, "serve"))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 주문을_수락_한다(String orderId) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(String.format("%s/%s/%s", ORDER_PATH, orderId, "accept"))
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 주문을_생성_한다(Map<String, Object> input) {
        return RestAssured.given().log().all()
            .body(input)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(ORDER_PATH)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 전체_주문을_조회한다() {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(ORDER_PATH)
            .then().log().all()
            .extract();
    }

    private Map<String, Object> 주문_입력(String type, String address) {
        return Map.of("type", type,
            "orderTableId", orderTableId,
            "deliveryAddress", address,
            "orderLineItems", List.of(
                Map.of("menuId", menuId
                    , "price", price
                    , "quantity", 3
                )
            )
        );
    }
}
