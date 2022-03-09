package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.testBuilders.OrderTableBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

	@Mock
	OrderTableRepository orderTableRepository;

	@Mock
	OrderRepository orderRepository;

	@InjectMocks
	OrderTableService orderTableService;

	@DisplayName("주문테이블을 생성 시 손님수는 0이고 미사용중으로 초기화된다")
	@Test
	void create() {
		// given
		OrderTable request = aOrderTable().withName("1번 테이블").build();

		given(orderTableRepository.save(any(OrderTable.class))).willAnswer(returnsFirstArg());

		// when
		OrderTable result = orderTableService.create(request);

		// then
		assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.isEmpty()).isTrue(),
				() -> assertThat(result.getNumberOfGuests()).isZero()
		);
	}

	@DisplayName("주문테이블 생성 시 이름이 비어있거나 공백인 경우 예외가 발생한다")
	@ParameterizedTest(name = "주문테이블 이름: {0}")
	@NullAndEmptySource
	void createInvalid(String name) {
		// given
		OrderTable request = aOrderTable().withName(name).build();

		// when then
		assertThatThrownBy(() -> orderTableService.create(request))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@DisplayName("주문테이블을 사용중으로 변경한다")
	@ParameterizedTest(name = "주문테이블 사용중 여부: {0}")
	@ValueSource(booleans = {true, false})
	void sitEmpty(boolean empty) {
		// given
		OrderTable orderTable = aOrderTableByEmpty(empty).build();
		UUID orderTableId = orderTable.getId();

		given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));

		// when
		OrderTable result = orderTableService.sit(orderTableId);

		// then
		assertThat(result.isEmpty()).isFalse();
	}

	@DisplayName("존재하지 않는 주문테이블을 사용중으로 변경하는 경우 예외가 발생한다")
	@Test
	void sitNotExist() {
		// given
		UUID notExistedOrderTableId = UUID.randomUUID();

		given(orderTableRepository.findById(notExistedOrderTableId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderTableService.sit(notExistedOrderTableId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문테이블을 미사용중으로 변경한다")
	@Test
	void clear() {
		// given
		OrderTable orderTable = aNotEmptyOrderTable().build();
		UUID orderTableId = orderTable.getId();

		given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));
		given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), same(OrderStatus.COMPLETED))).willReturn(false);

		// when
		OrderTable result = orderTableService.clear(orderTableId);

		// then
		assertAll(
				() -> assertThat(result).isNotNull(),
				() -> assertThat(result.isEmpty()).isTrue()
		);
	}

	@DisplayName("존재하지 않는 주문테이블을 미사용중으로 변경하는 경우 예외가 발생한다")
	@Test
	void clearNotExist() {
		// given
		UUID notExistedOrderTableId = UUID.randomUUID();

		OrderTable orderTable = aOrderTable().withEmpty(false).build();

		given(orderTableRepository.findById(notExistedOrderTableId)).willReturn(Optional.empty());

		// when then
		assertThatThrownBy(() -> orderTableService.clear(notExistedOrderTableId))
				.isInstanceOf(NoSuchElementException.class);
	}

	@DisplayName("주문상태가 완료가 아닌 주문테이블을 미사용으로 변경하는 경우 예외가 발생한다")
	@Test
	void clearInvalidOrderStatus() {
		// given
		OrderTable orderTable = aOrderTable().withEmpty(false).build();
		UUID orderTableId = orderTable.getId();

		given(orderTableRepository.findById(orderTableId)).willReturn(Optional.of(orderTable));
		given(orderRepository.existsByOrderTableAndStatusNot(any(OrderTable.class), same(OrderStatus.COMPLETED))).willReturn(true);

		// when then
		assertThatThrownBy(() -> orderTableService.clear(orderTableId))
				.isInstanceOf(IllegalStateException.class);
	}
}
