package kitchenpos.application;

import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Sql({"/truncate-all.sql", "/insert-order-integration.sql"})
@SpringBootTest
class OrderServiceTest {
    @Autowired
    private OrderService sut;

    @DisplayName("주문을 생성할 수 있다.")
    @Test
    void create() {
        final OrderType type = EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of(
                new OrderLineItem(1L, "f59b1e1c-b145-440a-aa6f-6095a0e2d63b", new BigDecimal("16000")));
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        final Order response = sut.create(request);

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

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문은 메뉴를 필수값으로 갖는다.")
    @Test
    void createWithoutMenu() {
        final OrderType type = EAT_IN;
        final List<OrderLineItem> orderLineItems = List.of();
        final String deliveryAddress = "주소";
        final Order request = new Order(type, orderLineItems, deliveryAddress, "8d710043-29b6-420e-8452-233f5a035520");

        assertThatThrownBy(() -> sut.create(request))
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

        assertThatThrownBy(() -> sut.create(request))
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

        assertThatThrownBy(() -> sut.create(request))
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

        assertThatThrownBy(() -> sut.create(request))
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

        assertThatThrownBy(() -> sut.create(request))
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

        assertThatThrownBy(() -> sut.create(request))
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

        assertThatThrownBy(() -> sut.create(request))
                .isInstanceOf(IllegalStateException.class);
    }
}
