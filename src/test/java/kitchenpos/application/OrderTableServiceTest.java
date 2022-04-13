package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static kitchenpos.MenuFixture.menu;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문테이블을 생성한다.")
    @Test
    void createTable() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName("1번 테이블");

        OrderTable createdOrderTable = orderTableService.create(orderTable);

        assertAll(
                () -> assertThat(createdOrderTable).isNotNull(),
                () -> assertThat(createdOrderTable.getId()).isNotNull(),
                () -> assertThat(createdOrderTable.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(createdOrderTable.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문테이블의 이름은 필수로 지정해야한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void necessaryTableName(String tableName) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(tableName);

        assertThatThrownBy(
                () -> orderTableService.create(orderTable)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문테이블을 착석상태로 변경한다.")
    @Test
    void sit() {
        OrderTable savedOrderTable = saveOrderTable("1번테이블");

        OrderTable orderTable = orderTableService.sit(savedOrderTable.getId());

        assertThat(orderTable.isEmpty()).isFalse();
    }

    @DisplayName("주문테이블을 빈테이블로 변경한다.")
    @Test
    void clear() {
        OrderTable savedOrderTable = saveOrderTable("1번테이블", 4, false);

        OrderTable orderTable = orderTableService.clear(savedOrderTable.getId());

        assertAll(
                () -> assertThat(orderTable.isEmpty()).isTrue(),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("완료되지 않은 주문이 존재하면 빈테이블로 변경할수 없다.")
    @Test
    void unableClear() {
        OrderTable savedOrderTable = saveOrderTable("1번테이블", 4, false);
        saveOrderTargetTable(savedOrderTable);

        assertThatThrownBy(
                () -> orderTableService.clear(savedOrderTable.getId())
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블의 손님수를 변경한다.")
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void changeNumberOfGuests(int numberOfGuests) {
        OrderTable savedOrderTable = saveOrderTable("1번 테이블", 1, false);
        savedOrderTable.setNumberOfGuests(numberOfGuests);

        OrderTable orderTable = orderTableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable);

        assertThat(orderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @DisplayName("손님수 변경시 사람수는 0명 이상이여야 한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -3})
    void unableNumberOfGuests(int numberOfGuests) {
        OrderTable savedOrderTable = saveOrderTable("1번 테이블", 1, false);
        savedOrderTable.setNumberOfGuests(numberOfGuests);

        assertThatThrownBy(
                () -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("빈 테이블은 사람수를 변경할 수 없다.")
    @ParameterizedTest
    @ValueSource(ints = {2, 4})
    void unableChangeNumberOfGuestsForEmptyTable(int numberOfGuests) {
        OrderTable savedOrderTable = saveOrderTable("1번 테이블", 0, true);
        savedOrderTable.setNumberOfGuests(numberOfGuests);

        assertThatThrownBy(
                () -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), savedOrderTable)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블을 조회한다.")
    @Test
    void findAll() {
        saveOrderTable("1번 테이블");
        saveOrderTable("2번 테이블");

        List<OrderTable> orderTables = orderTableService.findAll();

        assertThat(orderTables).hasSize(2);

    }

    public OrderTable saveOrderTable(String name) {
        return orderTableRepository.save(saveOrderTable(name, 0, true));
    }

    public OrderTable saveOrderTable(String name, int numberOfGuests, boolean empty) {
        OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);

        return orderTableRepository.save(orderTable);
    }

    public void saveOrderTargetTable(OrderTable savedOrderTable) {
        saveOrder(createEatInOrder(null, savedOrderTable, Arrays.asList(createOrderLineItem(menu(), 1))));
    }

    private OrderLineItem createOrderLineItem(Menu singleMenu, int quantity) {
        OrderLineItem orderLineItem = new OrderLineItem();
        orderLineItem.setMenuId(singleMenu.getId());
        orderLineItem.setQuantity(quantity);
        orderLineItem.setPrice(singleMenu.getPrice());
        return orderLineItem;
    }

    public Order createEatInOrder(OrderStatus status, OrderTable savedOrderTable, List<OrderLineItem> orderLineItems) {
        return createOrder(status, OrderType.EAT_IN, savedOrderTable.getId(), null, orderLineItems);
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    private Order createOrder(OrderStatus status, OrderType orderType, UUID savedOrderTableId, String address, List<OrderLineItem> orderLineItems) {
        Order order = new Order();
        order.setStatus(status);
        order.setType(orderType);
        order.setOrderTableId(savedOrderTableId);
        order.setDeliveryAddress(address);
        order.setOrderLineItems(orderLineItems);
        return order;
    }
}
