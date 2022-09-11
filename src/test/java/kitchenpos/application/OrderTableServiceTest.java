package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.helper.InMemoryOrderRepository;
import kitchenpos.helper.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final OrderRepository orderRepository = new InMemoryOrderRepository();

    private OrderTableService testTarget;

    @BeforeEach
    void setUp() {
        testTarget = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블 등록 테스트")
    @Nested
    class CreateTest {

        @DisplayName("주문 테이블을 등록 할 수 있다.")
        @Test
        void test01() {
            // given
            var request = new OrderTable();
            request.setName("1번 테이블");

            // when
            OrderTable actual = testTarget.create(request);

            // then
            assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(request.getName()),
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isOccupied()).isFalse()
            );
        }

        @DisplayName("주문 테이블 이름은 비어 있을 수 없다.")
        @Test
        void test02() {
            var request = new OrderTable();

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.create(request));
        }
    }

}