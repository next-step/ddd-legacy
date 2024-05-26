package kitchenpos.integrationTest;

import kitchenpos.application.OrderTableService;
import kitchenpos.domain.*;
import kitchenpos.fixtures.OrderFixture;
import kitchenpos.fixtures.OrderTableFixture;
import kitchenpos.repository.InMemoryOrderRepository;
import kitchenpos.repository.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
public class OrderTableServiceIntegrationTest {


    private OrderTableService orderTableService;

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Test
    void 주문_테이블을_생성_할_수_있다() {
        OrderTable orderTable = OrderTableFixture.create("테이블1", 0, false);

        OrderTable createdOrderTable = orderTableService.create(orderTable);

        assertThat(createdOrderTable.getId()).isNotNull();
        assertThat(createdOrderTable.getName()).isEqualTo("테이블1");
        assertThat(createdOrderTable.getNumberOfGuests()).isEqualTo(0);
        assertThat(createdOrderTable.isOccupied()).isFalse();
    }

    @Test
    void 이름이_없으면_주문_테이블_생성_실패() {
        OrderTable orderTable = OrderTableFixture.create("", 0, false);

        assertThatThrownBy(() -> orderTableService.create(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 주문_테이블을_앉힐_수_있다() {
        OrderTable orderTable = OrderTableFixture.create("테이블1", 0, false);
        OrderTable createdOrderTable = orderTableService.create(orderTable);

        OrderTable occupiedOrderTable = orderTableService.sit(createdOrderTable.getId());

        assertThat(occupiedOrderTable.isOccupied()).isTrue();
    }

    @Test
    void 존재하지_않는_주문_테이블에_앉힐_수_없다() {
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void 사용중인_주문_테이블을_비울_수_있다() {
        OrderTable createdOrderTable = orderTableService.create(OrderTableFixture.create("테이블1", 0, false));
        orderTableService.sit(createdOrderTable.getId());

        OrderTable clearedOrderTable = orderTableService.clear(createdOrderTable.getId());

        assertThat(clearedOrderTable.isOccupied()).isFalse();
        assertThat(clearedOrderTable.getNumberOfGuests()).isEqualTo(0);
    }

    @Test
    void 주문을통해_사용중인_테이블은_주문_상태가_완료되지_않은_경우_주문_테이블을_비울_수_없다() {
        OrderTable orderTable = OrderTableFixture.create("테이블1", 0, false);
        OrderTable createdOrderTable = orderTableService.create(orderTable);
        orderTableService.sit(createdOrderTable.getId());

        Order order = OrderFixture.create(OrderType.TAKEOUT, OrderStatus.ACCEPTED, null, null, null, createdOrderTable);
        orderRepository.save(order);

        assertThatThrownBy(() -> orderTableService.clear(createdOrderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 주문_테이블의_손님_수를_변경할_수_있다() {
        OrderTable createdOrderTable = orderTableService.create(OrderTableFixture.create("테이블1", 0, false));
        orderTableService.sit(createdOrderTable.getId());

        OrderTable updateRequest = createdOrderTable;
        updateRequest.setNumberOfGuests(4);

        OrderTable updatedOrderTable = orderTableService.changeNumberOfGuests(createdOrderTable.getId(), updateRequest);

        assertThat(updatedOrderTable.getNumberOfGuests()).isEqualTo(4);
    }

    @Test
    void 손님_수가_음수인_경우_변경_실패() {
        OrderTable createdOrderTable = orderTableService.create(OrderTableFixture.create("테이블1", 0, false));
        orderTableService.sit(createdOrderTable.getId());

        OrderTable updateRequest = createdOrderTable;
        updateRequest.setNumberOfGuests(-1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(createdOrderTable.getId(), updateRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 비어있는_테이블의_손님_수를_변경_할_수_없다() {
        OrderTable createdOrderTable = orderTableService.create(OrderTableFixture.create("테이블1", 0, false));

        OrderTable updateRequest = createdOrderTable;
        updateRequest.setNumberOfGuests(4);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(createdOrderTable.getId(), updateRequest))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 모든_주문_테이블을_조회할_수_있다() {
        orderTableService.create(OrderTableFixture.create("테이블1", 0, false));

        orderTableService.create(OrderTableFixture.create("테이블2", 0, false));

        List<OrderTable> orderTables = orderTableService.findAll();
        assertThat(orderTables).hasSize(2);
    }

}
