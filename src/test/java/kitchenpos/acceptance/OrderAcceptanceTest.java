package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.MenuAcceptanceStep;
import kitchenpos.acceptance.step.MenuGroupAcceptanceStep;
import kitchenpos.acceptance.step.OrderAcceptanceStep;
import kitchenpos.acceptance.step.OrderTableAcceptanceStep;
import kitchenpos.acceptance.step.ProductAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static kitchenpos.fixture.ProductFixture.createProductWithId;

@AcceptanceTest
class OrderAcceptanceTest {
    @MockBean
    PurgomalumClient purgomalumClient;

    @LocalServerPort
    int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("먹고가기 유형의 시나리오")
    @Test
    void orderWhenEatInTest() {
        // 상품 생성
        ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));
        // 추천메뉴 생성
        Response 추천메뉴 = MenuGroupAcceptanceStep.create(createMenuGroup(null, "추천메뉴"));
        UUID recommendMenuGroupId = UUID.fromString(추천메뉴.getBody().jsonPath().getString("id"));
        MenuGroup menuGroup = createMenuGroup(recommendMenuGroupId);
        // 메뉴 생성
        Response friedChickenMenu = MenuAcceptanceStep.create(MenuFixture.createMenu(menuGroup, "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(UUID.fromString(ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000))).getBody().jsonPath().getString("id")), 1))));
        UUID friedChickenMenuId = friedChickenMenu.getBody().jsonPath().getUUID("id");
        // 주문테이블 생성
        Response oneTable = OrderTableAcceptanceStep.create(createOrderTable("1번테이블"));
        UUID oneTableId = oneTable.getBody().jsonPath().getUUID("id");

        // 주문 테이블 착석
        OrderTableAcceptanceStep.sit(oneTableId);

        // 주문 테이블 손님 수 변경
        OrderTableAcceptanceStep.changeNumberOfGuests(oneTableId, createOrderTable(oneTableId, "1번테이블", 4));

        // 먹고가기 주문 생성
        OrderLineItem orderLineItem = createOrderLineItem(friedChickenMenuId, BigDecimal.valueOf(16000), 1);
        Response order = OrderAcceptanceStep.create(createOrder(oneTableId, List.of(orderLineItem), OrderType.EAT_IN, null, null));
        UUID orderId = order.getBody().jsonPath().getUUID("id");

        // 주문 조회
        Response orders = OrderAcceptanceStep.findAll();

        // 주문 수락 변경
        OrderAcceptanceStep.accept(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();
        // 주문 serve 변경
        OrderAcceptanceStep.serve(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();

        // 주문 완료
        OrderAcceptanceStep.complete(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();
    }

    @DisplayName("포장하기 유형의 시나리오")
    @Test
    void takeOutOrderTest() {
        // 상품 생성
        ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));
        // 추천메뉴 생성
        Response 추천메뉴 = MenuGroupAcceptanceStep.create(createMenuGroup(null, "추천메뉴"));
        UUID recommendMenuGroupId = UUID.fromString(추천메뉴.getBody().jsonPath().getString("id"));
        MenuGroup menuGroup = createMenuGroup(recommendMenuGroupId);
        // 메뉴 생성
        Response friedChickenMenu = MenuAcceptanceStep.create(MenuFixture.createMenu(menuGroup, "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(UUID.fromString(ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000))).getBody().jsonPath().getString("id")), 1))));
        UUID friedChickenMenuId = friedChickenMenu.getBody().jsonPath().getUUID("id");
        // 주문테이블 생성
        Response oneTable = OrderTableAcceptanceStep.create(createOrderTable("1번테이블"));
        UUID oneTableId = oneTable.getBody().jsonPath().getUUID("id");
        // 먹고가기 주문 생성
        OrderLineItem orderLineItem = createOrderLineItem(friedChickenMenuId, BigDecimal.valueOf(16000), 1);
        Response order = OrderAcceptanceStep.create(createOrder(oneTableId, List.of(orderLineItem), OrderType.TAKEOUT, null, null));
        UUID orderId = order.getBody().jsonPath().getUUID("id");

        // 주문 조회
        Response orders = OrderAcceptanceStep.findAll();

        // 주문 수락 변경
        OrderAcceptanceStep.accept(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();
        // 주문 serve 변경
        OrderAcceptanceStep.serve(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();

        // 주문 완료
        OrderAcceptanceStep.complete(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();
    }

    @DisplayName("배달하기 유형의 시나리오")
    @Test
    void deliveryOrderTest() {
        // 상품 생성
        ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));
        // 추천메뉴 생성
        Response 추천메뉴 = MenuGroupAcceptanceStep.create(createMenuGroup(null, "추천메뉴"));
        UUID recommendMenuGroupId = UUID.fromString(추천메뉴.getBody().jsonPath().getString("id"));
        MenuGroup menuGroup = createMenuGroup(recommendMenuGroupId);
        // 메뉴 생성
        Response friedChickenMenu = MenuAcceptanceStep.create(MenuFixture.createMenu(menuGroup, "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(UUID.fromString(ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000))).getBody().jsonPath().getString("id")), 1))));
        UUID friedChickenMenuId = friedChickenMenu.getBody().jsonPath().getUUID("id");
        // 주문테이블 생성
        Response oneTable = OrderTableAcceptanceStep.create(createOrderTable("1번테이블"));
        UUID oneTableId = oneTable.getBody().jsonPath().getUUID("id");
        // 먹고가기 주문 생성
        OrderLineItem orderLineItem = createOrderLineItem(friedChickenMenuId, BigDecimal.valueOf(16000), 1);
        Response order = OrderAcceptanceStep.create(createOrder(oneTableId, List.of(orderLineItem), OrderType.DELIVERY, null, "서울시 송파구 위례성대로 2"));
        UUID orderId = order.getBody().jsonPath().getUUID("id");

        // 주문 조회
        Response orders = OrderAcceptanceStep.findAll();

        // 주문 수락 변경
        OrderAcceptanceStep.accept(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();
        // 주문 serve 변경
        OrderAcceptanceStep.serve(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();

        // 주문 배달 시작
        OrderAcceptanceStep.startDelivery(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();

        // 주문 배달 종료
        OrderAcceptanceStep.completeDelivery(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();

        // 주문 완료
        OrderAcceptanceStep.complete(orderId);

        // 주문 조회
        orders = OrderAcceptanceStep.findAll();
    }
}
