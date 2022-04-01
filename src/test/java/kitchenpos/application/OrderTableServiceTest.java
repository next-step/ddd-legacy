package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {
	@Mock
	private OrderTableRepository orderTableRepository;

	@Mock
	private OrderRepository orderRepository;

	@InjectMocks
	private OrderTableService orderTableService;

	@DisplayName("매장 테이블 전체를 조회할 수 있다")
	@Test
	void find_all_order_table() {
		// given
		OrderTable orderTable1 = new OrderTable("테이블 1", 5);
		OrderTable orderTable2 = new OrderTable("테이블 2");
		when(orderTableRepository.findAll()).thenReturn(Arrays.asList(orderTable1, orderTable2));

		// when
		List<OrderTable> orderTables = orderTableService.findAll();

		// then
		assertThat(orderTables.size()).isEqualTo(2);
		assertThat(orderTables.get(0).getName()).isEqualTo("테이블 1");
		assertThat(orderTables.get(0).getNumberOfGuests()).isEqualTo(5);
		assertThat(orderTables.get(0).isEmpty()).isFalse();
		assertThat(orderTables.get(1).getName()).isEqualTo("테이블 2");
		assertThat(orderTables.get(1).getNumberOfGuests()).isEqualTo(0);
		assertThat(orderTables.get(1).isEmpty()).isTrue();
	}

	@DisplayName("변경하려는 매장테이블은 비어있으면 안된다")
	@Test
	void when_to_change_order_table_must_not_be_empty() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		OrderTable orderTableRequest = new OrderTable(5);
		OrderTable orderTable = new OrderTable("테이블", 0);
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.of(orderTable));

		// when & then
		assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(uuid, orderTableRequest)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("인원을 변경하려는 매장 테이블은 미리 등록되어 있어야 한다")
	@Test
	void when_to_change_order_table_must_exist() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		OrderTable orderTableRequest = new OrderTable(3);
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(uuid, orderTableRequest)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("변경하려는 인원은 0 이상이어야 한다")
	@Test
	void when_to_change_number_of_guests_is_over_zero() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		OrderTable orderTableRequest = new OrderTable(-1);

		// when & then
		assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(uuid, orderTableRequest)).isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("매장 테이블의 인원 수를 변경할 수 있다")
	@Test
	void change_number_of_guests() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		OrderTable orderTableRequest = new OrderTable(5);
		OrderTable orderTable = new OrderTable("테이블", 3);
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.of(orderTable));

		// when
		OrderTable numberChangedOrderTable = orderTableService.changeNumberOfGuests(uuid, orderTableRequest);

		// then
		assertThat(numberChangedOrderTable.getNumberOfGuests()).isEqualTo(5);
	}

	@DisplayName("정리하려는 매장테이블의 주문 상태는 반드시 완료되어야만 한다")
	@Test
	void when_to_clear_order_status_is_completed() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		OrderTable orderTable = new OrderTable("테이블", 3);
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.of(orderTable));
		when(orderRepository.existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED))).thenReturn(true);

		// when & then
		assertThatThrownBy(() -> orderTableService.clear(uuid)).isInstanceOf(IllegalStateException.class);
	}

	@DisplayName("정리하려는 매장테이블은 미리 등록되어 있어야 한다")
	@Test
	void when_to_clear_order_table_must_exist() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderTableService.clear(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("매장 테이블을 정리할 수 있고, 정리하면 비어 있음으로 변경된다")
	@Test
	void clear_order_table() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		OrderTable orderTable = new OrderTable("테이블", 3);
		when(orderRepository.existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED))).thenReturn(false);
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.of(orderTable));

		// when
		OrderTable emptyTable = orderTableService.clear(uuid);

		// then
		assertThat(emptyTable.isEmpty()).isTrue();
		assertThat(emptyTable.getNumberOfGuests()).isZero();
	}

	@DisplayName("앉으려는 매장테이블은 미리 등록되어 있어야 한다")
	@Test
	void when_to_sit_order_table_must_exist() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> orderTableService.sit(uuid)).isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("매장 테이블에 앉을 수 있고, 앉으면 상태가 비어있지 않음으로 변경된다")
	@Test
	void sit_order_table() {
		// given
		UUID uuid = UUID.fromString("2f48f241-9d64-4d16-bf56-70b9d4e0e79a");
		OrderTable emptyTable = new OrderTable("빈 테이블", 0);
		when(orderTableRepository.findById(uuid)).thenReturn(Optional.of(emptyTable));

		// when
		OrderTable notEmptyTable = orderTableService.sit(uuid);

		// then
		assertThat(notEmptyTable.isEmpty()).isFalse();
	}

	@DisplayName("매장 테이블의 이름은 필수이다")
	@Test
	void order_table_name_is_not_empty() {
		// given
		OrderTable orderTableRequestWithNull = new OrderTable();
		OrderTable orderTableRequestWithEmptyName = new OrderTable("");

		// when & then
		assertThatThrownBy(() -> orderTableService.create(orderTableRequestWithNull)).isInstanceOf(IllegalArgumentException.class);
		assertThatThrownBy(() -> orderTableService.create(orderTableRequestWithEmptyName)).isInstanceOf(IllegalArgumentException.class);
	}


	@DisplayName("매장 테이블을 만들 수 있고, 만들어질 때는 비어있다")
	@Test
	void create_order_table() {
		// given
		OrderTable orderTableRequest = new OrderTable("테이블");
		OrderTable result = new OrderTable("테이블", 0);
		when(orderTableRepository.save(any())).thenReturn(result);

		// when
		OrderTable orderTable = orderTableService.create(orderTableRequest);

		// then
		assertThat(orderTable.getName()).isEqualTo("테이블");
		assertThat(orderTable.isEmpty()).isTrue();
	}
}
