package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
	private static final String TABLE_NAME = "table name";
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
	@DisplayName("가게 손님는 주문 테이블을 이용할 수 있습니다.")
	void usingTable() {
		//given
		OrderTable request = mock(OrderTable.class);
		when(request.getName()).thenReturn(TABLE_NAME);

		//then
		orderTableService.create(request);
		verify(orderTableRepository).save(any());
	}

	@Test
	@DisplayName("가게 점주는 주문을 받고 주문 테이블의 상태를 비어있지 않도록 변경할 수 있습니다.")
	void changeTableStatusGetGuests() {
		//given
		OrderTable orderTable = mock(OrderTable.class);
		when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

		//then
		orderTableService.sit(RANDOM_UUID);
		verify(orderTable).setEmpty(false);
	}


	@Test
	@DisplayName("가게 점주는 주문이 완료되고 나면 주문 테이블의 상태를 비어있도록 변경할 수 있습니다.")
	void changeTableStatusGetOrder() {
		//given
		OrderTable orderTable = mock(OrderTable.class);
		when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
		//when
		when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);
		//then
		orderTableService.clear(RANDOM_UUID);
		verify(orderTable).setEmpty(true);
		verify(orderTable).setNumberOfGuests(0);
	}

	@Test
	@DisplayName("가게 점주는 주문을 받고 주문 테이블의 상태를 비어있지 않도록 변경할 수 있지만, 주문한 음식이 완료되지 않으면 안됩니다.")
	void changeTableButIllegal() {
		//given
		OrderTable orderTable = mock(OrderTable.class);
		when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
		//when
		when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(true);
		//then
		Assertions.assertThatThrownBy(() -> orderTableService.clear(RANDOM_UUID))
			.isInstanceOf(IllegalStateException.class);
	}


	@Test
	@DisplayName("가게 점주는 주문 변동에 따라 손님의 수를 변경할 수 있습니다.")
	void changeNumberOfGuests() {
		//given
		OrderTable request = mock(OrderTable.class);
		OrderTable orderTable = mock(OrderTable.class);
		when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));
		//when
		when(request.getNumberOfGuests()).thenReturn(POSITIVE_NUMBER_OF_GUESTS);
		//then
		orderTableService.changeNumberOfGuests(RANDOM_UUID, request);
		verify(orderTable).setNumberOfGuests(POSITIVE_NUMBER_OF_GUESTS);
	}

	@Test
	@DisplayName("가게 점주는 주문 변동에 따라 손님의 수를 변경할 수 있지만, 손님의 수는 0명 이상이어야합니다.")
	void changeNumberOfGuestsButNotNegativeNum() {
		//given
		OrderTable request = mock(OrderTable.class);
		//when
		when(request.getNumberOfGuests()).thenReturn(NEGATIVE_NUMBER_OF_GUESTS);
		//then
		Assertions.assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(RANDOM_UUID, request))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("가게 점주와 손님은 주문 테이블의 모든 정보를 가져올 수 있습니다.")
	void findAll() {
		//given
		when(orderTableRepository.findAll()).thenReturn(Collections.singletonList(mock(OrderTable.class)));
		//then
		orderTableService.findAll();
		verify(orderTableRepository).findAll();
	}
}