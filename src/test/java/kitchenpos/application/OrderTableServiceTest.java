package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;

import java.util.List;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.helper.InMemoryOrderRepository;
import kitchenpos.helper.InMemoryOrderTableRepository;
import kitchenpos.helper.OrderFixture;
import kitchenpos.helper.OrderTableFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    private OrderTableService testTarget;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InMemoryOrderTableRepository();
        orderRepository = new InMemoryOrderRepository();
        testTarget = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블 등록 테스트")
    @Nested
    class CreateTest {

        @DisplayName("주문 테이블을 등록 할 수 있다.")
        @Test
        void test01() {
            // given
            OrderTable request = OrderTableFixture.request("1번 테이블");

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
        @ParameterizedTest
        @NullAndEmptySource
        void test02(String name) {
            OrderTable request = OrderTableFixture.request(name);

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
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_TABLE.get());

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
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE.get());

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
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE.get());
            orderRepository.save(OrderFixture.eatInOrder(OrderStatus.WAITING, orderTable));

            // when & then
            assertThatIllegalStateException()
                .isThrownBy(() -> testTarget.clear(orderTable.getId()));
        }
    }

    @DisplayName("주문 테이블 손님 수 변경 테스트")
    @Nested
    class ChangeNumberOfGuestsTest {

        @DisplayName("주문 테이블의 손님 수를 변경 할 수 있다.")
        @Test
        void test01() {
            // given
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE.get());
            OrderTable request = OrderTableFixture.request(orderTable.getNumberOfGuests() + 1);

            // when
            OrderTable actual = testTarget.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThat(actual.getNumberOfGuests()).isEqualTo(request.getNumberOfGuests());
        }

        @DisplayName("주문 테이블의 손님 수를 0명 미만으로 변경 할 수 없다.")
        @Test
        void test02() {
            // given
            OrderTable request = OrderTableFixture.request(-1);

            // when & then
            assertThatIllegalArgumentException()
                .isThrownBy(() -> testTarget.changeNumberOfGuests(any(), request));
        }

        @DisplayName("빈 주문 테이블의 손님 수를 변경 할 수 없다.")
        @Test
        void test03() {
            // given
            OrderTable request = OrderTableFixture.request(1);
            OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_TABLE.get());

            // when & then
            assertThatIllegalStateException()
                .isThrownBy(() -> testTarget.changeNumberOfGuests(orderTable.getId(), request));
        }
    }

    @DisplayName("주문 테이블 목록 조회 테스트")
    @Nested
    class FindAllTest {

        @DisplayName("주문 테이블 목록을 조회 할 수 있다.")
        @Test
        void test01() {
            // given
            OrderTable orderTable1 = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE.get());
            OrderTable orderTable2 = orderTableRepository.save(OrderTableFixture.EMPTY_TABLE.get());

            // when
            List<OrderTable> actual = testTarget.findAll();

            // then
            assertThat(actual)
                .map(OrderTable::getId)
                .anyMatch(orderTableId -> orderTableId.equals(orderTable1.getId()))
                .anyMatch(orderTableId -> orderTableId.equals(orderTable2.getId()));
        }
    }

}