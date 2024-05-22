package kitchenpos.application.order_table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.UUID;
import kitchenpos.application.OrderTableService;
import kitchenpos.application.fixture.OrderTableFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderTableServiceTest {

  private OrderTableService orderTableService;
  @Mock
  private OrderRepository orderRepository;

  @BeforeEach
  public void init() {
    OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    orderTableService = new OrderTableService(orderTableRepository, orderRepository);
  }

  @DisplayName("주문테이블을 등록할 수 있다.")
  @Test
  public void register() {
    OrderTable request = OrderTableFixture.create("오더테이블");
    OrderTable orderTable = orderTableService.create(request);
    assertThat(orderTable).isNotNull();
    assertThat(orderTable.isOccupied()).isFalse();
    assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
  }


  @DisplayName("주문테이블명이 1자 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @NullAndEmptySource
  @ParameterizedTest
  public void invalidName(String name) {
    OrderTable request = OrderTableFixture.create(name);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderTableService.create(request));
  }

  @DisplayName("주문테이블을 사용처리할 수 있다.")
  @Test
  public void occupy() {
    OrderTable request = OrderTableFixture.create("오더테이블");
    OrderTable orderTable = orderTableService.create(request);
    orderTable = orderTableService.sit(orderTable.getId());
    assertThat(orderTable.isOccupied()).isTrue();
  }

  @DisplayName("주문테이블의 인원을 변경할 수 있다.")
  @ValueSource(ints = {3,5,7})
  @ParameterizedTest
  public void changeNumberOfGuests(int numberOfGuests) {
    OrderTable request = OrderTableFixture.create("오더테이블");
    OrderTable orderTable = orderTableService.create(request);
    orderTable = orderTableService.sit(orderTable.getId());
    OrderTable request2 = OrderTableFixture.create("오더테이블", numberOfGuests);
    orderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), request2);
    assertThat(orderTable.getNumberOfGuests()).isEqualTo(numberOfGuests);
  }

  @DisplayName("주문테이블 인원이 0명 미만일 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(ints = {-3,-5,-7})
  @ParameterizedTest
  public void invalidNumberOfGuestsChange(int numberOfGuests) {
    OrderTable request = OrderTableFixture.create("오더테이블");
    OrderTable orderTable = orderTableService.create(request);
    UUID orderTableId = orderTable.getId();
    orderTable = orderTableService.sit(orderTable.getId());
    OrderTable request2 = OrderTableFixture.create("오더테이블", numberOfGuests);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, request2));
  }

  @DisplayName("주문테이블이 미사용중일 경우 IllegalArgumentException 예외 처리를 한다.")
  @ValueSource(ints = {-3,-5,-7})
  @ParameterizedTest
  public void unoccupiedTable(int numberOfGuests) {
    OrderTable request = OrderTableFixture.create("오더테이블");
    OrderTable orderTable = orderTableService.create(request);
    OrderTable request2 = OrderTableFixture.create("오더테이블", numberOfGuests);
    assertThatExceptionOfType(IllegalArgumentException.class)
        .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request2));
  }

  @DisplayName("주문테이블을 초기화할 수 있다.")
  @Test
  public void clear() {
    OrderTable request = OrderTableFixture.create("오더테이블");
    OrderTable orderTable = orderTableService.create(request);
    orderTable = orderTableService.sit(orderTable.getId());
    OrderTable request2 = OrderTableFixture.create("오더테이블", 3);
    orderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), request2);

    orderTable = orderTableService.clear(orderTable.getId());
    assertThat(orderTable.isOccupied()).isFalse();
    assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
  }

  @DisplayName("진행중인 주문이 있을 경우 IllegalStateException 예외 처리를 한다.")
  @Test
  public void failClear() {
    OrderTable request = OrderTableFixture.create("오더테이블");
    OrderTable orderTable = orderTableService.create(request);
    orderTable = orderTableService.sit(orderTable.getId());
    UUID orderTableId = orderTable.getId();
    given(orderRepository.existsByOrderTableAndStatusNot(any(), any()))
        .willReturn(true);

    assertThatExceptionOfType(IllegalStateException.class)
        .isThrownBy(() -> orderTableService.clear(orderTableId));
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
