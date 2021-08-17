package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.InMemoryOrderRepository;
import kitchenpos.domain.InMemoryOrderTableRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @DisplayName("주문테이블을 등록할 수 있다.")
    @Test
    void create() {
        // given
        final OrderTable expected = createOrderTableRequest("1번");

        // when
        final OrderTable actual = orderTableService.create(expected);

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isNotNull(),
            () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests()),
            () -> assertThat(actual.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문테이블의 이름이 올바르지 않으면 등록할 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void create_InvalidName(final String name) {
        // given
        final OrderTable expected = createOrderTableRequest(name);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.create(expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문테이블을 제공할 수 있다.")
    @Test
    void sit() {
        // given
        final OrderTable original = orderTableRepository.save(
            createOrderTable("1번", 4, true)
        );

        // when
        final OrderTable actual = orderTableService.sit(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getName()).isEqualTo(original.getName()),
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(original.getNumberOfGuests()),
            () -> assertThat(actual.isEmpty()).isFalse()
        );
    }

    @DisplayName("등록되어 있지 않은 주문테이블을 제공할 수 없다.")
    @NullSource
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void sit_UnregisteredOrderTable(final UUID id) {
        assertThatThrownBy(() -> orderTableService.sit(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문테이블을 회수할 수 있다.")
    @Test
    void clear() {
        // given
        final OrderTable original = orderTableRepository.save(
            createOrderTable("1번", 4, true)
        );
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(original);
        order.setStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);

        // when
        final OrderTable actual = orderTableService.clear(original.getId());

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getName()).isEqualTo(original.getName()),
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(0),
            () -> assertThat(actual.isEmpty()).isTrue()
        );
    }

    @DisplayName("등록되어 있지 않은 주문테이블을 회수할 수 없다.")
    @NullSource
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void clear_UnregisteredOrderTable(final UUID id) {
        assertThatThrownBy(() -> orderTableService.sit(id))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문이 완료되지 않은 주문테이블을 회수할 수 없다.")
    @Test
    void clear_InvalidOrderState() {
        // given
        final OrderTable original = orderTableRepository.save(
            createOrderTable("1번", 4, true)
        );
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderTable(original);
        order.setStatus(OrderStatus.WAITING);
        orderRepository.save(order);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.clear(original.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블의 정원을 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable original = orderTableRepository.save(
            createOrderTable("1번", 4, false)
        );
        final OrderTable expected = createOrderTableRequest(2);

        // when
        final OrderTable actual = orderTableService.changeNumberOfGuests(
            original.getId(),
            expected
        );

        // then
        assertThat(actual).isNotNull();
        assertAll(
            () -> assertThat(actual.getId()).isEqualTo(original.getId()),
            () -> assertThat(actual.getName()).isEqualTo(original.getName()),
            () -> assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests()),
            () -> assertThat(actual.isEmpty()).isFalse()
        );
    }

    @DisplayName("주문테이블의 정원이 올바르지 않으면 변경할 수 없다.")
    @ValueSource(ints = -4)
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void changeNumberOfGuests_InvalidNumberOfGuests(final int numberOfGuests) {
        // given
        final OrderTable original = orderTableRepository.save(
            createOrderTable("1번", 4, false)
        );
        final OrderTable expected = createOrderTableRequest(numberOfGuests);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(original.getId(), expected))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록되어 있지 않은 주문테이블의 정원을 변경할 수 없다.")
    @NullSource
    @ValueSource(strings = "00000000-000-0000-0000-000000000000")
    @ParameterizedTest(name = "{displayName} [{index}] {argumentsWithNames}")
    void changeNumberOfGuests_UnregisteredOrderTable(final UUID id) {
        // given
        final OrderTable expected = createOrderTableRequest(2);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(id, expected))
            .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("회수된 주문테이블의 정원을 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_EmptyOrderTable() {
        // given
        final OrderTable original = orderTableRepository.save(
            createOrderTable("1번", 4, true)
        );
        final OrderTable expected = createOrderTableRequest(2);

        // when
        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(original.getId(), expected))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문테이블의 목록을 조회할 수 있다.")
    @Test
    public void findAll() {
        // given
        orderTableRepository.save(createOrderTable("1번", 4, true));
        orderTableRepository.save(createOrderTable("2번", 4, true));

        // when
        final List<OrderTable> actual = orderTableService.findAll();

        // then
        assertThat(actual).hasSize(2);
    }

    private OrderTable createOrderTableRequest(final String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }

    private OrderTable createOrderTableRequest(final int numberOfGuests) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    private OrderTable createOrderTable(
        final String name,
        final int numberOfGuests,
        final boolean empty
    ) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setEmpty(empty);
        return orderTable;
    }
}
