package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("주문 테이블과 특정 상태가 아닌 주문이 존재하지 않으면 false를 반환한다.")
    @Test
    void exists_by_order_table_and_status_not_if_not_exists_then_false() {
        // given
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("테이블");
        orderTable.setNumberOfGuests(4);
        orderTable.setOccupied(true);
        entityManager.persist(orderTable);

        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.COMPLETED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderTable(orderTable);
        entityManager.persist(order);
        entityManager.flush();

        // when
        var result = orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED);

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("주문 테이블과 특정 상태가 아닌 주문이 존재하면 true를 반환한다.")
    @Test
    void exists_by_order_table_and_status_not_if_exists_then_true() {
        // given
        var orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());
        orderTable.setName("테이블");
        orderTable.setNumberOfGuests(4);
        orderTable.setOccupied(true);
        entityManager.persist(orderTable);

        var order = new Order();
        order.setId(UUID.randomUUID());
        order.setType(OrderType.EAT_IN);
        order.setStatus(OrderStatus.COMPLETED);
        order.setOrderDateTime(LocalDateTime.now());
        order.setOrderTable(orderTable);
        entityManager.persist(order);
        entityManager.flush();

        // when
        var result = orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.ACCEPTED);

        // then
        assertThat(result).isTrue();
    }
}
