package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;

    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new FakeOrderTableRepository();
        orderRepository = new FakeOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("새로운 주문 테이블을 추가할 수 있다.")
    @ValueSource(strings = {"주문 테이블 이름"})
    @ParameterizedTest
    void create(final String expectedName) {
        // given
        final OrderTable orderTable = createOrderTable(expectedName);

        // when
        final OrderTable actual = orderTableService.create(orderTable);

        // then
        assertAll(
                () -> assertThat(actual.getName())
                        .isEqualTo(expectedName),
                () -> assertThat(actual.getNumberOfGuests())
                        .isZero(),
                () -> assertThat(actual.isEmpty())
                        .isTrue());
    }

    @DisplayName("주문 테이블에 사람이 앉았음을 표시할 수 있다.")
    @Test
    void sit() {
        // given
        final OrderTable orderTable = createOrderTable("테이블 이름");
        orderTable.setEmpty(true);
        orderTableRepository.save(orderTable);

        // when
        orderTableService.sit(orderTable.getId());

        // then
        assertThat(orderTable.isEmpty())
                .isFalse();
    }

    @DisplayName("존재하지 않는 테이블은 앉았음을 표시할 수 없다.")
    @Test
    void sit_nonExist() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.sit(UUID.randomUUID()));
    }

    @DisplayName("주문 테이블이 비게 되었음을 표시할 수 있다.")
    @Test
    void clear() {
        // given
        final OrderTable orderTable = createOrderTable("테이블 이름");
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(123);
        orderTableRepository.save(orderTable);

        // when
        orderTableService.clear(orderTable.getId());

        // then
        assertAll(
                () -> assertThat(orderTable.isEmpty())
                        .isTrue(),
                () -> assertThat(orderTable.getNumberOfGuests())
                        .isZero());
    }

    @DisplayName("존재하지 않는 테이블은 비게 되었음을 표시할 수 없다.")
    @Test
    void clear_nonExist() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.clear(UUID.randomUUID()));
    }

    @DisplayName("주문 테이블에 앉아있는 손님의 숫자를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable orderTable = createOrderTable("테이블 이름");
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(123);
        orderTableRepository.save(orderTable);

        // when
        final int changedNumberOfGuests = 7;
        final OrderTable changedOrderTable = createOrderTable(orderTable.getName());
        changedOrderTable.setNumberOfGuests(changedNumberOfGuests);
        orderTableService.changeNumberOfGuests(orderTable.getId(), changedOrderTable);

        // then
        assertAll(
                () -> assertThat(orderTable.isEmpty())
                        .isFalse(),
                () -> assertThat(orderTable.getNumberOfGuests())
                        .isEqualTo(changedNumberOfGuests));
    }

    @DisplayName("주문 테이블에 앉아있는 손님의 숫자를 음수로 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_negative() {
        // given
        final OrderTable orderTable = createOrderTable("테이블 이름");
        orderTable.setEmpty(false);
        orderTable.setNumberOfGuests(123);
        orderTableRepository.save(orderTable);

        // when
        final int changedNumberOfGuests = -1;
        final OrderTable changedOrderTable = createOrderTable(orderTable.getName());
        changedOrderTable.setNumberOfGuests(changedNumberOfGuests);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changedOrderTable));
    }

    @DisplayName("존재하지 않는 주문 테이블에 앉아있는 손님의 숫자를 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_nonExist() {
        // given
        final OrderTable orderTable = createOrderTable("테이블 이름");

        // then
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("비어있는 테이블에 앉아있는 손님의 숫자를 변경할 수 없다.")
    @Test
    void changeNumberOfGuests_empty() {
        // given
        final OrderTable orderTable = createOrderTable("테이블 이름");
        orderTable.setEmpty(true);
        orderTableRepository.save(orderTable);

        // when
        final int changedNumberOfGuests = 123;
        final OrderTable changedOrderTable = createOrderTable(orderTable.getName());
        changedOrderTable.setNumberOfGuests(changedNumberOfGuests);

        // then
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changedOrderTable));
    }

    @DisplayName("주문 테이블들의 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        final List<String> expectedNames = Arrays.asList("테이블1", "테이블2", "테이블3");
        expectedNames.stream()
                .map(this::createOrderTable)
                .forEach(orderTableRepository::save);

        // when
        final List<String> actualNames = orderTableService.findAll().stream()
                .map(OrderTable::getName)
                .collect(Collectors.toList());

        // then
        assertThat(actualNames)
                .isEqualTo(expectedNames);
    }

    private OrderTable createOrderTable(final String name) {
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName(name);
        return orderTable;
    }
}
