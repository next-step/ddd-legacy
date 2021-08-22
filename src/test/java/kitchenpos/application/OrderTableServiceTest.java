package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

public class OrderTableServiceTest {
    private OrderTableService orderTableService;
    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private OrderRepository orderRepository = new InMemoryOrderRepository();
    private OrderTable orderTable;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
        orderTable = new OrderTable();
        orderTable.setName("주문테이블");
    }

    @DisplayName("주문 테이블을 등록할 수 있다.")
    @Test
    void create() {
        OrderTable saved = 주문테이블등록(orderTable);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(orderTable.getName()),
                () -> assertThat(saved.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(saved.isEmpty()).isTrue()
        );
    }

    @DisplayName("주문 테이블 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void create(String name) {
        orderTable.setName(name);

        assertThatThrownBy(() -> 주문테이블등록(orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }


    OrderTable 주문테이블등록(OrderTable orderTable) {
        return orderTableService.create(orderTable);
    }
}
