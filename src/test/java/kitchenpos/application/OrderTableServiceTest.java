package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static kitchenpos.stub.OrderTableStub.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("새 주문테이블을 등록할 수 있다.")
    @Test
    void createNewOrderTable() {
        //given
        OrderTable newOrderTable = generateEmptyOrderTable();
        when(orderTableRepository.save(any())).thenReturn(newOrderTable);

        //when
        OrderTable result = orderTableService.create(newOrderTable);

        //then
        assertThat(result).isEqualTo(newOrderTable);
    }

    @DisplayName("주문테이블 이름은 빈 값일 수 없다.")
    @Test
    void notAllowEmptyName() {
        //given
        OrderTable emptyNameOrderTable = generateEmptyNameOrderTable();

        //when & then
        assertThatThrownBy(() -> orderTableService.create(emptyNameOrderTable)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("이미 등록된 주문테이블에 손님이 사용중이라는 표시를 할 수 있다.")
    @Test
    void canSetNotEmptyCreatedOrderTable() {
        //given
        OrderTable createdTable = generateEmptyOrderTable();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(createdTable));

        //when
        OrderTable result = orderTableService.sit(createdTable.getId());

        //then
        assertThat(result.isEmpty()).isFalse();
    }

    @DisplayName("이미 등록된 주문테이블에 빈 테이블이라는 표시를 할 수 있다.")
    @Test
    void canSetEmptyCreatedOrderTable() {
        //given
        OrderTable createdTable = generateNotEmptyForTwinOrderTable();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(createdTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(Boolean.FALSE);

        //when
        OrderTable result = orderTableService.clear(createdTable.getId());

        //then
        assertAll(
                () -> assertThat(result.isEmpty()).isTrue(),
                () -> assertThat(result.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("빈 테이블 표시는 완료되지 않은 주문이 있는 테이블에는 할 수 없다.")
    @Test
    void canNotSetEmptyHavingInCompleteOrderOrderTable() {
        //given
        OrderTable createdTable = generateNotEmptyForTwinOrderTable();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(createdTable));
        when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(Boolean.TRUE);

        //when & then
        assertThatThrownBy(() -> orderTableService.clear(createdTable.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("이미 등록된 주문테이블에 테이블에 앉은 손님 수를 기록할 수 있다.")
    @Test
    void canSetNumberOfGuestsCreatedOrderTable() {
        //given
        OrderTable createdTable = generateNotEmptyForTwinOrderTable();
        OrderTable toChangeRequest = generateChangingNumberOfGuestsToFourRequest();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(createdTable));

        //when
        OrderTable result = orderTableService.changeNumberOfGuests(createdTable.getId(), toChangeRequest);

        assertThat(result.getNumberOfGuests()).isEqualTo(toChangeRequest.getNumberOfGuests());
    }

    @DisplayName("기록하는 손님 수는 0 명 이상이어야 한다.")
    @Test
    void mustBePositiveNumberNumberOfGuests() {
        //given
        OrderTable createdTable = generateNotEmptyForTwinOrderTable();
        OrderTable negativeNumberRequest = generateChangingNumberOfGuestsToNegativeNumberRequest();

        //when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(createdTable.getId(), negativeNumberRequest)).isInstanceOf(IllegalArgumentException.class);

    }

    @DisplayName("빈 테이블에는 손님 수를 기록할 수 없다.")
    @Test
    void mustBeNotEmptyToChangeNumberOfGuests() {
        //given
        OrderTable emptyOrderTable = generateEmptyOrderTable();
        OrderTable toChangeRequest = generateChangingNumberOfGuestsToFourRequest();
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(emptyOrderTable));

        //when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(emptyOrderTable.getId(), toChangeRequest)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("전체 주문테이블을 조회할 수 있다.")
    @Test
    void canFindAllOrderTables() {
        //given
        List<OrderTable> allOrderTables = new ArrayList<>();
        allOrderTables.add(generateEmptyOrderTable());
        allOrderTables.add(generateNotEmptyForTwinOrderTable());
        when(orderTableRepository.findAll()).thenReturn(allOrderTables);

        //when
        List<OrderTable> results = orderTableService.findAll();

        //then
        assertThat(results).containsExactlyElementsOf(allOrderTables);
    }

}