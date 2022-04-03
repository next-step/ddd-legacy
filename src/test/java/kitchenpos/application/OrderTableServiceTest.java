package kitchenpos.application;

import static kitchenpos.application.OrderTableServiceFixture.orderTable;
import static kitchenpos.application.OrderTableServiceFixture.orderTables;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @DisplayName("테이블을 등록할 수 있다.")
    @Test
    void create() {
        //given
        OrderTable orderTable = orderTable();

        //when
        orderTableService.create(orderTable);

        //then
        verify(orderTableRepository).save(any());
    }

    @DisplayName("주문 테이블 이름은 비어 있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void can_not_be_empty_orderTableName(String orderTableName) {

        //given
        OrderTable orderTable = orderTable();
        orderTable.setName(orderTableName);

        //when

        //then
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> orderTableService.create(orderTable));
    }

    @DisplayName("테이블에 앉을 수 있다.")
    @Test
    void sit() {

        //given
        OrderTable orderTable = orderTable();
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        //when
        OrderTable result = orderTableService.sit(orderTable.getId());

        //then
        assertAll(
            () -> verify(orderTableRepository).findById(any()),
            () -> assertThat(result.isEmpty()).isFalse()
        );

    }

    @DisplayName("테이블을 비울 수 있다.")
    @Test
    void clear() {

        //given
        OrderTable orderTable = orderTable();
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).willReturn(false);

        //when
        OrderTable result = orderTableService.clear(orderTable.getId());

        //then
        assertAll(
            () -> verify(orderTableRepository).findById(any()),
            () -> assertThat(result.isEmpty()).isTrue()
        );

    }

    @DisplayName("테이블에 앉은 고객의 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {

        //given
        OrderTable orderTable = orderTable();
        orderTable.setNumberOfGuests(6);
        orderTable.setEmpty(false);
        given(orderTableRepository.findById(orderTable.getId())).willReturn(Optional.of(orderTable));

        //when
        OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

        //then
        assertAll(
            () -> verify(orderTableRepository).findById(any()),
            () -> assertThat(result.getNumberOfGuests()).isEqualTo(orderTable.getNumberOfGuests())
        );

    }

    @DisplayName("주문 테이블 목록을 조회할 수 있다. ")
    @Test
    void findAll() {

        //given
        List<OrderTable> orderTables = orderTables();
        given(orderTableService.findAll()).willReturn(orderTables);

        //when
        List<OrderTable> result = orderTableService.findAll();

        //then
        assertThat(result).hasSize(orderTables.size());
    }

}
