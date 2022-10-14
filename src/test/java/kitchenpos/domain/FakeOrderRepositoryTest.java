package kitchenpos.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class FakeOrderRepositoryTest {

    // SUT

    private final FakeOrderRepository fakeOrderRepository = new FakeOrderRepository();

    @DisplayName("주문이 저장되어야 한다.")
    @Test
    void dyqyjdib() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());

        // when
        final Order savedOrder = this.fakeOrderRepository.save(order);

        // then
        assertThat(savedOrder).isEqualTo(order);
    }

    @DisplayName("저장된 주문은 ID로 찾을 수 있어야 한다.")
    @Test
    void edrlpaxz() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        final Order savedOrder = this.fakeOrderRepository.save(order);

        // when
        final Order foundOrder = this.fakeOrderRepository.findById(savedOrder.getId())
            .orElse(null);

        // then
        assertThat(foundOrder).isEqualTo(order);
    }

    @DisplayName("저장되지 않은 주문은 ID로 찾을 수 없어야 한다.")
    @Test
    void daikreqr() {
        // given
        final Order order = new Order();
        order.setId(UUID.randomUUID());
        this.fakeOrderRepository.save(order);

        // when
        final Order foundOrder = this.fakeOrderRepository.findById(UUID.randomUUID())
            .orElse(null);

        // then
        assertThat(foundOrder).isNull();
    }

    @DisplayName("빈 상태에서 모두 조회시 빈 List가 반환되어야 한다.")
    @Test
    void plpfwqwb() {
        // when
        final List<Order> orders = this.fakeOrderRepository.findAll();

        // then
        assertThat(orders).isEmpty();
    }

    @DisplayName("모두 조회시 저장된 수 만큼 조회되어야 한다.")
    @ValueSource(ints = {
        6, 3, 21, 1, 21,
        9, 20, 20, 5, 27,
    })
    @ParameterizedTest
    void mzetglfh(final int size) {
        // given
        IntStream.range(0, size)
            .forEach(n -> {
                final Order order = new Order();
                order.setId(UUID.randomUUID());
                this.fakeOrderRepository.save(order);
            });

        // when
        final List<Order> orders = this.fakeOrderRepository.findAll();

        // then
        assertThat(orders).hasSize(size);
    }

    @DisplayName("주문 테이블과 주문 상태를 기준으로 존재 여부를 확인한 결과 해당 상태의 주문만 존재하는 경우 false가 반환되어야 한다.")
    @EnumSource(OrderStatus.class)
    @ParameterizedTest
    void nnbkxdbv(final OrderStatus orderStatus) {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setStatus(orderStatus);
        this.fakeOrderRepository.save(order);

        // when
        final boolean result = this.fakeOrderRepository.existsByOrderTableAndStatusNot(
            orderTable,
            orderStatus
        );

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("주문 테이블과 주문 상태를 기준으로 존재 여부를 확인한 결과 해당 상태가 아닌 주문이 존재하는 경우 True가 반환되어야 한다.")
    @EnumSource(OrderStatus.class)
    @ParameterizedTest
    void pctdzfcg(final OrderStatus orderStatus) {
        // given
        final OrderTable orderTable = new OrderTable();
        orderTable.setId(UUID.randomUUID());

        final OrderStatus[] orderStatuses = OrderStatus.values();
        final int nextOrderStatusIndex = (orderStatus.ordinal() + 1) % orderStatuses.length;
        final OrderStatus nextOrderStatus = orderStatuses[nextOrderStatusIndex];

        final Order order = new Order();
        order.setOrderTableId(orderTable.getId());
        order.setStatus(nextOrderStatus);
        this.fakeOrderRepository.save(order);

        // when
        final boolean result = this.fakeOrderRepository.existsByOrderTableAndStatusNot(
            orderTable,
            orderStatus
        );

        // then
        assertThat(result).isTrue();
    }
}
