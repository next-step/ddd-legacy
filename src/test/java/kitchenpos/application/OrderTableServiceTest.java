package kitchenpos.application;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {
    OrderTableService orderTableService;
    @Mock
    OrderTableRepository orderTableRepository;
    @Mock
    OrderRepository orderRepository;

    @BeforeEach
    void setup() {
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("새로운 테이블 생성시 테이블 명이 주어져야 함.")
    @Test
    void createTableName() {
        OrderTable orderTable = createOrderTable(UUID.randomUUID(),null, true, 1);

        assertThatThrownBy(() -> orderTableService.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class);

    }
    @DisplayName("손님 착석시 테이블이 차지됨으로 설정.")
    @Test
    void createTableStatus() {
        OrderTable orderTable = createOrderTable(UUID.randomUUID(),"test", false, 2);
        when(orderTableRepository.findById(any())).thenReturn(Optional.ofNullable(orderTable));

        assertThat(orderTableService.sit(orderTable.getId()).isOccupied()).isTrue();
    }


    @DisplayName("주문 완료시 테이블을 비어있는 상태로 변경한다.")
    @Test
    void createTableClean() {
        OrderTable orderTable = createOrderTable(UUID.randomUUID(),"test", true, 2);
        when(orderTableRepository.findById(any())).thenReturn(Optional.ofNullable(orderTable));

        assertThat(orderTableService.sit(orderTable.getId()).isOccupied()).isTrue();

        when(orderRepository.existsByOrderTableAndStatusNot(any(),any())).thenReturn(false);

        OrderTable result = orderTableService.clear(orderTable.getId());
        assertThat(result.isOccupied()).isFalse();
        assertThat(result.getNumberOfGuests()).isEqualTo(0);

    }
    @DisplayName("주문이 완료되지 않은 경우 테이블을 비어있는 상태로 변경할 수 없다.")
    @Test
    void createTableUsing() {
        OrderTable orderTable = createOrderTable(UUID.randomUUID(),"test", true, 2);
        when(orderTableRepository.findById(any())).thenReturn(Optional.ofNullable(orderTable));

        assertThat(orderTableService.sit(orderTable.getId()).isOccupied()).isTrue();

        when(orderRepository.existsByOrderTableAndStatusNot(any(),any())).thenReturn(true);

        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
                .isInstanceOf(IllegalStateException.class);

    }
    @DisplayName("손님 수는 0 이상이어야 한다.")
    @Test
    void GuestNum() {
        OrderTable orderTable1 = createOrderTable(UUID.randomUUID(),"test", true, 2);
        OrderTable orderTable2 = createOrderTable(UUID.randomUUID(),"test2", true, -1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable1.getId(),orderTable2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블에 기존 손님이 있던 경우에만 손님수 변경이 가능하다.")
    @Test
    void changeGuestNum() {
        OrderTable orderTable1 = createOrderTable(UUID.randomUUID(),"test", false, 0);
        OrderTable orderTable2 = createOrderTable(UUID.randomUUID(),"test2", true, 100);
        when(orderTableRepository.findById(any())).thenReturn(Optional.ofNullable(orderTable1));

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable1.getId(),orderTable2))
                .isInstanceOf(IllegalStateException.class);

    }

    public OrderTable createOrderTable(UUID id,String name, boolean occupied, int numberOfGuest){
        OrderTable orderTable = new OrderTable();
        orderTable.setId(id);
        orderTable.setName(name);
        orderTable.setOccupied(occupied);
        orderTable.setNumberOfGuests(numberOfGuest);
        return orderTable;
    }

}
