package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class OrderTableServiceTest {
    @Autowired
    private OrderTableService orderTableService;

    @Nested
    class createTest {
        @DisplayName("주문 테이블을 생성한다.")
        @Test
        void createSuccessTest() {
            OrderTable orderTable = createOrderTable("1번");

            orderTable = orderTableService.create(orderTable);

            assertThat(orderTable.getId()).isNotNull();
            assertThat(orderTable.getNumberOfGuests()).isZero();
            assertThat(orderTable.isOccupied()).isFalse();
        }

        @DisplayName("이름이 빈값이거나 null인 경우 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createFailWhenNameIsNullAndEmptyTest(String name) {
            OrderTable orderTable = createOrderTable(name);

            assertThatThrownBy((() -> orderTableService.create(orderTable)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class sitTest {
    }

    @Nested
    class clearTest {
    }

    @Nested
    class changeNumberOfGuestsTest {
    }

    @Nested
    class findAllTest {
    }
}
