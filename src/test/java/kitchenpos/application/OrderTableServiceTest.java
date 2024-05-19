package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.fixture.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("사용자는 `주문 테이블`을 생성할 수 있다.")
    @Test
    void create() {
        // given
        final OrderTable request = new OrderTable();
        request.setName("테이블");

        when(orderTableRepository.save(any())).then(invocation -> {
            OrderTable savedOrderTable = invocation.getArgument(0);
            savedOrderTable.setId(UUID.randomUUID());
            return savedOrderTable;
        });

        // when
        OrderTable createdOrderTable = orderTableService.create(request);

        // then
        assertAll(
            () -> assertNotNull(createdOrderTable),
            () -> assertNotNull(createdOrderTable.getId()),
            () -> assertEquals(request.getName(), createdOrderTable.getName()),
            () -> assertEquals(0, createdOrderTable.getNumberOfGuests()),
            () -> assertFalse(createdOrderTable.isOccupied())
        );
    }

    @DisplayName("`주문 테이블`을 생성할 때 이름이 없으면 예외가 발생한다.")
    @Test
    void createWithEmptyName() {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setName("");

        // when & then
        assertThatThrownBy(() -> orderTableService.create(orderTable))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("사용자는 `주문 테이블`에 손님을 앉힐 수 있다.")
    @Test
    void sit() {
        // given
        final OrderTable orderTable = OrderTableFixture.createOrderTable();

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(
            Optional.of(orderTable));

        // when
        OrderTable sittingOrderTable = orderTableService.sit(orderTable.getId());

        // then
        assertAll(
            () -> assertNotNull(sittingOrderTable),
            () -> assertEquals(orderTable.getId(), sittingOrderTable.getId()),
            () -> assertTrue(sittingOrderTable.isOccupied())
        );
    }

    @DisplayName("사용자는 `주문 테이블`을 비울 수 있다.")
    @Test
    void clear() {
        // given
        final OrderTable orderTable = OrderTableFixture.createOrderTable();
        orderTable.setOccupied(true);

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(
            Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable,
            OrderStatus.COMPLETED)).thenReturn(false);

        // when
        OrderTable clearedOrderTable = orderTableService.clear(orderTable.getId());

        // then
        assertAll(
            () -> assertNotNull(clearedOrderTable),
            () -> assertEquals(orderTable.getId(), clearedOrderTable.getId()),
            () -> assertEquals(0, clearedOrderTable.getNumberOfGuests()),
            () -> assertFalse(clearedOrderTable.isOccupied())
        );
    }

    @DisplayName("`주문 테이블`을 비울 때 주문이 완료되지 않았으면 예외가 발생한다.")
    @Test
    void clearWithNotCompletedOrder() {
        // given
        final OrderTable orderTable = OrderTableFixture.createOrderTable();
        orderTable.setOccupied(true);

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(
            Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable,
            OrderStatus.COMPLETED)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("사용자는 `주문 테이블`의 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        // given
        final OrderTable orderTable = OrderTableFixture.createOrderTable();
        orderTable.setOccupied(true);

        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(4);

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(
            Optional.of(orderTable));

        // when
        OrderTable changedOrderTable = orderTableService.changeNumberOfGuests(orderTable.getId(),
            request);

        // then
        assertAll(
            () -> assertNotNull(changedOrderTable),
            () -> assertEquals(orderTable.getId(), changedOrderTable.getId()),
            () -> assertEquals(request.getNumberOfGuests(), changedOrderTable.getNumberOfGuests())
        );
    }

    @DisplayName("`주문 테이블`의 손님 수를 변경할 때 음식이 없으면 예외가 발생한다.")
    @Test
    void changeNumberOfGuestsWithEmptyOrder() {
        // given
        final OrderTable orderTable = OrderTableFixture.createOrderTable();

        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(4);

        when(orderTableRepository.findById(orderTable.getId())).thenReturn(
            Optional.of(orderTable));

        // when & then
        assertThatThrownBy(
            () -> orderTableService.changeNumberOfGuests(orderTable.getId(), request)
        ).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("`주문 테이블`의 손님 수를 변경할 때 손님 수가 음수이면 예외가 발생한다.")
    @Test
    void changeNumberOfGuestsWithNegativeNumberOfGuests() {
        // given
        final OrderTable request = new OrderTable();
        request.setNumberOfGuests(-1);

        // when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("`주문 테이블`을 조회할 수 있다")
    @Test
    void findAll() {
        // given
        final OrderTable orderTable = OrderTableFixture.createOrderTable();

        when(orderTableRepository.findAll()).thenReturn(List.of(orderTable));

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        assertAll(
            () -> assertNotNull(orderTables),
            () -> assertFalse(orderTables.isEmpty()),
            () -> assertEquals(1, orderTables.size()),
            () -> assertEquals(orderTable.getId(), orderTables.get(0).getId()),
            () -> assertEquals(orderTable.getName(), orderTables.get(0).getName()),
            () -> assertEquals(
                orderTable.getNumberOfGuests(),
                orderTables.get(0).getNumberOfGuests()
            ),
            () -> assertEquals(orderTable.isOccupied(), orderTables.get(0).isOccupied())
        );
    }

}
