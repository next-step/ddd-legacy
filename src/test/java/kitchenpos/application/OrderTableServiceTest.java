package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;
import java.util.UUID;

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
        @DisplayName("좌석에 착석 할 수 있다.")
        @Test
        void sitTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);

            orderTable = orderTableService.sit(orderTable.getId());

            assertThat(orderTable.isOccupied()).isTrue();
        }

        @DisplayName("존재하지 않은 주문 테이블의 경우 예외가 발생한다.")
        @Test
        void sitFailWhenNotExistOrderTableTest() {
            UUID notExistOrderTableId = UUID.randomUUID();
            assertThatThrownBy(() -> orderTableService.sit(notExistOrderTableId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class clearTest {
        @DisplayName("좌석을 비울 수 있다.")
        @Test
        void clearTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);
            orderTable = orderTableService.sit(orderTable.getId());

            orderTable = orderTableService.clear(orderTable.getId());

            assertThat(orderTable.isOccupied()).isFalse();
        }

        @Disabled("TODO: Order Fixtures를 생성하여 완료된 주문을 생성하고 테스트를 진행해야 합니다.")
        @DisplayName("좌석에 완료된 주문이 없는 경우 예외가 발생한다.")
        @Test
        void clearFailWhenNotExistCompletedOrderTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);
            orderTable = orderTableService.sit(orderTable.getId());

            // TODO: Order Fixtures를 생성하여 완료되지 않은 주문을 생성해야 합니다.
            
            UUID orderTableId = orderTable.getId();

            assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class changeNumberOfGuestsTest {
    }

    @Nested
    class findAllTest {
    }
}
