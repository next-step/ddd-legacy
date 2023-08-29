package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.testHelper.SpringBootTestHelper;
import kitchenpos.testHelper.fixture.OrderFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

public class OrderServiceTest extends SpringBootTestHelper {

    @Autowired
    OrderService orderService;
    @Autowired
    MenuService menuService;
    @SpyBean
    OrderRepository orderRepository;
    @Autowired
    OrderTableService orderTableService;
    List<Menu> menus;
    OrderTable orderTable;

    @BeforeEach
    public void init() {
        super.init();
        super.initProduct();
        super.initMenuGroup();
        super.initOrderTable();
        super.initMenu();

        this.menus = super.getMenus();
        this.orderTable = super.getOrderTable();
    }

    @DisplayName("배달주문을 등록할수 있다. 주문이 되면 ID가 설정되고, '대기중' 상태가 된다")
    @Test
    void test1() {
        //given
        Order createRequest = OrderFixture.createDeliveryOrderRequestBuilder()
            .deliveryAddress("배달 주소")
            .menu(menus.get(0), 1L, menus.get(0).getPrice())
            .type(OrderType.DELIVERY)
            .build();

        //when
        Order result = orderService.create(createRequest);

        //then
        assertAll(
            () -> assertThat(result.getId()).isNotNull(),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @DisplayName("매장주문을 등록할수 있다")
    @ParameterizedTest
    @ValueSource(longs = {-1, 0, 1, 2, 3, 4, 5})
    void test2(long quantity) {
        //given
        Order createRequest = OrderFixture.createEatInOrderRequestBuilder()
            .menu(menus.get(0), quantity, menus.get(0).getPrice())
            .orderTable(orderTable.getId())
            .type(OrderType.EAT_IN)
            .build();

        //when
        Order result = orderService.create(createRequest);

        //then
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("포장주문을 등록할수 있다")
    @Test
    void test3() {
        //given
        Order createRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .menu(menus.get(0), 1L, menus.get(0).getPrice())
            .type(OrderType.TAKEOUT)
            .build();

        //when
        Order result = orderService.create(createRequest);

        //then
        assertThat(result.getId()).isNotNull();
    }

    @DisplayName("주문을 등록할때에는 반드시 주문 유형이 있어야 한다")
    @Test
    void test4() {
        //given
        Order createRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .menu(menus.get(0), 1L, menus.get(0).getPrice())
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("주문을 등록할때에는 최소 하나 이상의 메뉴를 등록해야 한다")
    @Test
    void test5() {
        //given
        Order createRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .type(OrderType.TAKEOUT)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("노출되지 않은 메뉴는 주문할수 없다")
    @Test
    void test6() {
        //given
        Menu orderMenu = menus.get(0);
        menuService.hide(orderMenu.getId());
        Order createRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .menu(orderMenu, 1L, orderMenu.getPrice())
            .type(OrderType.TAKEOUT)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("저장된 메뉴의 가격과 주문한 메뉴의 가격이 다를수 없다")
    @Test
    void test7() {
        //given
        Menu orderMenu = menus.get(0);
        Order createRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .menu(orderMenu, 1L, orderMenu.getPrice().add(BigDecimal.ONE))
            .type(OrderType.TAKEOUT)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("'배달주문'과 '포장주문'이라면 메뉴 별로 0개 이상 주문해야 한다")
    @Test
    void test9() {
        //given
        Menu orderMenu = menus.get(0);
        long quantity = -1L;
        Order takeOutOrderRequest = OrderFixture.createTakeOutOrderCreateRequestBuilder()
            .menu(orderMenu, quantity, orderMenu.getPrice())
            .type(OrderType.TAKEOUT)
            .build();
        Order deliveryOrderRequest = OrderFixture.createDeliveryOrderRequestBuilder()
            .menu(orderMenu, quantity, orderMenu.getPrice())
            .type(OrderType.DELIVERY)
            .deliveryAddress("address")
            .build();

        //when && then
        assertAll(
            () -> assertThatThrownBy(
                () -> orderService.create(takeOutOrderRequest)
            ).isInstanceOf(IllegalArgumentException.class),
            () -> assertThatThrownBy(
                () -> orderService.create(deliveryOrderRequest)
            ).isInstanceOf(IllegalArgumentException.class)
        );

    }

    @DisplayName("배달주문은 배달주소가 반드시 있어야 한다")
    @Test
    void test10() {
        //given
        Menu orderMenu = menus.get(0);
        Order createRequest = OrderFixture.createDeliveryOrderRequestBuilder()
            .menu(orderMenu, 1L, orderMenu.getPrice())
            .type(OrderType.DELIVERY)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장식사 주문은 사용중인 테이블에서만 주문할수 있다.")
    @Test
    void test11() {
        //given
        Menu orderMenu = menus.get(0);
        orderTableService.clear(orderTable.getId());
        Order createRequest = OrderFixture.createEatInOrderRequestBuilder()
            .menu(orderMenu, 1L, orderMenu.getPrice())
            .orderTable(orderTable.getId())
            .type(OrderType.EAT_IN)
            .build();

        //when && then
        assertThatThrownBy(
            () -> orderService.create(createRequest)
        ).isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("매장 식사 주문 접수시 '접수 완료'상태로 변경되어야 한다")
    @Test
    void test12() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createEatInOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .orderTable(orderTable.getId())
                .type(OrderType.EAT_IN)
                .build()
        );
        //when
        Order result = orderService.accept(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("포장 주문 접수시 '접수 완료'상태로 변경되어야 한다")
    @Test
    void test13() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createTakeOutOrderCreateRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .type(OrderType.TAKEOUT)
                .build()
        );
        //when
        Order result = orderService.accept(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("배달 주문 접수시 '접수 완료'상태로 변경되어야 한다")
    @Test
    void test14() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createDeliveryOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .deliveryAddress("address")
                .type(OrderType.DELIVERY)
                .build()
        );
        //when
        Order result = orderService.accept(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("매장 식사 주문 서빙시 '서빙중' 상태로 변경되어야 한다")
    @Test
    void test15() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createEatInOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .orderTable(orderTable.getId())
                .type(OrderType.EAT_IN)
                .build()
        );
        orderService.accept(order.getId());

        //when
        Order result = orderService.serve(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("포장 주문 서빙시 '서빙중' 상태로 변경되어야 한다")
    @Test
    void test16() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createTakeOutOrderCreateRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .type(OrderType.TAKEOUT)
                .build()
        );
        orderService.accept(order.getId());

        //when
        Order result = orderService.serve(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달 주문 서빙시 '서빙중' 상태로 변경되어야 한다")
    @Test
    void test17() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createDeliveryOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .deliveryAddress("address")
                .type(OrderType.DELIVERY)
                .build()
        );
        orderService.accept(order.getId());

        //when
        Order result = orderService.serve(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배달 주문 시작시 '배달 중' 상태로 변경되어야 한다")
    @Test
    void test18() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createDeliveryOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .deliveryAddress("address")
                .type(OrderType.DELIVERY)
                .build()
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        //when
        Order result = orderService.startDelivery(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("배달 주문 완료시 '배달 완료' 상태로 변경되어야 한다")
    @Test
    void test19() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createDeliveryOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .deliveryAddress("address")
                .type(OrderType.DELIVERY)
                .build()
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.startDelivery(order.getId());

        //when
        Order result = orderService.completeDelivery(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("매장 식사 주문을 완료처리 할수 있다. 처리 후 테이블 내 오더가 모두 완료시 테이블이 초기화되어야 한다")
    @Test
    void test20() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createEatInOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .orderTable(orderTable.getId())
                .type(OrderType.EAT_IN)
                .build()
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        //when
        Order result = orderService.complete(order.getId());
        orderTable = orderTableService.findAll().get(0);

        //then
        assertAll(
            () -> assertThat(orderTable.isOccupied()).isFalse(),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );

    }

    @DisplayName("매장 식사 주문을 완료처리 할수 있다. 처리 후 테이블 내 완료되지 않은 오더가 존재시 테이블은 초기화되면 안된다")
    @Test
    void test21() {
        //given
        Menu orderMenu = menus.get(0);
        Order order1 = orderService.create(
            OrderFixture.createEatInOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .orderTable(orderTable.getId())
                .type(OrderType.EAT_IN)
                .build()
        );
        Order order2 = orderService.create(
            OrderFixture.createEatInOrderRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .orderTable(orderTable.getId())
                .type(OrderType.EAT_IN)
                .build()
        );
        orderService.accept(order1.getId());
        orderService.serve(order1.getId());

        //when
        Order result = orderService.complete(order1.getId());
        orderTable = orderTableService.findAll().get(0);

        //then
        assertAll(
            () -> assertThat(orderTable.isOccupied()).isTrue(),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED)
        );

    }

    @DisplayName("포장 주문을 완료처리 할수 있다")
    @Test
    void test22() {
        //given
        Menu orderMenu = menus.get(0);
        Order order = orderService.create(
            OrderFixture.createTakeOutOrderCreateRequestBuilder()
                .menu(orderMenu, 1L, orderMenu.getPrice())
                .type(OrderType.TAKEOUT)
                .build()
        );
        orderService.accept(order.getId());
        orderService.serve(order.getId());

        //when
        Order result = orderService.complete(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("'접수 완료'된 주문만 서빙할수 있다")
    @ParameterizedTest
    @MethodSource("test23MethodSource")
    void test23(String orderStatus) {
        //given
        Order order = new Order();
        order.setStatus(OrderStatus.valueOf(orderStatus));
        order.setId(UUID.randomUUID());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        //when && then
        assertThatThrownBy(
            () -> orderService.serve(order.getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    static Stream<String> test23MethodSource() {
        return Arrays.stream(OrderStatus.values())
            .filter(orderStatus -> orderStatus != OrderStatus.ACCEPTED)
            .map(OrderStatus::name);
    }

    @DisplayName("'배달 주문'만 배달할수 있다")
    @ParameterizedTest
    @MethodSource("test24MethodSource")
    void test24(String orderType) {
        //given
        Order order = new Order();
        order.setType(OrderType.valueOf(orderType));
        order.setId(UUID.randomUUID());
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        //when && then
        assertThatThrownBy(
            () -> orderService.startDelivery(order.getId())
        ).isInstanceOf(IllegalStateException.class);

    }

    static Stream<String> test24MethodSource() {
        return Arrays.stream(OrderType.values())
            .filter(orderType -> orderType != OrderType.DELIVERY)
            .map(OrderType::name);
    }

    @DisplayName("'서빙 중'인 배달주문만 배달할수 있다")
    @ParameterizedTest
    @MethodSource("test25MethodSource")
    void test25(String orderStatus) {
        //given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.valueOf(orderStatus));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        //when && then
        assertThatThrownBy(
            () -> orderService.startDelivery(order.getId())
        ).isInstanceOf(IllegalStateException.class);

    }

    static Stream<String> test25MethodSource() {
        return Arrays.stream(OrderStatus.values())
            .filter(orderStatus -> orderStatus != OrderStatus.SERVED)
            .map(OrderStatus::name);
    }

    @DisplayName("'배달 중'인 배달주문만 배달완료 처리 할수 있다")
    @ParameterizedTest
    @MethodSource("test26MethodSource")
    void test26(String orderStatus) {
        //given
        Order order = new Order();
        order.setType(OrderType.DELIVERY);
        order.setId(UUID.randomUUID());
        order.setStatus(OrderStatus.valueOf(orderStatus));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        //when && then
        assertThatThrownBy(
            () -> orderService.completeDelivery(order.getId())
        ).isInstanceOf(IllegalStateException.class);

    }

    static Stream<String> test26MethodSource() {
        return Arrays.stream(OrderStatus.values())
            .filter(orderStatus -> orderStatus != OrderStatus.DELIVERING)
            .map(OrderStatus::name);
    }
}
