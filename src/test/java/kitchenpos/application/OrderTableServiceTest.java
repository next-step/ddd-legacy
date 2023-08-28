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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.OrderTableFixture.TEST_ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    @InjectMocks
    private OrderTableService orderTableService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderTableRepository orderTableRepository;

    @Test
    @DisplayName("새로운_주문_테이블을_등록한다")
    void createOrderTable() {
        // given
        OrderTable orderTableRequest = TEST_ORDER_TABLE();
        given(orderTableRepository.save(any(OrderTable.class))).willReturn(orderTableRequest);

        // when
        OrderTable orderTable = orderTableService.create(orderTableRequest);

        // then
        verify(orderTableRepository, times(1)).save(any(OrderTable.class));
        assertThat(orderTable.getNumberOfGuests()).isZero();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름은_비어있을_수_없다")
    void nameNotEmpty(String input) {
        // given
        OrderTable nameNull = TEST_ORDER_TABLE();

        // when
        nameNull.setName(input);

        // then
        assertThatThrownBy(() -> orderTableService.create(nameNull))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블에_손님을_채운다")
    void sitTest() {
        // given
        OrderTable orderTable = TEST_ORDER_TABLE();
        UUID orderTableId = orderTable.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

        // when
        OrderTable sitTable = orderTableService.sit(orderTableId);

        // then
        assertThat(sitTable.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블에_있던_손님이_나가고_다시_손님을_받을_수_있는_상태로_변경한다")
    void clearTableTest() {
        // given
        OrderTable orderTable = TEST_ORDER_TABLE();
        UUID orderTableId = orderTable.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .willReturn(false);

        // when
        OrderTable clearedTable = orderTableService.clear(orderTableId);

        // then
        verify(orderRepository, times(1))
                .existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED);
        assertThat(clearedTable.isOccupied()).isFalse();
        assertThat(clearedTable.getNumberOfGuests()).isZero();
    }

    @Test
    @DisplayName("주문이_완료되지_않았다면_테이블을_정리할_수_없다")
    void clearStatusShouldCompleted() {
        // given
        OrderTable orderTable = TEST_ORDER_TABLE();
        UUID orderTableId = orderTable.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

        // when
        given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED))
                .willReturn(true);
        // then
        assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("테이블에_인원_수를_변경한다")
    void changeNumberOfGuest() {
        // given
        OrderTable orderTable = TEST_ORDER_TABLE();
        UUID orderTableId = orderTable.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

        // when
        orderTable.setOccupied(true);
        orderTable.setNumberOfGuests(15);
        OrderTable clearedTable = orderTableService.changeNumberOfGuests(orderTableId, orderTable);

        // then
        assertThat(clearedTable.isOccupied()).isTrue();
        assertThat(clearedTable.getNumberOfGuests()).isEqualTo(15);
    }

    @Test
    @DisplayName("변경할_인원_수는_0명_이상이어야_한다")
    void guestNumberShouldGreaterThanZero() {
        // given
        OrderTable orderTable = TEST_ORDER_TABLE();
        UUID orderTableId = orderTable.getId();

        // when
        orderTable.setNumberOfGuests(-1);

        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("사용가능한_테이블만_인원_수를_변경_할_수_있다")
    void changeGuestNumberShouldOccupiedTrue() {
        // given
        OrderTable orderTable = TEST_ORDER_TABLE();
        UUID orderTableId = orderTable.getId();
        given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

        // when
        orderTable.setNumberOfGuests(13);
        orderTable.setOccupied(false);

        // then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("모든_주문_테이블_정보를_가져온다")
    void findAllTest() {
        // given
        OrderTable orderTable = TEST_ORDER_TABLE();
        given(orderTableRepository.findAll()).willReturn(List.of(orderTable, orderTable));

        // when
        List<OrderTable> orderTables = orderTableService.findAll();

        // then
        verify(orderTableRepository, times(1)).findAll();
        assertThat(orderTables).hasSize(2);
        assertThat(orderTables).containsExactly(orderTable, orderTable);
    }
}