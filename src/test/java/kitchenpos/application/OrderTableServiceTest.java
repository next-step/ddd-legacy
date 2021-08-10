package kitchenpos.application;

import kitchenpos.DummyData;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest extends DummyData {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("테이블 생성")
    @Test
    void create() {
        OrderTable orderTable = orderTables.get(0);

        given(orderTableRepository.save(any())).willReturn(orderTable);

        OrderTable createOrderTable = orderTableService.create(orderTable);

        assertThat(createOrderTable).isNotNull();
    }

    @DisplayName("테이블 이름 없으면 예외")
    @Test
    void negativeTableName() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.create(new OrderTable()));
    }

    @DisplayName("테이블 활성화")
    @Test
    void sit() {
        OrderTable orderTable = orderTables.get(0);

        given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        OrderTable sit = orderTableService.sit(orderTable.getId());

        assertThat(sit.isEmpty()).isFalse();
    }

    @DisplayName("테이블 비활성화")
    @Test
    void clear() {
        OrderTable orderTable = orderTables.get(0);
        orderTable.setEmpty(TABLE_SIT);

        given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        OrderTable sit = orderTableService.clear(orderTable.getId());

        assertThat(sit.isEmpty()).isTrue();
    }

    @DisplayName("주문이 완료안된 테이블은 비활성화 불가")
    @Test
    void negativeClear() {
        OrderTable orderTable = orderTables.get(0);
        orderTable.setEmpty(TABLE_SIT);

        given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }

    @DisplayName("테이블 활성화/비활성화 시 테이블을 조회 못하면 예외 처리")
    @Test
    void negativeFindById() {
        assertThatThrownBy(() -> orderTableService.sit(null))
                .isInstanceOf(NoSuchElementException.class);

        assertThatThrownBy(() -> orderTableService.clear(null))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블 고객 수 변경")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void changeNumberOfGuests(int guestCount) {
        OrderTable orderTable = orderTables.get(0);
        orderTable.setEmpty(TABLE_SIT);
        orderTable.setNumberOfGuests(guestCount);

        given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        OrderTable changeOrderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        assertThat(changeOrderTable.getNumberOfGuests()).isEqualTo(guestCount);
    }

    @DisplayName("테이블 고객 수 0 미만 예외 처리")
    @Test
    void negativeNumberOfGuests() {
        OrderTable orderTable = orderTables.get(0);
        orderTable.setEmpty(TABLE_SIT);
        orderTable.setNumberOfGuests(-1);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("비활성화 테이블은 고객 수 변경 불가 ")
    @Test
    void negativeChangeNumberOfGuests() {
        OrderTable orderTable = orderTables.get(0);
        orderTable.setEmpty(TABLE_CLEAR);
        orderTable.setNumberOfGuests(1);

        given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
    }

    @DisplayName("테이블 내역 조회")
    @Test
    void findAll() {
        given(orderTableRepository.findAll()).willReturn(orderTables);

        List<OrderTable> findAll = orderTableService.findAll();

        verify(orderTableRepository).findAll();
        verify(orderTableRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(orderTables.containsAll(findAll)).isTrue(),
                () -> assertThat(orderTables.size()).isEqualTo(findAll.size())
        );
    }
}