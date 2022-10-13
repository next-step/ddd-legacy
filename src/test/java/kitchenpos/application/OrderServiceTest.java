package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.domain.OrderStatus.ACCEPTED;
import static kitchenpos.domain.OrderStatus.COMPLETED;
import static kitchenpos.domain.OrderStatus.DELIVERED;
import static kitchenpos.domain.OrderStatus.DELIVERING;
import static kitchenpos.domain.OrderStatus.SERVED;
import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql({"/truncate-all.sql", "/insert-order-integration.sql"})
@SpringBootTest
class OrderServiceTest extends IntegrationTest {
    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create() {
        final OrderType type = EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        final Order response = orderService.create(request);

        assertThat(response).isNotNull();
    }

    @DisplayName("상품의 타입은 비어있을 수 없다.")
    @Test
    void createWithoutOrderType() {
        final OrderType type = null;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문은 메뉴를 필수값으로 갖는다.")
    @Test
    void createWithoutMenu() {
        final OrderType type = EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of();
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문의 메뉴는 키친포스에 등록된 메뉴여야한다.")
    @Test
    void createWithNotRegisteredMenu() {
        final OrderType type = EAT_IN;
        final String unRegisteredMenuId = "f59b1e1c-b145-440a-aa6f-6095a0e2d63a";
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, unRegisteredMenuId, new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 주문이 아닌 경우 주문한 메뉴의 수량이 0보다 크거나 같아야 한다.")
    @EnumSource(names = {"DELIVERY", "TAKEOUT"})
    @ParameterizedTest
    void createWithGreaterThanZeroQuantity(final OrderType type) {
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(-1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("공개 설정된 메뉴만 주문할 수 있다.")
    @Test
    void createWithNoneDisplayedMenu() {
        final OrderType type = EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63c", new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("등록된 메뉴의 가격과 주문한 메뉴의 가격은 같아야 한다.")
    @Test
    void createWithDifferentPrice() {
        final OrderType type = EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("17000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달주문일 때, 주소는 비어있을 수 없다.")
    @Test
    void createWithEmptyAddress() {
        final OrderType type = DELIVERY;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("16000")));
        final String deliveryAddress = null;
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장주문일 때, 점유된 테이블을 선택할 수 없다.")
    @Test
    void createWithOccupiedTable() {
        final OrderType type = EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035521");

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 수락할 수 있다.")
    @Test
    void accept() {
        final UUID orderId = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8");

        final Order response = orderService.accept(orderId);

        assertThat(response.getStatus()).isEqualTo(ACCEPTED);
    }

    @DisplayName("주문을 요청해야 수락할 수 있다.")
    @Test
    void acceptWithNoOrder() {
        final UUID orderId = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd9");

        assertThatThrownBy(() -> orderService.accept(orderId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문을 제공할 수 있다.")
    @Test
    void serve() {
        final UUID orderId = UUID.fromString("79d78f38-3bff-457c-bb72-26319c985fd8");

        final Order response = orderService.serve(orderId);

        assertThat(response.getStatus()).isEqualTo(SERVED);
    }

    @DisplayName("주문을 수락해야 제공할 수 있다.")
    @Test
    void acceptWithNoAcceptedOrder() {
        final UUID orderId = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd9");

        assertThatThrownBy(() -> orderService.serve(orderId))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("배달 주문 시작요청을 할 수 있다.")
    @Test
    void startDelivery() {
        final UUID orderId = UUID.fromString("89d78f38-3bff-457c-bb72-26319c985fd8");

        final Order response = orderService.startDelivery(orderId);

        assertThat(response.getStatus()).isEqualTo(DELIVERING);
    }

    @DisplayName("배달 주문이 아니라면 요청할 수 없다.")
    @Test
    void startDeliveryWithNoDelivery() {
        final UUID orderId = UUID.fromString("79d78f38-3bff-457c-bb72-26319c985fd8");

        assertThatThrownBy(() -> orderService.startDelivery(orderId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문이 제공할 수 있는 상태여야 한다.")
    @Test
    void startDeliveryWithNoServedOrder() {
        final UUID orderId = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8");

        assertThatThrownBy(() -> orderService.startDelivery(orderId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 주문 완료 요청을 할 수 있다.")
    @Test
    void completeDelivery() {
        final UUID orderId = UUID.fromString("99d78f38-3bff-457c-bb72-26319c985fd8");

        final Order response = orderService.completeDelivery(orderId);

        assertThat(response.getStatus()).isEqualTo(DELIVERED);
    }

    @DisplayName("베달 중 상태가 아니라면 요청할 수 없다.")
    @Test
    void completeDeliveryWithNoDelivering() {
        final UUID orderId = UUID.fromString("69d78f38-3bff-457c-bb72-26319c985fd8");

        assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문을 완료할 수 있다.")
    @Test
    void complete() {
        final UUID orderId = UUID.fromString("09d78f38-3bff-457c-bb72-26319c985fd8");

        final Order response = orderService.complete(orderId);

        assertThat(response.getStatus()).isEqualTo(COMPLETED);
    }

    @DisplayName("배달 주문은 배달이 완료되어야 한다.")
    @Test
    void completeWithNoDelivered() {
        final UUID orderId = UUID.fromString("99d78f38-3bff-457c-bb72-26319c985fd8");

        assertThatThrownBy(() -> orderService.complete(orderId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("포장 주문과 매장 주문은 제공할 수 있는 상태여야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"19d78f38-3bff-457c-bb72-26319c985fd8", "29d78f38-3bff-457c-bb72-26319c985fd8"})
    void completeWithNoServed(final String strId) {
        final UUID orderId = UUID.fromString(strId);

        assertThatThrownBy(() -> orderService.complete(orderId))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("매장 주문의 경우 주문이 완료되면 테이블을 비워야 한다.")
    @Test
    void completeWith() {
        final UUID orderId = UUID.fromString("39d78f38-3bff-457c-bb72-26319c985fd8");

        final Order response = orderService.complete(orderId);

        assertThat(response.getOrderTable().isOccupied()).isFalse();
        assertThat(response.getOrderTable().getNumberOfGuests()).isEqualTo(0);
    }

    @DisplayName("주문을 여러개 조회할 수 있다.")
    @Test
    void findAll() {
        final List<Order> response = orderService.findAll();

        assertThat(response).hasSize(8);
    }
}
