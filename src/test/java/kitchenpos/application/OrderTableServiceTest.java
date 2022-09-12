package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.OrderFixture.eatInOrder;
import static kitchenpos.fixture.OrderTableFixture.orderTable;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableServiceTest {
    private OrderTableService orderTableService;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void beforeEach() {
        orderRepository = new InMemoryOrderRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Test
    @DisplayName("테이블 목록을 조회할 수 있다")
    void findOrderTables() {
        // given
        orderTableRepository.save(orderTable());

        // when
        final List<OrderTable> result = orderTableService.findAll();

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("테이블을 추가할 수 있다")
    void createOrderTables() {
        // given
        final OrderTable request = createOrderTableRequest();

        // when
        final OrderTable result = orderTableService.create(request);

        // then
        assertAll(
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo(request.getName()),
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(result.isOccupied()).isFalse()
        );
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름이 없거나, 이름에 빈값이 들어가 있으면 추가할 수 없다")
    void createOrderTablesNotName(final String input) {
        // given
        final OrderTable request = createOrderTableRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(
                () -> orderTableService.create(request)
        );
    }

    @Test
    @DisplayName("테이블에 착석할 수 있다")
    void sitOrderTable() {
        // given
        final UUID orderTableId = orderTableRepository.save(orderTable()).getId();

        // when
        final OrderTable sit = orderTableService.sit(orderTableId);

        // then
        assertThat(sit.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블이 존재하지 않으면 테이블에 착석할 수 없다")
    void sitOrderTableNotExist() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.sit(null));
    }

    @Test
    @DisplayName("테이블을 비울 수 있다")
    void clearOrderTable() {
        // given
        final UUID orderTableId = orderTableRepository.save(orderTable()).getId();

        // when
        final OrderTable result = orderTableService.clear(orderTableId);

        // then
        assertAll(
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(result.isOccupied()).isFalse()
        );
    }

    @Test
    @DisplayName("테이블이 존재하지 않으면 테이블을 비울 수 없다")
    void clearOrderTableNotExist() {
        assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderTableService.clear(null));
    }

    @Test
    @DisplayName("주문 테이블에 주문이 있고 주문의 상태가 완료된 상태가 아니라면 테이블을 비울 수 없다")
    void clearOrderTableNotOrder() {
        // given
        final OrderTable orderTable = orderTableRepository.save(orderTable());
        orderRepository.save(eatInOrder(OrderStatus.WAITING, orderTable));

        // when
        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }

    @Test
    @DisplayName("테이블에 앉은 손님의 수를 변경할 수 있다")
    void changeNumberOfGuests() {
        // given
        final UUID orderTableId = orderTableRepository.save(orderTable(true)).getId();
        final OrderTable request = changeNumberOfGuestsRequest();
        // when
        final OrderTable result = orderTableService.changeNumberOfGuests(orderTableId, request);

        // then
        assertAll(
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests()),
                () -> assertThat(result.isOccupied()).isTrue()
        );
    }

    @ParameterizedTest
    @ValueSource(ints = -1)
    @DisplayName("손님의 수가 0 미만이면 손님의 수를 변경할 수 없다")
    void changeNumberOfGuestsIsZero(int input) {
        // given
        final UUID orderTableId = orderTableRepository.save(orderTable(true)).getId();
        final OrderTable request = changeNumberOfGuestsRequest(input);

        // then
        assertThatIllegalArgumentException().isThrownBy(() ->
                orderTableService.changeNumberOfGuests(orderTableId, request)
        );
    }

    @Test
    @DisplayName("테이블이 존재하지 않으면 손님의 수를 변경할 수 없다")
    void changeNumberOfGuestsNotExistTable() {
        // given
        final OrderTable request = changeNumberOfGuestsRequest();

        // then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                orderTableService.changeNumberOfGuests(null, request)
        );
    }

    @Test
    @DisplayName("주문 테이블이 착석되지 않은 상태면 손님의 수를 변경할 수 없다")
    void changeNumberOfGuestsIsOrderTableOccupiedIsFalse() {
        // given
        final UUID orderTableId = orderTableRepository.save(orderTable()).getId();
        final OrderTable request = changeNumberOfGuestsRequest();

        // then
        assertThatIllegalStateException().isThrownBy(() ->
                orderTableService.changeNumberOfGuests(orderTableId, request)
        );
    }

    private OrderTable changeNumberOfGuestsRequest() {
        return changeNumberOfGuestsRequest(2);
    }

    private OrderTable changeNumberOfGuestsRequest(int numberOfGuests) {
        OrderTable orderTable = new OrderTable();
        orderTable.setNumberOfGuests(numberOfGuests);
        return orderTable;
    }

    private OrderTable createOrderTableRequest() {
        return createOrderTableRequest("1번");
    }

    private OrderTable createOrderTableRequest(String name) {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        return orderTable;
    }
}
