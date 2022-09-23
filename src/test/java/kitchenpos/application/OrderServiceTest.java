package kitchenpos.application;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
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
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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
    @DisplayName("주문 항목에 메뉴는 등록되어 있어야한다.")
    void orderItem_has_registMenu() {
        final Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.setMenuId(null);

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


    @Test
    @DisplayName("등록된 메뉴만 주문 등록이 가능하다.")
    void createdMenuRegisteredOrder() {
        Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = new OrderLineItem();

        request.setOrderLineItems(List.of(orderLineItem));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @Test
    @DisplayName("주문항목의 메뉴가 숨겨져 있으면 주문 등록을 할수가 없다")
    void orderItem_has_a_no_displayed_menu() {
        Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.getMenu().setDisplayed(false);
        request.setOrderLineItems(Collections.singletonList(orderLineItem));

        assertThatIllegalStateException().isThrownBy(() ->
                orderService.create(request)
        );
    }


    @ParameterizedTest
    @DisplayName("주문항목의 메뉴의 가격은 주문항목의 가격과 같아야 등록이 가능하다.")
    @ValueSource(ints = {-1, 2})
    void orderItemPrice_IsEqual_MenuPrice(int price) {
        Order request = createDeliveryOrder();
        OrderLineItem orderLineItem = createOrderLineItem();
        orderLineItem.getMenu().setPrice(BigDecimal.valueOf(1));
        orderLineItem.setPrice(BigDecimal.valueOf(price));
        request.setOrderLineItems(Collections.singletonList(orderLineItem));

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }


    @DisplayName("주문 유형이 배달주문일떄는 배달주소가 필수여야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void deliveryOrder_is_deliveryAddress_is_essential(String deliveryAddress) {
        Order request = createDeliveryOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        request.setDeliveryAddress(deliveryAddress);

        assertThatIllegalArgumentException().isThrownBy(() ->
                orderService.create(request)
        );
    }

    @DisplayName("등록할때 주문 유형이 매장식사인경우 주문 테이블이 반드시 필요하다.")
    @Test
    void eat_in_order_has_orderTable_essential() {
        Order request = createEatInOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));

        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문유형이 매장식사인 경우 사용중인 오더 테이블이 아닌경우 등록이 불가능하다.")
    @Test
    void eat_in_order_has_orderTable_is_no_occupied() {
        Order request = createEatInOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        OrderTable orderTable = createOrderTable();
        orderTable.setOccupied(false);
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());

        assertThatIllegalStateException().isThrownBy(
                () -> orderService.create(request)
        );
    }

    @Test
    @DisplayName("주문을 등록 한다.")
    void create() {
        Order request = createEatInOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        OrderTable orderTable = createOrderTable();
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());

        Order createOrder = orderService.create(request);

        assertAll(
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getOrderTable().getId()).isEqualTo(orderTable.getId()),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }


    private static Stream<List<OrderLineItem>> nullAndEmptyOrderLienItemList() {
        return Stream.of(null, new ArrayList<>());
    }

    private static OrderTable createOrderTable() {
        OrderTable request = new OrderTable();
        request.setOccupied(true);
        return orderTableRepository.save(request);
    }

    private static OrderLineItem createOrderLineItem() {
        OrderLineItem orderLineItem = new OrderLineItem();
        Menu menu = menuRepository.save(createMenu());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.ONE);
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setPrice(BigDecimal.ONE);
        orderLineItem.setQuantity(10);
        return orderLineItem;
    }

    private static Stream<Order> notEatInOrder() {
        return Stream.of(createDeliveryOrder(), createTakeOutOrder());
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
