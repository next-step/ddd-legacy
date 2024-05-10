package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("주문 테이블 생성 시 이름이 비어있거나 null일 때 IllegalArgumentException이 발생한다")
	void testCreateWithNullOrEmptyName(String name) {
		// given
		OrderTable table = new OrderTable();
		table.setName(name);

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				// when
				orderTableService.create(table);
			});
	}

	@Test
	@DisplayName("주문 테이블이 정상적으로 저장되는 경우")
	void testCreateSuccessfully() {
		// given
		OrderTable requestTable = new OrderTable();
		requestTable.setName("테이블13");
		when(orderTableRepository.save(any())).thenReturn(requestTable);

		// when
		OrderTable savedTable = orderTableService.create(requestTable);

		// then
		assertThat(savedTable).isNotNull();
		assertThat(savedTable.getName()).isEqualTo("테이블13");
	}

	@Test
	@DisplayName("주문 테이블에 손님이 앉을 때 존재하지 않는 테이블 ID로 요청하면 NoSuchElementException이 발생한다")
	void testSitWithNonExistentTable() {
		// given
		UUID nonExistentId = UUID.randomUUID();
		when(orderTableRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		// then
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> {
				// when
				orderTableService.sit(nonExistentId);
			});
	}

	@Test
	@DisplayName("주문 테이블에 손님이 앉을 때 존재하는 테이블 ID로 요청하면 테이블 상태가 사용 중으로 정상적으로 변경된다")
	void testSitSuccessfully() {
		// given
		UUID existingId = UUID.randomUUID();
		OrderTable existingTable = new OrderTable();
		existingTable.setId(existingId);
		when(orderTableRepository.findById(existingId)).thenReturn(Optional.of(existingTable));

		// when
		OrderTable occupiedTable = orderTableService.sit(existingId);

		// then
		assertThat(occupiedTable.isOccupied()).isTrue();
	}

	@Test
	@DisplayName("주문 테이블을 정리할 때 존재하지 않는 테이블 ID로 요청하면 NoSuchElementException이 발생한다")
	void testClearWithNonExistentTable() {
		// given
		UUID nonExistentId = UUID.randomUUID();
		when(orderTableRepository.findById(nonExistentId)).thenReturn(Optional.empty());

		// then
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> {
				// when
				orderTableService.clear(nonExistentId);
			});
	}

	@Test
	@DisplayName("주문 테이블을 정리할 때 주문 상태가 완료되지 않은 경우 IllegalStateException이 발생한다")
	void testClearWithIncompleteOrders() {
		// given
		UUID existingId = UUID.randomUUID();
		OrderTable tableWithOrders = new OrderTable();
		tableWithOrders.setId(existingId);
		when(orderTableRepository.findById(existingId)).thenReturn(Optional.of(tableWithOrders));
		when(orderRepository.existsByOrderTableAndStatusNot(tableWithOrders, OrderStatus.COMPLETED)).thenReturn(true);

		// then
		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> {
				// when
				orderTableService.clear(existingId);
			});
	}

	@Test
	@DisplayName("주문 테이블을 정리할 때 주문 상태가 모두 완료되었으면 테이블 상태가 사용 가능으로 정상적으로 변경된다")
	void testClearSuccessfully() {
		// given
		UUID existingId = UUID.randomUUID();
		OrderTable table = new OrderTable();
		table.setId(existingId);
		when(orderTableRepository.findById(existingId)).thenReturn(Optional.of(table));
		when(orderRepository.existsByOrderTableAndStatusNot(table, OrderStatus.COMPLETED)).thenReturn(false);

		// when
		OrderTable clearedTable = orderTableService.clear(existingId);

		// then
		assertThat(clearedTable.isOccupied()).isFalse();
		assertThat(clearedTable.getNumberOfGuests()).isEqualTo(0);
	}

	@Test
	@DisplayName("손님 수 변경 시 입력된 수가 0보다 작으면 IllegalArgumentException이 발생한다")
	void testChangeNumberOfGuestsNegative() {
		// given
		UUID tableId = UUID.randomUUID();
		OrderTable requestTable = new OrderTable();
		requestTable.setNumberOfGuests(-1);

		// then
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> {
				// when
				orderTableService.changeNumberOfGuests(tableId, requestTable);
			});
	}

	@Test
	@DisplayName("손님 수 변경 시 존재하지 않는 테이블 ID로 요청하면 NoSuchElementException이 발생한다")
	void testChangeNumberOfGuestsNonExistentTable() {
		// given
		UUID nonExistentId = UUID.randomUUID();
		when(orderTableRepository.findById(nonExistentId)).thenReturn(Optional.empty());
		OrderTable requestTable = new OrderTable();
		requestTable.setNumberOfGuests(5);

		// then
		assertThatExceptionOfType(NoSuchElementException.class)
			.isThrownBy(() -> {
				// when
				orderTableService.changeNumberOfGuests(nonExistentId, requestTable);
			});
	}

	@Test
	@DisplayName("손님 수 변경 시 테이블이 사용 중이 아니면 IllegalStateException이 발생한다")
	void testChangeNumberOfGuestsTableNotOccupied() {
		// given
		UUID existingId = UUID.randomUUID();
		OrderTable existingTable = new OrderTable();
		existingTable.setId(existingId);
		existingTable.setOccupied(false);
		when(orderTableRepository.findById(existingId)).thenReturn(Optional.of(existingTable));
		OrderTable requestTable = new OrderTable();
		requestTable.setNumberOfGuests(5);

		// then
		assertThatExceptionOfType(IllegalStateException.class)
			.isThrownBy(() -> {
				// when
				orderTableService.changeNumberOfGuests(existingId, requestTable);
			});
	}

	@Test
	@DisplayName("손님 수 변경 시 테이블이 사용 중이고 요청된 손님 수가 유효하면 손님 수가 정상적으로 변경된다")
	void testChangeNumberOfGuestsSuccessfully() {
		// given
		UUID existingId = UUID.randomUUID();

		OrderTable existingTable = new OrderTable();
		existingTable.setId(existingId);
		existingTable.setOccupied(true);
		existingTable.setNumberOfGuests(3);

		when(orderTableRepository.findById(existingId)).thenReturn(Optional.of(existingTable));

		OrderTable requestTable = new OrderTable();
		requestTable.setNumberOfGuests(5);

		// when
		OrderTable updatedTable = orderTableService.changeNumberOfGuests(existingId, requestTable);

		// then
		assertThat(updatedTable.getNumberOfGuests()).isEqualTo(5);
	}

	@Test
	@DisplayName("모든 주문 테이블을 조회할 때 정상적으로 모든 테이블이 조회된다")
	void testFindAll() {
		// given
		List<OrderTable> expectedTables = Arrays.asList(new OrderTable(), new OrderTable());
		when(orderTableRepository.findAll()).thenReturn(expectedTables);

		// when
		List<OrderTable> foundTables = orderTableService.findAll();

		// then
		assertThat(foundTables).hasSize(2);
	}
}
