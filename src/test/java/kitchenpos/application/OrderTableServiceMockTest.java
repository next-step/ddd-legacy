package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static kitchenpos.fixture.OrderFixture.주문_테이블_생성;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceMockTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("주문 테이블을 생성한다")
    @Test
    void create() {
        주문_테이블을_생성한다("테이블A");
    }

    @DisplayName("주문 테이블 생성 시, 이름이 null 혹은 빈 값이면 생성을 실패한다")
    @NullAndEmptySource
    @ParameterizedTest
    void create_exception(String name) {
        //given
        OrderTable orderTable = 주문_테이블_생성(name);

        //when
        //then
        assertThatIllegalArgumentException().isThrownBy(() -> orderTableService.create(orderTable));
    }

    @DisplayName("주문 테이블을 사용중 상태로 변경한다")
    @Test
    void sit() {
        사용중인_주문_테이블을_생성한다("테이블A");
    }

    @DisplayName("존재하지 않는 주문 테이블은 사용중 상태로 변경할 수 없다")
    @Test
    void sit_exception() {
        //given
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
        then(orderTableRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 테이블을 미사용 상태로 변경한다")
    @Test
    void clear() {
        //given
        OrderTable orderTable = 주문_테이블을_생성한다("테이블A");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

        //when
        OrderTable clearedOrderTable = orderTableService.clear(UUID.randomUUID());

        //then
        assertThat(clearedOrderTable.isOccupied()).isFalse();
        assertThat(clearedOrderTable.getNumberOfGuests()).isZero();
        then(orderTableRepository).should(times(1)).findById(any());
        then(orderRepository).should(times(1)).existsByOrderTableAndStatusNot(any(), any());
    }

    @DisplayName("존재하지 않는 주문 테이블은 미사용 상태로 변경할 수 없다")
    @Test
    void clear_orderTable_exception() {
        // given
        OrderTable orderTable = 주문_테이블을_생성한다("테이블A");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

        // when
        // then
        assertThatIllegalStateException().isThrownBy(() -> orderTableService.clear(UUID.randomUUID()));
        then(orderTableRepository).should(times(1)).findById(any());
        then(orderRepository).should(times(1)).existsByOrderTableAndStatusNot(any(), any());
    }

    @DisplayName("주문이 완료되지 않은 주문 테이블은 미사용 상태로 변경할 수 없다")
    @Test
    void clear_order_exception() {
        // given
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID())).isInstanceOf(NoSuchElementException.class);
        then(orderTableRepository).should(times(1)).findById(any());
        then(orderRepository).should(never()).existsByOrderTableAndStatusNot(any(), any());
    }

    @DisplayName("주문 테이블의 인원 수를 변경한다")
    @Test
    void changeNumberOfGuests() {
        //given
        OrderTable orderTable = 주문_테이블_생성("테이블A", 5);
        OrderTable sittedOrderTable = 사용중인_주문_테이블을_생성한다("테이블A");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(sittedOrderTable));

        //when
        OrderTable changedOrderTable = orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable);

        //then
        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
        then(orderTableRepository).should(times(2)).findById(any());
    }

    @DisplayName("주문 테이블 인원 수 변경 시, 인원 수는 0명 미만이면 변경을 실패한다")
    @Test
    void changeNumberOfGuests_numberOfGuest_exception() {
        //given
        OrderTable orderTable = 주문_테이블_생성("테이블A", -1);

        //when
        //then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable));
        then(orderTableRepository).should(never()).findById(any());
    }

    @DisplayName("존재하지 않는 주문 테이블은 인원 수 변경이 불가하다")
    @Test
    void changeNumberOfGuests_orderTalbe_exception() {
        //given
        OrderTable orderTable = 주문_테이블_생성("테이블A", 5);
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        //when
        //then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable))
                .isInstanceOf(NoSuchElementException.class);
        then(orderTableRepository).should(times(1)).findById(any());
    }

    @DisplayName("사용중이 아닌 주문 테이블은 인원 수 변경이 불가하다")
    @Test
    void changeNumberOfGuests_occupied_exception() {
        //given
        OrderTable orderTable = 주문_테이블_생성("테이블A", 5);
        OrderTable clearOrderTable = 주문_테이블을_생성한다("테이블A");
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(clearOrderTable));

        //when
        //then
        assertThatIllegalStateException()
                .isThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable));
        then(orderTableRepository).should(times(1)).findById(any());
    }

    @DisplayName("주문 테이블 목록을 조회한다")
    @Test
    void findAll() {
        //given
        OrderTable orderTable = 주문_테이블_생성("테이블A");
        when(orderTableRepository.findAll()).thenReturn(Collections.singletonList(orderTable));

        //when
        List<OrderTable> orderTables = orderTableService.findAll();

        //then
        assertThat(orderTables.size()).isOne();
        assertThat(orderTables).contains(orderTable);
    }

    private OrderTable 주문_테이블을_생성한다(String name) {
        //given
        OrderTable orderTable = 주문_테이블_생성(name);
        when(orderTableRepository.save(any())).thenReturn(orderTable);

        //when
        OrderTable createdOrderTable = orderTableService.create(orderTable);

        //then
        then(orderTableRepository).should(times(1)).save(any());
        생성된_주문_테이블을_검증한다(orderTable, createdOrderTable);
        return createdOrderTable;
    }

    private OrderTable 사용중인_주문_테이블을_생성한다(String name) {
        OrderTable orderTable = 주문_테이블_생성(name);
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

        //when
        OrderTable sitedOrderTable = orderTableService.sit(UUID.randomUUID());

        //then
        assertThat(sitedOrderTable.isOccupied()).isTrue();
        then(orderTableRepository).should(times(1)).findById(any());
        return sitedOrderTable;
    }

    private void 생성된_주문_테이블을_검증한다(OrderTable orderTable, OrderTable createdOrderTable) {
        assertThat(createdOrderTable.getName()).isEqualTo(orderTable.getName());
        assertThat(createdOrderTable.getNumberOfGuests()).isZero();
        assertThat(createdOrderTable.isOccupied()).isFalse();
    }
}
