package kitchenpos.order.domain;

import kitchenpos.domain.Name;
import kitchenpos.ordertable.domain.NumberOfGuests;
import kitchenpos.ordertable.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static kitchenpos.order.domain.OrderFixture.orderLineItems;
import static org.assertj.core.api.Assertions.*;

@DisplayName("주문 테이블")
class OrderTableTest {

    @DisplayName("주문 테이블 생성 시 주문 테이블명을 입력받는다.")
    @Test
    void createOrderTable() {
        assertThatNoException().isThrownBy(() -> orderTable("주문테이블명", 1));
    }

    @DisplayName("주문 테이블 생성 시 주문 테이블명은 필수이다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createOrderTable(String name) {
        assertThatThrownBy(() -> orderTable(name, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }

    @DisplayName("주문 테이블의 착석여부를 착석으로 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"테이블"})
    void change(String name) {
        OrderTable orderTable = orderTable(name, 1);
        assertThat(orderTable.isOccupied()).isFalse();
        orderTable.occupied();
        assertThat(orderTable.isOccupied()).isTrue();
    }

    @DisplayName("주문 테이블의 착석여부를 공석으로 변경할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"테이블"})
    void vacant(String name) {
        OrderTable orderTable = orderTable(name, 1);
        assertThat(orderTable.isOccupied()).isFalse();
        orderTable.occupied();
        assertThat(orderTable.isOccupied()).isTrue();
        orderTable.vacant();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블의 착석 인원 변경 시 0명보다 작을 수 없다.")
    @Test
    void validateGuestsNumber() {
        OrderTable orderTable = orderTable("주문테이블명", 1);
        orderTable.occupied();
        assertThat(orderTable.isOccupied()).isTrue();
        assertThatThrownBy(() -> orderTable.changeNumberOfGuests(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 테이블의 착석 인원 변경 시 0명보다 작을 수 없다.");
    }

    @DisplayName("주문 테이블이 공석일 경우 착석 인원을 변경 할 수 없다.")
    @Test
    void validateOccupied() {
        OrderTable orderTable = orderTable("주문테이블명", 1);
        assertThat(orderTable.isOccupied()).isFalse();
        assertThatThrownBy(() -> orderTable.changeNumberOfGuests(1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 테이블이 공석일 경우 착석 인원을 변경 할 수 없다.");
    }

    @DisplayName("주문 테이블을 생성할 수 있다.")
    @Test
    void createProduct() {
        assertThatNoException().isThrownBy(() -> orderTable("주문테이블명", 1));
    }

    @DisplayName("주문 테이블의 착석 인원을 변경 할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        OrderTable orderTable = orderTable("주문테이블명", 1);
        orderTable.occupied();
        orderTable.changeNumberOfGuests(10);
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(10);
    }

    @DisplayName("주문 테이블 공석으로 변경 시 주문 상태가 완료일때만 변경 가능하다.")
    @Test
    void vacant_status() {
        List<OrderLineItem> orderLineItems = orderLineItems();
        Order order = new Order(OrderType.DELIVERY, orderLineItems, new OrderTable(new Name("테이블명", false), new NumberOfGuests(1)), new DeliveryAddress("주소"));
        assertThatThrownBy(() -> order.vacant())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 테이블 공석으로 변경 시 주문 상태가 완료일때만 변경 가능하다.");
    }

    private static OrderTable orderTable(String name, int numberOfGuests) {
        return new OrderTable(new Name(name, false), new NumberOfGuests(numberOfGuests));
    }

}
