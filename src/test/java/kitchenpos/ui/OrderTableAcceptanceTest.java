package kitchenpos.ui;

import kitchenpos.domain.OrderTable;
import kitchenpos.setup.MenuGroupSetup;
import kitchenpos.setup.MenuSetup;
import kitchenpos.setup.OrderTableSetup;
import kitchenpos.setup.ProductSetup;
import kitchenpos.util.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static kitchenpos.fixture.OrderTableFixture.generateOrderTable;
import static org.hamcrest.Matchers.equalTo;


class OrderTableAcceptanceTest extends AcceptanceTest {
    @Autowired
    private MenuGroupSetup menuGroupSetup;

    @Autowired
    private MenuSetup menuSetup;

    @Autowired
    private ProductSetup productSetup;

    @Autowired
    private OrderTableSetup orderTableSetup;

    @DisplayName("테이블을 생성한다")
    @Test
    void createTable() {
        // given
        final OrderTable orderTable = generateOrderTable();

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(orderTable))
                .when()
                .post(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value())
                .assertThat()
                .body("name", equalTo(orderTable.getName()))
                .body("numberOfGuests", equalTo(orderTable.getNumberOfGuests()))
                .body("occupied", equalTo(orderTable.isOccupied()))
        ;
    }

    @DisplayName("테이블 이름을 반드시 지정해야 한다")
    @NullAndEmptySource
    @ParameterizedTest
    void nullOrEmptyName(final String name) {
        // given
        final OrderTable orderTable = generateOrderTable(name);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(orderTable))
                .when()
                .post(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("테이블을 점유한다")
    @Test
    void sitTable() {
        // given
        final OrderTable orderTable = orderTableSetup.setupOrderTable(generateOrderTable(0, false));

        // expected
        final String path = getPath() + "/" + orderTable.getId().toString() + "/sit";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(orderTable))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
                .body("occupied", equalTo(true))
        ;
    }

    @DisplayName("손님 수를 변경할 수 있다")
    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable orderTable = orderTableSetup.setupOccupiedOrderTable();
        final int newNumberOfGuests = 3;
        orderTable.setNumberOfGuests(newNumberOfGuests);

        // expected
        final String path = getPath() + "/" + orderTable.getId().toString() + "/number-of-guests";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(orderTable))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
                .body("numberOfGuests", equalTo(newNumberOfGuests))
        ;
    }


    @DisplayName("테이블 손님 수는 0명 미만으로 변경할 수 없다")
    @Test
    void negativeNumberOfGuests() {
        // given
        final OrderTable orderTable = orderTableSetup.setupOccupiedOrderTable();
        orderTable.setNumberOfGuests(-1);

        // expected
        final String path = getPath() + "/" + orderTable.getId().toString() + "/number-of-guests";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(orderTable))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("점유 중이 아닌 테이블의 손님 수를 변경할 수 없다")
    @Test
    void changeNumberOfGuestsOfNotOccupiedTable() {
        // given
        final OrderTable orderTable = orderTableSetup.setupNewOrderTable();
        orderTable.setNumberOfGuests(3);

        // expected
        final String path = getPath() + "/" + orderTable.getId().toString() + "/number-of-guests";

        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(writeValueAsBytes(orderTable))
                .when()
                .put(path)
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("테이블 점유를 해제한다")
    @Test
    void clearTable() {
        // TODO: 주문 인수 테스트 작성 후 테스트 재작성
//        // given
//        final OrderTable orderTable = orderTableSetup.setupOccupiedOrderTable();
//
//        // expected
//        final String path = getPath() + "/" + orderTable.getId().toString() + "/clear";
//
//        given().contentType(MediaType.APPLICATION_JSON_VALUE)
//                .body(writeValueAsBytes(orderTable))
//                .when()
//                .put(path)
//                .then()
//                .log()
//                .all()
//                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
//        ;
    }

    @DisplayName("테이블 목록을 조회한다")
    @Test
    void getAllOrderTables() {
        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.OK.value())
        ;
    }

    @Override
    protected String getPath() {
        return "/api/order-tables";
    }
}