package kitchenpos.testglue.ordertable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import javax.persistence.EntityNotFoundException;
import kitchenpos.application.OrderTableService;
import kitchenpos.application.fixture.OrderTableMother;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.util.testglue.TestGlueConfiguration;
import kitchenpos.util.testglue.TestGlueOperation;
import kitchenpos.util.testglue.TestGlueSupport;
import kitchenpos.util.testglue.test.TestGlueResponse;

@TestGlueConfiguration
public class OrderTableTestGlueConfiguration extends TestGlueSupport {

	private final OrderTableService orderTableService;
	private final OrderTableRepository orderTableRepository;

	public OrderTableTestGlueConfiguration(OrderTableService orderTableService, OrderTableRepository orderTableRepository) {
		this.orderTableService = orderTableService;
		this.orderTableRepository = orderTableRepository;
	}

	@TestGlueOperation("{} 주문 테이블을 생성하면")
	public void createOrderTable1(String name) {
		OrderTable orderTable = OrderTableMother.create(name);

		TestGlueResponse<OrderTable> response = createResponse(() -> orderTableService.create(orderTable));

		put("orderTableResponse", response);
	}

	@TestGlueOperation("{} 주문 테이블을 생성하고")
	public void createOrderTable2(String name) {
		OrderTable orderTable = OrderTableMother.create(name);

		put(name, orderTableService.create(orderTable));
	}

	@TestGlueOperation("{} 주문 테이블이 생성된다")
	public void createOrderTableResponse_success(String name) {
		TestGlueResponse<OrderTable> response = getAsType("orderTableResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isTrue();

		UUID id = response.getData().getId();
		assertThat(orderTableRepository.findById(id)).isNotEmpty();
	}

	@TestGlueOperation("주문 테이블 생성에 실패한다")
	public void createOrderTableResponse_fail() {
		TestGlueResponse<OrderTable> response = getAsType("orderTableResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isFalse();
	}

	@TestGlueOperation("주문 테이블 변경에 실패한다")
	public void changeOrderTableResponse_fail() {
		TestGlueResponse<OrderTable> response = getAsType("orderTableResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isFalse();
	}

	@TestGlueOperation("{} 주문 테이블에 손님이 앉으면")
	public void sitOrderTable1(String name) {
		OrderTable orderTable = getAsType(name, OrderTable.class);

		UUID id = orderTable.getId();

		put("orderTableResponse", createResponse(() -> orderTableService.sit(id)));
	}

	@TestGlueOperation("{} 주문 테이블에 손님이 앉고")
	public void sitOrderTable2(String name) {
		OrderTable orderTable = getAsType(name, OrderTable.class);
		UUID id = orderTable.getId();

		put(name, orderTableService.sit(id));
	}

	@TestGlueOperation("{} 주문 테이블이 occupied상태가 된다.")
	public void sitOrderTableResponse(String name) {
		TestGlueResponse<OrderTable> response = getAsType("orderTableResponse", TestGlueResponse.class);

		UUID id = response.getData().getId();

		OrderTable orderTable = orderTableRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		assertThat(orderTable.isOccupied()).isTrue();
	}

	@TestGlueOperation("{} 주문 테이블 손님의 수를 {} 로 변경하면")
	public void changeNumberOfGuests(String name, String number) {
		OrderTable orderTable = getAsType(name, OrderTable.class);

		UUID id = orderTable.getId();

		OrderTable orderTableDto = new OrderTable();
		orderTableDto.setNumberOfGuests(Integer.parseInt(number));

		put(
			"orderTableResponse",
			createResponse(() -> orderTableService.changeNumberOfGuests(id, orderTableDto))
		);
	}

	@TestGlueOperation("{} 의 손님의 수가 {}로 변경된다")
	public void changeNumberOfGuestsResponse(String name, String number) {
		TestGlueResponse<OrderTable> response = getAsType("orderTableResponse", TestGlueResponse.class);

		UUID id = response.getData().getId();

		OrderTable orderTable = orderTableRepository.findById(id).orElseThrow(EntityNotFoundException::new);

		assertThat(orderTable.getNumberOfGuests()).isEqualTo(Integer.parseInt(number));
	}

	@TestGlueOperation("{} 주문 테이블을 정리하면")
	public void clear(String name) {
		OrderTable orderTable = getAsType(name, OrderTable.class);

		TestGlueResponse<OrderTable> response = createResponse(() -> orderTableService.clear(orderTable.getId()));

		put("orderTableResponse", response);
	}

	@TestGlueOperation("주문 테이블은 초기화 된다")
	public void clearResponse() {
		TestGlueResponse<OrderTable> response = getAsType("orderTableResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isTrue();

		OrderTable orderTable = orderTableRepository.findById(response.getData().getId())
			.orElseThrow(EntityNotFoundException::new);

		assertThat(orderTable.isOccupied()).isFalse();
		assertThat(orderTable.getNumberOfGuests()).isZero();
	}

	@TestGlueOperation("주문 테이블 초기화에 실패한다")
	public void clear_fail() {
		TestGlueResponse<OrderTable> response = getAsType("orderTableResponse", TestGlueResponse.class);

		assertThat(response.isOk()).isFalse();
	}
}
