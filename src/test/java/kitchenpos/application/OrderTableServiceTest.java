package kitchenpos.application;

import kitchenpos.builder.OrderBuilder;
import kitchenpos.builder.OrderTableBuilder;
import kitchenpos.domain.*;
import kitchenpos.mock.MockOrderRepository;
import kitchenpos.mock.MockOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest {
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new MockOrderTableRepository();
        orderRepository = new MockOrderRepository(orderTableRepository);
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("이름으로 주문 테이블을 추가 후, 접객 인원을 0으로 초기화하고 공석으로 비운다.")
    @Test
    void create() {
        final OrderTable expected = OrderTableBuilder.anOrderTable().setName("1번 테이블").build();

        final OrderTable actual = orderTableService.create(expected);

        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(orderTableRepository.findById(actual.getId()).isPresent()).isTrue(),
                () -> assertThat(actual.getName()).isEqualTo(expected.getName()),
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isEmpty()).isTrue()
        );
    }

    @DisplayName("이름은 필수고, 빈 문자열이 아니어야 한다")
    @ParameterizedTest
    @NullAndEmptySource
    void create(final String name) {
        final OrderTable expected = OrderTableBuilder.anOrderTable().setName(name).build();

        assertThatThrownBy(() -> orderTableService.create(expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("특정 주문 테이블의 식별자로 주문 테이블을 공석이 아니도록 채운다")
    @Test
    void sit() {
        final OrderTable orderTable = orderTableRepository.save(OrderTableBuilder.anOrderTable().setEmpty(true).build());

        final OrderTable actual = orderTableService.sit(orderTable.getId());

        assertThat(actual.isEmpty()).isFalse();
    }

    @DisplayName("식별자로 특정 주문 테이블을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void sit(final UUID orderTableTd) {
        assertThatThrownBy(() -> orderTableService.sit(orderTableTd))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("특정 주문 테이블의 식별자로 주문 테이블을 공석으로 비운고, 접객 인원을 0으로 바꾼다")
    @Test
    void clear() {
        final OrderTable orderTable = orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setNumberOfGuests(1)
                .setEmpty(false)
                .build());
        orderRepository.save(OrderBuilder.anOrder()
                .setOrderTable(orderTable)
                .setType(OrderType.EAT_IN)
                .setStatus(OrderStatus.COMPLETED)
                .build());

        final OrderTable actual = orderTableService.clear(orderTable.getId());

        assertAll(
                () -> assertThat(actual.isEmpty()).isTrue(),
                () -> assertThat(actual.getNumberOfGuests()).isZero()
        );
    }

    @DisplayName("식별자로 특정 주문 테이블을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void clear(final UUID orderTableTd) {
        assertThatThrownBy(() -> orderTableService.clear(orderTableTd))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 테이블에서 받은 주문이 있다면 해당 주문이 완료 상태여야 한다")
    @Test
    void clearNotCompleteStatusOrder() {
        final OrderTable orderTable = orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setNumberOfGuests(1)
                .setEmpty(false)
                .build());
        orderRepository.save(OrderBuilder.anOrder()
                .setOrderTable(orderTable)
                .setType(OrderType.EAT_IN)
                .setStatus(OrderStatus.WAITING)
                .build());

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("특정 주문 테이블의 식별자와 바꿀 접객 인원으로 접객 인원을 바꾼다")
    @Test
    void changeNumberOfGuests() {
        final OrderTable orderTable = orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setNumberOfGuests(1)
                .setEmpty(false)
                .build());
        final OrderTable expected = OrderTableBuilder.anOrderTable().setNumberOfGuests(2).build();

        final OrderTable actual = orderTableService.changeNumberOfGuests(orderTable.getId(), expected);

        assertThat(actual.getNumberOfGuests()).isEqualTo(expected.getNumberOfGuests());
    }

    @DisplayName("접객 인원은 0 이상이어야 한다")
    @ParameterizedTest
    @ValueSource(ints = -1)
    void changeNumberOfGuests(final int numberOfGuests) {
        final OrderTable orderTable = orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setEmpty(false)
                .build());
        final OrderTable expected = OrderTableBuilder.anOrderTable().setNumberOfGuests(numberOfGuests).build();

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), expected))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("식별자로 특정 주문 테이블을 조회할 수 있어야 한다")
    @ParameterizedTest
    @NullSource
    void changeNumberOfGuests(final UUID orderTableId) {
        final OrderTable expected = OrderTableBuilder.anOrderTable().setNumberOfGuests(2).build();

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, expected))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 테이블은 공석이 아니어야 한다")
    @ParameterizedTest
    @ValueSource(booleans = true)
    void changeNumberOfGuests(final boolean empty) {
        final OrderTable orderTable = orderTableRepository.save(OrderTableBuilder.anOrderTable()
                .setEmpty(empty)
                .build());
        final OrderTable expected = OrderTableBuilder.anOrderTable().setNumberOfGuests(2).build();

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), expected))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블 전체 목록을 조회한다")
    @Test
    void findAll() {
        final int expected = 2;

        IntStream.range(0, expected)
                .forEach(index -> orderTableRepository.save(OrderTableBuilder.anOrderTable().build()));

        assertThat(orderTableService.findAll()).hasSize(expected);
    }
}
