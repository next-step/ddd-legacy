package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;


@DisplayName(value = " Order 테스트")
@Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class OrderTest {

    private static final String TEST_PRODUCT_NAME = "TEST치킨";
    private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.fromString("3b528244-34f7-406b-bb7e-690912f66b10");
    private static final UUID ORDER_UUID = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8");

    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    private static final BigDecimal 후라이드치킨_OVER_PRICE = new BigDecimal(21000);
    private static final UUID ORDER_TABLE_ID = UUID.fromString("8d710043-29b6-420e-8452-233f5a035520");
    private static final String 후라이드치킨_MENU_NAME = "후라이드 치킨메뉴";
    private static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");
    private static final UUID 후라이드치킨_MENU_HIDE_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63c");
    private static final UUID 후라이드치킨_MENU_GROUP_UUID = UUID.fromString("cbc75fae-feb0-4bb1-8be2-cb8ce5d8fded");
    private static final String 한마리메뉴_MENU_GROUP_NAME = "한마리메뉴";
    private static final OrderStatus ORDER_STATUS_주문대기 = WAITING;
    private static final OrderStatus ORDER_STATUS_주문수락 = ACCEPTED;
     private static final OrderStatus ORDER_STATUS_배달중 = DELIVERING;
    private static final OrderStatus ORDER_STATUS_배달완료 = DELIVERED;
    private static final OrderStatus ORDER_STATUS_제공완료 = SERVED;
    private static final OrderType ORDER_TYPE_배달주문 = DELIVERY;
    private static final OrderType ORDER_TYPE_포장주문 = TAKEOUT;
    private static final OrderType ORDER_TYPE_매장내식사주문 = EAT_IN;
    private static final LocalDateTime ORDER_DATE_TIME_주문요청시간  = LocalDateTime.now();

    @SpyBean
    private MenuRepository menuRepository;
    @SpyBean
    private MenuGroupRepository menuGroupRepository;
    private FakeKitchenridersClient fakeKitchenridersClient;
    private OrderRepository orderRepository;

    private OrderService orderService;

    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        orderRepository = spy(new InMemoryOrderRepository());
        orderTableRepository = new InMemoryOrderTableRepository();
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        fakeKitchenridersClient = new FakeKitchenridersClient();
        orderService = spy(new OrderService(orderRepository,menuRepository,orderTableRepository,fakeKitchenridersClient));
    }

    @DisplayName(value = "주문 추가 기능")
    @Nested
    class OrderCreateTest {
        private static final BigDecimal 후라이드치킨_MINUS_PRICE = new BigDecimal(-10);
        private static final String 후라이드치킨_PROFANITY_MENU_NAME = "fucking 치킨메뉴";
        private static final int MINUS_QUANTITY = -1;
        private static final int DEFAULT_QUANTITY = 1;
        public static final OrderType ORDER_TYPE_미선택 = null;
        private static final long ZERO_QUANTITY = 0;
        private static final String ORDER_TABLE_NAME = "1번";
        public static final boolean TABLE_USABLE = true;
        private static final boolean TABLE_UNUSABLE = false;

        @DisplayName(value = "주문 추가기능 & 주문 검증이 끝나면 주문대기(WAITING) 상태가 됩니다")
        @Test
        void validateStateWaiting() {
            Mockito.clearInvocations(orderService, orderRepository);
            menuRepository.save(createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct()));
            Order orderRequest = createOrder();
            Order orderResponse = orderService.create(orderRequest);
            verify(orderService,times(1)).create(Mockito.any());
            verify(orderRepository,times(1)).save(Mockito.any());

            assertAll(
                    () -> assertThat(orderResponse).isNotNull(),
                    () -> assertThat(orderResponse.getDeliveryAddress()).isEqualTo(orderRequest.getDeliveryAddress()),
                    () -> assertThat(orderResponse.getId()).isNotNull(),
                    () -> assertThat(orderResponse.getType()).isEqualTo(orderRequest.getType()),
                    () -> assertThat(orderResponse.getOrderDateTime()).isNotNull(),
                    () -> assertThat(orderResponse.getOrderLineItems()).hasSize(1),
                    () -> assertThat(orderResponse.getStatus()).isEqualTo(WAITING)
            );
        }

        @DisplayName(value = "주문 타입이 없으면 안됩니다.")
        @Test
        void invalidOrderType() {
            Order order = createOrder(ORDER_UUID, ORDER_TYPE_미선택, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                    createOrderLineItem(), "강남구");
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "주문은 최소한 1개 이상의 주문상품을 선택해야 합니다.")
        @Test
        void invalidOrderLineItems() {
            Order order = createOrder(ORDER_TYPE_미선택);
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "주문상품의 개수와 주문상품속 메뉴들의 총 개수는 같아야 합니다.")
        @Test
        void notEqualMenuAndOrderLineItemSize() {
            OrderLineItem orderLineItem = createOrderLineItem(null, DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
            Order order = createOrder(orderLineItem, "강남구");
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "배달이거나 포장주문일 경우, 주문상품의 수량은 0개 이상이어야 합니다.")
        @Test
        void invalidOrderLineItemSize() {
            OrderLineItem orderLineItem = createOrderLineItem(후라이드치킨_MENU_UUID, MINUS_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
            Order order = createOrder(orderLineItem, "강남구");

            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "선택된 메뉴가 비노출되어 있는 상태면 안됩니다.")
        @Test
        void selectedMenuNotDisplayed() {
            var menuId = menuRepository.save(createMenu(후라이드치킨_DEFAULT_PRICE, false, createMenuProduct())).getId();
            Order order = createOrder(createOrderLineItem(menuId), "강남구");
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);
            assertThatIllegalStateException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "선택된 메뉴의 금액과 주문요청한 메뉴의 금액은 같아야 합니다.")
        @Test
        void notEqualMenuAndOrderMenuPrice() {
            OrderLineItem orderLineItem = createOrderLineItem(후라이드치킨_MENU_UUID, DEFAULT_QUANTITY, 후라이드치킨_OVER_PRICE);
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(createOrder(orderLineItem, "강남구"));
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "배달주문인 경우, 반드시 배달 주소를 입력하여 배달기사에게 전달합니다.")
        @Test
        void validateDeliveryAddress() {
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(createOrder(createOrderLineItem(), ""));
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "매장내 식사일 경우, 배정요청한 주문 테이블이 사용가능이어야 합니다.")
        @Test
        void validateEatInOrderTable() {
            var menuId = menuRepository.save(createMenu(후라이드치킨_DEFAULT_PRICE, false, createMenuProduct())).getId();
            OrderTable orderTable = createOrderTable(ORDER_TABLE_ID, ORDER_TABLE_NAME, 0, TABLE_UNUSABLE);
            orderTableRepository.save(orderTable);
            Order order = createOrder(ORDER_UUID, ORDER_TYPE_매장내식사주문, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                    createOrderLineItem(menuId), "강남구",orderTable,orderTable.getId());
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderService.create(order);
            assertThatIllegalStateException().isThrownBy(throwingCallable);
        }




        /*@DisplayName(value = "메뉴를 등록할 수 있다.")
        @Test
        void createMenu() {
            Mockito.clearInvocations(menuRepository, productRepository);
            Product product = Product(OrderTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);

            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1, 후라이드치킨_PRODUCT_UUID));
            Menu menuRequest = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);

            Menu menu = menuService.create(menuRequest);

            //행위검증
            verify(menuRepository, times(1)).save(ArgumentCaptor.forClass(Menu.class).capture());
            verify(productRepository, times(1)).findAllByIdIn(Mockito.anyList());
            verify(productRepository, times(1)).findById(Mockito.any());
            verify(menuGroupRepository, times(1)).findById(Mockito.any());


            assertAll(
                    () -> assertThat(menu.getId()).isNotNull(),
                    () -> assertThat(menu.getMenuGroup().getId()).isEqualTo(menuRequest.getMenuGroupId()),
                    () -> assertThat(menu.getName()).isEqualTo(menuRequest.getName()),
                    () -> assertThat(menu.getMenuProducts()).hasSize(menuRequest.getMenuProducts().size()),
                    () -> assertThat(menu.isDisplayed()).isEqualTo(menuRequest.isDisplayed()),
                    () -> assertThat(menu.getPrice()).isEqualTo(menuRequest.getPrice())
            );

        }


        @DisplayName(value = "메뉴의 주문 상품이 1개 이상 존재해야 합니다")
        @Test
        void invalidMenuProduct() {
            Product product = Product(OrderTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);

            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1, 후라이드치킨_PRODUCT_UUID));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_MINUS_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴 내부 상품의 수량은 0개 이상이어야 합니다.")
        @Test
        void menuProductNotEqualsProductSize() {
            Product product = Product(OrderTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            List<MenuProduct> menuProducts = List.of(MenuProduct(product, MINUS_QUANTITY, 후라이드치킨_PRODUCT_UUID));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);
            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "등록하려는 메뉴의 가격이 메뉴에 포함된 상품의 총 가격보다 높으면 안됩니다.")
        @Test
        void invalidTotalMenuAmount() {
            Product product = Product(OrderTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);

            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1, 후라이드치킨_PRODUCT_UUID));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_OVER_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);

            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }

        @DisplayName(value = "메뉴의 이름이 없거나 비속어가 들어가 있으면 안됩니다.")
        @Test
        void invalidMenuName() {
            Product product = Product(OrderTest.후라이드치킨_PRODUCT_UUID, TEST_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
            MenuGroup menuGroup = MenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
            List<MenuProduct> menuProducts = List.of(MenuProduct(product, 1, 후라이드치킨_PRODUCT_UUID));
            Menu menu = Menu(후라이드치킨_MENU_UUID, 후라이드치킨_PROFANITY_MENU_NAME, 후라이드치킨_DEFAULT_PRICE, menuGroup, menuProducts, 후라이드치킨_MENU_GROUP_UUID);

            ThrowingCallable throwingCallable = () -> menuService.create(menu);
            assertThatIllegalArgumentException().isThrownBy(throwingCallable);
        }
        */
    }


    private MenuProduct createMenuProduct(final Product product, final int quantity, final UUID productId) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProduct(product);
        menuProduct.setQuantity(quantity);
        menuProduct.setProductId(productId);
        return menuProduct;
    }

    private MenuProduct createMenuProduct() {
        return createMenuProduct(createProduct(), 1, 후라이드치킨_PRODUCT_UUID);
    }

    private static Product createProduct(final UUID uuid, final String name, final BigDecimal price) {
        Product product = new Product();
        product.setId(uuid);
        product.setName(name);
        product.setPrice(price);
        return product;

    }
    private Product createProduct() {
        Product product = new Product();
        product.setId(후라이드치킨_PRODUCT_UUID);
        product.setName(TEST_PRODUCT_NAME);
        product.setPrice(후라이드치킨_DEFAULT_PRICE);
        return product;
    }


    private static MenuGroup createMenuGroup(final String name, final UUID id) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(name);
        menuGroup.setId(id);
        return menuGroup;
    }
    private static MenuGroup createMenuGroup() {
         return createMenuGroup(한마리메뉴_MENU_GROUP_NAME, 후라이드치킨_MENU_GROUP_UUID);
    }

    private static Menu createMenu(final UUID id, final String name, final BigDecimal price, final MenuGroup menuGroup, final MenuProduct menuProducts, final UUID menugroupId) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroup(menuGroup);
        menu.setDisplayed(true);
        menu.setMenuProducts(Arrays.asList(menuProducts));
        menu.setMenuGroupId(menugroupId);
        return menu;
    }

    private static Menu createMenu(final BigDecimal price, final boolean displayed, final MenuProduct menuProducts) {
        Menu menu = new Menu();
        menu.setId(후라이드치킨_MENU_UUID);
        menu.setName(후라이드치킨_MENU_NAME);
        menu.setPrice(price);
        menu.setMenuGroup(createMenuGroup());
        menu.setDisplayed(displayed);
        menu.setMenuProducts(Arrays.asList(menuProducts));
        return menu;
    }

    private static Order createOrder(final UUID id, final OrderType orderType, final OrderStatus orderStatus, final LocalDateTime orderDateTime,
                                     final OrderLineItem orderLineItem, final String deliveryAddress) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderDateTime(orderDateTime);
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }

    private Order createOrder(final UUID id, final OrderType orderType, final OrderStatus orderStatus, final LocalDateTime orderDateTime,
                              final OrderLineItem orderLineItem, final String deliveryAddress, OrderTable orderTable, UUID orderTableId) {
        Order order = new Order();
        order.setId(id);
        order.setType(orderType);
        order.setStatus(orderStatus);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setOrderDateTime(orderDateTime);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTable(orderTable);
        order.setOrderTableId(orderTableId);
        return order;
    }

    private Order createOrder(OrderLineItem orderLineItem, String deliveryAddress) {
        Order order = new Order();
        order.setId(ORDER_UUID);
        order.setType(ORDER_TYPE_배달주문);
        order.setStatus(ORDER_STATUS_주문대기);
        order.setOrderLineItems(Arrays.asList(orderLineItem));
        order.setDeliveryAddress(deliveryAddress);
        return order;
    }



    private static Order createOrder() {
        return createOrder(ORDER_UUID, ORDER_TYPE_배달주문, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                createOrderLineItem(), "강남구");
    }

    private static Order createOrder(OrderType orderType) {
        return createOrder(ORDER_UUID, orderType, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                createOrderLineItem(), "강남구");
    }




    private static OrderLineItem createOrderLineItem(UUID menuId, long quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setQuantity(quantity);
        orderLineItem.setMenuId(menuId);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }
    private static OrderLineItem createOrderLineItem() {
        return createOrderLineItem(후라이드치킨_MENU_UUID, OrderCreateTest.DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
    }

    private OrderLineItem createOrderLineItem(UUID menuId) {
        return createOrderLineItem(menuId, OrderCreateTest.DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
    }

    private static OrderTable createOrderTable(UUID orderTableId, String orderTableName, int numberOfGuest, boolean istableUsable) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(orderTableId);
        orderTable.setName(orderTableName);
        orderTable.setNumberOfGuests(numberOfGuest);
        orderTable.setOccupied(istableUsable);
        return orderTable;
    }

}
