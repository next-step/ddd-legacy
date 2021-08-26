package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
    @InjectMocks
    private OrderTableService orderTableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("주문 테이블 추가 - 성공")
    void createOrderTable() {
        // given
        OrderTable mockOrderTable = generateOrderTable(UUID.randomUUID());

        // mocking
        given(orderTableRepository.save(any())).willReturn(mockOrderTable);

        // when
        OrderTable newOrderTable = orderTableService.create(mockOrderTable);

        // then
        assertThat(newOrderTable.getId()).isNotNull();
        assertThat(newOrderTable.getName()).isEqualTo(mockOrderTable.getName());
    }

    private OrderTable generateOrderTable(UUID id) {
        OrderTable OrderTable = new OrderTable();
        OrderTable.setId(id);
        OrderTable.setName("OrderTable 1");
        return OrderTable;
    }

    @Test
    @DisplayName("주문 테이블 추가 - 실패: 빈 입력 값")
    void addOrderTable_BadRequest_Empty_Input() {
        // given
        OrderTable OrderTable = generateOrderTable(UUID.randomUUID());

        // when
        OrderTable.setName(null);

        // then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderTableService.create(OrderTable));
    }

    @Test
    @DisplayName("테이블 착석 - 성공")
    void sitOrderTable() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        OrderTable sit = orderTableService.sit(orderTable.getId());

        // then
        assertThat(sit.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("테이블 착석 - 실패: 잘못된 id")
    void sitOrderTable_NoSuchElementException() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderTableService.sit(orderTable.getId()));
    }

    @Test
    @DisplayName("테이블 비우기 - 성공")
    void clearOrderTable() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(false);

        // when
        OrderTable clear = orderTableService.clear(orderTable.getId());

        // then
        assertThat(clear.isEmpty()).isTrue();
        assertThat(clear.getNumberOfGuests()).isEqualTo(0);
    }

    @Test
    @DisplayName("테이블 비우기 - 실패: 잘못된 id")
    void clearOrderTable_NoSuchElementException() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }

    @Test
    @DisplayName("테이블 비우기 - 실패: 완료되지 않은 주문 존재")
    void clearOrderTable_IllegalStateException_IncompleteOrderExists() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), any())).willReturn(true);

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }

    @Test
    @DisplayName("테이블 손님 수 변경 - 성공")
    void orderTableChangeNumberOfGuests() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());
        orderTable.setEmpty(false);

        OrderTable request = generateOrderTable(UUID.randomUUID());
        request.setId(orderTable.getId());
        request.setNumberOfGuests(5);

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        OrderTable changeNumberOfGuests = orderTableService.changeNumberOfGuests(orderTable.getId(), request);

        // then
        assertThat(changeNumberOfGuests.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
    }

    @Test
    @DisplayName("테이블 손님 수 변경 - 실패: 손님수가 0 보다 작다")
    void orderTableChangeNumberOfGuests_IllegalArgumentException_Invalid_Min_numberOfGuests() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());
        orderTable.setEmpty(false);

        OrderTable request = generateOrderTable(UUID.randomUUID());
        request.setId(orderTable.getId());
        request.setNumberOfGuests(-1);

        // when then
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request));
    }

    @Test
    @DisplayName("테이블 손님 수 변경 - 실패: 잘못된 order table id")
    void orderTableChangeNumberOfGuests_NoSuchElementException_Invalid_orderTableId() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());
        orderTable.setEmpty(false);

        OrderTable request = generateOrderTable(UUID.randomUUID());
        request.setId(orderTable.getId());
        request.setNumberOfGuests(5);

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        // when then
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request));
    }

    @Test
    @DisplayName("테이블 손님 수 변경 - 실패: 빈 테이블")
    void orderTableChangeNumberOfGuests_NoSuchElementException_Table_Guest_Empty() {
        // given
        OrderTable orderTable = generateOrderTable(UUID.randomUUID());
        orderTable.setEmpty(true);

        OrderTable request = generateOrderTable(UUID.randomUUID());
        request.setId(orderTable.getId());
        request.setNumberOfGuests(5);

        // mocking
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when then
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request));
    }

    @Test
    @DisplayName("모든 주문 테이블 조회")
    void findAllOrderTable() {
        // given
        int size = 10;
        List<OrderTable> mockOrderTables = generateOrderTables(size);

        // mocking
        given(orderTableRepository.findAll()).willReturn(mockOrderTables);

        // when
        List<OrderTable> OrderTables = orderTableService.findAll();

        // then
        assertThat(OrderTables.size()).isEqualTo(size);
    }

    private List<OrderTable> generateOrderTables(int size) {
        return IntStream.range(0, size).mapToObj(i -> generateOrderTable(UUID.randomUUID())).collect(Collectors.toList());
    }
}