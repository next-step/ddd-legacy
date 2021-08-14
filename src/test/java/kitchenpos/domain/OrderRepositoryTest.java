package kitchenpos.domain;

import kitchenpos.FixtureData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest extends FixtureData {

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        fixtureOrders();
    }

    @DisplayName("주문 생성")
    @Test
    void createOrder() {
        // given
        Order order = orders.get(0);

        given(orderRepository.save(any())).willReturn(order);

        // when
        Order createOrder = orderRepository.save(order);

        // then
        assertThat(createOrder).isNotNull();
    }

    @DisplayName("주문 조회")
    @Test
    void findById() {
        // given
        Order order = orders.get(0);

        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        Order find = orderRepository.findById(order.getId()).get();

        // then
        assertThat(find.getId()).isEqualTo(order.getId());
    }

    @DisplayName("주문 내역 조회")
    @Test
    void findAll() {
        // given
        given(orderRepository.findAll()).willReturn(orders);

        // when
        List<Order> findAll = orderRepository.findAll();

        // then
        verify(orderRepository).findAll();
        verify(orderRepository, times(1)).findAll();
        assertAll(
                () -> assertThat(orders.containsAll(findAll)).isTrue(),
                () -> assertThat(orders.size()).isEqualTo(findAll.size())
        );
    }

    @DisplayName("테이블 주문완료 확인")
    @Test
    void existsByOrderTableAndStatusNot() {
        // given
        Order order = orders.get(0);
        order.getOrderTable();
        order.setStatus(OrderStatus.COMPLETED);

        given(orderRepository.existsByOrderTableAndStatusNot(order.getOrderTable(), OrderStatus.COMPLETED)).willReturn(true);

        // when
        boolean exists = orderRepository.existsByOrderTableAndStatusNot(order.getOrderTable(), OrderStatus.COMPLETED);

        // then
        assertThat(exists).isTrue();
    }
}
