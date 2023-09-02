package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @Test
    void 주문_테이블_등록_성공() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", false, 2);
        given(orderTableRepository.save(any()))
                .willReturn(orderTable);

        //when
        OrderTable result = orderTableService.create(orderTable);

        //then
        verify(orderTableRepository).save(any());
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(orderTable.getName());
        assertThat(result.isOccupied()).isEqualTo(orderTable.isOccupied());
        assertThat(result.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests());
    }

    @Test
    void 주문_테이블_점유상태로_변경_성공() {
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
    void 주문_테이블을_정리_성공() {
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
    void 주문상태가_완료여야_주문_테이블을_정리할_수_있다_다른상태라면_IllegalStateException_발생() {
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
    void 주문_테이블의_손님_수를_변경_성공() {
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
    void 손님_수_변경시_0명미만이라면_IllegalArgumentException_발생() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", true, -1);

        //when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 점유상태어야_손님_수를_변경할_수_있다_점유상태가_아니라면_IllegalStateException_발생() {
        //given
        OrderTable orderTable = createOrderTable("주문테이블", false, 2);

        given(orderTableRepository.findById(any()))
                .willReturn(Optional.of(orderTable));

        //when, then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 모든_주문테이블을_조회_성공() {
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