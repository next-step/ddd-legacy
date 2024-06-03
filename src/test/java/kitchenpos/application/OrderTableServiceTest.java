package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixtures.FixtureOrder;
import kitchenpos.infra.order.InMemoryOrderRepository;
import kitchenpos.infra.order.InMemoryOrderTableRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

  private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
  private final OrderRepository orderRepository = new InMemoryOrderRepository();
  private final OrderTableService orderTableService;

  public OrderTableServiceTest() {
    this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
  }

  @Nested
  @DisplayName("주문테이블 생성")
  class Nested1 {
    @Test
    @DisplayName(value = "주문테이블을 생성하기 위해 이름을 입력해야 한다.")
    void case1() {
      final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
      orderTableRepository.save(orderTable);

      final OrderTable actual = orderTableService.create(orderTable);
      Assertions.assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName(value = "주문테이블의 자리가 비어있도록 생성된다.")
    void case2() {
      final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
      orderTableRepository.save(orderTable);

      final OrderTable actual = orderTableService.create(orderTable);
      Assertions.assertThat(actual.isOccupied()).isFalse();
    }

    @Test
    @DisplayName(value = "주문테이블을 사용하기 위해 주문테이블이 존재해야 한다.")
    void case3() {
      final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
      orderTableRepository.save(orderTable);

      final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
      Assertions.assertThat(actual.isOccupied()).isTrue();
    }
  }

  @Nested
  @DisplayName("주문테이블 앉기")
  class Nested2 {
    @Test
    @DisplayName(value = "주문테이블의 자리를 고객이 앉도록 설정한다.")
    void case4() {
      final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
      orderTableRepository.save(orderTable);

      final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
      Assertions.assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    @DisplayName(value = "주문테이블에 앉은 인원을 입력하기 위해 주문테이블이 존재해야 한다.")
    void case5() {
      final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
      orderTableRepository.save(orderTable);

      final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
      Assertions.assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName(value = "주문테이블에 앉은 인원을 입력하기 위해 1명 이상은 있어야 한다.")
    void case6() {
      final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
      orderTableRepository.save(orderTable);

      final OrderTable actual = orderTableService.sit(orderTable.getId(), 3);
      Assertions.assertThat(actual).isNotNull();
    }

    @Test
    @DisplayName(value = "주문테이블에 앉은 인원을 입력하기 위해 자리가 비어있어야 한다.")
    void case7() {
      final OrderTable orderTable = FixtureOrder.fixtureOrderTable();
      orderTable.setOccupied(true);
      orderTableRepository.save(orderTable);

      Assertions.assertThatIllegalStateException()
          .isThrownBy(() -> orderTableService.sit(orderTable.getId(), 3));
    }
  }

  @Nested
  @DisplayName("주문테이블 청소")
  class Nested3 {
    @Test
    @DisplayName(value = "주문테이블을 청소하기 위해 주문테이블이 존재해야 한다, 주문테이블을 청소하기 위해 주문이 {완료} 되어야 한다.")
    void case8() {
      final Order order = FixtureOrder.fixtureOrder();
      order.setStatus(OrderStatus.SERVED);

      final OrderTable orderTable = order.getOrderTable();
      orderTable.setOccupied(true);
      orderTable.setNumberOfGuests(5);

      order.setId(orderTable.getId());

      orderTableRepository.save(orderTable);
      orderRepository.save(order);

      final OrderTable clear = orderTableService.clear(orderTable.getId());
      Assertions.assertThat(clear.isOccupied()).isFalse();
      Assertions.assertThat(clear.getNumberOfGuests()).isZero();
    }
  }
}
