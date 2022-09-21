package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
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
import kitchenpos.fixture.MenuFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class OrderServiceTest {

    private static MenuRepository menuRepository;
    private static OrderRepository orderRepository;

    private static OrderTableRepository orderTableRepository;
    private static KitchenridersClient kitchenridersClien;

    private static OrderService orderService;

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
        Order request = createDeliveryOrder();
        request.setOrderLineItems(orderLineItems);

        assertThatIllegalArgumentException().isThrownBy(() ->
            orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문 항목에 메뉴는 등록되어 있는 메뉴여야만 한다.")
    void orderItem_has_registMenu() {
        final Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.setMenu(createMenu());
        request.setOrderLineItems(List.of(orderLineItem));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));
    }


    @ParameterizedTest
    @MethodSource("notEatInOrder")
    @DisplayName("주문 유형이 매장 식사가 아닌 경우 주문 항목의 수량은 0개 이상 이어야 한다.")
    void not_EatInOrder_Is_Quantity_Not_LessThen_Zero(Order request) {
        List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(-1));
        request.setOrderLineItems(orderLineItems);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(request));
    }

    private static Stream<Order> notEatInOrder() {
        return Stream.of(createDeliveryOrder(), createTakeOutOrder());
    }



    @Test
    @DisplayName("등록된 메뉴만 주문 등록이 가능하다.")
    void createdMenuRegisteredOrder() {
        Order request = new Order();
        request.setType(OrderType.DELIVERY);
        OrderLineItem orderLineItem = new OrderLineItem();

        request.setOrderLineItems(List.of(orderLineItem));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    private static Stream<List<OrderLineItem>> nullAndEmptyOrderLienItemList() {
        return Stream.of(null, new ArrayList<>());
    }

    private static OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        final Menu menu = menuRepository.save(createMenu());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.ONE);
        orderLineItem.setQuantity(10);
        return orderLineItem;
    }


    private static OrderLineItem createOrderLineItem(long quantity) {
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }

    private static Order createDeliveryOrder() {
        final Order order = new Order();
        order.setType(OrderType.DELIVERY);
        return order;
    }

    private static Order createTakeOutOrder() {
        final Order order = new Order();
        order.setType(OrderType.TAKEOUT);
        return order;
    }

    private static Order createEatInOrder() {
        final Order order = new Order();
        order.setType(OrderType.EAT_IN);
        return order;
    }
}
