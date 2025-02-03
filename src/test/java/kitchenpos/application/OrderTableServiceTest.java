package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

@Transactional
@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    //region [주문 테이블 등록]
    @DisplayName("주문 테이블의 이름을 지정하여 생성할 수 있다")
    @Test
    void createOrderTable() {
        OrderTable request = createOrderTable("1번테이블");

        OrderTable orderTable = orderTableService.create(request);

        assertThat(orderTable.getName()).isEqualTo("1번테이블");
    }

    @DisplayName("주문 테이블의 이름은 공백만 입력할 수 없으며 반드시 입력되어야 한다")
    @NullAndEmptySource
    @ParameterizedTest
    void notNullTableName(String name) {
        OrderTable request = createOrderTable(name);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.create(request));
    }

    @DisplayName("주문 테이블 생성 시, 고객의 수는 0명, 테이블의 사용유무는 사용안함이다")
    @Test
    void createOrderTableWithNullName() {
        OrderTable request = createOrderTable("1번");

        OrderTable orderTable = orderTableService.create(request);

        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }
    //endregion

    //region [고객을 주문 테이블에 배정]
    @DisplayName("주문 테이블에 고객이 배정되면 테이블의 사용유무가 사용 중으로 바뀐다")
    @Test
    void sit() {
        OrderTable orderTable = orderTableService.create(createOrderTable("1번테이블"));

        OrderTable resultOrderTable = orderTableService.sit(orderTable.getId());

        assertThat(resultOrderTable.isOccupied()).isTrue();
    }
    //endregion

    //region [주문테이블 정리]
    @DisplayName("테이블의 주문 상태가 완료가 아니면 정리할 수 없다")
    @EnumSource(value = OrderStatus.class, names = "COMPLETED", mode = EnumSource.Mode.EXCLUDE)
    @ParameterizedTest
    void validateOrderStatus(OrderStatus status) {
        //given
        OrderTable orderTable = orderTableService.create(createOrderTable("1번테이블"));
        orderTableService.sit(orderTable.getId());
        orderTable.setNumberOfGuests(4);
        orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        Order order = createUnnamedOrder(orderTable.getId());
        //when, then
        order.setStatus(status);
        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }

    @DisplayName("테이블 정리하면 손님의 수 0명, 테이블 사용유무 안함으로 변경된다")
    @Test
    void clear() {
        //given
        OrderTable orderTable = orderTableService.create(createOrderTable("1번테이블"));
        orderTableService.sit(orderTable.getId());
        orderTable.setNumberOfGuests(4);
        orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        Order order = createUnnamedOrder(orderTable.getId());
        //when
        order.setStatus(OrderStatus.COMPLETED);
        OrderTable clearOrder = orderTableService.clear(orderTable.getId());
        //then
        assertThat(clearOrder.getNumberOfGuests()).isZero();
        assertThat(clearOrder.isOccupied()).isFalse();
    }
    //endregion

    //region [고객의 수 변경]
    @DisplayName("테이블의 고객 수를 0미만으로 변경할 수 없다")
    @Test
    void changeNumberOfGuestsByNegative() {
        OrderTable orderTable = orderTableService.create(createOrderTable("1번테이블"));

        orderTable.setNumberOfGuests(-1);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("테이블의 사용유무가 사용안함이면 손님의 수를 변경할 수 없다")
    @Test
    void changeNumberOfGuestsByUnUse() {
        OrderTable orderTable = orderTableService.create(createOrderTable("1번테이블"));

        orderTable.setOccupied(false);
        orderTable.setNumberOfGuests(10);

        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }
    //endregion

    //region [모든 주문 테이블 조회]
    @DisplayName("모든 테이블 조회가 가능하다")
    @Test
    void findAll() {
        orderTableService.create(createOrderTable("1번테이블"));
        orderTableService.create(createOrderTable("2번테이블"));
        orderTableService.create(createOrderTable("3번테이블"));

        List<OrderTable> orderTables = orderTableService.findAll();

        assertThat(orderTables).hasSize(3);
        assertThat(orderTables)
                .extracting(OrderTable::getName)
                .contains("1번테이블", "2번테이블", "3번테이블");
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

    private OrderTable createOrderTable(String name) {
        return createOrderTable(null, name, 0);
    }

    private OrderTable createOrderTable(UUID id, String name, int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
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

    private Order createUnnamedOrder(UUID orderTableId) {
        Product unnamedProduct = productRepository.save(createProduct(UUID.randomUUID(), "unnamed", BigDecimal.ZERO));
        MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(UUID.randomUUID(), "unnamed"));
        MenuProduct displayMenuProduct = createMenuProduct(unnamedProduct.getId(), unnamedProduct, 1);
        Menu unknownMenu = menuRepository.save(createMenu(UUID.randomUUID(), menuGroup, menuGroup.getId(), "unnamed", BigDecimal.ZERO, true, List.of(displayMenuProduct)));

        OrderLineItem orderLineItem = createOrderLineItem(unknownMenu.getId(), BigDecimal.ZERO, 1);
        return orderService.create(createEatInOrder(List.of(orderLineItem), orderTableId, LocalDateTime.now()));
    }
}