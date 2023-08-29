package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kitchenpos.fixture.OrderTableFixtures.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @Test
    void 주문_테이블은_등록할_수_있다() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", false, 2);
        given(orderTableRepository.save(any()))
                .willReturn(orderTable);

        //when
        OrderTable result = orderTableService.create(orderTable);

        //then
        assertThat(result.getName()).isEqualTo(orderTable.getName());
        assertThat(result.isOccupied()).isEqualTo(orderTable.isOccupied());
        assertThat(result.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @Test
    void 주문_테이블은_점유상태로_변경할_수_있다() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", false, 2);

        given(orderTableRepository.findById(any()))
                .willReturn(Optional.of(orderTable));

        //when
        OrderTable result = orderTableService.sit(orderTable.getId());

        //then
        assertThat(result.isOccupied()).isEqualTo(true);
    }

    @Test
    void 주문_테이블을_정리할_수_있다() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", false, 2);

        given(orderTableRepository.findById(any()))
                .willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                .willReturn(false);

        //when
        OrderTable result = orderTableService.clear(orderTable.getId());

        //then
        assertThat(result.getNumberOfGuests()).isEqualTo(0);
        assertThat(result.isOccupied()).isEqualTo(false);
    }

    @Test
    void 주문_테이블을_정리하려면_주문상태가_완료여야한다() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", false, 2);

        given(orderTableRepository.findById(any()))
                .willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
                .willReturn(true);

        //when, then
        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_테이블의_손님_수를_변경할_수_있다() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", true, 2);

        given(orderTableRepository.findById(any()))
                .willReturn(Optional.of(orderTable));

        //when
        OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        //then
        assertThat(result.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @Test
    void 손님_수를_변경하려면_0명이상이어야_한다() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", true, -1);

        //when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 손님_수를_변경하려면_점유상태어야_한다() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", false, 2);

        given(orderTableRepository.findById(any()))
                .willReturn(Optional.of(orderTable));

        //when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 모든_주문테이블을_조회할_수_있다() {
        //given
        OrderTable orderTable1 = createOrderTable("주문테이블1", false, 2);
        OrderTable orderTable2 = createOrderTable("주문테이블2", false, 2);

        List<OrderTable> orderTables = List.of(orderTable1, orderTable2);

        given(orderTableRepository.findAll())
                .willReturn(orderTables);

        //when
        List<OrderTable> result = orderTableService.findAll();

        //then
        assertThat(result.size()).isEqualTo(orderTables.size());
    }
}