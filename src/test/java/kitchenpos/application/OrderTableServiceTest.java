package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import kitchenpos.test_fixture.OrderTableTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OrderTableService 클래스")
@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableService sut;

    @Autowired
    private DatabaseCleanStep databaseCleanStep;

    @DisplayName("주문 테이블을 생성")
    @Nested
    class Describe_create {

        @DisplayName("새로운 주문 테이블을 생성할 수 있다.")
        @Test
        void create() {
            // given
            OrderTable orderTable = OrderTableTestFixture.create()
                    .changeId(null)
                    .getOrderTable();

            // when
            OrderTable result = sut.create(orderTable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("테스트 테이블");
            assertThat(result.getNumberOfGuests()).isEqualTo(0);
            assertThat(result.isOccupied()).isFalse();
        }
    }
}
