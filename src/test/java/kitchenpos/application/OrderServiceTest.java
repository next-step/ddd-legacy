package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.exception.DeliveryAddressNotFoundException;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.inMemory.InMemoryMenuRepository;
import kitchenpos.inMemory.InMemoryOrderRepository;
import kitchenpos.inMemory.InMemoryOrderTableRepository;
import kitchenpos.infra.FakeKitchenridersClient;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @DisplayName("주문은 고유 ID, 상태, 종류, 주문 목록, 주문 시간을 갖는다.")
    @Test
    void create() {
        // given
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        final Order order = OrderFixture.create(OrderType.TAKEOUT, 족보_세트, 1);

        // when
        final Order actual = orderService.create(order);

        // then
        Assertions.assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getType()).isNotNull(),
                () -> assertThat(actual.getStatus()).isNotNull(),
                () -> assertThat(actual.getOrderDateTime()).isNotNull(),
                () -> assertThat(actual.getOrderLineItems()).isNotEmpty()
        );

    }

    @DisplayName("주문은 반드시 하나의 주문 상태를 가지고 있다.")
    @Test
    void checkOrderType() {
        // given
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        final Order order = OrderFixture.create(OrderType.TAKEOUT, 족보_세트, 1);

        // when
        final Order actual = orderService.create(order);

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("주문은 반드시 하나의 주문 종류를 가지고 있다.")
    @Test
    void checkOrderStatus() {
        // given
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        final Order order = OrderFixture.create(null, 족보_세트, 1);

        // when, then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 상품을 받고 간다.")
    @Test
    void completeTakeOutOrder() {
        // given
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        final Order order = OrderFixture.create(OrderType.TAKEOUT, 족보_세트, 1);

        // when
        Order actual = orderService.create(order);
        actual.setStatus(OrderStatus.SERVED);
        actual = orderService.complete(actual.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("빈 테이블에서 매장식사를 할 수 있다.")
    @Test
    void checkCanUsingTable() {
        // given
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        final Order order = OrderFixture.create(OrderType.EAT_IN, 족보_세트, 1);

        OrderTable simpleOrderTable = OrderTableFixture.createSimpleOrderTable("4인석-1");
        final OrderTable actualOrderTable = orderTableRepository.save(simpleOrderTable);
        order.setOrderTableId(simpleOrderTable.getId());

        // when, then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문한 음식이 나오면 라이더는 주문 금액, 배달주소를 확인하여 배달한다.")
    @Test
    void startDelivery() {
        // given
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        final Order order = OrderFixture.create(OrderType.DELIVERY, 족보_세트, 1);

        // when, then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(order))
                .isInstanceOf(DeliveryAddressNotFoundException.class);

    }

    @DisplayName("손님이 상품을 수령하면 배달 완료한다.")
    @Test
    void completeDelivery() {
        // given
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        final Order order = OrderFixture.create(OrderType.TAKEOUT, 족보_세트, 1);

        // when
        Order actual = orderService.create(order);
        actual.setStatus(OrderStatus.DELIVERING);
        actual = orderService.completeDelivery(actual.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("등록한 모든 주문 목록을 볼 수 있다.")
    @Test
    void findAll() {
        // TODO 주문 종류별로 픽스쳐 만들기
        final Menu 족보_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(43_000));
        orderService.create(OrderFixture.create(OrderType.TAKEOUT, 족보_세트, 1));
        final Menu 족보막_세트 = menuRepository.save(MenuServiceTest.getSimpleMenu(57_000));
        orderService.create(OrderFixture.create(OrderType.TAKEOUT, 족보막_세트, 1));

        final List<Order> actual = orderService.findAll();
        assertThat(actual).hasSize(2);
    }

}
