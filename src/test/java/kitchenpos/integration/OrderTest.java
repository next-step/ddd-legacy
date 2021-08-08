package kitchenpos.integration;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.integration.annotation.TestAndRollback;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class OrderTest extends IntegrationTestRunner {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("주문 생성 ( 주문 'Type' 은 반드시 존재 해야 한다. )")
    @TestAndRollback
    public void create_with_null_type() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(false);
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(BigDecimal.valueOf(15000));
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 생성 ( 주문 생성시 '주문 아이템 목록' 이 'null' 일 수 없다. )")
    @TestAndRollback
    public void create_with_null_orderLineItem() {
        //given
        final Order request = new Order();
        request.setType(OrderType.EAT_IN);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 생성 ( 주문 생성시 '주문 아이템 목록' 이 'empty' 일 수 없다. )")
    @TestAndRollback
    public void create_with_empty_orderLineItem() {
        //given
        final List<OrderLineItem> orderLineItems = new ArrayList<>();
        final Order request = new Order();
        request.setType(OrderType.EAT_IN);
        request.setOrderLineItems(orderLineItems);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 생성 ( 주문 생성시 '주문 아이템' 의 수량은 0보다 작을 수 없다. )")
    @TestAndRollback
    public void create_with_minus_orderLineItem_quantity() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final long minusQuantity = -1L;
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(BigDecimal.valueOf(15000));
        orderLineItem_1.setQuantity(minusQuantity);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 생성 ( 주문 생성시 '주문 아이템' 에 해당되는 메뉴가 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void create_with_not_persisted_orderLineItem_menu() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();

        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(BigDecimal.valueOf(15000));
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 생성 ( 주문 생성시 '주문 아이템' 에 해당되는 메뉴의 'Display' 속성이 'true' 여야 한다. )")
    @TestAndRollback
    public void create_with_orderLineItem_menu_display_false() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(false);
        menu.setPrice(BigDecimal.valueOf(15000));
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(BigDecimal.valueOf(15000));
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 생성 ( 주문 생성시 '주문 아이템' 의 가격과 아이템에 해당되는 메뉴의 가격이 같아야 한다. )")
    @TestAndRollback
    public void create_with_not_equals_orderLineItem_price_and_menu_price() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(false);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(20000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));
    }

    @DisplayName("주문 생성 ( OrderType 'TakeOut' )")
    @TestAndRollback
    public void create_with_type_takeout() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.TAKEOUT);

        //when
        final Order order = orderService.create(request);

        //then
        assertThat(order.getId()).isNotNull();
        assertThat(order.getType()).isEqualTo(request.getType());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getOrderLineItems().get(0).getMenu()).isEqualTo(menu);
        assertThat(order.getOrderLineItems().get(0).getMenu().getId()).isEqualTo(menu.getId());
        assertThat(order.getOrderLineItems().get(0).getQuantity()).isEqualTo(1L);
    }

    @DisplayName("주문 생성 ( OrderType이 'DELIVERY' 면 주문 요청 주소는 'null' 일 수 없다. )")
    @TestAndRollback
    public void create_with_delivery_null_address() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.DELIVERY);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));

    }

    @DisplayName("주문 생성 ( OrderType이 'DELIVERY' 면 주문 요청 주소는 'empty' 일 수 없다. )")
    @TestAndRollback
    public void create_with_delivery_empty_address() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final String emptyAddress = "";
        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.DELIVERY);
        request.setDeliveryAddress(emptyAddress);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));

    }

    @DisplayName("주문 생성 ( OrderType 'DELIVERY' )")
    @TestAndRollback
    public void create_with_type_delivery() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final String deliveryAddress = "서울시 중랑구";
        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.DELIVERY);
        request.setDeliveryAddress(deliveryAddress);

        //when
        final Order order = orderService.create(request);

        //then
        assertThat(order.getId()).isNotNull();
        assertThat(order.getType()).isEqualTo(request.getType());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getDeliveryAddress()).isEqualTo(deliveryAddress);
        assertThat(order.getOrderLineItems().get(0).getMenu()).isEqualTo(menu);
        assertThat(order.getOrderLineItems().get(0).getMenu().getId()).isEqualTo(menu.getId());
        assertThat(order.getOrderLineItems().get(0).getQuantity()).isEqualTo(1L);
    }

    @DisplayName("주문 생성 ( OrderType이 'EAT_IN' 면 테이블 ID는 'null' 일 수 없다. )")
    @TestAndRollback
    public void create_with_eat_in_null_tableId() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);

        //when, then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(request));

    }

    @DisplayName("주문 생성 ( OrderType이 'EAT_IN' 면 요청 테이블은 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void create_with_eat_in_non_persisted_orderTable() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        final UUID orderTableId = UUID.randomUUID();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(orderTableId);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(request));

    }

    @DisplayName("주문 생성 ( OrderType이 'EAT_IN' 면 요청 테이블은 '이용중' 이어야 한다. )")
    @TestAndRollback
    public void create_with_eat_in_empty_orderTable() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        final boolean emptyStatus = false;
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(emptyStatus);
        orderTable.setNumberOfGuests(0);

        orderTableRepository.save(orderTable);

        final Order request = new Order();

        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(orderTableId);
        request.setOrderTable(orderTable);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(request));

    }

    @DisplayName("주문 생성 ( OrderType 'EAT_IN'")
    @TestAndRollback
    public void create_with_eat_in() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(true);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final Order request = new Order();
        request.setOrderLineItems(List.of(orderLineItem_1));
        request.setType(OrderType.EAT_IN);
        request.setOrderTableId(orderTableId);
        request.setOrderTable(orderTable);

        //when
        final Order order = orderService.create(request);

        //then
        assertThat(order.getId()).isNotNull();
        assertThat(order.getType()).isEqualTo(request.getType());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        assertThat(order.getOrderDateTime()).isNotNull();
        assertThat(order.getOrderTable()).isEqualTo(orderTable);
        assertThat(order.getOrderLineItems().get(0).getMenu()).isEqualTo(menu);
        assertThat(order.getOrderLineItems().get(0).getMenu().getId()).isEqualTo(menu.getId());
        assertThat(order.getOrderLineItems().get(0).getQuantity()).isEqualTo(1L);

    }

    @DisplayName("주문 수락 ( 주문 수락 요청시 해당 주문은 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void accept_with_not_persisted_order() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenuId(menuId);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setOrderTable(orderTable);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.accept(orderId));

    }

    @DisplayName("주문 수락 ( 주문 수락 요청시 해당 주문의 상태는 'WAITING' 이어야 한다. )")
    @TestAndRollback
    public void accept_with_not_status_waiting() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setStatus(OrderStatus.SERVED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(orderId);
        order.setOrderTable(orderTable);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(orderId));

    }

    @DisplayName("주문 수락 ( EAT_IN )")
    @TestAndRollback
    public void accept_type_eat_in() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setStatus(OrderStatus.WAITING);
        order.setOrderDateTime(LocalDateTime.now());
        order.setId(orderId);
        order.setOrderTable(orderTable);

        orderRepository.save(order);

        //when
        final Order acceptedOrder = orderService.accept(orderId);

        //then
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

    }

    @DisplayName("주문 수락 ( DELIVERY )")
    @TestAndRollback
    public void accept_type_delivery() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.WAITING);
        order.setId(orderId);

        orderRepository.save(order);

        //when
        final Order acceptedOrder = orderService.accept(orderId);

        //then
        assertThat(acceptedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

    }

    @DisplayName("주문 제공완료 ( 제공완료 요청시 해당 주문은 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void serve_with_not_persisted_order() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.WAITING);
        order.setId(orderId);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.serve(orderId));

    }

    @DisplayName("주문 제공완료 ( 제공완료 요청시 해당 주문의 상태는 'ACCEPTED' 이어야 한다. )")
    @TestAndRollback
    public void serve_with_order_status_not_accepted() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.WAITING);
        order.setId(orderId);
        order.setOrderTable(orderTable);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(orderId));

    }

    @DisplayName("주문 제공완료 ( EAT_IN )")
    @TestAndRollback
    public void serve_with_eat_in() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.ACCEPTED);
        order.setId(orderId);

        orderRepository.save(order);

        //when
        final Order servedOrder = orderService.serve(orderId);

        //when
        assertThat(servedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);

    }

    @DisplayName("주문 배달 시작 ( 배달 시작 요청시 해당 주문은 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void start_delivery_with_not_persisted_order() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.ACCEPTED);
        order.setId(orderId);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.startDelivery(orderId));

    }

    @DisplayName("주문 배달 시작 ( 배달 시작 요청시 해당 주문의 Type은 'delivery' 이어야 한다. )")
    @TestAndRollback
    public void start_delivery_with_not_type_delivery() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(orderId));

    }

    @DisplayName("주문 배달 시작 ( 배달 시작 요청시 해당 주문의 상태는 '제공완료' 이어야 한다. )")
    @TestAndRollback
    public void start_delivery_with_status_not_served() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.ACCEPTED);
        order.setId(orderId);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(orderId));

    }

    @DisplayName("주문 배달 시작")
    @TestAndRollback
    public void start_delivery_() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);

        orderRepository.save(order);

        //when
        final Order startDeliveryOrder = orderService.startDelivery(orderId);

        //then
        assertThat(startDeliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);

    }

    @DisplayName("배달 완료 ( 배달 완료 요청시 해당 주문은 영속화 되어 있어야 한다. )")
    @TestAndRollback
    public void complete_delivery_with_not_persisted_order() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.completeDelivery(orderId));

    }

    @DisplayName("배달 완료 ( 배달 완료 요청시 해당 주문의 Type은 'DELIVERY' 이어야 한다. )")
    @TestAndRollback
    public void complete_delivery_with_type_not_delivery() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);
        order.setOrderTable(orderTable);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.completeDelivery(orderId));

    }

    @DisplayName("배달 완료 ( 배달 완료 요청시 해당 주문의 상태는 '배달중' 이어야 한다. )")
    @TestAndRollback
    public void complete_delivery_with_status_not_delivering() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.completeDelivery(orderId));

    }

    @DisplayName("주문 완료")
    @TestAndRollback
    public void complete_delivery() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);
        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.DELIVERING);
        order.setId(orderId);

        orderRepository.save(order);

        //when
        final Order completeDeliveryOrder = orderService.completeDelivery(orderId);

        //then
        assertThat(completeDeliveryOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문 완료 ( 주문 완료 요청시 해당 주문은 영속화 되어있어야 한다. )")
    @TestAndRollback
    public void complete_with_not_persisted_order() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);

        //when, then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.complete(orderId));

    }

    @DisplayName("주문 완료 ( 주문 Type 타입이 'DELIVERY' 인 경우 주문의 상태는 '배달 완료' 이어야 한다 )")
    @TestAndRollback
    public void complete_with_type_delivery_status_not_delivered() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.DELIVERY);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(orderId));

    }

    @DisplayName("주문 완료 ( 주문 Type 타입이 'TAKEOUT', 'EAT_IN' 인 경우 주문의 상태는 '제공 완료' 이어야 한다 )")
    @TestAndRollback
    public void complete_with_type_takeout_status_not_served() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.TAKEOUT);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.ACCEPTED);
        order.setId(orderId);

        orderRepository.save(order);

        //when, then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(orderId));

    }

    @DisplayName("주문 완료 ( Type 'TAKEOUT' )")
    @TestAndRollback
    public void complete_type_takeout() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.TAKEOUT);
        order.setOrderTableId(orderTableId);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);
        order.setOrderTable(orderTable);

        orderRepository.save(order);

        //when
        final Order completedOrder = orderService.complete(orderId);

        //then
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

    }

    @DisplayName("주문 완료 ( Type 'EAT_IN' )")
    @TestAndRollback
    public void complete_type_eat_in() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId = UUID.randomUUID();
        final Order order = new Order();
        order.setOrderTableId(orderId);
        order.setOrderLineItems(List.of(orderLineItem_1));
        order.setType(OrderType.EAT_IN);
        order.setOrderTableId(orderTableId);
        order.setOrderDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.SERVED);
        order.setId(orderId);
        order.setOrderTable(orderTable);

        orderRepository.save(order);

        //when
        final Order completedOrder = orderService.complete(orderId);
        final OrderTable emptyTable = orderTableRepository.findById(orderTableId).get();

        //then
        assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(emptyTable.getNumberOfGuests()).isEqualTo(0);
        assertThat(emptyTable.isEmpty()).isTrue();

    }

    @DisplayName("모든 Order 조회")
    @TestAndRollback
    public void findAll() {
        //given
        final UUID menuGroupId = UUID.randomUUID();
        final MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(menuGroupId);
        menuGroup.setName("메인 메뉴");

        menuGroupRepository.save(menuGroup);

        final UUID productId = UUID.randomUUID();
        final Product product_1 = new Product();
        product_1.setId(productId);
        product_1.setName("후라이드 치킨");
        product_1.setPrice(BigDecimal.valueOf(15000));

        productRepository.save(product_1);

        final MenuProduct menuProduct_1 = new MenuProduct();
        menuProduct_1.setProductId(productId);
        menuProduct_1.setQuantity(1L);
        menuProduct_1.setProduct(product_1);

        final UUID menuId = UUID.randomUUID();
        final Menu menu = new Menu();
        final BigDecimal menuPrice = BigDecimal.valueOf(15000);
        menu.setId(menuId);
        menu.setName("후라이드 한마리");
        menu.setDisplayed(true);
        menu.setPrice(menuPrice);
        menu.setMenuProducts(List.of(menuProduct_1));
        menu.setMenuGroup(menuGroup);

        menuRepository.save(menu);

        final UUID orderTableId = UUID.randomUUID();
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");
        orderTable.setId(orderTableId);
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(1);

        orderTableRepository.save(orderTable);

        final BigDecimal orderLineItemPrice = BigDecimal.valueOf(15000);
        final OrderLineItem orderLineItem_1 = new OrderLineItem();
        orderLineItem_1.setMenu(menu);
        orderLineItem_1.setPrice(orderLineItemPrice);
        orderLineItem_1.setQuantity(1L);

        final UUID orderId_1 = UUID.randomUUID();
        final Order order_1 = new Order();
        order_1.setOrderTableId(orderId_1);
        order_1.setOrderLineItems(List.of(orderLineItem_1));
        order_1.setType(OrderType.EAT_IN);
        order_1.setOrderTableId(orderTableId);
        order_1.setOrderDateTime(LocalDateTime.now());
        order_1.setStatus(OrderStatus.SERVED);
        order_1.setId(orderId_1);
        order_1.setOrderTable(orderTable);

        final UUID orderId_2 = UUID.randomUUID();
        final Order order_2 = new Order();
        order_2.setOrderTableId(orderId_2);
        order_2.setOrderLineItems(List.of(orderLineItem_1));
        order_2.setType(OrderType.DELIVERY);
        order_2.setOrderTableId(orderTableId);
        order_2.setOrderDateTime(LocalDateTime.now());
        order_2.setStatus(OrderStatus.WAITING);
        order_2.setId(orderId_2);
        order_2.setOrderTable(orderTable);

        orderRepository.saveAll(List.of(order_1, order_2));
        
        //when
        final List<Order> orders = orderService.findAll();

        //then
        assertThat(orders.size()).isEqualTo(2);
        assertThat(orders.get(0)).isEqualTo(order_1);
        assertThat(orders.get(1)).isEqualTo(order_2);
    }
}
