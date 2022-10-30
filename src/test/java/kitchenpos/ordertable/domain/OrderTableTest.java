package kitchenpos.ordertable.domain;

import kitchenpos.common.vo.Name;
import kitchenpos.order.domain.Order;
import kitchenpos.order.domain.OrderLineItem;
import kitchenpos.order.domain.OrderType;
import kitchenpos.order.vo.DeliveryAddress;
import kitchenpos.ordertable.vo.NumberOfGuests;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;
import java.util.UUID;

import static kitchenpos.menu.menu.MenuFixture.menu;
import static kitchenpos.menu.menu.MenuFixture.menuProducts;
import static kitchenpos.menu.menugroup.MenuGroupFixture.menuGroup;
import static kitchenpos.order.OrderFixture.orderLineItems;
import static org.assertj.core.api.Assertions.*;

@DisplayName("주문 테이블")
class OrderTableTest {

    @DisplayName("주문 테이블 생성 시 주문 테이블명을 입력받는다.")
    @ParameterizedTest
    @CsvSource({"주문테이블명, 1"})
    void createOrderTable(String name, int numberOfGuests) {
        assertThatNoException().isThrownBy(() -> orderTable(name, numberOfGuests));
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
    @CsvSource({"주문테이블명, 1"})
    void change(String name, int numberOfGuests) {
        OrderTable orderTable = orderTable(name, numberOfGuests);
        assertThat(orderTable.isOccupied()).isFalse();
        orderTable.occupied();
        assertThat(orderTable.isOccupied()).isTrue();
    }

    @DisplayName("주문 테이블의 착석여부를 공석으로 변경할 수 있다.")
    @ParameterizedTest
    @CsvSource({"주문테이블명, 1"})
    void vacant(String name, int numberOfGuests) {
        OrderTable orderTable = orderTable(name, numberOfGuests);
        assertThat(orderTable.isOccupied()).isFalse();
        orderTable.occupied();
        assertThat(orderTable.isOccupied()).isTrue();
        orderTable.vacant();
        assertThat(orderTable.isOccupied()).isFalse();
    }

    @DisplayName("주문 테이블의 착석 인원 변경 시 0명보다 작을 수 없다.")
    @ParameterizedTest
    @CsvSource({"주문테이블명, 1, -1"})
    void validateGuestsNumber(String name, int numberOfGuests, int changeNumberOfGuests) {
        OrderTable orderTable = orderTable(name, numberOfGuests);
        orderTable.occupied();
        assertThat(orderTable.isOccupied()).isTrue();
        assertThatThrownBy(() -> orderTable.changeNumberOfGuests(new NumberOfGuests(changeNumberOfGuests)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 테이블의 착석 인원 변경 시 0명보다 작을 수 없다.");
    }

    @DisplayName("주문 테이블이 공석일 경우 착석 인원을 변경 할 수 없다.")
    @ParameterizedTest
    @CsvSource({"주문테이블명, 1, 1"})
    void validateOccupied(String name, int numberOfGuests, int changeNumberOfGuests) {
        OrderTable orderTable = orderTable(name, numberOfGuests);
        assertThat(orderTable.isOccupied()).isFalse();
        assertThatThrownBy(() -> orderTable.changeNumberOfGuests(new NumberOfGuests(changeNumberOfGuests)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 테이블이 공석일 경우 착석 인원을 변경 할 수 없다.");
    }

    @DisplayName("주문 테이블을 생성할 수 있다.")
    @ParameterizedTest
    @CsvSource({"주문테이블명, 1"})
    void createProduct(String name, int numberOfGuests) {
        assertThatNoException().isThrownBy(() -> orderTable(name, numberOfGuests));
    }

    @DisplayName("주문 테이블의 착석 인원을 변경 할 수 있다.")
    @ParameterizedTest
    @CsvSource({"주문테이블명, 1, 10"})
    void changeNumberOfGuests(String name, int numberOfGuests, int changeNumberOfGuests) {
        OrderTable orderTable = orderTable(name, numberOfGuests);
        orderTable.occupied();
        orderTable.changeNumberOfGuests(new NumberOfGuests(changeNumberOfGuests));
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(changeNumberOfGuests);
    }

    @DisplayName("주문 테이블 공석으로 변경 시 주문 상태가 완료일때만 변경 가능하다.")
    @ParameterizedTest
    @CsvSource({"주문테이블명, 1, 10"})
    void vacant_status(String name, int numberOfGuests) {
        assertThatThrownBy(() -> order(name, numberOfGuests, orderLineItems(menu(menuGroup(UUID.randomUUID()), menuProducts(UUID.randomUUID())))).vacant())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 테이블 공석으로 변경 시 주문 상태가 완료일때만 변경 가능하다.");
    }

    private static Order order(String name, int numberOfGuests, List<OrderLineItem> orderLineItems) {
        return new Order(UUID.randomUUID(), OrderType.DELIVERY, orderLineItems, orderTable(name, numberOfGuests), new DeliveryAddress("주소"));
    }

    private static OrderTable orderTable(String name, int numberOfGuests) {
        return new OrderTable(UUID.randomUUID(), new Name(name, false), new NumberOfGuests(numberOfGuests));
    }

}
