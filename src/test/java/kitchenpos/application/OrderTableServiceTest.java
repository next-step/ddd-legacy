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

import java.util.List;
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
            assertThat(orderTable.getNumberOfGuests()).isZero();
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
        @DisplayName("손님 수를 변경할 수 있다.")
        @Test
        void changeNumberOfGuestsSuccessTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);
            orderTableService.sit(orderTable.getId());
            orderTable.setNumberOfGuests(4);
            orderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

            assertThat(orderTable.getNumberOfGuests()).isEqualTo(4);
        }

        @DisplayName("손님 수가 0보다 작으면 예외가 발생한다.")
        @Test
        void changeNumberOfGuestsFailWhenNumberOfGuestsIsLessThanZeroTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);
            orderTableService.sit(orderTable.getId());
            orderTable.setNumberOfGuests(-1);

            UUID orderTableId = orderTable.getId();
            OrderTable changeOrderTable = orderTable;
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, changeOrderTable))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("착석하지 않은 경우 예외가 발생한다.")
        @Test
        void changeNumberOfGuestsFailWhenNotSitTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);
            orderTable.setNumberOfGuests(4);

            UUID orderTableId = orderTable.getId();
            OrderTable changeOrderTable = orderTable;
            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, changeOrderTable))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class findAllTest {
        @DisplayName("주문 테이블 목록을 조회한다.")
        @Test
        void findAllSuccessTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);

            List<OrderTable> orderTables = orderTableService.findAll();
            List<UUID> orderTableIds = orderTables.stream()
                    .map(OrderTable::getId)
                    .toList();

            assertThat(orderTableService.findAll()).hasSize(1);
            assertThat(orderTableIds).contains(orderTable.getId());
        }
    }
}
