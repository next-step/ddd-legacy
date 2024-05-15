package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fixture.OrderFixture;
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

	private OrderTable validOrderTable;

	@BeforeEach
	void setUp() {
		validOrderTable = OrderFixture.createValidOrderTable();
	}

	@Nested
	class create {
		@ParameterizedTest
		@NullAndEmptySource
		@DisplayName("주문 테이블 생성 시 이름이 비어있거나 null일 때 주문 테이블 생성을 할 수 없다")
		void createWithNullOrEmptyName(String name) {
			// given
			validOrderTable.setName(name);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.create(validOrderTable);
				});
		}

		@Test
		@DisplayName("주문 테이블을 저장할 수 있다")
		void createOrderTableSuccessfully() {
			// given
			when(orderTableRepository.save(any(OrderTable.class))).thenReturn(validOrderTable);

			// when
			OrderTable savedTable = orderTableService.create(validOrderTable);

			// then
			assertThat(savedTable).isNotNull();
			assertThat(savedTable.getName()).isEqualTo(validOrderTable.getName());
		}
	}

	@Nested
	class sit {
		@Test
		@DisplayName("주문 테이블을 사용 중 상태로 변경 시 존재하지 않는 주문 테이블 ID로 요청하면 주문 테이블을 사용 중 상태로 변경할 수 없다")
		void sitWithNonExistentTable() {
			// given
			when(orderTableRepository.findById(validOrderTable.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.sit(validOrderTable.getId());
				});
		}

		@Test
		@DisplayName("주문 테이블을 사용 중 상태로 변경 시 존재하는 테이블 ID로 요청하면 주문 테이블을 사용 중 상태로 변경할 수 있다")
		void sitSuccessfully() {
			// given
			when(orderTableRepository.findById(validOrderTable.getId())).thenReturn(Optional.of(validOrderTable));

			// when
			OrderTable occupiedTable = orderTableService.sit(validOrderTable.getId());

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
			when(orderTableRepository.findById(validOrderTable.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.clear(validOrderTable.getId());
				});
		}

		@Test
		@DisplayName("주문 테이블을 사용 가능 상태로 변경 시 완료되지 않은 주문이 존재하는 경우 주문 테이블을 사용 가능 상태로 변경할 수 없다")
		void clearWithIncompleteOrders() {
			// given
			when(orderTableRepository.findById(validOrderTable.getId())).thenReturn(Optional.of(validOrderTable));
			when(orderRepository.existsByOrderTableAndStatusNot(validOrderTable, OrderStatus.COMPLETED))
				.thenReturn(true);

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.clear(validOrderTable.getId());
				});
		}

		@Test
		@DisplayName("주문 테이블을 사용 가능 상태로 변경 시 주문이 모두 완료되었으면 주문 테이블을 사용 가능 상태로 변경할 수 있다")
		void clearSuccessfully() {
			// given
			when(orderTableRepository.findById(validOrderTable.getId())).thenReturn(Optional.of(validOrderTable));
			when(orderRepository.existsByOrderTableAndStatusNot(validOrderTable, OrderStatus.COMPLETED))
				.thenReturn(false);

			// when
			OrderTable clearedTable = orderTableService.clear(validOrderTable.getId());

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
			validOrderTable.setNumberOfGuests(-1);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.changeNumberOfGuests(validOrderTable.getId(), validOrderTable);
				});
		}

		@Test
		@DisplayName("손님 수 변경 시 존재하지 않는 테이블 ID로 요청하면 손님 수 변경을 할 수 없다")
		void changeNumberOfGuestsNonExistentTable() {
			// given
			validOrderTable.setNumberOfGuests(5);

			when(orderTableRepository.findById(validOrderTable.getId())).thenReturn(Optional.empty());

			// then
			assertThatExceptionOfType(NoSuchElementException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.changeNumberOfGuests(validOrderTable.getId(), validOrderTable);
				});
		}

		@Test
		@DisplayName("손님 수 변경 시 테이블이 사용 중이 아니면 손님 수 변경을 할 수 없다")
		void changeNumberOfGuestsTableNotOccupied() {
			// given
			validOrderTable.setOccupied(false);
			validOrderTable.setNumberOfGuests(3);

			OrderTable requestOrderTable = new OrderTable();
			requestOrderTable.setId(validOrderTable.getId());
			requestOrderTable.setNumberOfGuests(5);

			when(orderTableRepository.findById(requestOrderTable.getId())).thenReturn(Optional.of(validOrderTable));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> {
					// when
					orderTableService.changeNumberOfGuests(requestOrderTable.getId(), requestOrderTable);
				});
		}

		@Test
		@DisplayName("손님 수 변경 시 주문 테이블이 사용 중이고 요청된 손님 수가 유효하면 손님 수 변경을 할 수 있다")
		void changeNumberOfGuestsSuccessfully() {
			// given
			validOrderTable.setOccupied(true);
			validOrderTable.setNumberOfGuests(3);

			OrderTable requestOrderTable = new OrderTable();
			requestOrderTable.setId(validOrderTable.getId());
			requestOrderTable.setNumberOfGuests(5);

			when(orderTableRepository.findById(requestOrderTable.getId())).thenReturn(Optional.of(validOrderTable));

			// when
			OrderTable updatedOrderTable = orderTableService.changeNumberOfGuests(requestOrderTable.getId(),
				requestOrderTable);

			// then
			assertThat(updatedOrderTable.getNumberOfGuests()).isEqualTo(5);
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
