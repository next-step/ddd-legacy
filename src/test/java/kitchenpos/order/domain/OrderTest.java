package kitchenpos.order.domain;

import kitchenpos.ordertable.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static kitchenpos.menu.menu.MenuFixture.*;
import static kitchenpos.menu.menugroup.MenuGroupFixture.menuGroup;
import static kitchenpos.order.OrderFixture.*;
import static kitchenpos.ordertable.OrderTableFixture.orderTable;
import static org.assertj.core.api.Assertions.*;

@DisplayName("주문")
class OrderTest {

    @DisplayName("주문 타입은 null 일 수 없다.")
    @ParameterizedTest
    @CsvSource({"메뉴그룹, false, -1"})
    void requireOrderType() {
        assertThatThrownBy(() -> 주문타입NULL(menu(menuGroup(), menuProducts()), orderTable()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 타입을 입력해주세요.");
    }

    @DisplayName("주문 항목은 비어 있을 수 없다.")
    @Test
    void orderLineItemsNotNull() {
        assertThatThrownBy(() -> 주문항목NULL(menu(menuGroup(), menuProducts())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 항목은 비어 있을 수 없습니다.");
    }

    @DisplayName("주문을 수락 할 수 있다.")
    @Test
    void acceptSuccess() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("WAITING 상태가 아니면 접수를 받을 수 없다.")
    @Test
    void acceptFail() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        assertThatThrownBy(order::accept)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("WAITING 상태만 접수가능합니다.");
    }

    @DisplayName("접수 상태가 아니면 제공할 수 없다.")
    @Test
    void served_fail() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        assertThatThrownBy(order::served)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ACCEPTED 상태만 SERVED 상태로 변경가능합니다");
    }

    @DisplayName("주문에 대해 제공할 수 있다.")
    @Test
    void served_success() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("배송을 시작할 수 있다.")
    @Test
    void delivering_success() {
        Order order = deliveryOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        order.delivering();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 타입이 DELIVERING일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void delivering_fail_delivering() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        assertThatThrownBy(order::delivering)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 타입이 DELIVERY일 경우에만 배송 시작을 할 수 있습니다.");
    }

    @DisplayName("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.")
    @Test
    void delivering_fail_served() {
        Order order = deliveryOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        assertThatThrownBy(order::delivering)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 SERVED일 경우에만 배송 시작을 할 수 있다.");
    }

    @DisplayName("배송을 완료할 수 있다.")
    @Test
    void delivered_success() {
        Order order = deliveryOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        order.delivering();
        order.delivered();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.")
    @Test
    void delivered_fail() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        assertThatThrownBy(order::delivered)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 DELIVERING일 경우에만 배송을 완료할 수 있다.");
    }

    @DisplayName("주문 상태가 DELIVERED가 아니면 주문을 완료할 수 없다.")
    @Test
    void completed_fail_delivered() {
        Order order = deliveryOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        order.delivering();
        assertThatThrownBy(order::completed)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 상태가 DELIVERED가 아니면 주문을 완료할 수 없다.");
    }

    @DisplayName("주문 타입이 TAKEOUT이고 주문상태가 SERVED일 경우 주문을 완료할 수 있다.")
    @Test
    void completed_takeout() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        order.completed();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 타입이 EAT_IN이고 주문상태가 SERVED일 경우 주문을 완료할 수 있다.")
    @Test
    void completed_eatIn() {
        Order order = takeoutOrder(menu(menuGroup(), menuProducts()));
        order.accept();
        order.served();
        order.completed();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("매장 주문에서 착석된 테이블을 선택할 수 없다.")
    @Test
    void validateMenuSize() {
        OrderTable orderTable = orderTable();
        orderTable.occupied();
        assertThatThrownBy(() -> eatInOrder(menu(menuGroup(), menuProducts()), orderTable))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("매장 주문에서 착석된 테이블을 선택할 수 없다.");
    }

    @DisplayName("안보이는 메뉴가 주문될 수 없다.")
    @Test
    void orderHiddenMenu() {
        assertThatThrownBy(() -> takeoutOrder(안보이는메뉴(menuGroup(), menuProducts())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("안보이는 메뉴가 주문될 수 없다.");
    }

    @DisplayName("배달 주문이면 배송지가 없을 수 없다.")
    @Test
    void createEmptyDeliveryAddress() {
        assertThatThrownBy(() -> 배송지없는배달주문(menu(menuGroup(), menuProducts())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("배달 주문이면 배송지가 없을 수 없다.");
    }

    @DisplayName("주문을 완료할 수 있다.")
    @Test
    void orderComplete() {
        Order order = deliveryOrder(menu(menuGroup(), menuProducts()));
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
        assertThatNoException().isThrownBy(() -> eatInOrder(menu(menuGroup(), menuProducts()), orderTable()));
    }
}
