package kitchenpos.application;

import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderServiceTest extends InitTest {
    @Resource
    private OrderService target;

    @Test
    @DisplayName("1개 이상의 메뉴를 주문할 수 있으며, 각 메뉴를 1개 이상 주문할 수 있다.")
    void create() {
        Order request = buildValidTakeoutDelivery();

        target.create(request);
    }

    @Test
    @DisplayName("주문 시점의 각 메뉴별 주문가격과 실제 메뉴 가격이 다르면 안된다.")
    void noMenuOrderLineItemPriceDiffer() {
        Order request = buildValidTakeoutDelivery();
        request.getOrderLineItems().get(0).setPrice(BigDecimal.TEN);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("배달 주문은 주소를 가진다.")
    void createDelivery() {
        Order request = buildValidDeliveryOrder();

        target.create(request);
    }

    @Test
    @DisplayName("홀 주문은 먼저 테이블을 점유해야 한다.")
    void createEatIn() {
        Order request = buildValidEatInOrder();
        OrderTable orderTable = request.getOrderTable();
        orderTable.setOccupied(true);
        orderTableRepository.save(orderTable);

        target.create(request);
    }

    @Test
    @DisplayName("디피되지 않는 메뉴는 주문할 수 없다.")
    void cannotOrderNotDisplayedMenu() {
        Order request = buildValidTakeoutDelivery();
        Menu menu = request.getOrderLineItems().get(0).getMenu();
        menu.setDisplayed(false);
        menuRepository.save(menu);

        assertThatThrownBy(() -> {
            target.create(request);
        })
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("홀/포장 주문은 WAITING -> ACCEPTED -> SERVED -> COMPLETED 순서로 진행되며 이전 상태로 되돌아 갈 수 없다. 홀 주문의 경우, 음식을 다 먹고 손님이 떠나면 COMPLETED 상태가 된다.(해당 테이블의 점유가 해제된다.)")
    void orderStateEatIn() {
        Order request = buildValidEatInOrder();
        request.setOrderTable(buildOccupiedOrderTable());
        request.setOrderTableId(OCCUPIED_ORDER_TABLE_ID);

        Order result = target.create(request);
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.WAITING);

        target.accept(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.ACCEPTED);

        target.serve(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.SERVED);

        Order complete = target.complete(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.COMPLETED);
        assertThat(complete.getOrderTable().isOccupied()).isFalse();
    }

    @Test
    @DisplayName("배달 주문은 WAITING -> ACCEPTED -> SERVED -> DELIVERING -> DELIVERED -> COMPLETED 순서로 진행되며 이전 상태로 되돌아 갈 수 없다. 배달 주문의 경우, ACCEPTED 상태로 바뀌면서 외부 배달 시스템에 배달 요청을 보낸다.")
    void orderStateDelivery() {
        Order request = buildValidDeliveryOrder();

        Order result = target.create(request);
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.WAITING);

        target.accept(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.ACCEPTED);


        target.serve(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.SERVED);

        target.startDelivery(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.DELIVERING);

        target.completeDelivery(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.DELIVERED);

        target.complete(result.getId());
        assertThat(orderRepository.findById(result.getId()).get().getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}
