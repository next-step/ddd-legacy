package kitchenpos.order.domain;

import kitchenpos.domain.Name;
import kitchenpos.ordertable.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("주문 테이블")
class OrderTableTest {

    @DisplayName("주문 테이블 생성 시 주문 테이블명을 입력받는다.")
    @Test
    void createOrderTable() {
        assertThatNoException().isThrownBy(() -> new OrderTable(new Name("주문테이블명", false)));
    }

    @DisplayName("주문 테이블 생성 시 주문 테이블명은 필수이다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createOrderTable(String name) {
        assertThatThrownBy(() -> new OrderTable(new Name(name, false)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }
}
