package kitchenpos.bo;

import kitchenpos.dao.OrderDao;
import kitchenpos.dao.OrderTableDao;
import kitchenpos.model.OrderTable;
import kitchenpos.support.OrderTableBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class TableBoTest extends MockTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private OrderTableDao orderTableDao;

    @InjectMocks
    private TableBo sut;

    @Test
    @DisplayName("주문 테이블 생성")
    void createOrderTable() {
        // given
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(OrderTableBuilder.orderTable().build());

        // when
        sut.create(OrderTableBuilder.orderTable().build());

        // then
        verify(orderTableDao).save(any(OrderTable.class));
        verifyNoInteractions(orderDao);
    }

    @Test
    @DisplayName("주문 테이블 목록 조회")
    void getOrderTables() {
        // given
        given(orderTableDao.findAll())
                .willReturn(Collections.singletonList(
                    OrderTableBuilder.orderTable().build())
                );

        // when
        sut.list();

        // then
        verify(orderTableDao).findAll();
    }

    @Test
    @DisplayName("테이블을 비움")
    void changeTableEmptyStatus() {
        // given
        OrderTableBuilder orderTableBuilder = OrderTableBuilder.orderTable()
                .withEmpty(false);

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTableBuilder.build()));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList()))
                .willReturn(false);
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(orderTableBuilder.build());

        // when
        sut.changeEmpty(1L, orderTableBuilder.withEmpty(true).build());

        // then
        verify(orderTableDao).findById(anyLong());
        verify(orderDao).existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList());
        verify(orderTableDao).save(any(OrderTable.class));
    }

    @Test
    @DisplayName("테이블을 비울 때 주문 테이블 정보를 찾지 못하면 예외 던짐")
    void shouldThrowExceptionWhenNotFoundOrderTable() {
        // given
        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> sut.changeEmpty(1L, OrderTableBuilder.EMPTY_ORDER_TABLE));

        // then
        verify(orderTableDao).findById(anyLong());
        verifyNoInteractions(orderDao);
    }

    @Test
    @DisplayName("테이블을 비울 때 주문 테이블이 그룹이면 예외 던짐")
    void shouldThrowExceptionWhenOrderTableHasTableGroup() {
        // given
        final OrderTable orderTable =  OrderTableBuilder.orderTable()
                .withEmpty(false)
                .withtTableGroupId(10L)
                .build();

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTable));

        // when
        assertThatThrownBy(() -> sut.changeEmpty(1L, OrderTableBuilder.EMPTY_ORDER_TABLE));

        // then
        verify(orderTableDao).findById(anyLong());
        verifyNoInteractions(orderDao);
    }

    @Test
    @DisplayName("테이블을 비울 때 주문의 상태가 COOKING, MEAL 이면 예외 던짐")
    void shouldThrowExceptionWhenCanNotChangeOrderStatuses() {
        // given
        final OrderTable orderTable =  OrderTableBuilder.orderTable()
                .withEmpty(false)
                .build();

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTable));
        given(orderDao.existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList()))
                .willReturn(true);

        // when
        assertThatThrownBy(() -> sut.changeEmpty(1L, OrderTableBuilder.EMPTY_ORDER_TABLE));

        // then
        verify(orderTableDao).findById(anyLong());
        verify(orderDao).existsByOrderTableIdAndOrderStatusIn(anyLong(), anyList());
    }

    @Test
    @DisplayName("손님 숫자 수정")
    void changeGuestNumbers() {
        // given
        final OrderTableBuilder orderTableBuilder =  OrderTableBuilder.orderTable()
                .withEmpty(false)
                .withNoOfGuests(10);

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTableBuilder.build()));
        given(orderTableDao.save(any(OrderTable.class)))
                .willReturn(orderTableBuilder.build());

        // when
        sut.changeNumberOfGuests(1L, orderTableBuilder.build());

        // then
        verify(orderTableDao).findById(anyLong());
        verify(orderTableDao).save(any(OrderTable.class));
        verifyNoInteractions(orderDao);
    }

    @Test
    @DisplayName("손님 숫자 수정 시 손님 숫자가 0보다 작으면 예외 던짐")
    void shouldThrowExceptionWhenNumberOfGuestsIsUnderZero() {
        // given
        final OrderTable orderTable = OrderTableBuilder.orderTable()
                .withNoOfGuests(-1)
                .build();

        // when
        assertThatThrownBy(() -> sut.changeNumberOfGuests(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verifyNoInteractions(orderTableDao);
        verifyNoInteractions(orderDao);
    }

    @Test
    @DisplayName("손님 숫자 수정 시 주문 테이블 못 찾으면 예외 던짐")
    void shouldThrowExceptionWithNotFoundOrderTable() {
        // given
        final OrderTable orderTable = OrderTableBuilder.orderTable()
                .withNoOfGuests(1)
                .build();

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> sut.changeNumberOfGuests(1L, orderTable))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(orderTableDao).findById(anyLong());
        verifyNoInteractions(orderDao);
    }

    @Test
    @DisplayName("손님 숫자 수정 시 주문 테이블이 비어있으면 예외 던짐")
    void shouldThrowExceptionWithNotEmptyTable() {
        // given
        final OrderTableBuilder orderTableBuilder = OrderTableBuilder.orderTable()
                .withNoOfGuests(1);

        given(orderTableDao.findById(anyLong()))
                .willReturn(Optional.of(orderTableBuilder.withEmpty(true).build()));

        // when
        assertThatThrownBy(() -> sut.changeNumberOfGuests(1L, orderTableBuilder.build()))
                .isInstanceOf(IllegalArgumentException.class);

        // then
        verify(orderTableDao).findById(anyLong());
        verifyNoInteractions(orderDao);
    }
}
