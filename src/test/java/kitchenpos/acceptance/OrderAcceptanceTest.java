package kitchenpos.acceptance;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import kitchenpos.acceptance.step.MenuAcceptanceStep;
import kitchenpos.acceptance.step.MenuGroupAcceptanceStep;
import kitchenpos.acceptance.step.OrderAcceptanceStep;
import kitchenpos.acceptance.step.OrderTableAcceptanceStep;
import kitchenpos.acceptance.step.ProductAcceptanceStep;
import kitchenpos.config.AcceptanceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;

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
        final Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000));
        final Response productCreateResponse = ProductAcceptanceStep.create(product);
        final UUID productId = productCreateResponse.getBody().jsonPath().getUUID("id");
        product.setId(productId);

        // 추천메뉴 생성
        final MenuGroup menuGroup = createMenuGroup("추천메뉴");
        final Response recommendedMenuGroup = MenuGroupAcceptanceStep.create(menuGroup);
        final UUID recommendMenuGroupId = recommendedMenuGroup.getBody().jsonPath().getUUID("id");
        menuGroup.setId(recommendMenuGroupId);

        // 메뉴 생성
        final Menu menu = createMenu(menuGroup, "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(product, 1)));
        final Response friedChickenMenuResponse = MenuAcceptanceStep.create(menu);
        final UUID friedChickenMenuId = friedChickenMenuResponse.getBody().jsonPath().getUUID("id");
        menu.setId(friedChickenMenuId);

        // 주문테이블 생성
        final OrderTable orderTable = createOrderTable("1번테이블");
        final Response orderTableResponse = OrderTableAcceptanceStep.create(orderTable);
        final UUID oneTableId = orderTableResponse.getBody().jsonPath().getUUID("id");
        orderTable.setId(oneTableId);

        // 주문 테이블 착석
        OrderTableAcceptanceStep.sit(oneTableId);

        // 주문 테이블 손님 수 변경
        orderTable.setNumberOfGuests(4);
        OrderTableAcceptanceStep.changeNumberOfGuests(oneTableId, orderTable);

        // 주문 테이블 조회
        final Response orderTables = OrderTableAcceptanceStep.findAll();

        assertThat(orderTables.getBody().jsonPath().getList("occupied")).contains(true);
        assertThat(orderTables.getBody().jsonPath().getList("numberOfGuests")).contains(4);

        // 먹고가기 주문 생성
        final OrderLineItem orderLineItem = createOrderLineItem(friedChickenMenuId, BigDecimal.valueOf(16000), 1);
        final Order order = createOrder(orderTable, List.of(orderLineItem), OrderType.EAT_IN, null, null);
        final Response orderResponse = OrderAcceptanceStep.create(order);
        final UUID orderId = orderResponse.getBody().jsonPath().getUUID("id");
        order.setId(orderId);

        // 주문 조회
        Response ordersResponse = OrderAcceptanceStep.findAll();
        List<OrderStatus> orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.WAITING);

        // 주문 수락 변경
        OrderAcceptanceStep.accept(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.ACCEPTED);

        // 주문 serve 변경
        OrderAcceptanceStep.serve(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.SERVED);

        // 주문 완료
        OrderAcceptanceStep.complete(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.COMPLETED);

    }

    @DisplayName("포장하기 유형의 시나리오")
    @Test
    void takeOutOrderTest() {
        // 상품 생성
        final Response productCreateResponse = ProductAcceptanceStep.create(createProductWithId("후라이드 치킨", BigDecimal.valueOf(16000)));
        final UUID productId = productCreateResponse.getBody().jsonPath().getUUID("id");

        // 추천메뉴 생성
        final Response 추천메뉴 = MenuGroupAcceptanceStep.create(createMenuGroup(null, "추천메뉴"));
        final UUID recommendMenuGroupId = UUID.fromString(추천메뉴.getBody().jsonPath().getString("id"));
        final MenuGroup menuGroup = createMenuGroup(recommendMenuGroupId);
        // 메뉴 생성
        final Response friedChickenMenu = MenuAcceptanceStep.create(createMenu(menuGroup, "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(productId, 1))));
        final UUID friedChickenMenuId = friedChickenMenu.getBody().jsonPath().getUUID("id");
        // 주문테이블 생성
        final Response oneTable = OrderTableAcceptanceStep.create(createOrderTable("1번테이블"));
        final UUID oneTableId = oneTable.getBody().jsonPath().getUUID("id");
        // 먹고가기 주문 생성
        final OrderLineItem orderLineItem = createOrderLineItem(friedChickenMenuId, BigDecimal.valueOf(16000), 1);
        final Response order = OrderAcceptanceStep.create(createOrder(createOrderTable(oneTableId), List.of(orderLineItem), OrderType.TAKEOUT, null, null));
        final UUID orderId = order.getBody().jsonPath().getUUID("id");

        // 주문 조회
        Response ordersResponse = OrderAcceptanceStep.findAll();
        List<OrderStatus> orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.WAITING);

        // 주문 수락 변경
        OrderAcceptanceStep.accept(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.ACCEPTED);

        // 주문 serve 변경
        OrderAcceptanceStep.serve(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.SERVED);

        // 주문 완료
        OrderAcceptanceStep.complete(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.COMPLETED);
    }

    @DisplayName("배달하기 유형의 시나리오")
    @Test
    void deliveryOrderTest() {
        // 상품 생성
        final Product product = createProduct("후라이드 치킨", BigDecimal.valueOf(16000));
        final Response productCreateResponse = ProductAcceptanceStep.create(createProduct("후라이드 치킨", BigDecimal.valueOf(16000)));
        final UUID productId = productCreateResponse.getBody().jsonPath().getUUID("id");
        product.setId(productId);

        // 추천메뉴 생성
        final MenuGroup menuGroup = createMenuGroup("추천메뉴");
        final Response recommendedMenuGroupResponse = MenuGroupAcceptanceStep.create(menuGroup);
        final UUID recommendMenuGroupId = recommendedMenuGroupResponse.getBody().jsonPath().getUUID("id");
        menuGroup.setId(recommendMenuGroupId);

        // 메뉴 생성
        final Menu friedChickenMenu = createMenu(menuGroup, "후라이드 치킨", BigDecimal.valueOf(16000), true, List.of(createMenuProduct(productId, 1)));
        final Response friedChickenMenuResponse = MenuAcceptanceStep.create(friedChickenMenu);
        final UUID friedChickenMenuId = friedChickenMenuResponse.getBody().jsonPath().getUUID("id");
        friedChickenMenu.setPrice(BigDecimal.valueOf(16000));

        // 주문테이블 생성
        final OrderTable orderTable = createOrderTable("1번테이블");
        final Response oneTableResponse = OrderTableAcceptanceStep.create(createOrderTable("1번테이블"));
        final UUID oneTableId = oneTableResponse.getBody().jsonPath().getUUID("id");
        orderTable.setId(oneTableId);

        // 먹고가기 주문 생성
        final OrderLineItem orderLineItem = createOrderLineItem(friedChickenMenuId, BigDecimal.valueOf(16000), 1);
        final Order order = createOrder(createOrderTable(oneTableId), List.of(orderLineItem), OrderType.DELIVERY,
                null, "서울시 강남구 역삼동");
        final Response orderResponse = OrderAcceptanceStep.create(order);
        final UUID orderId = orderResponse.getBody().jsonPath().getUUID("id");
        order.setId(orderId);

        // 주문 조회
        Response ordersResponse = OrderAcceptanceStep.findAll();
        List<OrderStatus> orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.WAITING);

        // 주문 수락 변경
        OrderAcceptanceStep.accept(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.ACCEPTED);

        // 주문 serve 변경
        OrderAcceptanceStep.serve(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.SERVED);

        // 주문 배달 시작
        OrderAcceptanceStep.startDelivery(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.DELIVERING);

        // 주문 배달 종료
        OrderAcceptanceStep.completeDelivery(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.DELIVERED);

        // 주문 완료
        OrderAcceptanceStep.complete(orderId);

        // 주문 조회
        ordersResponse = OrderAcceptanceStep.findAll();
        orderStatuses = ordersResponse.getBody()
                .jsonPath()
                .getList("status", String.class)
                .stream()
                .map(OrderStatus::valueOf)
                .toList();

        assertThat(ordersResponse.getBody().jsonPath().getList("id", UUID.class)).contains(orderId);
        assertThat(orderStatuses).contains(OrderStatus.COMPLETED);
    }
}
