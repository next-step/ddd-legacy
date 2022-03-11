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

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
  private static final UUID RANDOM_UUID = UUID.randomUUID();
  private static final Integer POSITIVE_NUMBER_OF_GUESTS = 10;
  private static final Integer NEGATIVE_NUMBER_OF_GUESTS = -10;

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
    OrderTable request = orderTable();

    when(orderTableRepository.save(any())).thenReturn(request);
    //then
    assertDoesNotThrow(() -> {
      orderTableService.create(request);
    });
//		verify(orderTableRepository).save(any());

  }

  @Test
  @DisplayName("주문 테이블의 상태를 비어있지 않도록 변경")
  void changeTableStatusGetGuests() {
    //given
    OrderTable request = orderTable();
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(request));

    //then
    assertDoesNotThrow(() -> {
      orderTableService.sit(RANDOM_UUID);
    });
  }

  @Test
  @DisplayName("주문 테이블의 상태를 비어있도록 변경")
  void changeTableStatusGetOrder() {
    //given
    OrderTable orderTable = orderTable();
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

    //when
    when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

    //then
    orderTableService.clear(RANDOM_UUID);
    assertAll(() -> {
      assertThat(orderTable.isEmpty()).isEqualTo(true);
      assertThat(orderTable.getNumberOfGuests()).isZero();
    });
  }

  @Test
  @DisplayName("가게 점주는 주문을 받을 때, 음식이 완료되지 않으면 IllegalStateException 예외 발생")
  void changeTableButIllegal() {
    //given
    OrderTable orderTable = orderTable();
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

    //when
    when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);

    //then
    assertThatThrownBy(() -> orderTableService.clear(RANDOM_UUID))
            .isInstanceOf(IllegalStateException.class);
  }

  @Test
  @DisplayName("손님 수 변경")
  void changeNumberOfGuests() {
    //given
    OrderTable request = orderTable();
    OrderTable orderTable = mock(OrderTable.class);
    //when
    when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
    request.setNumberOfGuests(POSITIVE_NUMBER_OF_GUESTS);
    //then
    assertDoesNotThrow(() -> {
      orderTableService.changeNumberOfGuests(RANDOM_UUID, request);
    });
  }

  @Test
  @DisplayName("손님 수를 변경할 때, 손님 수가 0 이상이 아니면 IllegalArgumentException 예외 발생")
  void changeNumberOfGuestsButNotNegativeNum() {
    //given
    OrderTable request = orderTable();
    //when
    request.setNumberOfGuests(NEGATIVE_NUMBER_OF_GUESTS);

    //then
    assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(RANDOM_UUID, request))
            .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  @DisplayName("주문 테이블 정보 전체 조회")
  void findAll() {
    //given
    when(orderTableRepository.findAll()).thenReturn(Collections.singletonList(orderTable()));

    //then
    assertDoesNotThrow(() -> {
      orderTableService.findAll();
    });
  }
}
