package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @MockBean
    @Autowired
    OrderTableRepository orderTableRepository;

    @MockBean
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderTableService orderTableService;

    @DisplayName("가게 테이블을 생성할 수 있습니다.")
    @Test
    void createOrderTable() {
        when(orderTableRepository.save(any(OrderTable.class))).then(returnsFirstArg());

        final OrderTable orderTable = orderTableService.create(orderTable(
                DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, DEFAULT_OCCUPIED
        ));

        assertAll(
                () -> assertNotNull(orderTable.getId()),
                () -> assertThat(orderTable.getName()).isEqualTo(DEFAULT_ORDER_TABLE_NAME),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(DEFAULT_NUMBER_OF_GUESTS),
                () -> assertThat(orderTable.isOccupied()).isFalse()
        );
    }

    @DisplayName("가게 테이블의 이름이 이름이 없거나 비어있으면 가게 테이블을 생성할 수 없습니다.")
    @ParameterizedTest(name = "입력값 {0}")
    @NullAndEmptySource
    void createOrderTableWithEmptyName(final String name) {
        final OrderTable orderTable = orderTable(name, DEFAULT_NUMBER_OF_GUESTS, DEFAULT_OCCUPIED);

        assertThatThrownBy(() -> orderTableService.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("가게 테이블에 앉을 수 있습니다.")
    @Test
    void sitOrderTable() {
        final OrderTable orderTable = orderTable();
        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderTableRepository.save(any(OrderTable.class))).then(returnsFirstArg());

        final OrderTable actual = orderTableService.sit(orderTable.getId());

        assertThat(actual.isOccupied()).isTrue();
    }

    @DisplayName("가게 테이블이 없을 경우 가게 테이블에 앉을 수 없습니다.")
    @Test
    void sitOrderTableWithNonExistOrderTable() {
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderTableService.sit(createOrderTableId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("가게 테이블을 비울 수 있습니다.")
    @Test
    void clearOrderTable() {
        final OrderTable orderTable = orderTable(
                createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, true
        );
        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(false);
        when(orderTableRepository.save(any(OrderTable.class))).then(returnsFirstArg());

        final OrderTable actual = orderTableService.clear(orderTable.getId());

        assertAll(
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isOccupied()).isFalse()
        );
    }

    @DisplayName("가게 테이블이 모든 주문이 완료되지 않았을 경우 가게 테이블을 비울 수 없습니다.")
    @Test
    void clearOrderTableWithNonCompletedOrder() {
        final OrderTable orderTable = orderTable(
                createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, true
        );
        when(orderTableRepository.findById(orderTable.getId())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).thenReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("가게 테이블의 손님 수를 변경할 수 있습니다.")
    @Test
    void changeNumberOfGuests() {
        final OrderTable existedOrderTable = orderTable(
                createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, true
        );
        when(orderTableRepository.findById(existedOrderTable.getId())).thenReturn(Optional.of(existedOrderTable));
        when(orderTableRepository.save(any(OrderTable.class))).then(returnsFirstArg());
        final OrderTable renewedOrderTable = orderTable(
                existedOrderTable.getId(), existedOrderTable.getName(), 1, existedOrderTable.isOccupied()
        );
        final OrderTable actual = orderTableService.changeNumberOfGuests(existedOrderTable.getId(), renewedOrderTable);

        assertThat(actual.getNumberOfGuests()).isEqualTo(1);
    }

    @DisplayName("가게 테이블이 비어있을 경우 손님 수를 변경할 수 없습니다.")
    @Test
    void changeNumberOfGuestsWhenOrderTableIsEmpty() {
        final OrderTable existedOrderTable = orderTable(
                createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, DEFAULT_OCCUPIED
        );
        when(orderTableRepository.findById(existedOrderTable.getId())).thenReturn(Optional.of(existedOrderTable));
        final OrderTable renewedOrderTable = orderTable(
                existedOrderTable.getId(), existedOrderTable.getName(), 1, existedOrderTable.isOccupied()
        );

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(existedOrderTable.getId(), renewedOrderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("가게 테이블이 없을 경우 손님 수를 변경할 수 없습니다.")
    @Test
    void changeNumberOfGuestsWhenOrderTableIsNotExist() {
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
        final OrderTable renewedOrderTable = orderTable(
                createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, 1, true
        );

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(createOrderTableId(), renewedOrderTable))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("가게 테이블의 손님 수를 0명 미만으로 변경할 수 없습니다.")
    @Test
    void changeNumberOfGuestsWhenNumberOfGuestsIsNegative() {
        final OrderTable existedOrderTable = orderTable(
                createOrderTableId(), DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, true
        );
        when(orderTableRepository.findById(existedOrderTable.getId())).thenReturn(Optional.of(existedOrderTable));
        final OrderTable renewedOrderTable = orderTable(
                existedOrderTable.getId(), existedOrderTable.getName(), -1, existedOrderTable.isOccupied()
        );

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(existedOrderTable.getId(), renewedOrderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
