package kitchenpos.application;

import kitchenpos.domain.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static kitchenpos.application.OrderFixture.createOrderRequest;
import static kitchenpos.application.OrderTableFixture.createOrderTableRequest;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("테이블을 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"테이블1, 4"})
    void create(final String name, final int numberOfGuests) {
        //given
        final OrderTable requestTable = createOrderTableRequest(name, numberOfGuests);
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(requestTable);

        //when
        final OrderTable actualRTable = orderTableService.create(requestTable);

        //then
        assertThat(actualRTable.getName()).isEqualTo(name);
    }

    @DisplayName("테이블 생성 시 이름이 비었다면 예외가 발생한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void create2(final String name) {
        //given
        final OrderTable requestTable = createOrderTableRequest(name);

        //when, then
        assertThatIllegalArgumentException().isThrownBy(() -> orderTableService.create(requestTable));

    }

    @DisplayName("테이블을 sit 상태로 변경할 수 있다.")
    @Test
    void sit() {
        //given
        final OrderTable orderTableRequest = createOrderTableRequest();
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(createOrderTableRequest());
        when(orderTableRepository.findById(any())).thenReturn(java.util.Optional.of(orderTableRequest));

        final OrderTable actual = orderTableRepository.save(orderTableRequest);

        //when
        final OrderTable seatedTable = orderTableService.sit(actual.getId());

        //then
        assertThat(seatedTable.isOccupied()).isTrue();

    }

    @DisplayName("테이블의 좌석을 clear 할 수 있다.")
    @Test
    void clear() {
        //given
        final OrderTable orderTable = createOrderTableRequest("테이블1", 4);
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(orderTable);
        when(orderTableRepository.findById(any())).thenReturn(java.util.Optional.of(orderTable));

        final Order requestOrder = createOrderRequest(orderTable, "서울");
        when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class))).thenReturn(false);
        final Order savedOrder = orderRepository.save(requestOrder);

        final OrderTable actual = orderTableRepository.save(orderTable);

        //when
        final OrderTable clearedTable = orderTableService.clear(actual.getId());

        //then
        assertThat(clearedTable.getNumberOfGuests()).isZero();
        assertThat(clearedTable.isOccupied()).isFalse();


    }

    @DisplayName("주문이 complete면 테이블의 좌석을 clear 할 때 예외가 발생한다.")
    @Test
    void clear2() {
        //given
        final OrderTable orderTable = createOrderTableRequest("테이블1", 4, true);
        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(orderTable);
        when(orderTableRepository.findById(any())).thenReturn(java.util.Optional.of(orderTable));
        final Order requestOrder = createOrderRequest(orderTable, OrderStatus.COMPLETED);
        when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class))).thenReturn(true);
        when(orderRepository.save(any(Order.class))).thenReturn(requestOrder);
        orderRepository.save(requestOrder);

        final OrderTable actual = orderTableRepository.save(orderTable);

        //when, then
        assertThatIllegalStateException().isThrownBy(() -> orderTableService.clear(actual.getId()));

    }

    @DisplayName("테이블의 손님 수를 변경할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        //given
        final OrderTable targetOrdertable = createOrderTableRequest("테이블1", 4, true);
        final OrderTable requestOrderTable = createOrderTableRequest(6);
        when(orderTableRepository.findById(any())).thenReturn(java.util.Optional.of(targetOrdertable));

        //when
        final OrderTable actualOrderTable = orderTableService.changeNumberOfGuests(targetOrdertable.getId(), requestOrderTable);

        //then
        assertThat(actualOrderTable.getNumberOfGuests()).isEqualTo(6);
    }

    @DisplayName("테이블의 손님 수를 음수로 변경 시 예외가 발생한다.")
    @Test
    void changeNumberOfGuests2() {
        //given
        final OrderTable targetOrdertable = createOrderTableRequest("테이블1", 4, true);
        final OrderTable requestOrderTable = createOrderTableRequest(-1);

        //when, then
        assertThatIllegalArgumentException().isThrownBy(() -> orderTableService.changeNumberOfGuests(targetOrdertable.getId(), requestOrderTable));
    }

    @DisplayName("테이블의 손님 수를 변경 시 테이블이 비어있으면 예외가 발생한다..")
    @Test
    void changeNumberOfGuests3() {
        //given
        final OrderTable targetOrdertable = createOrderTableRequest("테이블1", 4, false);
        final OrderTable requestOrderTable = createOrderTableRequest(3);
        when(orderTableRepository.findById(any())).thenReturn(java.util.Optional.of(targetOrdertable));

        //when, then
        assertThatIllegalStateException().isThrownBy(() -> orderTableService.changeNumberOfGuests(targetOrdertable.getId(), requestOrderTable));
    }

    @DisplayName("모든 테이블을 조회할 수 있다.")
    @Test
    void findAll() {
        //given
        final OrderTable orderTable1 = createOrderTableRequest("테이블1", 4);
        final OrderTable orderTable2 = createOrderTableRequest("테이블2", 4);
        when(orderTableRepository.findAll()).thenReturn(List.of(orderTable1, orderTable2));

        //when
        List<OrderTable> actual = orderTableService.findAll();

        //then
        assertThat(actual).hasSize(2);
    }
}