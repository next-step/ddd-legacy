package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.exception.*;
import kitchenpos.infra.FakeKitchenridersClient;
import kitchenpos.infra.Kitchenriders;
import kitchenpos.repository.InMemoryMenuRepository;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.UUID;


@DisplayName("[주문]")
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final Kitchenriders kitchenridersClient = new FakeKitchenridersClient();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Test
    @DisplayName("주문 방식은 반드시 있어야 한다.")
    void orderTypeNotNullTest() {
        Order order = new Order();

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(OrderTypeNullException.class);
    }

    @Test
    @DisplayName("주문시 주문 메뉴는 반드시 있어야 한다.")
    void orderLineItemNotEmptyTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(OrderLineItemEmptyException.class);
    }

    @Test
    @DisplayName("조회된 메뉴의 갯수와 주문 메뉴에 포함된 메뉴의 갯수는 같아야 한다.")
    void menuSizeSameOrderLineItemSizeTest() {

        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);

        OrderLineItem notExistOrderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);

        order.setOrderLineItems(Arrays.asList(orderLineItem, notExistOrderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NotTheSameSizeException.class);
    }

    @Test
    @DisplayName("주문 방식이 `배달`, `포장` 인경우에는 주문한 메뉴의 갯수는 반드시 있어야한다.")
    void deliveryTakeoutQuantityLessThanZeroTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(-1);

        order.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(QuantityLessThenZeroException.class);
    }

    @Test
    @DisplayName("해당 메뉴가 미노출 상태라면 주문할 수 없다.")
    void menuDisplayIsFalseTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(false);
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(2);

        order.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(DisplayFalseException.class);
    }

    @Test
    @DisplayName("해당 메뉴의 가격과 주문 메뉴의 가격이 일치 하지 않으면 주문할 수 없다.")
    void menuPriceNotTheSameRequestPriceTest() {
        Order order = new Order();
        order.setType(OrderType.TAKEOUT);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(19_000L));
        orderLineItem.setQuantity(2);

        order.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(NotTheSamePriceException.class);
    }

    @Test
    @DisplayName("주문 방식이 `배달`인 경우, 배달하고자 하는 주소는 반드시 존재해야 한다. 없다면 DeliveryAddressEmptyException 발생")
    void deliveryRequiredDeliveryAddress() {
        Order orderRequest = new Order();
        orderRequest.setType(OrderType.DELIVERY);

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(DeliveryAddressEmptyException.class);
    }

    @Test
    @DisplayName("매장 식사인데, 주문 테이블 ID가 존재하지 않는 경우 OrderTableNotFoundException 발생")
    void orderTableNotFoundTest() {

        Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderTableId(UUID.randomUUID());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(OrderTableNotFoundException.class);
    }

    @Test
    @DisplayName("주문테이블이 비어있다면 IllegalStateException 발생")
    void orderTableIsEmptyTrueTest() {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setEmpty(true);
        orderTableRepository.save(orderTable);

        Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderTableId(orderTable.getId());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));
        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문을 할 수 있다")
    void orderTableIsEmptyTru1eTest() {

        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTableRepository.save(orderTable);

        Order orderRequest = new Order();
        orderRequest.setType(OrderType.EAT_IN);
        orderRequest.setOrderTableId(orderTable.getId());

        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(18_000L));
        menuRepository.save(menu);

        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setMenu(menu);
        orderLineItem.setPrice(BigDecimal.valueOf(18_000L));
        orderLineItem.setQuantity(2);

        orderRequest.setOrderLineItems(Arrays.asList(orderLineItem));

        Order actual = orderService.create(orderRequest);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> Assertions.assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> Assertions.assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN)
        );
    }

    @Test
    @DisplayName("주문 수락 시점에 주문 정보가 있어야 한다.")
    void orderAcceptExistOrderTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.DELIVERY);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(NoSuchElementException.class);

    }

    @Test
    @DisplayName("주문 수락시 OrderType 상태는 `WAITING` 이어야 한다.")
    void orderAcceptStatusTest() {
        Order order = new Order();
        order.setId(UUID.randomUUID());
//        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        AssertionsForClassTypes.assertThatThrownBy(() -> orderService.accept(order.getId()))
                .isInstanceOf(IllegalStateException.class);

    }
}