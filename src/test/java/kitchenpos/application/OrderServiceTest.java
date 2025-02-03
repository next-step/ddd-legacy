package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class OrderServiceTest {

    private static final UUID DISPLAY_MENU_ID = UUID.randomUUID();
    private static final UUID UNDISPLAYED_MENU_ID = UUID.randomUUID();
    private static final UUID ORDER_TABLE_ID = UUID.randomUUID();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderService orderService;


    @BeforeEach
    void setUp() {
        Product product1 = productRepository.save(createProduct(UUID.randomUUID(), "전시", new BigDecimal(25000)));
        Product product2 = productRepository.save(createProduct(UUID.randomUUID(), "비전시", new BigDecimal(7000)));

        UUID menuGroupId = UUID.randomUUID();
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(menuGroupId, "단품"));

        MenuProduct displayMenuProduct = createMenuProduct(product1.getId(), product1, 1);
        menuRepository.save(createMenu(DISPLAY_MENU_ID, menuGroup, menuGroup.getId(), "전시메뉴", new BigDecimal(25000), true, List.of(displayMenuProduct)));

        MenuProduct unDisplayMenuProduct = createMenuProduct(product2.getId(), product2, 1);
        menuRepository.save(createMenu(UNDISPLAYED_MENU_ID, menuGroup, menuGroup.getId(), "비전시메뉴", new BigDecimal(7000), false, List.of(unDisplayMenuProduct)));
    }

    //region [주문 생성]
    @DisplayName("주문의 종류는 반드시 입력해야 한다")
    @NullSource
    @ParameterizedTest
    void orderType(OrderType status) {
        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
        Order nullOrderTypeRequest = createOrder(status, List.of(orderLineItem), "", null, null);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(nullOrderTypeRequest));
    }

    @DisplayName("포장 주문을 생성한다. 주문이 정상적으로 생성되면 상태는 대기 중으로 변경된다")
    @Test
    void createTakeOutOrder() {
        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
        Order orderRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());

        Order orderResult = orderService.create(orderRequest);

        assertThat(orderResult.getType()).isEqualTo(OrderType.TAKEOUT);
        assertThat(orderResult.getOrderLineItems()).isNotNull();
        assertThat(orderResult.getOrderLineItems()).hasSize(1);
        assertThat(orderResult.getDeliveryAddress()).isNull();
        assertThat(orderResult.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("배달 주문을 생성한다. 주문이 정상적으로 생성되면 상태는 대기 중으로 변경된다")
    @Test
    void createDeliveryOrder() {
        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
        Order orderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());

        Order orderResult = orderService.create(orderRequest);

        assertThat(orderResult.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(orderResult.getOrderLineItems()).isNotNull();
        assertThat(orderResult.getOrderLineItems()).hasSize(1);
        assertThat(orderResult.getDeliveryAddress()).isNotNull();
        assertThat(orderResult.getDeliveryAddress()).isEqualTo("경기도 고양시..XX동 XX호");
        assertThat(orderResult.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("매장 내 식사 주문을 생성한다. 주문이 정상적으로 생성되면 상태는 대기 중으로 변경된다")
    @Test
    void createEatInOrder() {
        OrderTable request = createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4);
        OrderTable orderTable = orderTableRepository.save(request);

        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), 1);
        Order orderRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

        Order orderResult = orderService.create(orderRequest);

        assertThat(orderResult.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(orderResult.getOrderLineItems()).isNotNull();
        assertThat(orderResult.getOrderLineItems()).hasSize(1);
        assertThat(orderResult.getDeliveryAddress()).isNull();
        assertThat(orderResult.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("어떤 메뉴를 몇 개 시킬지에 대한 주문 상세 내역이 1개 이상 있어야 한다")
    @ParameterizedTest
    @MethodSource("nullOrEmptyList")
    void notNullOrderLineItems(List<OrderLineItem> orderLineItems) {
        Order takeOutRequest = createTakeOutOrder(orderLineItems, LocalDateTime.now());
        Order deliveryOrderRequest = createDeliveryOrder(orderLineItems, "경기도 고양시..XX동 XX호", LocalDateTime.now());
        OrderTable orderTable = orderTableRepository.save(createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
        Order eatInOrderRequest = createEatInOrder(orderLineItems, orderTable.getId(), LocalDateTime.now());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(takeOutRequest));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(deliveryOrderRequest));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(eatInOrderRequest));
    }

    private static Stream<List> nullOrEmptyList() {
        return Stream.of(null, Collections.emptyList());
    }

    @DisplayName("배달/포장 주문의 경우, 주문한 메뉴의 수량이 0개 이상이어야 한다")
    @Test
    void validateQuantity() {
        int negativeQuantity = -1;
        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal("25000"), negativeQuantity);
        Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
        Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(takeOutRequest));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(deliveryOrderRequest));
    }

    @DisplayName("메뉴판에 전시하지 않은 메뉴는 주문할 수 없다")
    @Test
    void validateMenuOrder() {
        OrderLineItem undisplayMenuOrderItem = createOrderLineItem(UNDISPLAYED_MENU_ID, new BigDecimal("25000"), 1);
        Order takeOutRequest = createTakeOutOrder(List.of(undisplayMenuOrderItem), LocalDateTime.now());
        Order deliveryOrderRequest = createDeliveryOrder(List.of(undisplayMenuOrderItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());
        OrderTable orderTable = orderTableRepository.save(createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
        Order eatInOrderRequest = createEatInOrder(List.of(undisplayMenuOrderItem), orderTable.getId(), LocalDateTime.now());

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(takeOutRequest));
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(deliveryOrderRequest));
        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(eatInOrderRequest));
    }

    @DisplayName("메뉴의 가격과 주문 내역의 가격은 동일해야 한다")
    @Test
    void validateOrderPrice() {
        BigDecimal menuPrice = menuRepository.findById(DISPLAY_MENU_ID).get().getPrice();

        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, menuPrice.add(BigDecimal.ONE), 1);
        Order takeOutRequest = createTakeOutOrder(List.of(orderLineItem), LocalDateTime.now());
        Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), "경기도 고양시..XX동 XX호", LocalDateTime.now());
        OrderTable orderTable = orderTableRepository.save(createOrderTable(ORDER_TABLE_ID, "1번테이블", true, 4));
        Order eatInOrderRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(takeOutRequest));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(deliveryOrderRequest));
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(eatInOrderRequest));
    }

    @DisplayName("배달의 경우, 배달 주소가 반드시 입력되어야 하며, 공백만 입력되어서는 안된다")
    @ParameterizedTest
    @NullAndEmptySource
    void notNullOrNotEmpty(String deliveryAddress) {
        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
        Order deliveryOrderRequest = createDeliveryOrder(List.of(orderLineItem), deliveryAddress, LocalDateTime.now());

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderService.create(deliveryOrderRequest));
    }

    @DisplayName("매장 내 취식일 경우, 고객에게 배정된 주문 테이블이 있어야 한다")
    @Test
    void validateOrderTable() {
        UUID unknownTableId = UUID.randomUUID();
        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
        Order eatInOrderRequest = createEatInOrder(List.of(orderLineItem), unknownTableId, LocalDateTime.now());

        assertThatThrownBy(() -> orderService.create(eatInOrderRequest))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("매장 내 취식일 경우, 주문 테이블의 사용유무가 사용 중이어야 한다")
    @Test
    void validateOrderTableStatus() {
        boolean tableOccupied = false;
        OrderTable orderTable = orderTableRepository.save(createOrderTable(ORDER_TABLE_ID, "1번테이블", tableOccupied, 0));
        OrderLineItem orderLineItem = createOrderLineItem(DISPLAY_MENU_ID, new BigDecimal(25000), 1);
        Order eatInOrderRequest = createEatInOrder(List.of(orderLineItem), orderTable.getId(), LocalDateTime.now());

        assertThatIllegalStateException()
                .isThrownBy(() -> orderService.create(eatInOrderRequest));
    }
    //endregion

    private Product createProduct(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private MenuProduct createMenuProduct(UUID productId, Product product, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setProductId(productId);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private Menu createMenu(UUID id, MenuGroup menuGroup, UUID menuGroupId, String name, BigDecimal price, boolean displayed, List<MenuProduct> products) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setMenuGroup(menuGroup);
        menu.setMenuGroupId(menuGroupId);
        menu.setName(name);
        menu.setPrice(price);
        menu.setDisplayed(displayed);
        menu.setMenuProducts(products);
        return menu;
    }

    private MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    private OrderTable createOrderTable(UUID id, String name, boolean occupied, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    private Order createTakeOutOrder(List<OrderLineItem> orderLineItems, LocalDateTime orderDateTime) {
        return createOrder(OrderType.TAKEOUT, orderLineItems, null, null, orderDateTime);
    }

    private Order createDeliveryOrder(List<OrderLineItem> orderLineItems, String deliveryAddress, LocalDateTime orderDateTime) {
        return createOrder(OrderType.DELIVERY, orderLineItems, deliveryAddress, null, orderDateTime);
    }

    private Order createEatInOrder(List<OrderLineItem> orderLineItems, UUID orderTableId, LocalDateTime orderDateTime) {
        return createOrder(OrderType.EAT_IN, orderLineItems, null, orderTableId, orderDateTime);
    }


    private Order createOrder(OrderType orderType, List<OrderLineItem> orderLineItems, String deliveryAddress, UUID orderTableId, LocalDateTime orderDateTime) {
        Order order = new Order();
        order.setType(orderType);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableId);
        order.setOrderDateTime(orderDateTime);
        return order;
    }

    private OrderLineItem createOrderLineItem(UUID menuId, BigDecimal price, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);
        return orderLineItem;
    }
}
