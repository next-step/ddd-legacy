package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static kitchenpos.KitchenposFixture.*;
import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

  @Mock
  private OrderTableRepository orderTableRepository;

  @Mock
  private OrderRepository orderRepository;

  @InjectMocks
  private OrderTableService orderTableService;

  @Test
  @DisplayName("가게 손님은 주문 테이블을 이용할 수 있습니다.")
  void usingTable() {
    //given
    OrderTable request = 정상_오더_테이블();

    when(orderTableRepository.save(any())).thenReturn(request);

    //then
    assertDoesNotThrow(() -> {
      OrderTable orderTable = orderTableService.create(request);
      assertThat(orderTable.getName()).isEqualTo(request.getName());
    });
  }

  @Test
  @DisplayName("주문 테이블의 상태를 비어있지 않도록 변경")
  void changeTableStatusGetGuests() {
    //given
    OrderTable request = 정상_오더_테이블();
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(request));

    //then
    assertDoesNotThrow(() -> {
      OrderTable sitOrder = orderTableService.sit(ID);
      assertThat(sitOrder.isEmpty()).isFalse();
    });
  }

  @Test
  @DisplayName("주문 테이블의 상태를 비어있도록 변경")
  void changeTableStatusGetOrder() {
    //given
    OrderTable orderTable = 정상_오더_테이블();
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

    //when
    when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

    //then
    orderTableService.clear(ID);
    assertAll(() -> {
      assertThat(orderTable.isEmpty()).isEqualTo(true);
      assertThat(orderTable.getNumberOfGuests()).isZero();
    });
  }

  @Test
  @DisplayName("가게 점주는 주문을 받을 때, 음식이 완료되지 않으면 IllegalStateException 예외 발생")
  void changeTableButIllegal() {
    //given
    OrderTable orderTable = 정상_오더_테이블();
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

    //when
    when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

    //then
    assertThatThrownBy(() -> orderTableService.clear(ID))
      .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("손님 수 변경")
  void changeNumberOfGuests() {
    //given
    OrderTable request = 오더_테이블_손님_10명();
    OrderTable result = 정상_오더_테이블();
    //when
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(result));
    //then
    assertDoesNotThrow(() -> {
      orderTableService.changeNumberOfGuests(ID, request);
      assertThat(result.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
    });
  }

  @Test
  @DisplayName("손님 수를 변경할 때, 손님 수가 0 이상이 아니면 IllegalArgumentException 예외 발생")
  void changeNumberOfGuestsButNotNegativeNum() {
    //given
    OrderTable request = 오더_테이블_손님_음수();

    //then
    assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(ID, request))
      .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("주문 테이블 정보 전체 조회")
  void findAll() {
    //given
    when(orderTableRepository.findAll()).thenReturn(오더_테이블_리스트_사이즈_1());

    //then
    assertDoesNotThrow(() -> {
      List<OrderTable> tables = orderTableService.findAll();
      assertThat(tables.size()).isEqualTo(1);
    });

  }
}
