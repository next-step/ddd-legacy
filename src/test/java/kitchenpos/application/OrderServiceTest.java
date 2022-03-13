package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderTableService orderTableService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private MenuGroupService menuGroupService;
    @Autowired
    private ProductService productService;

    @DisplayName("주문유형을 반드시 입력해주어야 한다.")
    @Test
    void orderTypeIsMandatory() {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(null, orderLineItems, "성남시 분당구 정자동", orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문유형이 매장식사인 경우, 주문테이블 정보를 입력해야 한다.")
    @Test
    void orderTypeEatIn() {
        /// given
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems, "성남시 분당구 정자동", orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("비어있는 주문테이블에는 매장식사 주문이 불가능하다.")
    @Test
    void eatInToEmptyTable() {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        OrderTable emptyTable = orderTableService.clear(orderTable.getId());
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems, "성남시 분당구 정자동", emptyTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문유형이 배달인 경우 배달주소 정보를 입력해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyAddress(String deliveryAddress) {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(OrderType.DELIVERY, orderLineItems, deliveryAddress, orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }
    
    @DisplayName("주문유형이 매장식사가 아닌경우, 주문수량을 반드시 입력해야 한다.")
    @ParameterizedTest
    @CsvSource(value = {"DELIVERY:-1", "TAKEOUT:-1"}, delimiter = ':')
    void orderTypeQuantity(String orderTypeName, int quantity) {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), quantity, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(OrderType.valueOf(orderTypeName), orderLineItems, "성남시 분당구 정자동", orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 시에는 반드시 주문항목을 입력해 주어야 한다.")
    @ParameterizedTest
    @EnumSource(OrderType.class)
    void orderLineItemsAreMandatory(OrderType orderType) {
        // given
        OrderTable orderTable = createOrderTable();

        // when
        Order orderRequest = createOrderRequest(orderType, Collections.emptyList(), "성남시 분당구 정자동", orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 메뉴를 선택할 수 없다.")
    @Test
    void notExistsMenu() {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = new Menu();
        menu.setId(UUID.randomUUID());

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems, "성남시 분당구 정자동", orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("판매중이지 않은(숨겨진) 메뉴는 선택할 수 없다.")
    @Test
    void hideMenu() {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();
        menu = menuService.hide(menu.getId());

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems, "성남시 분당구 정자동", orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("메뉴의 가격과 주문항목의 가격은 반드시 동일해야 한다.")
    @Test
    void illegalOrderLinePrice() {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("16000")));

        // when
        Order orderRequest = createOrderRequest(OrderType.EAT_IN, orderLineItems, "성남시 분당구 정자동", orderTable.getId());

        // then
        assertThatThrownBy(() -> orderService.create(orderRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문생성시 초기 상태는 '대기'이다")
    @ParameterizedTest
    @EnumSource(OrderType.class)
    void defaultState(OrderType orderType) {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(orderType, orderLineItems, "성남시 분당구 정자동", orderTable.getId());
        Order order = orderService.create(orderRequest);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("배달유형 주문의 주문상태 순서 테스트")
    @Test
    void deliveryTypeOrder() {
        // given
        OrderTable orderTable = createOrderTable();
        Menu menu = createMenu();

        List<OrderLineItem> orderLineItems = new ArrayList<>();
        orderLineItems.add(createOrderLineItemRequest(menu.getId(), 1, new BigDecimal("15000")));

        // when
        Order orderRequest = createOrderRequest(OrderType.DELIVERY, orderLineItems, "성남시 분당구 정자동", orderTable.getId());
        Order order = orderService.create(orderRequest);

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        order = orderService.accept(order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        order = orderService.serve(order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
        order = orderService.startDelivery(order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        order = orderService.completeDelivery(order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        order = orderService.complete(order.getId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }


    private Order createOrderRequest(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress, UUID orderTableId) {
        Order orderCreateRequest = new Order();
        orderCreateRequest.setType(orderType);
        orderCreateRequest.setOrderLineItems(orderLineItems);
        orderCreateRequest.setDeliveryAddress(deliveryAddress);
        orderCreateRequest.setOrderTableId(orderTableId);
        return orderCreateRequest;
    }

    private OrderLineItem createOrderLineItemRequest(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    private Menu createMenu() {
        MenuGroup menuGroupRequest = MenuGroupServiceTest.createMenuGroupRequest("한마리메뉴");
        MenuGroup menuGroup = menuGroupService.create(menuGroupRequest);

        Product productRequest = ProductServiceTest.createProductRequest("후라이드치킨", new BigDecimal("15000"));
        Product product = productService.create(productRequest);

        List<MenuProduct> menuProducts = new ArrayList<>();
        menuProducts.add(MenuServiceTest.createMenuProduct(product, 1));

        Menu menuCreateRequest = MenuServiceTest.createMenuRequest(new BigDecimal("15000"), menuGroup, menuProducts, "후라이드치킨");
        return menuService.create(menuCreateRequest);
    }

    private OrderTable createOrderTable() {
        OrderTable orderTableCreateRequest = OrderTableServiceTest.createOrderTableCreateRequest("1번");
        OrderTable orderTable = orderTableService.create(orderTableCreateRequest);

        orderTable = orderTableService.sit(orderTable.getId());
        OrderTable changeNumberOfGuestsRequest = new OrderTable();
        changeNumberOfGuestsRequest.setNumberOfGuests(2);
        return orderTableService.changeNumberOfGuests(orderTable.getId(), changeNumberOfGuestsRequest);
    }
}
