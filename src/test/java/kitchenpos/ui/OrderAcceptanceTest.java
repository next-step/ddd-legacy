package kitchenpos.ui;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.setup.MenuGroupSetup;
import kitchenpos.setup.MenuSetup;
import kitchenpos.setup.OrderTableSetup;
import kitchenpos.setup.ProductSetup;
import kitchenpos.util.AcceptanceTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static io.restassured.RestAssured.given;
import static kitchenpos.fixture.MenuGroupFixture.generateMenuGroup;
import static kitchenpos.fixture.MenuGroupFixture.generateMenuGroupWithName;
import static kitchenpos.fixture.OrderFixture.initializeDeliveryOrder;
import static kitchenpos.fixture.OrderFixture.initializeEatInOrder;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.equalTo;


class OrderAcceptanceTest extends AcceptanceTest {

    @Autowired
    private OrderTableSetup orderTableSetup;

    @Autowired
    private MenuSetup menuSetup;

    @DisplayName("주문을 생성한다")
    @Test
    void createOrder() throws Exception {
        // given
        final Menu menu = menuSetup.setupMenu();
        final long quantity = 1;
        final Order order = initializeDeliveryOrder(menu, quantity);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(order))
                .when()
                .post(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.CREATED.value())
                .assertThat()
                .body("type", equalTo(order.getType().name()))
                .body("status", equalTo(order.getStatus().name()))
                .body("orderDateTime", Matchers.notNullValue())
                .body("orderLineItems[0].menu.id", equalTo(menu.getId().toString()))
                .body("orderLineItems[0].quantity", equalTo(Long.valueOf(quantity).intValue()))
                .body("deliveryAddress", equalTo(order.getDeliveryAddress()))
                .body("orderTable", nullValue())
        ;
    }

    @DisplayName("배달 주문은 주소가 있어야 한다")
    @Test
    void addressMustExist() throws Exception {
        // given
        final Menu menu = menuSetup.setupMenu();
        final long quantity = 1;
        final Order order = initializeDeliveryOrder(menu, quantity);
        order.setDeliveryAddress(null);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(order))
                .when()
                .post(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("매장 식사 주문은 테이블이 포함되어야 한다")
    @Test
    void orderTableMustBeExist() throws Exception {
        // given
        final Menu menu = menuSetup.setupMenu();
        final long quantity = 1;
        final Order order = initializeEatInOrder(menu, quantity, null);

        // expected
        given().contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(objectMapper.writeValueAsBytes(order))
                .when()
                .post(getPath())
                .then()
                .log()
                .all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        ;
    }

    @DisplayName("주문 목록을 조회한다")
    @Test
    void getAllOrders() {
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
        return "/api/orders";
    }
}