package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
public class OrderTableServiceTest {
    @Autowired
    private OrderTableService orderTableService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private MenuGroupRepository menuGroupRepository;
    @Autowired
    private MenuRepository menuRepository;


    @DisplayName("주문 테이블 등록하기")
    @Nested
    class OrderTableRegisterTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문 테이블을 정상적으로 등록한다")
        @Test
        void create_order_table_successfully() {
            // given
            OrderTable request = new OrderTable();
            request.setName("테이블 1");

            // when
            OrderTable orderTable = orderTableService.create(request);

            // then
            assertAll(
                    () -> assertThat(orderTable.getId()).isNotNull(),
                    () -> assertThat(orderTable.getName()).isEqualTo(request.getName()),
                    () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(orderTable.isOccupied()).isFalse()
            );
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("이름은 반드시 입력해야 합니다")
        @Test
        void name_must_be_input() {
            // given
            OrderTable request = createOrderTable(null, 0, false);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.create(request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("테이블에 손님 배정하기")
    @Nested
    class SitOrderTableTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("테이블에 손님을 배정한다")
        @Test
        void sit_order_table_successfully() {
            // given
            OrderTable orderTable = createInitializedOrderTable();

            // when
            OrderTable resultOrderTable = orderTableService.sit(orderTable.getId());

            // then
            assertThat(resultOrderTable.isOccupied()).isTrue();
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("존재하지 않는 테이블에 손님을 배정할 수 없다")
        @Test
        void sit_non_existent_order_table() {
            // given
            UUID nonExistentOrderTableId = UUID.randomUUID();

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.sit(nonExistentOrderTableId);

            // then
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("테이블에 손님 수 변경하기")
    @Nested
    class ChangeNumberOfGuestsTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("테이블에 앉은 손님 수를 변경한다")
        @Test
        void change_number_of_guests_successfully() {
            // given
            OrderTable orderTable = createInitializedOrderTable();
            orderTableService.sit(orderTable.getId());

            OrderTable request = new OrderTable();
            request.setNumberOfGuests(4);

            // when
            OrderTable resultOrderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThat(resultOrderTable.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("손님 수는 0명 이상이어야 한다")
        @Test
        void number_of_guests_must_be_positive() {
            // given
            OrderTable orderTable = createInitializedOrderTable();
            OrderTable request = new OrderTable();
            request.setNumberOfGuests(-1);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThatIllegalArgumentException()
                    .isThrownBy(throwingCallable);
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("사용중이 아닌 테이블의 손님 수를 변경할 수 없다")
        @Test
        void change_number_of_guests_on_empty_table() {
            // given
            OrderTable orderTable = createInitializedOrderTable();
            OrderTable request = new OrderTable();
            request.setNumberOfGuests(4);

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("테이블 정리하기")
    @Nested
    class ClearOrderTableTest {

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("테이블을 정리한다")
        @Test
        void clear_order_table_successfully() {
            // given
            OrderTable orderTable = createInitializedOrderTable();

            // when
            OrderTable resultOrderTable = orderTableService.clear(orderTable.getId());

            // then
            assertAll(
                    () -> assertThat(resultOrderTable.getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(resultOrderTable.isOccupied()).isFalse()
            );
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문이 완료된 테이블을 정리한다")
        @Test
        void clear_order_table_with_completed_order() {
            // given
            OrderTable orderTable = createInitializedOrderTable();
            orderTableService.sit(orderTable.getId());

            createEatInOrderWithCompleteState(orderTable);

            // when
            OrderTable resultOrderTable = orderTableService.clear(orderTable.getId());

            // then
            assertAll(
                    () -> assertThat(resultOrderTable.getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(resultOrderTable.isOccupied()).isFalse()
            );
        }

        @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        @DisplayName("주문이 완료되지 않은 테이블은 정리할 수 없다")
        @Test
        void clear_order_table_with_uncompleted_order() {
            // given
            OrderTable orderTable = createInitializedOrderTable();
            orderTableService.sit(orderTable.getId());

            Order order = createEeaInOrder(orderTable);
            orderService.accept(order.getId());

            // when
            ThrowableAssert.ThrowingCallable throwingCallable = () -> orderTableService.clear(orderTable.getId());

            // then
            assertThatIllegalStateException()
                    .isThrownBy(throwingCallable);
        }
    }

    @DisplayName("주문 테이블 조회하기")
    @Nested
    class FindAllOrderTablesTest {

        @SqlGroup({
                @Sql(value = "/setup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
                @Sql(value = "/delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
        })
        @DisplayName("모든 주문 테이블을 조회한다")
        @Test
        void find_all_order_tables() {
            // when
            List<OrderTable> orderTables = orderTableService.findAll();

            // then
            assertThat(orderTables).isNotEmpty();
        }
    }

    private OrderTable createInitializedOrderTable() {
        OrderTable request = createOrderTable("테이블 1", 0, false);
        return orderTableService.create(request);
    }

    private Order createEeaInOrder(OrderTable orderTable) {
        Product product = createProduct(후라이드치킨_PRODUCT_UUID, 후라이드치킨_PRODUCT_NAME, 후라이드치킨_DEFAULT_PRICE);
        productRepository.save(product);

        MenuGroup menuGroup = createMenuGroup(치킨류_MENU_GROUP_UUID, 치킨류_MENU_GROUP_NAME);
        menuGroupRepository.save(menuGroup);

        List<MenuProduct> menuProducts = List.of(createMenuProduct(후라이드치킨_PRODUCT_UUID, product, 1));
        Menu menu = createMenu(후라이드치킨_MENU_UUID, 후라이드치킨_MENU_NAME, 후라이드치킨_MENU_DEFAULT_PRICE, 치킨류_MENU_GROUP_UUID, menuGroup, menuProducts);
        menuRepository.save(menu);

        List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(후라이드치킨_MENU_UUID, 2, 후라이드치킨_MENU_DEFAULT_PRICE));
        Order request = createOrder(OrderType.EAT_IN, orderLineItems, "서울시 강남구", orderTable.getId(), orderTable);
        return orderService.create(request);
    }

    private Order createEatInOrderWithCompleteState(OrderTable orderTable) {
        Order order = createEeaInOrder(orderTable);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.complete(order.getId());
        return order;
    }

    private static Product createProduct(UUID id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private static MenuGroup createMenuGroup(UUID id, String name) {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setId(id);
        menuGroup.setName(name);
        return menuGroup;
    }

    private static Menu createMenu(UUID id, String name, BigDecimal price, UUID menuGroupId, MenuGroup menuGroup, List<MenuProduct> menuProducts) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setName(name);
        menu.setPrice(price);
        menu.setMenuGroupId(menuGroupId);
        menu.setMenuGroup(menuGroup);
        menu.setMenuProducts(menuProducts);
        menu.setDisplayed(true);
        return menu;
    }

    private static MenuProduct createMenuProduct(UUID productId, Product proudct, int quantity) {
        MenuProduct menuProduct = new MenuProduct();
        menuProduct.setProductId(productId);
        menuProduct.setProduct(proudct);
        menuProduct.setQuantity(quantity);
        return menuProduct;
    }

    private static OrderLineItem createOrderLineItem(UUID menuId, int quantity, BigDecimal price) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(menuId);
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(price);
        return orderLineItem;
    }

    private static Order createOrder(OrderType type, List<OrderLineItem> orderLineItems, String deliveryAddress, UUID orderTableUuid, OrderTable orderTable) {
        Order order = new Order();
        order.setType(type);
        order.setOrderLineItems(orderLineItems);
        order.setDeliveryAddress(deliveryAddress);
        order.setOrderTableId(orderTableUuid);
        order.setOrderTable(orderTable);
        return order;
    }

    private static OrderTable createOrderTable(String name, int numberOfGuests, boolean occupied) {
        return createOrderTable(null, name, numberOfGuests, occupied);
    }

    private static OrderTable createOrderTable(UUID id, String name, int numberOfGuests, boolean occupied) {
        OrderTable request = new OrderTable();
        request.setId(id);
        request.setName(name);
        request.setNumberOfGuests(numberOfGuests);
        request.setOccupied(occupied);
        return request;
    }

    private static final UUID 후라이드치킨_PRODUCT_UUID = UUID.randomUUID();
    private static final UUID 후라이드치킨_MENU_UUID = UUID.randomUUID();
    private static final UUID 치킨류_MENU_GROUP_UUID = UUID.randomUUID();
    private static final String 후라이드치킨_PRODUCT_NAME = "후라이드치킨";
    private static final String 치킨류_MENU_GROUP_NAME = "치킨류";
    private static final String 후라이드치킨_MENU_NAME = "후라이드치킨";
    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    private static final BigDecimal 후라이드치킨_MENU_DEFAULT_PRICE = new BigDecimal(19000);
}