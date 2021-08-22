package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.emptyList;
import static kitchenpos.application.MenuServiceTest.메뉴만들기;
import static kitchenpos.application.OrderTableServiceTest.주문테이블만들기;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderServiceTest {
    private OrderService orderService;
    private OrderRepository orderRepository = new InMemoryOrderRepository();
    private MenuRepository menuRepository = new InMemoryMenuRepository();
    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();
    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();
    private ProductRepository productRepository = new InMemoryProductRepository();
    private Order deliveryOrder;
    private Order takeOutOrder;
    private Order eatInOrder;
    private List<OrderLineItem> orderLineItems;
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
        orderLineItems = new ArrayList<>(Arrays.asList(주문항목만들기(menuRepository, menuGroupRepository, productRepository), 주문항목만들기(menuRepository, menuGroupRepository, productRepository)));
        orderTable = 주문테이블만들기(orderTableRepository);
        deliveryOrder = new Order();
        deliveryOrder.setType(OrderType.DELIVERY);
        deliveryOrder.setOrderLineItems(orderLineItems);
        deliveryOrder.setDeliveryAddress("배달주소");
        takeOutOrder = new Order();
        takeOutOrder.setType(OrderType.TAKEOUT);
        takeOutOrder.setOrderLineItems(orderLineItems);
        eatInOrder = new Order();
        eatInOrder.setType(OrderType.EAT_IN);
        eatInOrder.setOrderLineItems(orderLineItems);
        eatInOrder.setOrderTable(orderTable);
        eatInOrder.setOrderTableId(orderTable.getId());
    }

    @DisplayName("배달주문을 생성할 수 있다.")
    @Test
    void delivery_create() {
        final Order saved = 주문등록(deliveryOrder);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.DELIVERY),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("배달주문은 배달주소를 포함해야한다.")
    @Test
    void delivery_create_address() {
        deliveryOrder.setDeliveryAddress(null);

        assertThatThrownBy(() -> 주문등록(deliveryOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("포장주문을 생성할 수 있다.")
    @Test
    void takeOut_create() {
        final Order saved = 주문등록(takeOutOrder);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.TAKEOUT),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("매장 식사주문을 생성할 수 있다.")
    @Test
    void eatIn_create() {
        final Order saved = 주문등록(eatInOrder);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull()
        );
    }

    @DisplayName("매장 식사시엔 비어있지 않은 테이블 정보를 지녀야한다.")
    @Test
    void eatIn_create_table() {
        OrderTable newTable = new OrderTable();
        newTable.setId(UUID.randomUUID());
        newTable.setEmpty(true);
        orderTableRepository.save(newTable);
        eatInOrder.setOrderTableId(newTable.getId());

        assertThatThrownBy(() -> 주문등록(eatInOrder))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 방법을 선택하지 않으면 주문할 수 없다.")
    @Test
    void create_type(){
        deliveryOrder.setType(null);
        takeOutOrder.setType(null);
        eatInOrder.setType(null);

        assertAll(
                () -> assertThatThrownBy(() -> 주문등록(deliveryOrder))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> 주문등록(takeOutOrder))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> 주문등록(eatInOrder))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("주문시 한 가지 이상의 비어있지 않은 주문항목이 필요하다.")
    @Test
    void create_orderLineItem() {
        deliveryOrder.setOrderLineItems(emptyList());

        assertThatThrownBy(() -> 주문등록(deliveryOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 식사주문은 수량이 0보다 적을 수 있다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void eatIn_create(int quantity) {
        orderLineItems.get(0).setQuantity(quantity);

        assertThat(주문등록(eatInOrder).getId()).isNotNull();
    }

    @DisplayName("포장주문과 배달주문은 수량이 0보다 작을 수 없다.")
    @ValueSource(strings = "-1")
    @ParameterizedTest
    void deliveryAndTakeOut_create(int quantity) {
        orderLineItems.get(0).setQuantity(quantity);

        assertThatThrownBy(() -> 주문등록(deliveryOrder))
            .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> 주문등록(takeOutOrder))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문항목은 메뉴와 가격이 동일해야한다.")
    @ValueSource(strings = {"3000000000"})
    @ParameterizedTest
    void create_menu(BigDecimal price) {
        orderLineItems.get(0).setPrice(price);

        assertAll(
                () -> assertThatThrownBy(() -> 주문등록(deliveryOrder))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> 주문등록(takeOutOrder))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> 주문등록(eatInOrder))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    Order 주문등록(Order request) {
        return orderService.create(request);
    }

    public static OrderLineItem 주문항목만들기(MenuRepository menuRepository, MenuGroupRepository menuGroupRepository, ProductRepository productRepository) {
        Menu menu = 메뉴만들기(menuRepository, menuGroupRepository, productRepository);
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setMenuId(menu.getId());
        orderLineItem.setQuantity(2);
        orderLineItem.setPrice(menu.getPrice());
        return orderLineItem;
    }

}
