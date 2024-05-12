package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

	@Nested
	class create {
		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주문 테이블 생성 시 이름이 비어있거나 null일 때 주문 테이블 생성을 할 수 없다")
		void createWithNullOrEmptyName(String name) {
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
		@DisplayName("주문 테이블을 저장할 수 있다")
		void createSuccessfully() {
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
	}

	@Nested
	class sit {
		@Test
		@DisplayName("주문 테이블을 사용 중 상태로 변경 시 존재하지 않는 주문 테이블 ID로 요청하면 주문 테이블을 사용 중 상태로 변경할 수 없다")
		void sitWithNonExistentTable() {
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
		@DisplayName("주문 테이블을 사용 중 상태로 변경 시 존재하는 테이블 ID로 요청하면 주문 테이블을 사용 중 상태로 변경할 수 있다")
		void sitSuccessfully() {
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
	}

	@Nested
	class clear {
		@Test
		@DisplayName("주문 테이블을 사용 가능 상태로 변경 시 존재하지 않는 테이블 ID로 요청하면 주문 테이블을 사용 가능 상태로 변경할 수 없다")
		void clearWithNonExistentTable() {
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
		@DisplayName("주문 테이블을 사용 가능 상태로 변경 시 완료되지 않은 주문이 존재하는 경우 주문 테이블을 사용 가능 상태로 변경할 수 없다")
		void clearWithIncompleteOrders() {
			// given
			UUID existingId = UUID.randomUUID();
			OrderTable tableWithOrders = new OrderTable();
			tableWithOrders.setId(existingId);
			when(orderTableRepository.findById(existingId)).thenReturn(Optional.of(tableWithOrders));
			when(orderRepository.existsByOrderTableAndStatusNot(tableWithOrders, OrderStatus.COMPLETED)).thenReturn(
				true);

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.clear(existingId);
				});
		}

		@Test
		@DisplayName("주문 테이블을 사용 가능 상태로 변경 시 주문이 모두 완료되었으면 주문 테이블을 사용 가능 상태로 변경할 수 있다")
		void clearSuccessfully() {
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
	}

	@Nested
	class changeNumberOfGuests {
		@Test
		@DisplayName("손님 수 변경 시 입력된 수가 0보다 작으면 손님 수 변경을 할 수 없다")
		void changeNumberOfGuestsNegative() {
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
		@DisplayName("손님 수 변경 시 존재하지 않는 테이블 ID로 요청하면 손님 수 변경을 할 수 없다")
		void changeNumberOfGuestsNonExistentTable() {
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
		@DisplayName("손님 수 변경 시 테이블이 사용 중이 아니면 손님 수 변경을 할 수 없다")
		void changeNumberOfGuestsTableNotOccupied() {
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
		@DisplayName("손님 수 변경 시 주문 테이블이 사용 중이고 요청된 손님 수가 유효하면 손님 수 변경을 할 수 있다")
		void changeNumberOfGuestsSuccessfully() {
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
	}

	@Nested
	class findAll {
		@Test
		@DisplayName("주문 테이블 데이터가 비어 있을 때 모든 주문 테이블을 조회하면 주문 테이블을 조회할 수 없다")
		void findAllWhenEmpty() {
			// given
			when(orderTableRepository.findAll()).thenReturn(Collections.emptyList());

			// when
			List<OrderTable> foundTables = orderTableService.findAll();

			// then
			assertThat(foundTables).isEmpty();
		}

		@Test
		@DisplayName("주문 테이블 데이터가 비어 있지 않을 때 모든 주문 테이블을 조회하면 모든 주문 테이블을 조회할 수 있다")
		void findAllWhenNotEmpty() {
			// given
			List<OrderTable> expectedTables = Arrays.asList(new OrderTable(), new OrderTable());
			when(orderTableRepository.findAll()).thenReturn(expectedTables);

			// when
			List<OrderTable> foundTables = orderTableService.findAll();

			// then
			assertThat(foundTables).hasSize(2);
		}
	}
}
