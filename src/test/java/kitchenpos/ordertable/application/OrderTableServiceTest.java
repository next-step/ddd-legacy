package kitchenpos.ordertable.application;

import kitchenpos.domain.Name;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.order.application.InMemoryOrderRepository;
import kitchenpos.order.application.InMemoryOrderTableRepository;
import kitchenpos.ordertable.domain.NumberOfGuests;
import kitchenpos.ordertable.domain.OrderTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("주문 테이블")
class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        orderTableRepository.save(orderTable("주문테이블명", 1));
        assertThat(orderTableService.findAll()).hasSize(1);
    }

    private static OrderTable orderTable(String name, int numberOfGuests) {
        return new OrderTable(new Name(name, false), new NumberOfGuests(numberOfGuests));
    }

}
