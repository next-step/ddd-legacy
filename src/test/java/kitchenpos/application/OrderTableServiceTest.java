package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.helper.InMemoryOrderRepository;
import kitchenpos.helper.InMemoryOrderTableRepository;
import kitchenpos.helper.OrderFixture;
import kitchenpos.helper.OrderTableFixture;
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

    @DisplayName("주문 테이블 착석 테스트")
    @Nested
    class SitTest {

        @DisplayName("주문 테이블에 손님이 착석 할 수 있다.")
        @Test
        void test01() {
            // given
            var orderTable = OrderTableFixture.create(false);
            orderTableRepository.save(orderTable);

            // when
            OrderTable actual = testTarget.sit(orderTable.getId());

            // then
            assertThat(actual.isOccupied()).isTrue();
        }
    }

    @DisplayName("주문 테이블 빈 테이블 세팅 테스트")
    @Nested
    class ClearTest {

        @DisplayName("주문 테이블을 빈 테이블로 세팅 할 수 있다.")
        @Test
        void test01() {
            // given
            OrderTable orderTable = OrderTableFixture.create(1, true);
            orderTableRepository.save(orderTable);

            // when
            OrderTable actual = testTarget.clear(orderTable.getId());

            // then
            assertAll(
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isOccupied()).isFalse()
            );
        }

        @DisplayName("주문 테이블의 주문 상태가 완료 상태가 아니면, 빈 테이블로 세팅 할 수 없다.")
        @Test
        void test02() {
            // given
            OrderTable orderTable = OrderTableFixture.create(1, true);
            orderTableRepository.save(orderTable);
            orderRepository.save(OrderFixture.create(
                OrderType.EAT_IN,
                OrderStatus.WAITING,
                orderTable
            ));

            // when & then
            assertThatIllegalStateException()
                .isThrownBy(() -> testTarget.clear(orderTable.getId()));
        }
    }

}