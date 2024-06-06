package kitchenpos.application.order_table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import java.util.UUID;
import kitchenpos.application.OrderTableService;
import kitchenpos.application.fake.repository.InMemoryOrderTableRepository;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.application.fixture.OrderTableFixture;
import kitchenpos.application.fake.repository.InMemoryMenuRepository;
import kitchenpos.application.fake.repository.InMemoryOrderRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

  private OrderTableService orderTableService;

  private OrderRepository orderRepository;

  private MenuRepository menuRepository;

  @BeforeEach
  public void init() {
    this.menuRepository = new InMemoryMenuRepository();
    this.orderRepository = new InMemoryOrderRepository();
    OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    this.orderTableService = new OrderTableService(orderTableRepository, this.orderRepository);
  }

  @Nested
  @DisplayName("주문테이블을 등록할 수 있다.")
  class Register {
    @DisplayName("성공")
    @Test
    public void register() {
      String name = "주문테이블";
      OrderTable orderTable = OrderTableFixture.create(name);
      orderTable = orderTableService.create(orderTable);
      assertThat(orderTable.getId()).isNotNull();
      assertThat(orderTable.getName()).isEqualTo(name);
      assertThat(orderTable.isOccupied()).isFalse();
      assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
    }

    @DisplayName("주문테이블명은 1자 이상이어야한다.")
    @NullAndEmptySource
    @ParameterizedTest
    public void invalidName(String name) {
      OrderTable request = OrderTableFixture.create(name);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderTableService.create(request));
    }
  }

  @DisplayName("주문테이블을 사용처리할 수 있다.")
  @Test
  public void occupy() {
    OrderTable request = OrderTableFixture.normal();
    OrderTable orderTable = orderTableService.create(request);
    orderTable = orderTableService.sit(orderTable.getId());
    assertThat(orderTable.isOccupied()).isTrue();
  }

  @Nested
  @DisplayName("주문테이블의 인원을 변경할 수 있다.")
  class ChangeNumberOfGuests {
    @DisplayName("성공")
    @ValueSource(ints = {3,5,7})
    @ParameterizedTest
    public void changeNumberOfGuests(int numberOfGuests) {
      OrderTable request = OrderTableFixture.create("주문테이블", true);
      OrderTable orderTable = orderTableService.create(request);
      ReflectionTestUtils.setField(orderTable, "occupied", true);
      OrderTable request2 = OrderTableFixture.create("오더테이블", numberOfGuests);
      orderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), request2);
      assertThat(orderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
    }

    @DisplayName("변경할 인원은 0명 이상이어야한다.")
    @ValueSource(ints = {-3,-5,-7})
    @ParameterizedTest
    public void invalidNumberOfGuestsChange(int numberOfGuests) {
      OrderTable request = OrderTableFixture.create("오더테이블", true);
      OrderTable orderTable = orderTableService.create(request);
      UUID orderTableId = orderTable.getId();
      OrderTable request2 = OrderTableFixture.create("오더테이블", numberOfGuests);
      assertThatExceptionOfType(IllegalArgumentException.class)
          .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request2));
    }

    @DisplayName("주문테이블은 사용중이어야한다.")
    @Test
    public void unoccupiedTable() {
      OrderTable request = OrderTableFixture.create("오더테이블");
      OrderTable orderTable = orderTableService.create(request);
      OrderTable request2 = OrderTableFixture.create("오더테이블", 3);
      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request2));
    }
  }

  @Nested
  @DisplayName("주문테이블을 초기화할 수 있다.")
  class Clear {
    @DisplayName("성공")
    @Test
    public void clear() {
      OrderTable request = OrderTableFixture.create("오더테이블");
      OrderTable orderTable = orderTableService.create(request);
      orderTable = orderTableService.clear(orderTable.getId());
      assertThat(orderTable.isOccupied()).isFalse();
      assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
    }

    @DisplayName("진행중인 주문이 없을 경우 초기화할 수 있다.")
    @Test
    public void failClear() {
      OrderTable request = OrderTableFixture.create("오더테이블", true);
      OrderTable orderTable = orderTableService.create(request);
      orderTable = orderTableService.sit(orderTable.getId());
      UUID orderTableId = orderTable.getId();

      Menu menu = MenuFixture.createDefaultMenu();
      menuRepository.save(menu);
      OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(menu, 3L, 200L);
      Order order = OrderFixture.create(OrderType.EAT_IN, OrderStatus.WAITING, List.of(orderLineItem),"주소", orderTable);
      order = orderRepository.save(order);

      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(() -> orderTableService.clear(orderTableId));
    }
  }

  @DisplayName("등록된 주문테이블 전체를 조회한다.")
  @Test
  public void findAll() {
    OrderTable request1 = OrderTableFixture.create("오더테이블1");
    OrderTable orderTable1 = orderTableService.create(request1);
    OrderTable request2 = OrderTableFixture.create("오더테이블2");
    OrderTable orderTable2 = orderTableService.create(request2);

    List<OrderTable> orderTables = orderTableService.findAll();
    assertThat(orderTables).hasSize(2);
  }

}
