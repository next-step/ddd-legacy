package kitchenpos.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import kitchenpos.common.MockitoUnitTest;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.stub.OrderTableStub;

class OrderTableServiceTest extends MockitoUnitTest {

	@Mock
	private OrderRepository orderRepository;
	@Mock
	private OrderTableRepository orderTableRepository;

	@InjectMocks
	private OrderTableService orderTableService;

	private OrderTable orderTable;
	private OrderTable notEmptyOrderTable;

	@BeforeEach
	void setUp() {
		orderTable = OrderTableStub.createDefault();
		notEmptyOrderTable = OrderTableStub.createCustom("새로운 테이블", false, 5);
	}

	@DisplayName("모든 테이블을 조회할 수 있다.")
	@Test
	void findAll() {
		// given
		List<OrderTable> orderTables = List.of(OrderTableStub.createDefault(), OrderTableStub.createDefault());
		when(orderTableRepository.findAll()).thenReturn(orderTables);

		// when
		List<OrderTable> results = orderTableRepository.findAll();

		// then
		assertAll(
			() -> assertThat(results).isNotEmpty(),
			() -> assertThat(results).containsExactlyElementsOf(orderTables)
		);
	}

	@DisplayName("주문 테이블 등록 시")
	@Nested
	class CreateTest {

		@DisplayName("새로운 주문 테이블을 등록할 수 있다")
		@Test
		void create() {
			// given
			OrderTable orderTable = OrderTableStub.createDefault();
			when(orderTableRepository.save(any()))
				.thenReturn(orderTable);

			// when
			OrderTable result = orderTableService.create(orderTable);

			// then
			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result).isEqualTo(orderTable)
			);
		}

		@DisplayName("테이블의 기본 상태는 0명의 손님이며 비어있다.")
		@Test
		void createDefault() {
			// given
			OrderTable orderTable = new OrderTable();

			// when
			boolean occupied = orderTable.isOccupied();
			int numberOfGuests = orderTable.getNumberOfGuests();

			// then
			assertAll(
				() -> assertThat(occupied).isFalse(),
				() -> assertThat(numberOfGuests).isZero()
			);
		}

		@DisplayName("테이블 이름이 빈 값이라면 예외 처리한다.")
		@ParameterizedTest
		@NullAndEmptySource
		void createFailByName(String name) {
			// given
			OrderTable orderTable = OrderTableStub.createCustom(name, false, 0);

			// then
			assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> orderTableService.create(orderTable));
		}
	}

	@DisplayName("주문 테이블 수정 시")
	@Nested
	class UpdateTest {

		@DisplayName("착석 여부를 변경 & 판단할 수 있다.")
		@Test
		void sit() {
			// given
			when(orderTableRepository.findById(any())).thenReturn(Optional.of(orderTable));

			// when
			OrderTable result = orderTableService.sit(orderTable.getId());

			// then
			assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.isOccupied()).isTrue()
			);
		}

		@DisplayName("앉은 손님의 수를 변경할 수 있다.")
		@Test
		void changeNumberOfGuests() {
			// given
			OrderTable orderTable = OrderTableStub.createCustom("새로운 테이블", true, 1);

			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.of(orderTable));

			// when
			OrderTable result = orderTableService.changeNumberOfGuests(orderTable.getId(), notEmptyOrderTable);

			// then
			assertThat(result.getNumberOfGuests())
				.isEqualTo(notEmptyOrderTable.getNumberOfGuests());
		}

		@DisplayName("앉은 손님의 수를 변경 시, 손님 수가 0보다 작다면 예외 처리한다.")
		@Test
		void changeNumberOfGuestsFailByInvalidNumber() {
			// given
			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.of(orderTable));

			// then
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), notEmptyOrderTable));
		}

		@DisplayName("앉은 손님의 수를 변경 시, 기존에 앉아있는 손님이 없었다면 예외 처리한다.")
		@Test
		void changeNumberOfGuestsFailByEmptyTable() {
			// given
			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.of(orderTable));

			// then
			assertThatIllegalStateException()
				.isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), notEmptyOrderTable));
		}
	}

	@DisplayName("테이블 정리 시")
	@Nested
	class ClearTest {

		@DisplayName("주문 완료된 테이블만 정리할 수 있다.")
		@Test
		void clear() {
			// given
			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.of(orderTable));

			// when
			OrderTable result = orderTableService.clear(UUID.randomUUID());

			// then
			assertAll(Arrays.asList(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.getNumberOfGuests()).isZero()
			));
		}

		@DisplayName("주문 완료되지 않은 테이블을 정리하려 하면 예외 처리한다.")
		@Test
		void clearFailByOrderStatus() {
			// given
			when(orderTableRepository.findById(any()))
				.thenReturn(Optional.of(notEmptyOrderTable));

			when(orderRepository.existsByOrderTableAndStatusNot(notEmptyOrderTable, OrderStatus.COMPLETED))
				.thenReturn(true);

			// when
			assertThatExceptionOfType(IllegalStateException.class)
				.isThrownBy(() -> orderTableService.clear(notEmptyOrderTable.getId()));
		}
	}
}
