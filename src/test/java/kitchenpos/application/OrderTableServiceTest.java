package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.InMemoryOrderRepository;
import kitchenpos.infra.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("주문테이블 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

  private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
  private final OrderRepository orderRepository = new InMemoryOrderRepository();

  private OrderTableService orderTableService;

  @BeforeEach
  void setUp() {
    this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
  }

  @Nested
  @DisplayName("메뉴그룹을 등록할 때")
  class WhenCreate {

    @DisplayName("유효한 매장테이블명을 입력하면 생성된 매장테이블을 반환한다")
    @Test
    void givenValidOrderTable_whenCreate_thenReturnOrderTable() {
      // given
      OrderTable requestOrderTable = new OrderTable();
      requestOrderTable.setName("1번");

      // when
      OrderTable createdOrderTable = orderTableService.create(requestOrderTable);

      // then
      assertThat(createdOrderTable.getId()).isNotNull();
      assertThat(createdOrderTable.getName()).isEqualTo("1번");
      assertThat(createdOrderTable.getNumberOfGuests()).isZero();
      assertThat(createdOrderTable.isOccupied()).isFalse();
    }

    @DisplayName("주문테이블 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest(name = "{displayName}: [{index}] {argumentsWithNames}")
    void givenEmptyName_whenCreate_thenIllegalArgumentException(String name) {
      // given
      OrderTable requestOrderTable = new OrderTable();
      requestOrderTable.setName(name);

      // when & then
      assertThatIllegalArgumentException()
          .isThrownBy(() -> orderTableService.create(requestOrderTable));
    }
  }

  @Nested
  @DisplayName("주문테이블을 사용할 때")
  class WhenSit {

    @DisplayName("주문테이블 ID를 입력받아 주문테이블 사용 중 처리할 수 있다.")
    @Test
    void givenOrderTableId_whenSit_thenReturnOrderTable() {
      // given
      OrderTable orderTable = createInitOrderTable();
      orderTableRepository.save(orderTable);

      // when
      OrderTable createdOrderTable = orderTableService.sit(orderTable.getId());

      // then
      assertThat(createdOrderTable.getId()).isNotNull();
      assertThat(createdOrderTable.getName()).isEqualTo("1번");
      assertThat(createdOrderTable.getNumberOfGuests()).isZero();
      assertThat(createdOrderTable.isOccupied()).isTrue();
    }

    @DisplayName("주문테이블이 존재하지 않을 경우 처리할 수 없다.")
    @Test
    void givenNotFoundOrderTable_whenSit_thenNoSuchElementException() {
      // given
      OrderTable orderTable = createInitOrderTable();

      // when & then
      assertThatThrownBy(() -> orderTableService.sit(orderTable.getId()))
          .isInstanceOf(NoSuchElementException.class);
    }
  }

  @Nested
  @DisplayName("주문테이블을 정리할 때")
  class WhenClear {

    @DisplayName("주문테이블 ID를 입력받아 주문테이블을 정리할 수 있다")
    @Test
    void givenOrderTableId_whenClear_thenReturnOrderTable() {
      // given
      OrderTable orderTable = creationRequestOrderTable("5번", 4, true);
      orderTableRepository.save(orderTable);

      // when
      OrderTable clearedOrderTable = orderTableService.clear(orderTable.getId());

      // then
      assertThat(clearedOrderTable.getId()).isNotNull();
      assertThat(clearedOrderTable.getName()).isEqualTo("5번");
      assertThat(clearedOrderTable.getNumberOfGuests()).isEqualTo(0);
      assertThat(clearedOrderTable.isOccupied()).isEqualTo(false);
    }

    @DisplayName("주문테이블에 포함된 주문 건 중 주문완료 처리가 안된 주문건이 존재할 경우 정리할 수 없다.")
    @Test
    void givenOrderTableId_whenClear_thenIllegalStateException() {
      // given
      OrderTable requestOrderTable = creationRequestOrderTable("5번", 4, true);
      OrderTable orderTable = orderTableRepository.save(requestOrderTable);

      Order order = new Order();
      order.setId(UUID.randomUUID());
      order.setOrderTable(orderTable);
      order.setStatus(OrderStatus.SERVED);
      orderRepository.save(order);

      // when & then
      assertThatIllegalStateException()
          .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
    }
  }

  @Nested
  @DisplayName("주문테이블 손님 수를 변경할 때")
  class WhenChangeNumberOfGuests {

    @DisplayName("주문테이블 ID와 손님 수를 입력받아 손님 수를 변경할 수 있다.")
    @Test
    void givenChangeOrderTable_whenChangeNumberOfGuests_thenReturnOrderTable() {
      // given
      OrderTable orderTable = creationRequestOrderTable("3번", 3, true);
      orderTableRepository.save(orderTable);

      OrderTable requestOrderTable = new OrderTable();
      requestOrderTable.setNumberOfGuests(4);

      // when
      OrderTable clearedOrderTable = orderTableService.changeNumberOfGuests(orderTable.getId(),
          requestOrderTable);

      // then
      assertThat(clearedOrderTable.getId()).isNotNull();
      assertThat(clearedOrderTable.getName()).isEqualTo("3번");
      assertThat(clearedOrderTable.getNumberOfGuests()).isEqualTo(4);
      assertThat(clearedOrderTable.isOccupied()).isEqualTo(true);
    }

    @DisplayName("주문테이블 변경 손님 수는 0원 보다 작을 수 없다.")
    @Test
    void givenNegativeNumberOfGuest_whenChangeNumberOfGuests_thenIllegalArgumentException() {
      // given
      OrderTable orderTable = creationRequestOrderTable("3번", 3, true);

      OrderTable requestOrderTable = new OrderTable();
      requestOrderTable.setNumberOfGuests(-1);

      // when & then
      assertThatIllegalArgumentException()
          .isThrownBy(
              () -> orderTableService.changeNumberOfGuests(orderTable.getId(), requestOrderTable));
    }

    @DisplayName("주문테이블이 사용 중 상태가 아니면 처리할 수 없다.")
    @Test
    void givenNotOccupied_whenChangeNumberOfGuests_thenIllegalArgumentException() {
      // given
      OrderTable orderTable = creationRequestOrderTable("3번", 3, false);
      orderTableRepository.save(orderTable);

      OrderTable requestOrderTable = new OrderTable();
      requestOrderTable.setNumberOfGuests(4);

      // when & then
      assertThatIllegalStateException()
          .isThrownBy(
              () -> orderTableService.changeNumberOfGuests(orderTable.getId(), requestOrderTable));
    }
  }

  @Nested
  @DisplayName("주문테이블을 조회할 때")
  class WhenFind {

    @DisplayName("생성된 주문테이블 목록을 조회할 수 있다.")
    @Test
    void givenOrderTables_whenFindAll_thenReturnOrderTables() {
      // given
      List.of(
          creationRequestOrderTable("1번", 3, true),
          creationRequestOrderTable("2번", 4, true)
      ).forEach(orderTableRepository::save);

      // when
      List<OrderTable> orders = orderTableService.findAll();

      // then
      assertThat(orders).hasSize(2);
      assertThat(orders).extracting(OrderTable::getName).contains("1번", "2번");
      assertThat(orders).extracting(OrderTable::getNumberOfGuests).contains(3, 4);
    }
  }

  private static OrderTable createInitOrderTable() {
    return creationRequestOrderTable("1번", 0, false);
  }

  private static OrderTable creationRequestOrderTable(String name, int numberOfGuests,
      boolean occupied) {
    final OrderTable orderTable = new OrderTable();
    orderTable.setId(UUID.randomUUID());
    orderTable.setName(name);
    orderTable.setNumberOfGuests(numberOfGuests);
    orderTable.setOccupied(occupied);
    return orderTable;
  }
}
