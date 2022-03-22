package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.DefaultIntegrationTestConfig;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class OrderServiceTest extends DefaultIntegrationTestConfig {

    private static final UUID NOT_EXIST_ID = UUID.randomUUID();

    private static final String NAME_MENU = "menuName";

    private static final BigDecimal PRICE1 = BigDecimal.valueOf(100L);
    private static final BigDecimal PRICE2 = BigDecimal.valueOf(200L);

    private static final String DELIVERY_ADDRESS = "testDeliveryAddress";

    @MockBean
    private KitchenridersClient mockKitchenridersClient;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService service;

    private Product product1;
    private Product productZeroPrice;

    private Menu menu1;
    private Menu menuZeroPrice;
    private Menu menuNotDisplayed;

    private OrderTable emptyOrderTable;
    private OrderTable notEmptyOrderTable;

    @BeforeEach
    void setUp() {
        product1 = createProduct("testProductName1", BigDecimal.valueOf(10L));
        productZeroPrice = createProduct("productZeroPrice", BigDecimal.ZERO);

        final MenuProduct menuProduct1 = new MenuProduct();
        menuProduct1.setProduct(product1);
        menuProduct1.setQuantity(2);

        menu1 = createMenu(NAME_MENU, true, PRICE1, menuProduct1);
        saveMenu(menu1);

        menuNotDisplayed = createMenu(NAME_MENU, false, PRICE1, menuProduct1);
        saveMenu(menuNotDisplayed);

        final MenuProduct menuProductZeroPrice = new MenuProduct();
        menuProductZeroPrice.setProduct(productZeroPrice);
        menuProductZeroPrice.setQuantity(1);

        menuZeroPrice = createMenu(NAME_MENU, true, BigDecimal.ZERO, menuProductZeroPrice);
        saveMenu(menuZeroPrice);

        emptyOrderTable = orderTableRepository.save(createOrderTable(true));
        notEmptyOrderTable = orderTableRepository.save(createOrderTable(false));
    }

    private OrderTable createOrderTable(final boolean empty) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("orderTableName-" + empty);
        orderTable.setNumberOfGuests(0);
        orderTable.setEmpty(empty);

        return orderTable;
    }

    private Order createOrderForTakeOut(final OrderStatus orderStatus,
        final OrderLineItem... orderLineItems) {

        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setType(OrderType.TAKEOUT);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItems));

        return order;
    }

    private Order createOrderForEatIn(final OrderStatus orderStatus,
        final OrderTable orderTable, final OrderLineItem... orderLineItems) {

        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setType(OrderType.EAT_IN);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItems));
        order.setOrderTable(orderTable);

        return order;
    }

    private Order createOrderForDelivery(final OrderStatus orderStatus,
        final OrderLineItem... orderLineItems) {

        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderDateTime(LocalDateTime.now());
        order.setType(OrderType.DELIVERY);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItems));
        order.setDeliveryAddress(DELIVERY_ADDRESS);

        return order;
    }

    private OrderLineItem create(final Menu menu, final int quantity) {
        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenu(menu);
        orderLineItem.setQuantity(quantity);

        return orderLineItem;
    }

    private Product createProduct(final String name, final BigDecimal price) {
        final Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setPrice(price);
        product.setName(name);

        return product;
    }

    private Menu createMenu(final String name, final boolean displayed, final BigDecimal price,
        final MenuProduct... menuProducts) {

        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(UUID.randomUUID());
        menuGroup.setName("testMenuGroup");

        final Menu menu = new Menu();
        menu.setId(UUID.randomUUID());
        menu.setPrice(price);
        menu.setName(name);
        menu.setMenuProducts(Collections.unmodifiableList(Arrays.asList(menuProducts)));
        menu.setDisplayed(displayed);
        menu.setMenuGroup(menuGroup);

        return menu;
    }

    private Order createRequestForTakeOut(@Nullable final List<OrderLineItem> orderLineItems) {
        final Order request = new Order();
        request.setType(OrderType.TAKEOUT);
        request.setOrderLineItems(orderLineItems);

        return request;
    }

    private Order createRequestForEatIn(final @Nullable UUID orderTableId,
        @Nullable final List<OrderLineItem> orderLineItems) {

        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(orderLineItems);
        request.setOrderTableId(orderTableId);

        return request;
    }

    private Order createRequestForDelivery(@Nullable final String deliveryAddress,
        @Nullable final List<OrderLineItem> orderLineItems) {

        final Order request = new Order();
        request.setType(OrderType.DELIVERY);
        request.setOrderLineItems(orderLineItems);
        request.setDeliveryAddress(deliveryAddress);

        return request;
    }

    private OrderLineItem createRequest(final UUID menuId,
        final BigDecimal price, final int quantity) {

        final OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        orderLineItem.setQuantity(quantity);

        return orderLineItem;
    }

    private void saveMenu(final Menu menu) {
        menuGroupRepository.save(menu.getMenuGroup());

        for (final MenuProduct menuProduct : menu.getMenuProducts()) {
            productRepository.save(menuProduct.getProduct());
        }

        menuRepository.save(menu);
    }

    private void assertOrdersForFindAll(final List<Order> results,
        final Order expResult1, final Order expResult2) {

        assertThat(results).hasSize(2);

        final Order result1;
        final Order result2;
        if (results.get(0).getId().equals(expResult1.getId())) {
            result1 = expResult1;
            result2 = expResult2;
        } else {
            result1 = expResult2;
            result2 = expResult1;
        }

        assertThat(result1).usingRecursiveComparison().isEqualTo(expResult1);
        assertThat(result2).usingRecursiveComparison().isEqualTo(expResult2);
    }

    private void assertOrderForCreateWithTakeOut(final Order result,
        final Menu expMenu, final int expQuantity) {

        assertThat(result.getId()).isNotNull();
        assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(result.getOrderDateTime()).isBetween(LocalDateTime.now().minusSeconds(10L),
            LocalDateTime.now());
        assertThat(result.getDeliveryAddress()).isNull();
        assertThat(result.getOrderTable()).isNull();

        final List<OrderLineItem> orderLineItems = result.getOrderLineItems();
        assertThat(orderLineItems).hasSize(1);

        final OrderLineItem orderLineItem = orderLineItems.get(0);
        assertThat(orderLineItem.getMenu().getId()).isEqualTo(expMenu.getId());
        assertThat(orderLineItem.getQuantity()).isEqualTo(expQuantity);
    }

    private void assertOrderForCreateWithEatIn(final Order result,
        final Menu expMenu, final int expQuantity, final UUID orderTableId) {

        assertThat(result.getId()).isNotNull();
        assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(result.getOrderDateTime()).isBetween(LocalDateTime.now().minusSeconds(10L),
            LocalDateTime.now());
        assertThat(result.getDeliveryAddress()).isNull();

        assertThat(result.getOrderTable().getId()).isEqualTo(orderTableId);

        final List<OrderLineItem> orderLineItems = result.getOrderLineItems();
        assertThat(orderLineItems).hasSize(1);

        final OrderLineItem orderLineItem = orderLineItems.get(0);
        assertThat(orderLineItem.getMenu().getId()).isEqualTo(expMenu.getId());
        assertThat(orderLineItem.getQuantity()).isEqualTo(expQuantity);
    }

    private void assertOrderForCreateWithDelivery(final Order result,
        final Menu expMenu, final int expQuantity, final String deliveryAddress) {

        assertThat(result.getId()).isNotNull();
        assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(result.getOrderDateTime()).isBetween(LocalDateTime.now().minusSeconds(10L),
            LocalDateTime.now());
        assertThat(result.getOrderTable()).isNull();

        assertThat(result.getDeliveryAddress()).isEqualTo(deliveryAddress);

        final List<OrderLineItem> orderLineItems = result.getOrderLineItems();
        assertThat(orderLineItems).hasSize(1);

        final OrderLineItem orderLineItem = orderLineItems.get(0);
        assertThat(orderLineItem.getMenu().getId()).isEqualTo(expMenu.getId());
        assertThat(orderLineItem.getQuantity()).isEqualTo(expQuantity);
    }

    private void assertOrderWithStatus(final Order result,
        final Order expResult, final OrderStatus expStatus) {

        assertThat(result)
            .usingRecursiveComparison()
            .ignoringFields("status")
            .isEqualTo(expResult);

        assertThat(result.getStatus()).isEqualTo(expStatus);
    }

    private void verifyRequestDelivery(final Order order, final BigDecimal expPrice) {
        verify(mockKitchenridersClient)
            .requestDelivery(order.getId(), expPrice, order.getDeliveryAddress());
    }

    @DisplayName("주문 타입이 없다면 예외를 발생시킨다")
    @Test
    void create_when_null_type() {
        // given
        final Order nullOrderTypeRequest = new Order();

        // when & then
        assertThatThrownBy(() -> service.create(nullOrderTypeRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문에 메뉴가 없다면 예외를 발생시킨다")
    @Test
    void create_when_null_orderLineItems() {
        // given
        final Order nullOrderLineItemRequest = createRequestForTakeOut(null);

        // when & then
        assertThatThrownBy(() -> service.create(nullOrderLineItemRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문에 메뉴가 없다면 예외를 발생시킨다")
    @Test
    void create_when_empty_orderLineItems() {
        // given
        final Order nullOrderLineItemRequest = createRequestForTakeOut(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> service.create(nullOrderLineItemRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 메뉴가 존재하지 않는다면 예외를 발생시킨다")
    @Test
    void create_when_not_exist_menu() {
        // given
        final OrderLineItem orderLineItemRequest = createRequest(NOT_EXIST_ID, PRICE1, 1);
        final Order notExistMenuRequest
            = createRequestForTakeOut(Collections.singletonList(orderLineItemRequest));

        // when & then
        assertThatThrownBy(() -> service.create(notExistMenuRequest))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문한 메뉴 수량이 음수라면 예외를 발생시킨다")
    @Test
    void create_when_negative_quantity() {
        // given
        final OrderLineItem negativeQuantityOrderLineItemRequest
            = createRequest(menu1.getId(), menu1.getPrice(), -1);
        final Order request = createRequestForTakeOut(
            Collections.singletonList(negativeQuantityOrderLineItemRequest));

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("미노출 메뉴를 주문하면 예외를 발생시킨다")
    @Test
    void create_when_not_displayed_menu() {
        // given
        final OrderLineItem orderLineItemRequest
            = createRequest(menuNotDisplayed.getId(), menuNotDisplayed.getPrice(), 1);
        final Order notDisplayedMenuRequest
            = createRequestForTakeOut(Collections.singletonList(orderLineItemRequest));

        // when & then
        assertThatThrownBy(() -> service.create(notDisplayedMenuRequest))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문의 메뉴 가격과 실제 메뉴의 가격이 다르다면 예외를 발생시킨다")
    @Test
    void create_when_diff_price() {
        // given
        final BigDecimal diffPrice = BigDecimal.valueOf(2L);

        final OrderLineItem diffPriceOrderLineItemRequest
            = createRequest(menu1.getId(), diffPrice, 1);
        final Order request
            = createRequestForTakeOut(Collections.singletonList(diffPriceOrderLineItemRequest));

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이크아웃 주문을 등록한다")
    @Test
    void create_when_takeOut_order() {
        // given
        final OrderLineItem orderLineItemRequest
            = createRequest(menu1.getId(), menu1.getPrice(), 1);
        final Order request
            = createRequestForTakeOut(Collections.singletonList(orderLineItemRequest));

        // when
        final Order result = service.create(request);

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderForCreateWithTakeOut(result, menu1, 1);
        assertOrderForCreateWithTakeOut(savedOrder, menu1, 1);
    }

    @DisplayName("매장식사 주문의 테이블이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void create_when_eatIn_order_but_not_exist_orderTable() {
        // given
        final OrderLineItem orderLineItemRequest
            = createRequest(menu1.getId(), menu1.getPrice(), 1);
        final Order request
            = createRequestForEatIn(NOT_EXIST_ID, Collections.singletonList(orderLineItemRequest));

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(NoSuchElementException.class);
    }


    @DisplayName("매장식사 주문의 테이블이 비어있지 않으면 예외를 발생시킨다")
    @Test
    void create_when_eatIn_order_but_not_empty_orderTable() {
        // given
        final OrderLineItem orderLineItemRequest
            = createRequest(menu1.getId(), menu1.getPrice(), 1);
        final Order request = createRequestForEatIn(notEmptyOrderTable.getId(),
            Collections.singletonList(orderLineItemRequest));

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장식사 주문을 등록한다")
    @Test
    void create_when_eatIn_order() {
        // given
        final OrderLineItem orderLineItemRequest
            = createRequest(menu1.getId(), menu1.getPrice(), 1);
        final Order request = createRequestForEatIn(emptyOrderTable.getId(),
            Collections.singletonList(orderLineItemRequest));

        // when
        final Order result = service.create(request);

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderForCreateWithEatIn(result, menu1, 1, emptyOrderTable.getId());
        assertOrderForCreateWithEatIn(savedOrder, menu1, 1, emptyOrderTable.getId());
    }

    @DisplayName("배달 주문의 주소가 존재하지 않으면 예외를 발생시킨다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_when_delivery_but_null_or_empty_deliveryAddress(
        final String nullOrEmptyDeliveryAddress) {
        // given
        final OrderLineItem orderLineItemRequest
            = createRequest(menu1.getId(), menu1.getPrice(), 1);
        final Order request = createRequestForDelivery(nullOrEmptyDeliveryAddress,
            Collections.singletonList(orderLineItemRequest));

        // when & then
        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달 주문을 등록한다")
    @Test
    void create_when_delivery() {
        // given
        final OrderLineItem orderLineItemRequest
            = createRequest(menu1.getId(), menu1.getPrice(), 1);
        final Order request = createRequestForDelivery(DELIVERY_ADDRESS,
            Collections.singletonList(orderLineItemRequest));

        // when
        final Order result = service.create(request);

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderForCreateWithDelivery(result, menu1, 1, DELIVERY_ADDRESS);
        assertOrderForCreateWithDelivery(savedOrder, menu1, 1, DELIVERY_ADDRESS);
    }

    @DisplayName("주문이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void accept_when_not_exist_order() {
        // given

        // when & then
        assertThatThrownBy(() -> service.accept(NOT_EXIST_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문이 대기상태가 아니면 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderStatus.class, mode = Mode.EXCLUDE, names = {"WAITING"})
    @ParameterizedTest
    void accept_when_not_waiting_status_order(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForTakeOut(orderStatus, orderLineItem));

        // when & then
        assertThatThrownBy(() -> service.accept(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이크아웃 주문을 수락한다")
    @Test
    void accept_when_takeOut_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForTakeOut(OrderStatus.WAITING, orderLineItem));

        // when
        final Order result = service.accept(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.ACCEPTED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.ACCEPTED);
    }

    @DisplayName("매장식사 주문을 수락한다")
    @Test
    void accept_when_eatIn_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(
            createOrderForEatIn(OrderStatus.WAITING, emptyOrderTable, orderLineItem));

        // when
        final Order result = service.accept(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.ACCEPTED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.ACCEPTED);
    }

    @DisplayName("배달주문을 수락한다")
    @Test
    void accept_when_delivery_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(
            createOrderForDelivery(OrderStatus.WAITING, orderLineItem));

        // when
        final Order result = service.accept(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.ACCEPTED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.ACCEPTED);
    }

    @DisplayName("배달주문을 수락하면 라이더 배차를 요청한다")
    @Test
    void accept_when_delivery_order_and_request_delivery() {
        // given
        final OrderLineItem orderLineItem1 = create(menu1, 1);
        final OrderLineItem orderLineItem2 = create(menu1, 1);

        final Order order = orderRepository.save(
            createOrderForDelivery(OrderStatus.WAITING, orderLineItem1, orderLineItem2));

        // when
        service.accept(order.getId());

        // verify
        final BigDecimal expPriceTotal = menu1.getPrice().add(menu1.getPrice());
        verifyRequestDelivery(order, expPriceTotal);
    }

    @DisplayName("주문이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void serve_when_not_exist_order() {
        // given

        // when & then
        assertThatThrownBy(() -> service.serve(NOT_EXIST_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문이 수락상태가 아니면 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderStatus.class, mode = Mode.EXCLUDE, names = {"ACCEPTED"})
    @ParameterizedTest
    void serve_when_not_accepted_status_order(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(
            createOrderForTakeOut(orderStatus, orderLineItem));

        // when & then
        assertThatThrownBy(() -> service.serve(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("수락 상태 테이크아웃 주문을 제공한다")
    @Test
    void serve_when_takeOut_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForTakeOut(OrderStatus.ACCEPTED, orderLineItem));

        // when
        final Order result = service.serve(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.SERVED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.SERVED);
    }

    @DisplayName("수락 상태 매장식사 주문을 제공한다")
    @Test
    void serve_when_eatIn_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(
            createOrderForEatIn(OrderStatus.ACCEPTED, emptyOrderTable, orderLineItem));

        // when
        final Order result = service.serve(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.SERVED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.SERVED);
    }

    @DisplayName("수락 상태 배달 주문을 제공한다")
    @Test
    void serve_when_delivery_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForDelivery(OrderStatus.ACCEPTED, orderLineItem));

        // when
        final Order result = service.serve(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.SERVED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.SERVED);
    }

    @DisplayName("주문이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void startDelivery_when_not_exist_order() {
        // given

        // when & then
        assertThatThrownBy(() -> service.startDelivery(NOT_EXIST_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달주문이 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderType.class, mode = Mode.EXCLUDE, names = {"DELIVERY"})
    @ParameterizedTest
    void startDelivery_when_type_is_not_delivery(final OrderType orderType) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order;
        if (orderType == OrderType.TAKEOUT) {
            order = orderRepository.save(createOrderForTakeOut(OrderStatus.SERVED, orderLineItem));
        } else {
            order = orderRepository.save(
                createOrderForEatIn(OrderStatus.SERVED, emptyOrderTable, orderLineItem));
        }

        // when & then
        assertThatThrownBy(() -> service.startDelivery(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제공완료된 배달 주문이 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderStatus.class, mode = Mode.EXCLUDE, names = {"SERVED"})
    @ParameterizedTest
    void startDelivery_when_status_is_not_served(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForDelivery(orderStatus, orderLineItem));

        // when & then
        assertThatThrownBy(() -> service.startDelivery(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제공완료된 배달 주문의 배송을 시작한다")
    @Test
    void startDelivery() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForDelivery(OrderStatus.SERVED, orderLineItem));

        // when
        final Order result = service.startDelivery(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.DELIVERING);
        assertOrderWithStatus(savedOrder, order, OrderStatus.DELIVERING);
    }

    @DisplayName("주문이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void completeDelivery_when_not_exist_order() {
        // given

        // when & then
        assertThatThrownBy(() -> service.completeDelivery(NOT_EXIST_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달주문이 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderType.class, mode = Mode.EXCLUDE, names = {"DELIVERY"})
    @ParameterizedTest
    void completeDelivery_when_type_is_not_delivery(final OrderType orderType) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order;
        if (orderType == OrderType.TAKEOUT) {
            order = orderRepository.save(createOrderForTakeOut(OrderStatus.SERVED, orderLineItem));
        } else {
            order = orderRepository.save(
                createOrderForEatIn(OrderStatus.SERVED, emptyOrderTable, orderLineItem));
        }

        // when & then
        assertThatThrownBy(() -> service.completeDelivery(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달중인 배달 주문이 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderStatus.class, mode = Mode.EXCLUDE, names = {"DELIVERING"})
    @ParameterizedTest
    void completeDelivery_when_status_is_not_served(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForDelivery(orderStatus, orderLineItem));

        // when & then
        assertThatThrownBy(() -> service.completeDelivery(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달중인 배달 주문의 배송을 완료한다")
    @Test
    void completeDelivery() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForDelivery(OrderStatus.DELIVERING, orderLineItem));

        // when
        final Order result = service.completeDelivery(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.DELIVERED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.DELIVERED);
    }

    @DisplayName("주문이 존재하지 않으면 예외를 발생시킨다")
    @Test
    void complete_when_not_exist_order() {
        // given

        // when & then
        assertThatThrownBy(() -> service.complete(NOT_EXIST_ID))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문완료된 배달 주문이 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderStatus.class, mode = Mode.EXCLUDE, names = {"DELIVERED"})
    @ParameterizedTest
    void complete_when_not_delivered_status_delivery_order(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForDelivery(orderStatus, orderLineItem));

        // when & then
        assertThatThrownBy(() -> service.complete(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제공완료된 매장식사 주문이 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderStatus.class, mode = Mode.EXCLUDE, names = {"SERVED"})
    @ParameterizedTest
    void complete_when_not_served_status_eatIn_order(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(
            createOrderForEatIn(orderStatus, emptyOrderTable, orderLineItem));

        // when & then
        assertThatThrownBy(() -> service.complete(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("제공완료된 포장 주문이 아니면 예외를 발생시킨다")
    @EnumSource(value = OrderStatus.class, mode = Mode.EXCLUDE, names = {"SERVED"})
    @ParameterizedTest
    void complete_when_not_served_status_takeOut_order(final OrderStatus orderStatus) {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(createOrderForTakeOut(orderStatus, orderLineItem));

        // when & then
        assertThatThrownBy(() -> service.complete(order.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달완료된 배달 주문을 완료한다")
    @Test
    void complete_when_delivery_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForDelivery(OrderStatus.DELIVERED, orderLineItem));

        // when
        final Order result = service.complete(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.COMPLETED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.COMPLETED);
    }

    @DisplayName("제공완료된 포장 주문을 완료한다")
    @Test
    void complete_when_takeOut_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order
            = orderRepository.save(createOrderForTakeOut(OrderStatus.SERVED, orderLineItem));

        // when
        final Order result = service.complete(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.COMPLETED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.COMPLETED);
    }

    @DisplayName("제공완료된 매장식사 주문을 완료한다")
    @Test
    void complete_when_eatIn_order() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(
            createOrderForEatIn(OrderStatus.SERVED, emptyOrderTable, orderLineItem));

        // when
        final Order result = service.complete(order.getId());

        final Order savedOrder = orderRepository.getById(result.getId());

        // then
        assertOrderWithStatus(result, order, OrderStatus.COMPLETED);
        assertOrderWithStatus(savedOrder, order, OrderStatus.COMPLETED);
    }

    @DisplayName("제공완료된 매장식사 주문을 완료할 때, 테이블에서 주문한 모든 매장식사 주문이 완료 되었다면 테이블은 빈 상태로 만든다")
    @Test
    void complete_when_eatIn_order_verify_clean_table() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order = orderRepository.save(
            createOrderForEatIn(OrderStatus.SERVED, notEmptyOrderTable, orderLineItem));

        // when
        service.complete(order.getId());

        final OrderTable orderTable = orderTableRepository.getById(notEmptyOrderTable.getId());

        // then
        assertThat(orderTable.isEmpty()).isTrue();
        assertThat(orderTable.getNumberOfGuests()).isZero();
    }

    @DisplayName("제공완료된 매장식사 주문을 완료할 때, 테이블에서 주문한 모든 매장식사 주문이 완료 되지 않았다면 테이블을 빈 상태로 만들지 않는다")
    @Test
    void complete_when_eatIn_order_verify_can_not_clean_table() {
        // given
        final OrderLineItem orderLineItem = create(menu1, 1);
        final Order order1 = orderRepository.save(
            createOrderForEatIn(OrderStatus.SERVED, notEmptyOrderTable, orderLineItem));

        // 동일 테이블에 할당된 아직 완료되지 않은 주문
        orderRepository.save(
            createOrderForEatIn(OrderStatus.SERVED, notEmptyOrderTable, orderLineItem));

        // when
        service.complete(order1.getId());

        final OrderTable orderTable = orderTableRepository.getById(notEmptyOrderTable.getId());

        // then
        assertThat(orderTable).usingRecursiveComparison().isEqualTo(notEmptyOrderTable);
    }

    @DisplayName("주문이 없다면 빈 리스트를 반환한다")
    @Test
    void findAll_when_empty_orders() {
        // given

        // when
        final List<Order> result = service.findAll();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("주문이 있다면 그것이 포함된 리스트를 반환한다")
    @Test
    void findAll() {
        // given
        final OrderLineItem orderLineItem1 = create(menu1, 1);
        final Order order1
            = orderRepository.save(createOrderForTakeOut(OrderStatus.WAITING, orderLineItem1));

        final OrderLineItem orderLineItem2 = create(menuZeroPrice, 2);
        final Order order2
            = orderRepository.save(createOrderForTakeOut(OrderStatus.ACCEPTED, orderLineItem2));

        // when
        final List<Order> result = service.findAll();

        // then
        assertOrdersForFindAll(result, order1, order2);
    }
}
