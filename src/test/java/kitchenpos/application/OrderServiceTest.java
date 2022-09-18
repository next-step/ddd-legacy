package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.fake.FakeKitchenridersClient;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClien;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        kitchenridersClien = new FakeKitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
                kitchenridersClien);
    }

    @Test
    @DisplayName("주문 생성시 주문 타입은 필수 이다.")
    void orderTypeIsEssential() {
        Order request = new Order();
        request.setType(null);

        assertThatIllegalArgumentException().isThrownBy(() ->
            orderService.create(request)
        );
    }

    @ParameterizedTest
    @DisplayName("주문 생성시 주문 항목은 1개 이상 있어야 한다.")
    @MethodSource("nullAndEmptyOrderLienItemList")
    void orderLineIsEssential(List<OrderLineItem> orderLineItems) {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(orderLineItems);

        assertThatIllegalArgumentException().isThrownBy(() ->
            orderService.create(request)
        );
    }

    @Test
    @DisplayName("등록된 메뉴만 주문 등록이 가능하다.")
    void createdMenuRegisteredOrder() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        OrderLineItem orderLineItem = new OrderLineItem();

        request.setOrderLineItems(List.of(orderLineItem));

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

//    public static Order createEatInOrder() {
//        final Order order = new Order();
//        order.setType(OrderType.EAT_IN);
//        return order;
//    }
//
//    public static Order createEatInOrder() {
//        return new Order();
//    }
//    public static Order createEatInOrder() {
//        return new Order();
//    }




    private static Stream<List<OrderLineItem>> nullAndEmptyOrderLienItemList() {
        return Stream.of(null, new ArrayList<>());
    }
}
