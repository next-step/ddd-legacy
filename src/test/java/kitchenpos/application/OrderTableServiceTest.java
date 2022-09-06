package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.UUID;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

  private OrderTableService orderTableService;

  @Mock
  private OrderTableRepository orderTableRepository;

  @Mock
  private OrderRepository orderRepository;

  @BeforeEach
  void setUp() {
    this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
  }

  @DisplayName("유효한 매장테이블명을 입력하면 생성된 매장테이블을 반환한다")
  @Test
  void givenValidOrderTable_whenCreate_thenReturnOrderTable() {
    // given
    OrderTable requestOrderTable = new OrderTable();
    requestOrderTable.setName("1번");
    given(orderTableRepository.save(any(OrderTable.class))).willReturn(createInitOrderTable());

    // when
    OrderTable createdOrderTable = orderTableService.create(requestOrderTable);

    // then
    assertThat(createdOrderTable.getId()).isNotNull();
    assertThat(createdOrderTable.getName()).isEqualTo("1번");
    assertThat(createdOrderTable.getNumberOfGuests()).isEqualTo(0);
    assertThat(createdOrderTable.isOccupied()).isEqualTo(false);
  }

  private static OrderTable createInitOrderTable() {
    final OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName("1번");
    orderTable.setNumberOfGuests(0);
    orderTable.setOccupied(false);
    return orderTable;
  }
}
