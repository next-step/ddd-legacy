package kitchenpos;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.TestFixtureFactory.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @InjectMocks
    private OrderTableService orderTableService;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("주문 테이블 등록 - 정상적으로 주문 테이블 등록 후 반환")
    void createValidOrderTableReturnsCreatedOrderTable() {
        // Arrange
        OrderTable request = createOrderTable("1번 테이블");

        when(orderTableRepository.save(any(OrderTable.class))).thenReturn(new OrderTable());

        // Act
        OrderTable 등록된_주문테이블 = orderTableService.create(request);

        // Assert
        assertThat(등록된_주문테이블).isNotNull();
        verify(orderTableRepository, times(1)).save(any(OrderTable.class));
    }

    @Test
    @DisplayName("주문 테이블 등록 - 이름이 비어있을 때 예외 발생")
    void createEmptyTableNameThrowsIllegalArgumentException() {
        // Arrange
        OrderTable request = createOrderTable("");

        // Act & Assert
        assertThatThrownBy(() -> orderTableService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(orderTableRepository, never()).save(any(OrderTable.class));
    }

    @Test
    @DisplayName("주문 테이블 입장 - 정상적으로 주문 테이블 입장 처리 후 반환")
    void sitValidOrderTableReturnsOccupiedOrderTable() {
        // Arrange
        UUID orderTableId = UUID.randomUUID();
        OrderTable 주문_1번_테이블 = createOrderTable("1번 테이블");

        when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(주문_1번_테이블));

        // Act
        OrderTable 점유된_주문테이블 = orderTableService.sit(orderTableId);

        // Assert
        assertThat(점유된_주문테이블.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("주문 테이블 클리어 - 정상적으로 주문 테이블 클리어 후 반환")
    void clearValidOrderTableReturnsClearedOrderTable() {
        // Arrange
        UUID orderTableId = UUID.randomUUID();
        OrderTable 주문_1번_테이블 = createOrderTable("1번 테이블", 3, true);

        when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(주문_1번_테이블));
        when(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), any(OrderStatus.class)))
                .thenReturn(false);

        // Act
        OrderTable 클리어된_주문테이블= orderTableService.clear(orderTableId);

        // Assert
        assertThat(클리어된_주문테이블.isOccupied()).isFalse();
        assertThat(클리어된_주문테이블.getNumberOfGuests()).isEqualTo(0);
    }

    @Test
    @DisplayName("손님 수 수정 - 정상적으로 손님 수 수정 후 반환")
    void changeNumberOfGuestsValidOrderTableReturnsUpdatedOrderTable() {
        // Arrange
        UUID orderTableId = UUID.randomUUID();
        OrderTable 주문_1번_테이블 = createOrderTable("1번 테이블", 3, true);

        when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(주문_1번_테이블));

        OrderTable request =  createOrderTable("1번 테이블", 5, true);

        // Act
        OrderTable 손님수_수정된_주문테이블 = orderTableService.changeNumberOfGuests(orderTableId, request);

        // Assert
        assertThat(손님수_수정된_주문테이블.getNumberOfGuests()).isEqualTo(5);
    }

    @Test
    @DisplayName("손님 수 수정 - 음수 손님 수로 수정할 때 예외 발생")
    void changeNumberOfGuestsNegativeNumberOfGuestsThrowsIllegalArgumentException() {
        // Arrange
        UUID orderTableId = UUID.randomUUID();
        OrderTable request =  createOrderTable("1번 테이블", -1, true);

        // Act & Assert
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("전체 주문 테이블 조회")
    void findAllOrderTablesReturnsAllOrderTables() {
        // Arrange
        List<OrderTable> 전체테이블목록 = new ArrayList<>();
        전체테이블목록.add(createOrderTable("1번 테이블"));
        전체테이블목록.add(createOrderTable("2번 테이블"));

        when(orderTableRepository.findAll()).thenReturn(전체테이블목록);

        // Act
        List<OrderTable> allOrderTables = orderTableService.findAll();

        // Assert
        assertThat(allOrderTables).hasSize(2);
    }
}
