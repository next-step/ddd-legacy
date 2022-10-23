package kitchenpos.order.domain;

import kitchenpos.domain.Name;
import kitchenpos.ordertable.domain.NumberOfGuests;
import kitchenpos.ordertable.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static kitchenpos.order.domain.OrderFixture.orderLineItems;
import static org.assertj.core.api.Assertions.*;

@DisplayName("주문")
class OrderTest {

    @DisplayName("주문 타입은 null 일 수 없다.")
    @Test
    void requireOrderType() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        assertThatThrownBy(() -> new Order(null, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 타입을 입력해주세요.");
    }

    @DisplayName("주문 항목은 비어 있을 수 없다.")
    @Test
    void orderLineItemsNotNull() {
        assertThatThrownBy(() -> new Order(OrderType.TAKEOUT, null, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 항목은 비어 있을 수 없습니다.");
    }

    @DisplayName("주문을 수락 할 수 있다.")
    @Test
    void acceptSuccess() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void acceptFail() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThatThrownBy(order::accept)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("WAITING 상태만 접수가능합니다.");
    }

    @DisplayName("접수 상태가 아니면 제공할 수 없다.")
    @Test
    void served_fail() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        assertThatThrownBy(order::served)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ACCEPTED 상태만 SERVED 상태로 변경가능합니다");
    }

    @DisplayName("주문에 대해 제공할 수 있다.")
    @Test
    void served_success() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배송을 시작할 수 있다.")
    @Test
    void delivering_success() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        order.delivering();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 타입이 DELIVERING일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void delivering_fail_delivering() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        assertThatThrownBy(order::delivering)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 타입이 DELIVERY일 경우에만 배송 시작을 할 수 있습니다.");
    }

    @DisplayName("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void delivering_fail_served() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        assertThatThrownBy(order::delivering)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.");
    }

    @DisplayName("배송을 완료할 수 있다.")
    @Test
    void delivered_success() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        order.delivering();
        order.delivered();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.")
    @Test
    void delivered_fail() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        assertThatThrownBy(order::delivered)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.");
    }

    @DisplayName("주문 상태가 DELIVERED가 아니면 주문을 완료할 수 없다.")
    @Test
    void completed_fail_delivered() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        order.delivering();
        assertThatThrownBy(order::completed)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 상태가 DELIVERED가 아니면 주문을 완료할 수 없다.");
    }

    @DisplayName("주문 타입이 TAKEOUT이고 주문상태가 SERVED일 경우 주문을 완료할 수 있다.")
    @Test
    void completed_takeout() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.TAKEOUT, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        order.completed();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 타입이 EAT_IN이고 주문상태가 SERVED일 경우 주문을 완료할 수 있다.")
    @Test
    void completed_eatIn() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.EAT_IN, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        order.completed();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장 주문에서 착석된 테이블을 선택할 수 없다.")
    @Test
    void validateMenuSize() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        OrderTable orderTable = new OrderTable(new Name("테이블명", false), new NumberOfGuests(1));
        orderTable.occupied();
        assertThatThrownBy(() -> new Order(OrderType.DELIVERY, orderLineItems, orderTable, new DeliveryAddress("주소")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("매장 주문에서 착석된 테이블을 선택할 수 없다.");
    }

    @DisplayName("안보이는 메뉴가 주문될 수 없다.")
    @Test
    void orderHiddenMenu() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        OrderLineItem orderLineItem = orderLineItems.get(0);
        orderLineItem.getMenu().hide();
        OrderTable orderTable = new OrderTable(new Name("테이블명", false), new NumberOfGuests(1));
        orderTable.occupied();
        assertThatThrownBy(() -> new Order(OrderType.DELIVERY, orderLineItems, orderTable, new DeliveryAddress("주소")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("안보이는 메뉴가 주문될 수 없다.");
    }

    @DisplayName("배달 주문이면 배송지가 없을 수 없다.")
    @Test
    void createEmptyDeliveryAddress() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        OrderTable orderTable = new OrderTable(new Name("테이블명", false), new NumberOfGuests(1));
        assertThatThrownBy(() -> new Order(OrderType.DELIVERY, orderLineItems, orderTable, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("배달 주문이면 배송지가 없을 수 없다.");
    }

    @DisplayName("주문을 완료할 수 있다.")
    @Test
    void orderComplete() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        order.accept();
        order.served();
        order.delivering();
        order.delivered();
        order.completed();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 생성 시 주문 타입 / 주문 테이블 아이디 / 주문 항목 목록을 입력 받는다.")
    @Test
    void createOrder() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        assertThatNoException().isThrownBy(() -> new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소")));
    }
}
